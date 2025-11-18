package com.ProyectoTecnonet.tecnonet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNombreAndApellido(String nombre, String apellido);

    long countByRolNombreRol(String nombreRol);

    List<Usuario> findTop5ByRolNombreRolOrderByFechaRegistroDesc(@Param("nombreRol") String nombreRol);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol")
    List<Usuario> findAllWithRol();

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.rol.idRol = :idRol AND u.idUsuario <> :currentId")
    List<Usuario> findByRolIdExcludingSelf(@Param("idRol") Integer idRol, @Param("currentId") Integer currentId);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.rol.idRol IN (1, 2) AND u.idUsuario <> :currentId")
    List<Usuario> findStaffUsersExcludingSelf(@Param("currentId") Integer currentId);
}