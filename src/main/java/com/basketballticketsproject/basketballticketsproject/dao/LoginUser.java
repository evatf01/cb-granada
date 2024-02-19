package com.basketballticketsproject.basketballticketsproject.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginUser {
    private Long user_id;

    private String nombre;

    private String apellidos;

    private String email;

    @JsonProperty("isAdmin")
    private boolean isAdmin;

}
