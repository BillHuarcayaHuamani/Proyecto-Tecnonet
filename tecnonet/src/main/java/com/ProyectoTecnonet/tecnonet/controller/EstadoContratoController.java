package com.ProyectoTecnonet.tecnonet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoTecnonet.tecnonet.model.EstadoContrato;
import com.ProyectoTecnonet.tecnonet.repository.EstadoContratoRepository;

@RestController
@RequestMapping("/api/estados-contrato")
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
public class EstadoContratoController {

    @Autowired
    private EstadoContratoRepository estadoContratoRepository;

    @GetMapping
    public List<EstadoContrato> getAllEstadosContrato() {
        return estadoContratoRepository.findAll();
    }
}