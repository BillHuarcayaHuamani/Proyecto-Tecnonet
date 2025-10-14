package com.ProyectoTecnonet.tecnonet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "planes")
@Data
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer idPlan;

    @Column(name = "nombre_plan", nullable = false, length = 100)
    private String nombrePlan;

    @Column(name = "velocidad_descarga_mbps", nullable = false)
    private Integer velocidadDescargaMbps;

    @Column(name = "velocidad_carga_mbps", nullable = false)
    private Integer velocidadCargaMbps;

    @Column(name = "wifi_incluido")
    private Boolean wifiIncluido;

    @Column(name = "mes_gratis_promocion")
    private Integer mesGratisPromocion; 

    @Column(name = "puertos_ethernet")
    private Integer puertosEthernet;

    @Column(name = "precio_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMensual;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "activo")
    private Boolean activo;
}
