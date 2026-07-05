package org.example.homestylebe.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class CarrelloRequestDTO {

	public enum StatoCarrello {

		ATTIVO,
		CONVERTITO,
		ABBANDONATO

	}// StatoCarrello

	private UUID utenteId;

	private StatoCarrello stato;

}// CarrelloRequestDTO
