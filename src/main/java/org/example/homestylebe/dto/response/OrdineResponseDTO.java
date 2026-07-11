package org.example.homestylebe.dto.response;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class OrdineResponseDTO {
	
	public enum StatoOrdine {

	    IN_ELABORAZIONE,
	    SPEDITO,
	    CONSEGNATO,
	    ANNULLATO

	}//StatoOrdine
	
	private UUID id;

    private StatoOrdine statoOrdine;

    private LocalDateTime dataOrdine;

    private LocalDate dataPrevistaConsegna;

    private BigDecimal prezzoTotale;

    private UtenteResponseDTO utente;

    private IndirizzoResponseDTO indirizzoSpedizione;

    private List<DettaglioOrdineResponseDTO> prodotti;
    
    private List<SpedizioneResponseDTO> spedizione;

}//OrdineResponseDTO
