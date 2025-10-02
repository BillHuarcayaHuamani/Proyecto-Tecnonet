package com.ProyectoTecnonet.tecnonet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}
