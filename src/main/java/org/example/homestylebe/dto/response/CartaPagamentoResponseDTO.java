package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CartaPagamentoResponseDTO {
	
	public enum TipoCarta {

	    VISA,
	    MASTERCARD,
	    MAESTRO

	}//TipoCarta

    private UUID id;

    private UUID utenteId;

    private String intestatario;

    private TipoCarta tipoCarta;

    private String ultime4Cifre;

    private LocalDate scadenza;

    private boolean isDefault;

}//CartaPagamentoResponseDTO
