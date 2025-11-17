package com.ProyectoTecnonet.tecnonet.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.ProyectoTecnonet.tecnonet.dto.RegisterRequest;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.UsuarioRepository;
import com.ProyectoTecnonet.tecnonet.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERARIO')")
    public List<Usuario> getAllUsuarios() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = (Usuario) auth.getPrincipal();
        Integer currentId = usuarioActual.getIdUsuario();
        
        boolean esOperario = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OPERARIO"));

        if (esOperario) {
            return usuarioRepository.findByRolIdExcludingSelf(3, currentId);
        }

        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (esAdmin) {
            return usuarioRepository.findStaffUsersExcludingSelf(currentId);
        }

        return new ArrayList<>();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crearUsuarioPersonal(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            Usuario nuevoUsuario = usuarioService.registrarNuevoUsuario(registerRequest);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{idUsuario}/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> cambiarRolUsuario(
            @PathVariable Integer idUsuario,
            @RequestBody Map<String, Integer> requestBody) {
        try {
            Integer idNuevoRol = requestBody.get("idRol");
            if (idNuevoRol == null) {
                return ResponseEntity.badRequest().body("El ID del nuevo rol es obligatorio.");
            }
            
            Usuario usuarioActualizado = usuarioService.cambiarRolUsuario(idUsuario, idNuevoRol);
            return ResponseEntity.ok(usuarioActualizado);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}