package org.example.homestylebe.dto.request;

import lombok.Data;

import java.util.UUID;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagamentoRequestDTO {

    private UUID ordineId;

    private Boolean pagamentoOnline;

    private UUID cartaPagamentoId; // opzionale

    private Boolean pagamentoEffettuato;

    private BigDecimal importo;

    private LocalDateTime dataPagamento;

    private String fattura;

}// PagamentoRequestDTO
