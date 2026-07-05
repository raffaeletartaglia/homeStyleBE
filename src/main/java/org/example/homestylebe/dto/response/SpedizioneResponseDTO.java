package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SpedizioneResponseDTO {
	
	public enum Statospedizione{
		PREPARAZIONE,
		SPEDITO,
		IN_TRANSITO,
		CONSEGNATO,
        ANNULLATO

    }//Statospedizione

    private UUID id;

    private UUID ordineId;

    private String corriere;

    private String codiceTracking;

    private LocalDateTime dataSpedizione;

    private LocalDateTime dataConsegnaEffettiva;

    private Statospedizione statoSpedizione; // PREPARAZIONE, SPEDITO, IN_TRANSITO, CONSEGNATO

}//SpedizioneResponseDTO
