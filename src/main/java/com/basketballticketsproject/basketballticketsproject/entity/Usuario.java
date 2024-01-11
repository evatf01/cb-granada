package com.basketballticketsproject.basketballticketsproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID user_id;

    private String nombre;

    private String apellido;

    private String password;

    @Column(unique = true)
    private String email;

    private boolean is_admin = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "usuario", cascade = {
            CascadeType.ALL
    })
    private Set<Ticket> tickets;


    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "usuarios")
    @JsonIgnore
    private Set<Sorteo> sorteos = new HashSet<>();

}
