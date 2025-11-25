package com.ProyectoTecnonet.tecnonet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoTecnonet.tecnonet.dto.ContratoRequest;
import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.ContratoRepository;
import com.ProyectoTecnonet.tecnonet.service.ContratoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private ContratoService contratoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
    public List<Contrato> getAllContratos() {
        return contratoRepository.findAllWithDetails();
    }

    @PutMapping("/{idContrato}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
    public ResponseEntity<Contrato> updateEstadoContrato(
            @PathVariable Integer idContrato,
            @RequestBody Integer nuevoEstadoId) {
        
        Contrato contratoActualizado = contratoService.actualizarEstadoContrato(idContrato, nuevoEstadoId);
        return ResponseEntity.ok(contratoActualizado);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')") 
    public ResponseEntity<Contrato> crearContrato(@Valid @RequestBody ContratoRequest contratoRequest) {
        Contrato nuevoContrato = contratoService.crearContrato(contratoRequest);
        return ResponseEntity.ok(nuevoContrato);
    }

    @GetMapping("/mi-ultimo")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Contrato> getMiUltimoContrato() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = (Usuario) authentication.getPrincipal();

        return contratoRepository.findFirstByUsuarioIdUsuarioOrderByIdContratoDesc(usuarioActual.getIdUsuario())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/{idContrato}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<Contrato> actualizarDatosContrato(
            @PathVariable Integer idContrato,
            @Valid @RequestBody ContratoRequest contratoRequest) {
        Contrato contratoActualizado = contratoService.actualizarDatosContrato(idContrato, contratoRequest);
        return ResponseEntity.ok(contratoActualizado);
    }
}