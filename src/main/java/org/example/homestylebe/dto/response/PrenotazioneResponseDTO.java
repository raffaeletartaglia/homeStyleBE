package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PrenotazioneResponseDTO {

    private UUID id;

    private UUID utenteId;

    private UUID prodottoId;

    private Integer quantita;

    private LocalDateTime dataPrenotazione;

    private String stato;

    private LocalDateTime dataPrevistaDisponibilita;

}//PrenotazioneResponseDTO
