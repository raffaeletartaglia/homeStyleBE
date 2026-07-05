package org.example.homestylebe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequestDTO {

    @NotBlank(message = "Access token non presente")
    private String accessToken; // il JWT completo

    @NotBlank(message = "Refresh token non presente")
    private String refreshToken; // il refresh JWT completo
}
