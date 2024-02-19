package com.basketballticketsproject.basketballticketsproject.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;



@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(unique = true)
    private String path;

    private boolean entregada;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "partido_id")
    private Partido partido;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", entregada=" + entregada +
                ", partido=" + partido +
                ", usuario=" + usuario +
                '}';
    }
}
