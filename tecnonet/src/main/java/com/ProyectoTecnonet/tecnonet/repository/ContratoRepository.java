package com.ProyectoTecnonet.tecnonet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Contrato;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {
}
