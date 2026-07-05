package org.example.homestylebe.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CartaPagamentoRequestDTO {

    public enum TipoCarta {

        VISA,
        MASTERCARD,
        MAESTRO

    }// TipoCarta

    private UUID utenteId;

    private String numeroCarta;

    private String intestatario;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "MM/yy")
    private java.time.YearMonth scadenza;

    private String cvv;

    private TipoCarta tipoCarta;

    private boolean isDefault;

}// CartaPagamentoRequestDTO
