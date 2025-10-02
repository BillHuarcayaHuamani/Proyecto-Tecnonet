package com.ProyectoTecnonet.tecnonet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "FACTURAS")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_pago", nullable = false)
    private EstadoPago estadoPago; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato", nullable = false)
    private Contrato contrato;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    
    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @PrePersist
    protected void onCreate() {
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDate.now();
        }
    }
}