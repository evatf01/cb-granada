package com.basketballticketsproject.basketballticketsproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = DATE_FORMATTER)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = DATE_FORMATTER)
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaPartido;


    private String nombrePartido;

    private boolean stockEntradas = true;

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
