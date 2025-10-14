package com.ProyectoTecnonet.tecnonet.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ProyectoTecnonet.tecnonet.model.Rol;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.RolRepository;
import com.ProyectoTecnonet.tecnonet.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void registrarNuevoUsuario(Usuario usuario) throws Exception {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new Exception("El correo electrónico ya está registrado. Por favor, utiliza otro.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPassword()));

        Rol rolCliente = rolRepository.findById(3) 
                .orElseThrow(() -> new Exception("El rol de cliente no se encuentra en la base de datos."));
        usuario.setRol(rolCliente);

        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }
}
