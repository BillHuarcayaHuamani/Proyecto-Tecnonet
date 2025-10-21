package com.ProyectoTecnonet.tecnonet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoTecnonet.tecnonet.model.Solicitudes;
import com.ProyectoTecnonet.tecnonet.repository.SolicitudesRepository;

@RestController
@RequestMapping("/api/solicitudes")
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
public class SolicitudController {
    
    @Autowired
    private SolicitudesRepository solicitudesRepository;

    @GetMapping
    public List<Solicitudes> getAllSolicitudes() {
        return solicitudesRepository.findAllWithUsuario();
    }
}