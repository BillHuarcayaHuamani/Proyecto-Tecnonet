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

import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.repository.ContratoRepository;
import com.ProyectoTecnonet.tecnonet.service.ContratoService;

@RestController
@RequestMapping("/api/contratos")
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
public class ContratoController {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private ContratoService contratoService;

    @GetMapping
    public List<Contrato> getAllContratos() {
        return contratoRepository.findAllWithDetails();
    }

    @PutMapping("/{idContrato}/estado")
    public ResponseEntity<Contrato> updateEstadoContrato(
            @PathVariable Integer idContrato,
            @RequestBody Integer nuevoEstadoId) {
        
        Contrato contratoActualizado = contratoService.actualizarEstadoContrato(idContrato, nuevoEstadoId);
        return ResponseEntity.ok(contratoActualizado);
    }
}