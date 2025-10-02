package com.ProyectoTecnonet.tecnonet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Contrato;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {
    
    @Query("SELECT c FROM Contrato c " +
           "JOIN FETCH c.usuario u " +
           "JOIN FETCH c.plan p " +
           "JOIN FETCH c.estadoContrato e " + 
           "ORDER BY c.idContrato ASC")
    List<Contrato> findAllFullData(); 
}