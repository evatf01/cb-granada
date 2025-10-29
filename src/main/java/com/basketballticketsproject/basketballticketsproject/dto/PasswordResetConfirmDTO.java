package com.basketballticketsproject.basketballticketsproject.dto;

import lombok.Data;

@Data
public class PasswordResetConfirmDTO {
    private String token;
    private String newPassword;
}
