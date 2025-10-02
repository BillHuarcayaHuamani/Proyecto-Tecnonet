package com.ProyectoTecnonet.tecnonet.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ESTADOS_CONTRATO") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class EstadoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_contrato")
    private Integer idEstadoContrato;

    @Column(name = "nombre_estado", nullable = false, length = 50, unique = true)
    private String nombreEstado;

    @OneToMany(mappedBy = "estadoContrato", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contrato> contratos;
}