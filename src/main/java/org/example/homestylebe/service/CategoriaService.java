package org.example.homestylebe.service;

import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.entity.Categoria;
import org.example.homestylebe.repository.CategoriaRepository;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import org.example.homestylebe.exception.ValoreNonValidoException;
import org.example.homestylebe.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.example.homestylebe.utils.ControlliUtils;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.example.homestylebe.repository.StanzaRepository;
import org.example.homestylebe.entity.Stanza;
import java.util.ArrayList;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoriaService {

	private final CategoriaRepository categoriaRepo;
	private final StanzaRepository stanzaRepo;

	/**
	 * Recupera una categoria tramite il suo ID.
	 * Funziona correttamente. -- verificato 25/06/2026 --
	 *
	 * @param idCategoria l'UUID della categoria da recuperare
	 * @return l'entità Categoria trovata
	 */
	public Categoria prendiCategoriaPerId(UUID idCategoria) {
		log.info("Ricerca categoria con id: {}", idCategoria);
		ControlliUtils.controlloIdValido(idCategoria, "Categoria");

		Categoria categoria = categoriaRepo.findById(idCategoria).orElseThrow(
				() -> {
					log.error("Categoria con id: {} non trovata", idCategoria);
					return new EntitaNonTrovataException(ErroreCodice.CATEGORIA_NON_TROVATA);

				});

		log.info("Categoria con id: {} trovata", idCategoria);
		return categoria;
	}

	/**
	 * Ritorna la lista di tutte le categorie (paginata).
	 *
	 * @param pageable parametri di paginazione
	 * @return pagina di categorie
	 */
	public Page<Categoria> prendiTutteLeCategorie(Pageable pageable) {
		log.info("Recupero di tutte le categorie (paginata)");
		Page<Categoria> categorie = categoriaRepo.findAll(pageable);

		if (categorie.isEmpty()) {
			log.warn("Nessuna categoria trovata");
		} else {
			log.info("Trovate {} categorie nella pagina", categorie.getNumberOfElements());
		}

		return categorie;
	}

	/**
	 * Crea una nuova categoria.
	 * Funziona correttamente. -- verificato 25/06/2026 --
	 *
	 * @param categoria l'entità Categoria da salvare
	 * @return la categoria salvata
	 */
	public Categoria creaUnaCategoria(Categoria categoria) {

		if (!controllaLunghezzaNomeCategoria(categoria.getNomeCategoria())) {
			log.error("Nome categoria non valido: {}", categoria.getNomeCategoria());
			throw new ValoreNonValidoException(
					"Nome invalido, caratteri inferiori a 0 o superiori a 100 caratteri",
					ErroreCodice.CATEGORIA_DESCRIZIONE_NON_VALIDA);
		}

		if (!controlloLunghezzaDescrizione(categoria.getDescrizione())) {
			log.error("Descrizione categoria non valida");
			throw new ValoreNonValidoException(
					"Descrizione invalida, descrizione vuota o superiore a 255 caratteri",
					ErroreCodice.CATEGORIA_DESCRIZIONE_NON_VALIDA);
		}

		Categoria esistente = categoriaRepo.findByNomeCategoria(categoria.getNomeCategoria());
		if (esistente != null) {
			log.error("Categoria già esistente");
			throw new BusinessException(ErroreCodice.CATEGORIA_DUPLICATA);
		}

		List<Stanza> stanzeReali = new ArrayList<>();
		if (categoria.getStanze() != null) {
			for (Stanza stanzaTransient : categoria.getStanze()) {
				if (stanzaTransient.getTipologia() != null) {
					Stanza s = stanzaRepo.findByTipologia(stanzaTransient.getTipologia())
							.orElseThrow(() -> new EntitaNonTrovataException(ErroreCodice.STANZA_NON_TROVATA));
					stanzeReali.add(s);
				}
			}
		}
		categoria.setStanze(stanzeReali);

		Categoria salvata = categoriaRepo.save(categoria);
		log.info("Categoria creata con id: {}", salvata.getId());
		return salvata;
	}

	/**
	 * Aggiorna una categoria esistente.
	 * Funziona correttamente. -- verificato 25/06/2026 --
	 *
	 * @param idCategoria l'UUID della categoria da aggiornare
	 * @param categoria i nuovi dati della categoria
	 * @return la categoria aggiornata
	 */
	public Categoria aggiornaCategoria(UUID idCategoria, Categoria categoria) {

		log.info("Modifica categoria id: {}", idCategoria);
		ControlliUtils.controlloIdValido(idCategoria, "Categoria");

		Categoria vecchiaCategoria = categoriaRepo.findById(idCategoria).orElseThrow(
				() -> {
					log.error("Categoria con id: {} non trovata", idCategoria);
					return new EntitaNonTrovataException(ErroreCodice.CATEGORIA_NON_TROVATA);
				});

		vecchiaCategoria.setNomeCategoria(categoria.getNomeCategoria());
		vecchiaCategoria.setDescrizione(categoria.getDescrizione());

		Categoria esistente = categoriaRepo.findByNomeCategoria(categoria.getNomeCategoria());
		if (esistente != null && !esistente.getId().equals(idCategoria)) {
			log.error("Categoria già esistente");
			throw new BusinessException(ErroreCodice.CATEGORIA_DUPLICATA);
		}

		List<Stanza> stanzeReali = new ArrayList<>();
		if (categoria.getStanze() != null) {
			for (Stanza stanzaTransient : categoria.getStanze()) {
				if (stanzaTransient.getTipologia() != null) {
					Stanza s = stanzaRepo.findByTipologia(stanzaTransient.getTipologia())
							.orElseThrow(() -> new EntitaNonTrovataException(ErroreCodice.STANZA_NON_TROVATA));
					stanzeReali.add(s);
				}
			}
		}
		vecchiaCategoria.setStanze(stanzeReali);

		Categoria salvata = categoriaRepo.save(vecchiaCategoria);
		log.info("Categoria id: {} aggiornata", salvata.getId());
		return salvata;
	}

	/**
	 * Elimina una categoria tramite il suo ID.
	 * Funziona correttamente. -- verificato 25/06/2026 --
	 *
	 * @param idCategoria l'UUID della categoria da eliminare
	 */
	public void eliminaCategoria(UUID idCategoria) {

		log.info("Eliminazione categoria id: {}", idCategoria);
		ControlliUtils.controlloIdValido(idCategoria, "Categoria");

		Categoria vecchiaCategoria = categoriaRepo.findById(idCategoria).orElseThrow(
				() -> {
					log.error("Categoria con id: {} non trovata", idCategoria);
					return new EntitaNonTrovataException(ErroreCodice.CATEGORIA_NON_TROVATA);
				});

		if (vecchiaCategoria.getProdotti() != null && !vecchiaCategoria.getProdotti().isEmpty()) {
			log.error("Impossibile eliminare la categoria id: {} perché contiene prodotti", idCategoria);
			throw new ValoreNonValidoException("Impossibile eliminare la categoria perché contiene dei prodotti associati", ErroreCodice.OPERAZIONE_NON_CONSENTITA);
		}

		vecchiaCategoria.getStanze().clear();
		categoriaRepo.delete(vecchiaCategoria);
		log.info("Categoria id: {} eliminata", vecchiaCategoria.getId());
	}

	private boolean controllaLunghezzaNomeCategoria(String nomeCategoria) {
		return !nomeCategoria.isEmpty() && nomeCategoria.length() < 100;
	}

	private boolean controlloLunghezzaDescrizione(String descrizione) {
		return !descrizione.isEmpty() && descrizione.length() < 255;
	}
}
