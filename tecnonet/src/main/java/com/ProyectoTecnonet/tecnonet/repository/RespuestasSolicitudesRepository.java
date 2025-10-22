package com.ProyectoTecnonet.tecnonet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.RespuestasSolicitudes;

@Repository
public interface RespuestasSolicitudesRepository extends JpaRepository<RespuestasSolicitudes, Integer> {
}