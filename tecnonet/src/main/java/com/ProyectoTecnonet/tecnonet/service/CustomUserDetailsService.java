package com.ProyectoTecnonet.tecnonet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(">>> CustomUserDetailsService: Buscando usuario en la BD con email: " + username);

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> {
                    System.err.println(">>> CustomUserDetailsService: Usuario no encontrado con email: " + username);
                    return new UsernameNotFoundException("Usuario no encontrado con el email: " + username);
                });

        System.out.println(">>> CustomUserDetailsService: Usuario encontrado: " + usuario.getEmail() + ", Rol: " + usuario.getRol().getNombreRol());

        return usuario;
    }
}

