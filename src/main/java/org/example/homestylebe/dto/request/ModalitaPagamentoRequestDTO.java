package org.example.homestylebe.dto.request;

import org.example.homestylebe.entity.ModalitaPagamento;
import lombok.Data;

@Data
public class ModalitaPagamentoRequestDTO {

    // FISICO / ONLINE
    private ModalitaPagamento.Tipo tipo;

    // es: "PayPal", "Carta di credito", "Pagamento alla consegna"
    private String descrizione;
}
