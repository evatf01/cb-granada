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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    private String nombre;

    private String apellidos;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(name = "partidosAsistidos")
    private int partidosAsistidos = 0;

    private boolean is_admin = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "usuario", cascade = {
            CascadeType.ALL
    })
    private Set<Ticket> tickets;

    @Override
    public String toString() {
        return "Usuario {" +
                "id = " + user_id + ", nombre = " + nombre + ", email = " + email + ", is_admin = " + is_admin;
    }
}
