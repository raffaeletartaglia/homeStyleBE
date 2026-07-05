package org.example.homestylebe.service;

import org.springframework.stereotype.Service;
import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.repository.DettaglioOrdineRepository;
import org.example.homestylebe.repository.RecensioneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.homestylebe.entity.Recensione;
import org.example.homestylebe.entity.Utente;
import java.util.List;
import java.util.UUID;
import org.example.homestylebe.utils.ControlliUtils;
import org.example.homestylebe.exception.*;
import org.example.homestylebe.repository.UtenteRepository;
import org.example.homestylebe.entity.DettaglioOrdine;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecensioneService {

	private final RecensioneRepository recensioneRepo;
	private final UtenteRepository utenteRepo;
	private final DettaglioOrdineRepository dettaglioOrdineRepo;

	public Recensione creaRecensione(Recensione recensione) {
		log.info("Creazione nuova recensione per dettaglioOrdineId={} utenteId={}",
				recensione.getDettaglioOrdine().getId(),
				recensione.getUtente().getId());

		ControlliUtils.controlloIdValido(recensione.getDettaglioOrdine().getId(), "Dettaglio Ordine");
		ControlliUtils.controlloIdValido(recensione.getUtente().getId(), "Utente");

		DettaglioOrdine dettaglioOrdine = dettaglioOrdineRepo.findById(recensione.getDettaglioOrdine().getId())
				.orElseThrow(() -> {
					log.error("Dettaglio ordine non trovato id={}", recensione.getDettaglioOrdine().getId());
					return new EntitaNonTrovataException(ErroreCodice.DETTAGLIO_ORDINE_NON_TROVATO);
				});

		Utente utente = utenteRepo.findById(recensione.getUtente().getId())
				.orElseThrow(() -> {
					log.error("Utente non trovato id={}", recensione.getUtente().getId());
					return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
				});

		recensione.setDettaglioOrdine(dettaglioOrdine);
		recensione.setUtente(utente);

		if (recensioneRepo.existsByDettaglioOrdine_Id(recensione.getDettaglioOrdine().getId())) {
			log.error("Recensione già esistente per dettaglioOrdineId={}", recensione.getDettaglioOrdine().getId());
			throw new UnicitaException(
					"Recensione già esistente per questo ordine",
					ErroreCodice.RECENSIONE_GIA_ESISTENTE
			);
		}

		if (!controllaCommento(recensione.getCommento())) {
			log.error("Commento non valido");
			throw new ValoreNonValidoException(
					"Commento non valido",
					ErroreCodice.ERRORE_VALIDAZIONE
			);
		}

		if (!controllaValutazione(recensione.getValutazioneProdotto())) {
			log.error("Valutazione prodotto non valida: {}", recensione.getValutazioneProdotto());
			throw new ValoreNonValidoException(
					"Valutazione prodotto non valida",
					ErroreCodice.RECENSIONE_VALUTAZIONE_NON_VALIDA
			);
		}

		if (!controllaValutazione(recensione.getValutazioneConsegna())) {
			log.error("Valutazione consegna non valida: {}", recensione.getValutazioneConsegna());
			throw new ValoreNonValidoException(
					"Valutazione consegna non valida",
					ErroreCodice.RECENSIONE_VALUTAZIONE_NON_VALIDA
			);
		}

		if (!controllaValutazione(recensione.getValutazioneMontaggio())) {
			log.error("Valutazione montaggio non valida: {}", recensione.getValutazioneMontaggio());
			throw new ValoreNonValidoException(
					"Valutazione montaggio non valida",
					ErroreCodice.RECENSIONE_VALUTAZIONE_NON_VALIDA
			);
		}

		Recensione salvata = recensioneRepo.save(recensione);

		log.info("Recensione creata con successo id={}", salvata.getId());

		return salvata;

	}//creaRecensione

	public List<Recensione> getRecensioniByProdotto(UUID prodottoId) {
		log.info("Recupero recensioni per prodottoId={}", prodottoId);

		ControlliUtils.controlloIdValido(prodottoId, "Prodotto");

		List<Recensione> recensioni = recensioneRepo.findByDettaglioOrdineProdotto_Id(prodottoId);

		if (recensioni.isEmpty()) {
			log.error("Nessuna recensione trovata per il prodotto {}", prodottoId);
			throw new ValoreNonValidoException(
					"Lista recensioni vuota",
					ErroreCodice.RECENSIONE_NON_TROVATA
			);
		}

		log.info("Trovate {} recensioni per prodottoId={}", recensioni.size(), prodottoId);

		return recensioni;
	}//getRecensioniByProdotto

	public List<Recensione> getRecensioniByUtente(UUID utenteId) {
		log.info("Recupero recensioni per utenteId={}", utenteId);

		ControlliUtils.controlloIdValido(utenteId, "Utente");

		List<Recensione> recensioni = recensioneRepo.findByUtente_Id(utenteId);

		if (recensioni.isEmpty()) {
			log.info("Nessuna recensione trovata per l'utente {}", utenteId);
		} else {
			log.info("Trovate {} recensioni per utenteId={}", recensioni.size(), utenteId);
		}

		return recensioni;
	}//getRecensioniByUtente

	public List<Recensione> getRecensioneByDettaglioOrdine(UUID dettaglioOrdineId) {
		log.info("Recupero recensioni per dettaglioOrdineId={}", dettaglioOrdineId);

		ControlliUtils.controlloIdValido(dettaglioOrdineId, "Dettaglio Ordine");

		List<Recensione> recensioni = recensioneRepo.findByDettaglioOrdine_Id(dettaglioOrdineId);

		if (recensioni.isEmpty()) {
			log.info("Nessuna recensione trovata per dettaglioOrdineId={}", dettaglioOrdineId);
		} else {
			log.info("Trovate {} recensioni per dettaglioOrdineId={}", recensioni.size(), dettaglioOrdineId);
		}

		return recensioni;
	}//getRecensioneByDettaglioOrdine

	public Recensione modificaRecensione(UUID recensioneId, Recensione recensione) {
		log.info("Modifica recensione id={}", recensioneId);

		ControlliUtils.controlloIdValido(recensioneId, "Recensione");

		Recensione recensioneVecchia = recensioneRepo.findById(recensioneId)
				.orElseThrow(() -> {
					log.error("Recensione non trovata id={}", recensioneId);
					return new EntitaNonTrovataException(ErroreCodice.RECENSIONE_NON_TROVATA);
				});

		if (recensioneRepo.existsByDettaglioOrdine_Id(recensione.getDettaglioOrdine().getId())
				&& !recensione.getDettaglioOrdine().getId().equals(recensioneVecchia.getDettaglioOrdine().getId())) {
			log.error("Recensione già esistente per dettaglioOrdineId={}", recensione.getDettaglioOrdine().getId());
			throw new UnicitaException(
					"Recensione già esistente per questo ordine",
					ErroreCodice.RECENSIONE_GIA_ESISTENTE
			);
		}

		if (!controllaCommento(recensione.getCommento())) {
			log.error("Commento non valido");
			throw new ValoreNonValidoException(
					"Commento non valido",
					ErroreCodice.ERRORE_VALIDAZIONE
			);
		}

		if (!controllaValutazione(recensione.getValutazioneProdotto())) {
			log.error("Valutazione prodotto non valida: {}", recensione.getValutazioneProdotto());
			throw new ValoreNonValidoException(
					"Valutazione prodotto non valida",
					ErroreCodice.RECENSIONE_VALUTAZIONE_NON_VALIDA
			);
		}

		if (!controllaValutazione(recensione.getValutazioneConsegna())) {
			log.error("Valutazione consegna non valida: {}", recensione.getValutazioneConsegna());
			throw new ValoreNonValidoException(
					"Valutazione consegna non valida",
					ErroreCodice.RECENSIONE_VALUTAZIONE_NON_VALIDA
			);
		}

		if (!controllaValutazione(recensione.getValutazioneMontaggio())) {
			log.error("Valutazione montaggio non valida: {}", recensione.getValutazioneMontaggio());
			throw new ValoreNonValidoException(
					"Valutazione montaggio non valida",
					ErroreCodice.RECENSIONE_VALUTAZIONE_NON_VALIDA
			);
		}

		DettaglioOrdine dettaglioOrdine = dettaglioOrdineRepo.findById(recensione.getDettaglioOrdine().getId())
				.orElseThrow(() -> {
					log.error("Dettaglio ordine non trovato id={}", recensione.getDettaglioOrdine().getId());
					return new EntitaNonTrovataException(ErroreCodice.DETTAGLIO_ORDINE_NON_TROVATO);
				});

		Utente utente = utenteRepo.findById(recensione.getUtente().getId())
				.orElseThrow(() -> {
					log.error("Utente non trovato id={}", recensione.getUtente().getId());
					return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
				});

		recensioneVecchia.setDettaglioOrdine(dettaglioOrdine);
		recensioneVecchia.setCommento(recensione.getCommento());
		recensioneVecchia.setUtente(utente);
		recensioneVecchia.setValutazioneProdotto(recensione.getValutazioneProdotto());
		recensioneVecchia.setValutazioneConsegna(recensione.getValutazioneConsegna());
		recensioneVecchia.setValutazioneMontaggio(recensione.getValutazioneMontaggio());

		Recensione aggiornata = recensioneRepo.save(recensioneVecchia);

		log.info("Recensione aggiornata con successo id={}", aggiornata.getId());

		return aggiornata;

	}//modificaRecensione

	public void eliminaRecensione(UUID recensioneId) {
		log.info("Eliminazione recensione id={}", recensioneId);

		ControlliUtils.controlloIdValido(recensioneId, "RecensioneId");

		Recensione recensioneVecchia = recensioneRepo.findById(recensioneId)
				.orElseThrow(() -> {
					log.error("Recensione non trovata id={}", recensioneId);
					return new EntitaNonTrovataException(ErroreCodice.RECENSIONE_NON_TROVATA);
				});

		recensioneRepo.deleteById(recensioneId);

		log.info("Recensione eliminata con successo id={}", recensioneId);

	}//eliminaRecensione

	// ===== CONTROLLI =====

	private boolean controllaCommento(String commento) {
		log.debug("Controllo commento recensione");
		return commento != null && !commento.trim().isEmpty();
	}

	private boolean controllaValutazione(Integer valutazione) {
		log.debug("Controllo valutazione: {}", valutazione);
		return valutazione != null && valutazione >= 1 && valutazione <= 5;
	}

}//RecensioneService
