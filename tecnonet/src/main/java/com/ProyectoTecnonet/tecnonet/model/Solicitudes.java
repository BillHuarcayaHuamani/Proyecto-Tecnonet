package com.ProyectoTecnonet.tecnonet.model;

import java.time.LocalDateTime;
import java.util.List; 

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
@Table(name = "solicitudes")
@Data
public class Solicitudes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "asunto", nullable = false, length = 255)
    private String asunto;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "numero_remitente", length = 20)
    private String numeroRemitente;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "apellido_remitente")
    private String apellidoRemitente;

    @Column(name = "telefono_remitente")
    private String telefono_remitente;

    @OneToMany(mappedBy = "solicitud") 
    @ToString.Exclude
    private List<RespuestasSolicitudes> respuestasSolicitudes;
}