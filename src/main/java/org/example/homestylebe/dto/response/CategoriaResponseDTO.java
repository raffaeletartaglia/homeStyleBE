package org.example.homestylebe.dto.response;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CategoriaResponseDTO {

	private UUID id;
	private String nomeCategoria;
	private String descrizione;

	private List<StanzaResponseDTO> stanze;

}// CategoriaResponse
