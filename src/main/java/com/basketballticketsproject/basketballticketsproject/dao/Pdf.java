package com.basketballticketsproject.basketballticketsproject.dao;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pdf {
    private String file;
    private String tituloPartido;
    private String fechaPartido;
    private String fechaPublicacion;
}
