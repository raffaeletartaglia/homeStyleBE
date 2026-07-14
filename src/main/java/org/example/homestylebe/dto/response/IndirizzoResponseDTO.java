package org.example.homestylebe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.UUID;

@Data
public class IndirizzoResponseDTO {
	
	public enum TipoIndirizzo {

	    RESIDENZA,
	    SPEDIZIONE,
	    FATTURAZIONE

	}//TipoIndirizzo

    private UUID id;

    private UUID utenteId;

    private String nazione;

    private String via;

    private String numeroCivico;

    private String citta;

    private String provincia;

    private String cap;

    private TipoIndirizzo tipo;

    @JsonProperty("isDefault")
    private boolean isDefault;

}//IndirizzoResponseDTO
