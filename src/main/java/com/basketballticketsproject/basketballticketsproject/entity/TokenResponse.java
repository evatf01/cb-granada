package com.basketballticketsproject.basketballticketsproject.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class TokenResponse {

    public String token;
}
