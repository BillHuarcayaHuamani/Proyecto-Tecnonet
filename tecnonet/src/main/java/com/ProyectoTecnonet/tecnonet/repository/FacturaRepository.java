package com.ProyectoTecnonet.tecnonet.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Import Param
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    boolean existsByContratoAndFechaEmisionBetween(Contrato contrato, LocalDate fechaInicio, LocalDate fechaFin);

    long countByContrato(Contrato contrato);

    @Query("SELECT f FROM Factura f JOIN FETCH f.estadoPago ep JOIN FETCH f.contrato c JOIN FETCH c.usuario u JOIN FETCH c.plan p JOIN FETCH c.estadoContrato ec")
    List<Factura> findAllWithDetails();

    @Query("SELECT f FROM Factura f JOIN FETCH f.estadoPago ep " +
            "WHERE ep.idEstadoPago = 1 AND f.fechaVencimiento < :fechaActual")
    List<Factura> findFacturasPendientesVencidas(@Param("fechaActual") LocalDate fechaActual);
}