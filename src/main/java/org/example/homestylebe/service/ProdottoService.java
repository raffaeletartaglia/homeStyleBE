package org.example.homestylebe.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.example.homestylebe.exception.ErroreCodice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.homestylebe.utils.ControlliUtils;
import org.example.homestylebe.exception.*;

import org.example.homestylebe.repository.CategoriaRepository;
import org.example.homestylebe.repository.DettaglioOrdineRepository;
import org.example.homestylebe.repository.ProdottoRepository;

import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.entity.Categoria;
import org.example.homestylebe.entity.Ordine.StatoOrdine;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProdottoService {

	private final ProdottoRepository prodottoRepo;
	private final CategoriaRepository categoriaRepo;
	private final DettaglioOrdineRepository dettaglioOrdineRepo;

	/**
	 * Crea un nuovo prodotto.
	 * Funziona correttamente. -- verificato 26/06/2026 --
	 *
	 * @param prodotto i dati del prodotto da creare
	 * @return il prodotto salvato
	 */
	public Prodotto creaProdotto(Prodotto prodotto) {
		log.info("Creazione nuovo prodotto");

		if (!controlloMarca(prodotto.getMarca())) {
			log.error("Marca non valida: {}", prodotto.getMarca());
			throw new ValoreNonValidoException("Marca non valida", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloNomeProdotto(prodotto.getNomeProdotto())) {
			log.error("Nome prodotto non valido: {}", prodotto.getNomeProdotto());
			throw new ValoreNonValidoException("Nome prodotto non valido", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		ControlliUtils.controlloIdValido(prodotto.getCategoria().getId(), "Categoria");
		log.info("Id categoria valido: {}", prodotto.getCategoria().getId());

		Categoria categoria = categoriaRepo.findById(prodotto.getCategoria().getId())
				.orElseThrow(() -> {
					log.error("Categoria non trovata id={}", prodotto.getCategoria().getId());
					return new EntitaNonTrovataException(ErroreCodice.CATEGORIA_NON_TROVATA);
				});

		prodotto.setCategoria(categoria);
		log.info("Categoria associata al prodotto id={}", categoria.getId());

		validaProdotto(prodotto);

		Prodotto prodottoSalvato = prodottoRepo.save(prodotto);
		log.info("Prodotto creato con id={}", prodottoSalvato.getId());

		return prodottoSalvato;
	}// creaProdotto

	/**
	 * Elimina un prodotto.
	 * Funziona correttamente. -- verificato 26/06/2026 --
	 *
	 * @param prodottoId id del prodotto da eliminare
	 */
	public void deleteProdotto(UUID prodottoId) {

		log.info("Eliminazione prodotto id={}", prodottoId);

		ControlliUtils.controlloIdValido(prodottoId, "Prodotto");

		Prodotto prodotto = prodottoRepo.findById(prodottoId)
				.orElseThrow(() -> {
					log.error("Prodotto non trovato per id={}", prodottoId);
					return new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO);
				});

		if (dettaglioOrdineRepo.existsByProdottoIdAndOrdineStatoOrdineIn(
				prodottoId,
				List.of(StatoOrdine.IN_ELABORAZIONE, StatoOrdine.SPEDITO))) {

			log.error("Impossibile eliminare prodotto id={} - presente in ordini attivi", prodottoId);

			throw new ValoreNonValidoException(
					"Impossibile eliminare il prodotto: presente in ordini attivi",
					ErroreCodice.PRODOTTO_NON_DISPONIBILE);
		}

		prodottoRepo.delete(prodotto);

		log.info("Prodotto eliminato con successo id={}", prodottoId);
	}// deleteProdotto

	/**
	 * Ritorna un prodotto tramite ID.
	 * Funziona correttamente. -- verificato 26/06/2026 --
	 *
	 * @param prodottoId id del prodotto
	 * @return il prodotto trovato
	 */
	public Prodotto getProdottoById(UUID prodottoId) {
		log.info("Recupero prodotto id={}", prodottoId);

		ControlliUtils.controlloIdValido(prodottoId, "Prodotto");

		Prodotto prodotto = prodottoRepo.findById(prodottoId)
				.orElseThrow(() -> {
					log.error("Prodotto non trovato per id={}", prodottoId);
					return new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO);
				});

		return prodotto;
	}// getProdottoById

	/**
	 * Ritorna la lista di tutti i prodotti (paginata).
	 *
	 * @param pageable parametri di paginazione
	 * @return pagina dei prodotti
	 */
	public Page<Prodotto> getAllProdotti(Pageable pageable) {
		log.info("Recupero lista completa prodotti (paginata)");
		return prodottoRepo.findAll(pageable);
	}// getAllProdotti

	/**
	 * Ricerca suggerimenti prodotti (nome, marca, categoria, stanza).
	 *
	 * @param query la stringa di ricerca
	 * @return lista di suggerimenti con ID e Nome
	 */
	public List<org.example.homestylebe.dto.response.ProdottoSuggerimentoDTO> ricercaSuggerimenti(String query) {
		log.info("Ricerca suggerimenti prodotti con query: '{}'", query);
		if (query == null || query.trim().isEmpty() || query.trim().equalsIgnoreCase("null")) {
			return java.util.Collections.emptyList();
		}
		String likeQuery = "%" + query.trim().toLowerCase() + "%";
		List<Prodotto> prodotti = prodottoRepo.ricercaCompletaSuggerimenti(likeQuery);
		return prodotti.stream()
				.map(p -> new org.example.homestylebe.dto.response.ProdottoSuggerimentoDTO(p.getId(), p.getNomeProdotto()))
				.collect(java.util.stream.Collectors.toList());
	}

	/**
	 * Filtra i prodotti per stanza, categoria e/o testo di ricerca (paginato).
	 *
	 * @param stanzaId    ID della stanza (opzionale)
	 * @param categoriaId ID della categoria (opzionale)
	 * @param query       Testo di ricerca libero (opzionale)
	 * @param pageable    Parametri di paginazione
	 * @return Pagina di prodotti filtrati
	 */
	public Page<Prodotto> getProdottiFiltrati(UUID stanzaId, UUID categoriaId, String query, Pageable pageable) {
		log.info("Filtro prodotti - stanzaId={}, categoriaId={}, query='{}'", stanzaId, categoriaId, query);

		if (query != null && query.trim().equalsIgnoreCase("null")) {
			query = null;
		}

		if (query != null && !query.trim().isEmpty()) {
			String likeQuery = "%" + query.trim().toLowerCase() + "%";
			return prodottoRepo.ricercaCompletaPaginata(likeQuery, pageable);
		} else if (stanzaId != null && categoriaId != null) {
			return prodottoRepo.findByStanzaIdAndCategoriaId(stanzaId, categoriaId, pageable);
		} else if (stanzaId != null) {
			return prodottoRepo.findByStanzaId(stanzaId, pageable);
		} else if (categoriaId != null) {
			return prodottoRepo.findByCategoriaId(categoriaId, pageable);
		} else {
			return prodottoRepo.findAll(pageable);
		}
	}// getProdottiFiltrati

	/**
	 * Aggiorna un prodotto esistente.
	 * Funziona correttamente. -- verificato 26/06/2026 --
	 *
	 * @param prodottoId id del prodotto da modificare
	 * @param prodotto dati del prodotto aggiornati
	 * @return il prodotto aggiornato
	 */
	public Prodotto modificaProdotto(UUID prodottoId, Prodotto prodotto) {
		log.info("Modifica prodotto id={}", prodottoId);

		ControlliUtils.controlloIdValido(prodottoId, "Prodotto");

		Prodotto prodottoEsistente = prodottoRepo.findById(prodottoId)
				.orElseThrow(() -> {
					log.error("Prodotto non trovato id={}", prodottoId);
					return new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO);
				});

		ControlliUtils.controlloIdValido(prodotto.getCategoria().getId(), "Categoria");
		log.info("Id categoria valido: {}", prodotto.getCategoria().getId());

		Categoria categoria = categoriaRepo.findById(prodotto.getCategoria().getId())
				.orElseThrow(() -> {
					log.error("Categoria non trovata id={}", prodotto.getCategoria().getId());
					return new EntitaNonTrovataException(ErroreCodice.CATEGORIA_NON_TROVATA);
				});

		validaProdotto(prodotto);

		prodottoEsistente.setMarca(prodotto.getMarca());
		prodottoEsistente.setNomeProdotto(prodotto.getNomeProdotto());
		prodottoEsistente.setCategoria(categoria);
		prodottoEsistente.setColore(prodotto.getColore());
		prodottoEsistente.setModello(prodotto.getModello());
		prodottoEsistente.setPrezzo(prodotto.getPrezzo());
		prodottoEsistente.setDescrizione(prodotto.getDescrizione());
		prodottoEsistente.setIncludeMontaggio(prodotto.getIncludeMontaggio());
		prodottoEsistente.setSogliaRiordino(prodotto.getSogliaRiordino());
		prodottoEsistente.setQuantitaRiordinoStandard(prodotto.getQuantitaRiordinoStandard());
		prodottoEsistente.setGiacenza(prodotto.getGiacenza());
		prodottoEsistente.setDataProssimaDisponibilita(prodotto.getDataProssimaDisponibilita());

		Prodotto aggiornato = prodottoRepo.save(prodottoEsistente);

		log.info("Prodotto aggiornato con successo id={}", aggiornato.getId());

		return aggiornato;
	}// modificaProdotto

	private boolean controlloMarca(String marca) {
		log.debug("Controllo marca: {}", marca);
		return marca != null && !marca.isEmpty() && marca.length() < 100;
	}

	private boolean controlloNomeProdotto(String nomeProdotto) {
		log.debug("Controllo nome prodotto: {}", nomeProdotto);
		return nomeProdotto != null && !nomeProdotto.isEmpty() && nomeProdotto.length() < 150;
	}

	private boolean controlloColore(String colore) {
		log.debug("Controllo colore: {}", colore);
		return colore != null && !colore.isEmpty() && colore.length() < 50;
	}

	private boolean controlloModello(String modello) {
		log.debug("Controllo modello: {}", modello);
		return modello != null && !modello.isEmpty() && modello.length() < 100;
	}

	private boolean controlloPrezzo(BigDecimal prezzo) {
		log.debug("Controllo prezzo: {}", prezzo);
		return prezzo != null && prezzo.compareTo(BigDecimal.ZERO) > 0;
	}

	private boolean controlloDescrizione(String descrizione) {
		log.debug("Controllo descrizione: {}", descrizione);
		return descrizione != null && !descrizione.isBlank();
	}

	private boolean controlloIncludeMontaggio(Boolean includeMontaggio) {
		log.debug("Controllo include montaggio: {}", includeMontaggio);
		return includeMontaggio != null;
	}

	private boolean controlloSogliaRiordino(Integer soglia) {
		log.debug("Controllo soglia riordino: {}", soglia);
		return soglia != null && soglia >= 0;
	}

	private boolean controlloQuantitaRiordino(Integer quantita) {
		log.debug("Controllo quantità riordino: {}", quantita);
		return quantita != null && quantita > 0;
	}

	private boolean controlloDataDisponibilita(LocalDate data) {
		log.debug("Controllo data disponibilità: {}", data);
		return data == null || !data.isBefore(LocalDate.now());
	}

	private boolean controlloGiacenza(Integer giacenza) {
		log.debug("Controllo giacenza: {}", giacenza);
		return giacenza != null && giacenza >= 0;
	}

	private void validaProdotto(Prodotto prodotto) {
		if (!controlloColore(prodotto.getColore())) {
			log.error("Colore prodotto non valido: {}", prodotto.getColore());
			throw new ValoreNonValidoException("Colore prodotto non valido", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloModello(prodotto.getModello())) {
			log.error("Modello prodotto non valido: {}", prodotto.getModello());
			throw new ValoreNonValidoException("Modello prodotto non valido", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloPrezzo(prodotto.getPrezzo())) {
			log.error("Prezzo prodotto non valido: {}", prodotto.getPrezzo());
			throw new ValoreNonValidoException("Prezzo prodotto non valido", ErroreCodice.PRODOTTO_NON_DISPONIBILE);
		}

		if (!controlloDescrizione(prodotto.getDescrizione())) {
			log.error("Descrizione prodotto non valida: {}", prodotto.getDescrizione());
			throw new ValoreNonValidoException("Descrizione prodotto non valida", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloIncludeMontaggio(prodotto.getIncludeMontaggio())) {
			log.error("Include montaggio non valido");
			throw new ValoreNonValidoException("Include montaggio non valido", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloSogliaRiordino(prodotto.getSogliaRiordino())) {
			log.error("Soglia riordino non valida: {}", prodotto.getSogliaRiordino());
			throw new ValoreNonValidoException("Soglia riordino non valida", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloQuantitaRiordino(prodotto.getQuantitaRiordinoStandard())) {
			log.error("Quantità riordino standard non valida: {}", prodotto.getQuantitaRiordinoStandard());
			throw new ValoreNonValidoException("Quantità riordino standard non valida",
					ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloDataDisponibilita(prodotto.getDataProssimaDisponibilita())) {
			log.error("Data prossima disponibilità non valida: {}", prodotto.getDataProssimaDisponibilita());
			throw new ValoreNonValidoException("Data prossima disponibilità non valida",
					ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloGiacenza(prodotto.getGiacenza())) {
			log.error("Giacenza non valida: {}", prodotto.getGiacenza());
			throw new ValoreNonValidoException("Giacenza non valida", ErroreCodice.ERRORE_VALIDAZIONE);
		}
	}

}// ProdottoService
