package org.example.homestylebe.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DettaglioOrdineResponseDTO {
	
    private Integer quantita;
    private BigDecimal prezzoUnitario;
    private ProdottoResponseDTO prodotto;
    
    
}//DettaglioOrdineResponseDTO
