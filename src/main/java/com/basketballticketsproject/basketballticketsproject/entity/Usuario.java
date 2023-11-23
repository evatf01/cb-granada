package com.basketballticketsproject.basketballticketsproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    private String nombre;

    @Column(unique = true)
    private String email;

    private int asistencia_previa;

    private boolean entrada;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "usuarios")
    @JsonIgnore
    private Set<Sorteo> sorteos = new HashSet<>();


    @Override
    public String toString() {
        return "Usuario{" +
                "userId=" + userId +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", asistencia_previa=" + asistencia_previa +
                ", entrada=" + entrada +
                ", sorteos=" + sorteos +
                '}';
    }
}
