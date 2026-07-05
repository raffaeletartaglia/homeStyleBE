package org.example.homestylebe.dto.request;

import lombok.Data;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Data
public class ProdottoRequestDTO {

	private String marca;
	private String nomeProdotto;
	private UUID categoriaId;
	private String colore;
	private String modello;
	private float prezzo;
	private String descrizione;
	private Boolean includeMontaggio;
	private byte[] immagine;

	// campi gestione magazzino(SOLO ADMIN)
	private Integer sogliaRiordino;
	private Integer quantitaRiordinoStandard;
	private Integer giacenza;
	private LocalDate dataProssimaDisponibilita;

}// ProdottoRequest
