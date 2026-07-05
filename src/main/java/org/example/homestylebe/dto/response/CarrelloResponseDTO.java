package org.example.homestylebe.dto.response;

import lombok.Data;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class CarrelloResponseDTO {
	
	public enum StatoCarrello {

	    ATTIVO,
	    CONVERTITO,
	    ABBANDONATO

	}//StatoCarrello

    private UUID id;

    private UUID utenteId;

    private LocalDateTime dataCreazione;

    private StatoCarrello stato;

    private List<CarrelloProdottoResponseDTO> prodotti;

}//CarrelloResponseDTO
