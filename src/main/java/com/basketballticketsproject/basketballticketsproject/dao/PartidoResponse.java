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
}
