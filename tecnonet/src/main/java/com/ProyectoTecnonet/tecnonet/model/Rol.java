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
@Table(name = "ROLES")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false, length = 50, unique = true)
    private String nombreRol;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios;
}
