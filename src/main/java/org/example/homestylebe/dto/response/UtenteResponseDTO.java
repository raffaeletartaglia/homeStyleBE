package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UtenteResponseDTO {

    private UUID id;

    private String nome;

    private String cognome;

    private String email;

    private String numeroTelefono;

    private String ruolo;

    private LocalDateTime dataCreazione;
    
 
    
   
}//UtenteResponseDTO
