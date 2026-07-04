package org.example.homestylebe.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UtenteRequestDTO {

    @NotBlank(message = "Il nome non può essere vuoto")
    private String nome;

    @NotBlank(message = "Il cognome non può essere vuoto")
    private String cognome;

    @NotBlank(message = "Il numero di telefono non può essere vuoto")
    @Max(value = 10, message = "Il numero di telefono non può superare i 10 caratteri")
    @Min(value = 10, message = "Il numero di telefono non può essere inferiore ai 10 caratteri")
    private String numeroTelefono;

}// UtenteRequestDTO
