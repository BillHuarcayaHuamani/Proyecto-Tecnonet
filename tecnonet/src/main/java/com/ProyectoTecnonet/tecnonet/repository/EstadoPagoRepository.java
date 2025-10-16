package com.ProyectoTecnonet.tecnonet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.EstadoPago;

@Repository
public interface EstadoPagoRepository extends JpaRepository<EstadoPago, Integer> {
}
