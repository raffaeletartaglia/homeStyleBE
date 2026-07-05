package org.example.homestylebe.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class OrdineRequestDTO {

	public enum StatoOrdine {

		IN_ELABORAZIONE,
		SPEDITO,
		CONSEGNATO,
		ANNULLATO

	}// StatoOrdine

	private UUID utenteId;

	private UUID indirizzoSpedizioneId;

	private StatoOrdine statoOrdine;

	private LocalDate dataPrevistaConsegna;

}// OrdineRequestDTO
