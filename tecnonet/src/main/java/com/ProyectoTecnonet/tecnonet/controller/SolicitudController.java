package com.ProyectoTecnonet.tecnonet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoTecnonet.tecnonet.dto.SolicitudRequest;
import com.ProyectoTecnonet.tecnonet.model.Solicitudes;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.SolicitudesRepository;
import com.ProyectoTecnonet.tecnonet.service.SolicitudService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    
    @Autowired
    private SolicitudesRepository solicitudesRepository;

    @Autowired
    private SolicitudService solicitudService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
    public List<Solicitudes> getAllSolicitudes() {
        return solicitudesRepository.findAllWithUsuario();
    }

    @PostMapping 
    public ResponseEntity<?> crearSolicitud(@Valid @RequestBody SolicitudRequest solicitudRequest) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = null;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
             try {
                usuarioActual = (Usuario) authentication.getPrincipal();
             } catch (ClassCastException e) {
                 System.err.println("WARN: No se pudo obtener el principal de Usuario, posible configuración incorrecta.");
             }
        }

        try {
            Solicitudes solicitudGuardada = solicitudService.guardarSolicitud(solicitudRequest, usuarioActual);
            return ResponseEntity.ok(solicitudGuardada);
        } catch (Exception e) {
            System.err.println("Error al guardar solicitud: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud.");
        }
    }
}