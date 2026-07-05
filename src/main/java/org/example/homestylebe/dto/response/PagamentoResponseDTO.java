package org.example.homestylebe.dto.response;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagamentoResponseDTO {

    private UUID id;

    private UUID ordineId;

    private ModalitaPagamentoResponseDTO modalitaPagamento;

    private Boolean pagamentoEffettuato;

    private Integer numeroRate;

    private Integer rataCorrente;

    private BigDecimal importo;

    private BigDecimal importoRata;

    private LocalDateTime dataPagamento;

    private String fattura;

    // opzionale: ultime 4 cifre carta se pagamento online
    private String ultime4CifreCarta;

}//PagamentoResponseDTO
