package com.ProyectoTecnonet.tecnonet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "CONTACTOS")
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contacto")
    private Integer idContacto;


    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @Column(name = "nombre_remitente", nullable = false, length = 100)
    private String nombreRemitente;

    @Column(name = "apellido_remitente", length = 100)
    private String apellidoRemitente;

    @Column(name = "email_remitente", nullable = false, length = 255)
    private String emailRemitente;

    @Column(name = "telefono_remitente", length = 20)
    private String telefonoRemitente;

    @Column(name = "asunto", nullable = false, length = 255)
    private String asunto;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "asesor_genero", nullable = false)
    private AsesorGenero asesorGenero;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @PrePersist
    protected void onCreate() {
        this.fechaEnvio = LocalDateTime.now();
    }

    public enum AsesorGenero {
        MASCULINO,
        FEMENINO,
        OTRO
    }
}