package com.ProyectoTecnonet.tecnonet.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.dto.RegisterRequest;
import com.ProyectoTecnonet.tecnonet.model.Rol;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.RolRepository;
import com.ProyectoTecnonet.tecnonet.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrarNuevoUsuario(RegisterRequest registerRequest) throws Exception {
        String emailLower = registerRequest.getEmail().toLowerCase();
        
        if (emailLower.endsWith("@tecnonet.com")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean esAdmin = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

            if (!esAdmin) {
                throw new Exception("Acceso denegado: Solo los Administradores pueden crear cuentas corporativas.");
            }
        }

        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new Exception("El correo electrónico '" + registerRequest.getEmail() + "' ya está registrado.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registerRequest.getNombre());
        nuevoUsuario.setApellido(registerRequest.getApellido());
        nuevoUsuario.setEmail(registerRequest.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        int rolId;
        
        if (registerRequest.getIdRol() != null && emailLower.endsWith("@tecnonet.com")) {
            if (registerRequest.getIdRol() != 1 && registerRequest.getIdRol() != 2) {
                 throw new Exception("Rol inválido para cuenta corporativa.");
            }
            rolId = registerRequest.getIdRol();
        } else {
            rolId = 3; 
        }

        Rol rolAsignado = rolRepository.findById(rolId)
                .orElseThrow(() -> new Exception("Rol no encontrado ID: " + rolId));
        
        nuevoUsuario.setRol(rolAsignado);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepository.save(nuevoUsuario);
    }

    @Override
    @Transactional
    public Usuario cambiarRolUsuario(Integer idUsuario, Integer idNuevoRol) throws Exception {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Rol nuevoRol = rolRepository.findById(idNuevoRol)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));

        usuario.setRol(nuevoRol);
        
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}