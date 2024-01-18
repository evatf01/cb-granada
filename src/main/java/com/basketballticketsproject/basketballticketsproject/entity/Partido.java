package com.basketballticketsproject.basketballticketsproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.*;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @DateTimeFormat(pattern = DATE_FORMATTER)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = DATE_FORMATTER)
    private LocalDate fechaPartido;


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
