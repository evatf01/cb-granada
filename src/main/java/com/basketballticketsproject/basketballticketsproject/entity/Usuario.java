package com.basketballticketsproject.basketballticketsproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}
