package com.ProyectoTecnonet.tecnonet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "PLANES")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer idPlan;

    @Column(name = "nombre_plan", nullable = false, unique = true, length = 100)
    private String nombrePlan;

    @Column(name = "velocidad_descarga_mbps", nullable = false)
    private Integer velocidadDescargaMbps;

    @Column(name = "velocidad_carga_mbps", nullable = false)
    private Integer velocidadCargaMbps;

    @Column(name = "wifi_incluido")
    private Integer wifiIncluido;

    @Column(name = "mes_gratis_promocion", nullable = false)
    private Boolean mesGratisPromocion;

    @Column(name = "puertos_ethernet")
    private Integer puertosEthernet;

    @Column(name = "precio_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMensual;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private List<Contrato> contratos;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }
}