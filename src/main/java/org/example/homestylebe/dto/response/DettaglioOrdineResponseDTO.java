package org.example.homestylebe.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DettaglioOrdineResponseDTO {
	
    private UUID id;
    private Integer quantita;
    private BigDecimal prezzoUnitario;
    private ProdottoResponseDTO prodotto;
    private UUID resoId;
    private String statoReso;
    
}//DettaglioOrdineResponseDTO
