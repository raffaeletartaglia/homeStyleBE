package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecensioneResponseDTO {

    private UUID id;

    private UUID dettaglioOrdineId;

    private UUID utenteId;

    private Integer valutazioneProdotto;

    private Integer valutazioneConsegna;

    private Integer valutazioneMontaggio;

    private String commento;

    private LocalDateTime dataRecensione;

}//RecensioneResponseDTO
