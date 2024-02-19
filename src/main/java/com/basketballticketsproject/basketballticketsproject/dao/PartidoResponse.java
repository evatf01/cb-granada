package com.basketballticketsproject.basketballticketsproject.dao;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartidoResponse {

    private String fechaPartido;

    private String equipoVisitante;

    @Override
    public String toString() {
        return
                "Fecha: " + fechaPartido +
                ", Partido: " + equipoVisitante + "\n"  ;
    }
}
