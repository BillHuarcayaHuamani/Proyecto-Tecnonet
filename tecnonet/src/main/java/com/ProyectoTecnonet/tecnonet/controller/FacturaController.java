package com.ProyectoTecnonet.tecnonet.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoTecnonet.tecnonet.dto.FacturaDTO;
import com.ProyectoTecnonet.tecnonet.dto.PagoRequest;
import com.ProyectoTecnonet.tecnonet.model.Factura;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.FacturaRepository;
import com.ProyectoTecnonet.tecnonet.service.FacturacionService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private static final Logger logger = LoggerFactory.getLogger(FacturaController.class);

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturacionService facturacionService;

    @GetMapping("/todas")
    @PreAuthorize("hasRole('OPERARIO') or hasRole('ADMINISTRADOR')")
    public List<Factura> getAllFacturas() {
        return facturaRepository.findAllWithDetails();
    }

    @GetMapping("/mis-facturas")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<List<FacturaDTO>> getMisFacturas() { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = (Usuario) authentication.getPrincipal();

        List<Factura> todasLasFacturas = facturaRepository.findAllWithDetails();

        List<FacturaDTO> misFacturasDTO = todasLasFacturas.stream()
            .filter(factura -> factura.getContrato().getUsuario().getIdUsuario().equals(usuarioActual.getIdUsuario()))
            .map(FacturaDTO::new) 
            .collect(Collectors.toList());

        return ResponseEntity.ok(misFacturasDTO);
    }

    @PutMapping("/{idFactura}/pagar")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<?> pagarFactura( 
            @PathVariable Integer idFactura,
            @Valid @RequestBody PagoRequest pagoRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = (Usuario) authentication.getPrincipal();

        try {
            Factura factura = facturaRepository.findByIdWithContratoAndUsuario(idFactura)
                    .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada con ID: " + idFactura));

            if (!factura.getContrato().getUsuario().getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                logger.warn("Intento de pago no autorizado para factura {} por usuario {}", idFactura, usuarioActual.getEmail());
                return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso para pagar esta factura."));
            }

            Factura facturaPagada = facturacionService.marcarComoPagada(idFactura, pagoRequest.getMetodoPago());

            FacturaDTO facturaDTO = new FacturaDTO(facturaPagada);
            logger.info("Factura {} pagada exitosamente por usuario {}", idFactura, usuarioActual.getEmail());
            return ResponseEntity.ok(facturaDTO);

        } catch (EntityNotFoundException e) {
             logger.warn("Intento de pago para factura no encontrada: {}", idFactura);
             return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
             logger.warn("Intento de pago fallido para factura {} (estado no válido): {}", idFactura, e.getMessage());
             return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
             logger.error("Error inesperado al procesar pago para factura {}: {}", idFactura, e.getMessage(), e);
             return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor al procesar el pago."));
        }
    }
}