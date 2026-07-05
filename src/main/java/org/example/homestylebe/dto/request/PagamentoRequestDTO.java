package org.example.homestylebe.dto.request;

import lombok.Data;

import java.util.UUID;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagamentoRequestDTO {

    private UUID ordineId;

    private UUID modalitaPagamentoId;

    private UUID cartaPagamentoId; // opzionale

    private Boolean pagamentoEffettuato;

    private Integer numeroRate;

    private Integer rataCorrente;

    private BigDecimal importo;

    private BigDecimal importoRata;

    private LocalDateTime dataPagamento;

    private String fattura;

}// PagamentoRequestDTO
