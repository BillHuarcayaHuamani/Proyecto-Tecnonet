package com.ProyectoTecnonet.tecnonet.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ProyectoTecnonet.tecnonet.dto.RegisterRequest;
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
    public Usuario registrarNuevoUsuario(RegisterRequest registerRequest) throws Exception {
        if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new Exception("El correo electrónico ya está registrado. Por favor, utiliza otro.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registerRequest.getNombre());
        nuevoUsuario.setApellido(registerRequest.getApellido());
        nuevoUsuario.setEmail(registerRequest.getEmail());
        
        nuevoUsuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        int rolId;
        String rolNombre;
        String emailLowerCase = registerRequest.getEmail().toLowerCase();

        if (emailLowerCase.endsWith("@adtecnonet.com")) {
            rolId = 1;
            rolNombre = "Administrador";
        } else if (emailLowerCase.endsWith("@tecnonet.com")) {
            rolId = 2;
            rolNombre = "Operario";
        } else {
            rolId = 3;
            rolNombre = "Cliente";
        }

        Rol rolAsignado = rolRepository.findById(rolId) 
                .orElseThrow(() -> new Exception("El rol de " + rolNombre + " (ID: " + rolId + ") no se encuentra en la base de datos."));
        nuevoUsuario.setRol(rolAsignado);

        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepository.save(nuevoUsuario);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}