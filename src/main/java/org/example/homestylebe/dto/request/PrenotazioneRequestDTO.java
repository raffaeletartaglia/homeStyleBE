package org.example.homestylebe.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PrenotazioneRequestDTO {
	
	public enum StatoPrenotazione {
	    ATTIVA,
	    ESEGUITA,
	    ANNULLATA
	}//StatoPrenotazione

    private UUID utenteId;

    private UUID prodottoId;

    
    private Integer quantita;

    private LocalDateTime dataPrenotazione;

    private StatoPrenotazione stato; // ATTIVA, ESEGUITA, ANNULLATA

    private LocalDateTime dataPrevistaDisponibilita;

}//PrenotazioneRequestDTO
