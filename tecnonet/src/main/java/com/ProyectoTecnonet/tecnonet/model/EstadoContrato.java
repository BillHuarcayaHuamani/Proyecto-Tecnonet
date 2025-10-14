package com.ProyectoTecnonet.tecnonet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "estados_contrato")
@Data
public class EstadoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_contrato")
    private Integer idEstadoContrato;

    @Column(name = "nombre_estado", nullable = false, unique = true, length = 50)
    private String nombreEstado;
}
