package com.ProyectoTecnonet.tecnonet.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "respuestas_solicitudes") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class RespuestasSolicitudes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id_respuesta")
    private Long idRespuesta;

    @ManyToOne
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitudes solicitud;

    @ManyToOne
    @JoinColumn(name = "id_operario", nullable = false)
    private Usuario operario; 

    @Column(name = "respuesta", columnDefinition = "TEXT") 
    private String respuesta;

    @Column(name = "fecha_respuesta", nullable = false)
    private LocalDateTime fechaRespuesta;
}