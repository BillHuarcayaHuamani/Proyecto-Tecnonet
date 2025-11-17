package com.ProyectoTecnonet.tecnonet.service;

import java.util.Optional;
import com.ProyectoTecnonet.tecnonet.dto.RegisterRequest;
import com.ProyectoTecnonet.tecnonet.model.Usuario;

public interface UsuarioService {

    Usuario registrarNuevoUsuario(RegisterRequest registerRequest) throws Exception;

    Optional<Usuario> findByEmail(String email);

    Usuario cambiarRolUsuario(Integer idUsuario, Integer idNuevoRol) throws Exception;
}