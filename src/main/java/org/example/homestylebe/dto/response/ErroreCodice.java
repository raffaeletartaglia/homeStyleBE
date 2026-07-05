package org.example.homestylebe.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErroreCodice {

    private String codiceErrore;   // es. "CATEGORIA_NON_TROVATA"
    private String messaggio;      // messaggio per l'utente
    private LocalDateTime timestamp;
    private String path;           // URL della richiesta
}
