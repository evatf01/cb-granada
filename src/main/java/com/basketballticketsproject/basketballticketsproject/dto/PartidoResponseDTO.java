package com.basketballticketsproject.basketballticketsproject.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartidoResponseDTO {

    private String fechaPartido;

    private String equipoVisitante;

    private int entardasMultiplesMaxx = 3;

    @Override
    public String toString() {
        return
                "Fecha: " + fechaPartido +
                ", Partido: " + equipoVisitante + "\n"  ;
    }
}
