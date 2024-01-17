package com.basketballticketsproject.basketballticketsproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OrderBy
    private String fechaPartido;


    private String nombrePartido;

    private boolean sotckEntradas = true;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL)
    private Set<Ticket> tickets;


    @Override
    public String toString() {
        return "Partido{" +
                "fechaPartido='" + fechaPartido + '\'' +
                ", nombrePartido='" + nombrePartido + '\'' +
                '}';
    }
}
