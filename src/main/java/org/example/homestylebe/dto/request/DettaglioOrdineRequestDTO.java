package org.example.homestylebe.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DettaglioOrdineRequestDTO {

    private UUID ordineId;

    private UUID prodottoId;

    private Integer quantita;

    private BigDecimal prezzoUnitario;

}// DettaglioOrdineRequestDTO
