package com.ProyectoTecnonet.tecnonet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Contrato;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    long countByEstadoContratoNombreEstado(String nombreEstado);

    @Query("SELECT c FROM Contrato c " +
            "JOIN FETCH c.usuario u " +
            "JOIN FETCH c.plan p " +
            "JOIN FETCH c.estadoContrato e " +
            "WHERE e.nombreEstado = :nombreEstado " +
            "ORDER BY c.fechaContratacion DESC")
    List<Contrato> findTop5ByEstadoContratoNombreEstadoOrderByFechaContratacionDesc(
            @Param("nombreEstado") String nombreEstado);

    @Query("SELECT c FROM Contrato c JOIN FETCH c.usuario u JOIN FETCH c.plan p JOIN FETCH c.estadoContrato e")
    List<Contrato> findAllWithDetails();

    @Query("SELECT c FROM Contrato c JOIN FETCH c.plan p JOIN c.estadoContrato ec " +
            "WHERE ec.idEstadoContrato = :idEstadoActivo AND FUNCTION('DAY', c.fechaInicioServicio) = :diaDelMes")
    List<Contrato> findContratosActivosParaFacturar(@Param("idEstadoActivo") Integer idEstadoActivo,
            @Param("diaDelMes") int diaDelMes);

    @Query("SELECT c FROM Contrato c JOIN FETCH c.usuario u JOIN FETCH c.plan p JOIN FETCH c.estadoContrato e WHERE c.idContrato = :id")
    Optional<Contrato> findByIdWithDetails(@Param("id") Integer id);
}