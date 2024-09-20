package com.basketballticketsproject.basketballticketsproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginUserDTO {
    private Long id;

    private String nombre;

    private String apellidos;

    private String email;

    private int partidosAsistidos;

    private String token;

    @JsonProperty("isAdmin")
    private boolean isAdmin;


}
