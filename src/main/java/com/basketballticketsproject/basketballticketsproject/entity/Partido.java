package com.basketballticketsproject.basketballticketsproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
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

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL)
    private Set<Ticket> tickets;

}
