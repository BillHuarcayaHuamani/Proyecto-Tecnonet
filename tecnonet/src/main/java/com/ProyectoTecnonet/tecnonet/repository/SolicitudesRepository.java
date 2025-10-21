package com.ProyectoTecnonet.tecnonet.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Solicitudes;

@Repository
public interface SolicitudesRepository extends JpaRepository<Solicitudes, Integer> {

    @Query("SELECT DISTINCT s FROM Solicitudes s JOIN FETCH s.usuario u LEFT JOIN FETCH s.respuestasSolicitudes")
    List<Solicitudes> findAllWithUsuario();
}