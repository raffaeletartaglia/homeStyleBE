package org.example.homestylebe.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class ProdottoResponseUserDTO {
	
	private UUID id;
	private String marca;
	private String nomeProdotto;
	private String colore;
	private String modello;
	private float prezzo;
	private String descrizione;
	private Boolean includeMontaggio;
	private byte[] immagine;
	
	private CategoriaResponseDTO categoria;

}//ProdottoResponseUserDTO
