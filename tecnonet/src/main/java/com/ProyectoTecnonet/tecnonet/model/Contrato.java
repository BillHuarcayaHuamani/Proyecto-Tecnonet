package com.ProyectoTecnonet.tecnonet.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "CONTRATOS")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Integer idContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, referencedColumnName = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false, referencedColumnName = "id_plan")
    private Plan plan;

    @Column(name = "fecha_contratacion", nullable = false, updatable = false)
    private LocalDateTime fechaContratacion;

    @Column(name = "fecha_inicio_servicio")
    private LocalDate fechaInicioServicio;

    @Column(name = "fecha_fin_contrato")
    private LocalDate fechaFinContrato;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_contrato", nullable = false)
    private EstadoContrato estadoContrato;

    @Column(name = "direccion_instalacion", nullable = false, length = 255)
    private String direccionInstalacion;

    @Column(name = "numero_telefono_contacto", nullable = false, length = 20)
    private String numeroTelefonoContacto;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "costo_instalacion", precision = 10, scale = 2)
    private BigDecimal costoInstalacion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        this.fechaContratacion = LocalDateTime.now();
        if (this.estadoContrato == null) {
            this.estadoContrato = EstadoContrato.PENDIENTE_INSTALACION;
        }
    }

    public enum EstadoContrato {
        ACTIVO,
        PENDIENTE_INSTALACION,
        CANCELADO,
        FINALIZADO,
        SUSPENDIDO
    }
}