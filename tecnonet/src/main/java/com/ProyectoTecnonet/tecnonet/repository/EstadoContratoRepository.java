package com.ProyectoTecnonet.tecnonet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.EstadoContrato;

@Repository
public interface EstadoContratoRepository extends JpaRepository<EstadoContrato, Integer> {
    
}