package org.example.homestylebe.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class ModalitaPagamentoResponseDTO {
	
	public enum Tipopagamento{
		FISICO,
		ONLINE
		
	}//Tipopagamento

    private UUID id;

    private Tipopagamento tipo; // FISICO / ONLINE

    private String descrizione; // es: "Paypal", "Pagamento alla consegna"

}//ModalitaPagamentoResponseDTO
