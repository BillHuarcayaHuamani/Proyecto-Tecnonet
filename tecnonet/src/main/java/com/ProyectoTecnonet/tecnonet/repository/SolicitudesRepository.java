package com.ProyectoTecnonet.tecnonet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Solicitudes;

@Repository
public interface SolicitudesRepository extends JpaRepository<Solicitudes, Integer> {
}
