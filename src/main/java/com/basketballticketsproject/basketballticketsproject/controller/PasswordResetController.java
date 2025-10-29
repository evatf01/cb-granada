package com.basketballticketsproject.basketballticketsproject.controller;

import com.basketballticketsproject.basketballticketsproject.dto.ApiResponseDTO;
import com.basketballticketsproject.basketballticketsproject.dto.PasswordResetConfirmDTO;
import com.basketballticketsproject.basketballticketsproject.dto.PasswordResetRequestDTO;
import com.basketballticketsproject.basketballticketsproject.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cbgranada-api/v1/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponseDTO> requestReset(@RequestBody PasswordResetRequestDTO request) {
        boolean ok = passwordResetService.createAndSendToken(request.getEmail());
        if (ok) {
            return ResponseEntity.ok(new ApiResponseDTO(true, "Correo de recuperación enviado correctamente."));
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponseDTO(false, "El correo electrónico introducido no existe."));
        }
    }


    @PostMapping("/confirm")
    public ResponseEntity<ApiResponseDTO> confirmReset(@RequestBody PasswordResetConfirmDTO dto) {
        boolean success = passwordResetService.confirmReset(dto);
        if (success) {
            return ResponseEntity.ok(new ApiResponseDTO(true, "Password updated successfully."));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponseDTO(false, "Invalid or expired token."));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponseDTO> verifyResetToken(@RequestParam("token") String token) {
        boolean isValid = passwordResetService.verifyToken(token);
        if (isValid) {
            return ResponseEntity.ok(new ApiResponseDTO(true, "Valid token."));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponseDTO(false, "Invalid or expired token."));
        }
    }

}
