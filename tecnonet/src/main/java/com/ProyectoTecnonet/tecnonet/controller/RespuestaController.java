package com.ProyectoTecnonet.tecnonet.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.dto.RespuestaSolicitudDTO;
import com.ProyectoTecnonet.tecnonet.model.RespuestasSolicitudes;
import com.ProyectoTecnonet.tecnonet.model.Solicitudes;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.RespuestasSolicitudesRepository;
import com.ProyectoTecnonet.tecnonet.repository.SolicitudesRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/solicitudes/{solicitudId}/respuestas")
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
public class RespuestaController {

    @Autowired
    private RespuestasSolicitudesRepository respuestasRepository;

    @Autowired
    private SolicitudesRepository solicitudesRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> guardarRespuesta(
            @PathVariable Integer solicitudId,
            @Valid @RequestBody RespuestaSolicitudDTO respuestaDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario operarioActual = (Usuario) authentication.getPrincipal();

        Solicitudes solicitud = solicitudesRepository.findById(solicitudId)
            .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + solicitudId));

        if (solicitud.getRespuestasSolicitudes() != null && !solicitud.getRespuestasSolicitudes().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Esta solicitud ya ha sido respondida.");
        }

        RespuestasSolicitudes nuevaRespuesta = new RespuestasSolicitudes();
        nuevaRespuesta.setSolicitud(solicitud);
        nuevaRespuesta.setOperario(operarioActual);
        nuevaRespuesta.setRespuesta(respuestaDTO.getRespuesta());
        nuevaRespuesta.setFechaRespuesta(LocalDateTime.now());

        RespuestasSolicitudes respuestaGuardada = respuestasRepository.save(nuevaRespuesta);

        return ResponseEntity.ok(respuestaGuardada);
    }
}