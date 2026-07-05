package org.example.homestylebe.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class ProdottoRequestUserDTO {
	
	private String marca;
	private String nomeProdotto;
	private UUID categoriaId;
	private String colore;
	private String modello;
	private float prezzo;
	private String descrizione;
	private Boolean includeMontaggio;
	private byte[] immagine;

}//ProdottoRequestUserDTO
