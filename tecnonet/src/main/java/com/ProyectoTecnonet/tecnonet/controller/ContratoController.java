package com.ProyectoTecnonet.tecnonet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.model.EstadoContrato;
import com.ProyectoTecnonet.tecnonet.repository.ContratoRepository;
import com.ProyectoTecnonet.tecnonet.repository.EstadoContratoRepository;
import com.ProyectoTecnonet.tecnonet.service.FacturacionService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/contratos")
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
public class ContratoController {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private EstadoContratoRepository estadoContratoRepository;

    @Autowired
    private FacturacionService facturacionService;

    @GetMapping
    public List<Contrato> getAllContratos() {
        return contratoRepository.findAllWithDetails();
    }

    @PutMapping("/{idContrato}/estado")
    @Transactional
    public ResponseEntity<Contrato> updateEstadoContrato(
            @PathVariable Integer idContrato,
            @RequestBody Integer nuevoEstadoId) {

        System.out.println(">>> ContratoController: Recibida solicitud para cambiar estado de Contrato ID " + idContrato
                + " a Estado ID " + nuevoEstadoId);

        Contrato contrato = contratoRepository.findAllWithDetails().stream()
                .filter(c -> c.getIdContrato().equals(idContrato))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado: " + idContrato));

        EstadoContrato nuevoEstado = estadoContratoRepository.findById(nuevoEstadoId)
                .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado: " + nuevoEstadoId));

        System.out.println(">>> ContratoController: Contrato encontrado. Estado actual: "
                + contrato.getEstadoContrato().getNombreEstado());
        contrato.setEstadoContrato(nuevoEstado);
        System.out.println(">>> ContratoController: Contrato actualizado (en memoria) con nuevo estado: "
                + contrato.getEstadoContrato().getNombreEstado());

        if (contrato.getEstadoContrato().getIdEstadoContrato() == 1) {
            System.out.println(">>> ContratoController: El nuevo estado es Activo. Llamando a FacturacionService...");

            if (contrato.getPlan() == null) {
                System.err
                        .println(">>> ContratoController: Error - El Plan no se cargó en contrato antes de facturar.");
            } else {
                facturacionService.generarFacturasParaContrato(contrato);
                System.out.println(">>> ContratoController: Llamada a FacturacionService completada.");
            }
        } else {
            System.out
                    .println(">>> ContratoController: El nuevo estado NO es Activo. No se llama a FacturacionService.");
        }

        return ResponseEntity.ok(contrato);
    }
}