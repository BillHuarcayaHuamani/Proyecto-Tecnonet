package com.ProyectoTecnonet.tecnonet.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoTecnonet.tecnonet.model.Factura;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.FacturaRepository;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;

    @GetMapping("/todas")
    @PreAuthorize("hasRole('OPERARIO')")
    public List<Factura> getAllFacturas() {
        return facturaRepository.findAllWithDetails();
    }

    @GetMapping("/mis-facturas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Factura>> getMisFacturas() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = (Usuario) authentication.getPrincipal();
        
        List<Factura> todasLasFacturas = facturaRepository.findAllWithDetails();
        List<Factura> misFacturas = todasLasFacturas.stream()
            .filter(factura -> factura.getContrato().getUsuario().getIdUsuario().equals(usuarioActual.getIdUsuario()))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(misFacturas);
    }
}