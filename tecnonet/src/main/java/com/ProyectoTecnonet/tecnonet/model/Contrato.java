package com.ProyectoTecnonet.tecnonet.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "contratos")
@Data
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Integer idContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_contrato", nullable = false)
    private EstadoContrato estadoContrato;

    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;

    @Column(name = "fecha_inicio_servicio", nullable = false)
    private LocalDate fechaInicioServicio;

    @Column(name = "fecha_fin_contrato", nullable = false)
    private LocalDate fechaFinContrato;

    @Column(name = "direccion_instalacion", nullable = false, length = 255)
    private String direccionInstalacion;

    @Column(name = "numero_telefono_contacto", length = 20)
    private String numeroTelefonoContacto;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "costo_instalacion", precision = 10, scale = 2)
    private BigDecimal costoInstalacion;

    @Column(name = "observaciones")
    private String observaciones;

    @ToString.Exclude
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.REMOVE) // cascade REMOVE para replicar ON DELETE CASCADE de SQL
    private List<Factura> facturas;
}
