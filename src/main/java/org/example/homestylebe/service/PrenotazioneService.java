package org.example.homestylebe.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.example.homestylebe.exception.ErroreCodice;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.transaction.Transactional;

import org.example.homestylebe.utils.ControlliUtils;
import org.example.homestylebe.exception.*;

import org.example.homestylebe.entity.Prenotazione;
import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.entity.Utente;

import org.example.homestylebe.repository.PrenotazioneRepository;
import org.example.homestylebe.repository.ProdottoRepository;
import org.example.homestylebe.repository.UtenteRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrenotazioneService {

	private final PrenotazioneRepository prenotazioneRepo;
	private final UtenteRepository utenteRepo;
	private final ProdottoRepository prodottoRepo;

	@Transactional
	public Prenotazione creaPrenotazione(Prenotazione prenotazione) {

		log.info("Creazione nuova prenotazione");

		if (prenotazione == null) {
			throw new ValoreNonValidoException("Prenotazione nulla", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloQuantita(prenotazione.getQuantita())) {
			log.error("Quantità non valida: {}", prenotazione.getQuantita());
			throw new ValoreNonValidoException("Quantità non valida", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloDataPrevista(prenotazione.getDataPrevistaDisponibilita())) {
			log.error("Data prevista disponibilità non valida");
			throw new ValoreNonValidoException("Data prevista disponibilità non valida", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		if (!controlloDataPrenotazione(prenotazione.getDataPrenotazione())) {
			log.error("Data prenotazione non valida");
			throw new ValoreNonValidoException("Data prenotazione non valida", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		// se non valorizzata la imposto automaticamente
		if (prenotazione.getDataPrenotazione() == null) {
			prenotazione.setDataPrenotazione(LocalDateTime.now());
		}

		if (prenotazione.getUtente() == null) {
			throw new ValoreNonValidoException("Utente non specificato", ErroreCodice.UTENTE_NON_TROVATO);
		}

		ControlliUtils.controlloIdValido(prenotazione.getUtente().getId(), "Utente");

		Utente utente = utenteRepo.findById(prenotazione.getUtente().getId())
				.orElseThrow(() -> {
					log.error("Utente non trovato: {}", prenotazione.getUtente().getId());
					return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
				});

		prenotazione.setUtente(utente);
		log.info("Utente associato alla prenotazione: {}", utente.getId());

		if (prenotazione.getProdotto() == null) {
			throw new ValoreNonValidoException("Prodotto non specificato", ErroreCodice.PRODOTTO_NON_TROVATO);
		}

		ControlliUtils.controlloIdValido(prenotazione.getProdotto().getId(), "Prodotto");

		Prodotto prodotto = prodottoRepo.findById(prenotazione.getProdotto().getId())
				.orElseThrow(() -> {
					log.error("Prodotto non trovato: {}", prenotazione.getProdotto().getId());
					return new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO);
				});

		prenotazione.setProdotto(prodotto);
		log.info("Prodotto associato alla prenotazione: {}", prodotto.getId());

        // Controllo se il prodotto è effettivamente esaurito
        if (prodotto.getGiacenza() != null && prodotto.getGiacenza() > 0) {
            log.error("Impossibile prenotare il prodotto {}: giacenza disponibile ({})", prodotto.getId(), prodotto.getGiacenza());
            throw new org.example.homestylebe.exception.ProdottoNonEsauritoException("Il prodotto può essere prenotato solo se esaurito", ErroreCodice.ERRORE_VALIDAZIONE);
        }

		// stato iniziale sempre ATTIVA
		prenotazione.setStato(Prenotazione.Stato.ATTIVA);

		Prenotazione prenotazioneSalvata = prenotazioneRepo.save(prenotazione);

		log.info("Prenotazione creata con id: {}", prenotazioneSalvata.getId());

		return prenotazioneSalvata;

	}//creaPrenotazione

	public List<Prenotazione> getPrenotazioniByUtente(UUID utenteId) {

		log.info("Recupero prenotazioni per utenteId={}", utenteId);

		ControlliUtils.controlloIdValido(utenteId, "Utente");

		Utente utente = utenteRepo.findById(utenteId)
				.orElseThrow(() -> {
					log.error("Utente non trovato: {}", utenteId);
					return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
				});

		List<Prenotazione> prenotazioni = prenotazioneRepo.findByUtente(utente);

		log.info("Trovate {} prenotazioni per utente {}", prenotazioni.size(), utenteId);

		return prenotazioni;

	}//getPrenotazioniByUtente

	public List<Prenotazione> getPrenotazioniByProdotto(UUID prodottoId) {

		log.info("Recupero prenotazioni per prodottoId={}", prodottoId);

		ControlliUtils.controlloIdValido(prodottoId, "Prodotto");

		Prodotto prodotto = prodottoRepo.findById(prodottoId)
				.orElseThrow(() -> {
					log.error("Prodotto non trovato: {}", prodottoId);
					return new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO);
				});

		List<Prenotazione> prenotazioni = prenotazioneRepo.findByProdotto(prodotto);

		log.info("Trovate {} prenotazioni per prodotto {}", prenotazioni.size(), prodottoId);

		return prenotazioni;

	}//getPrenotazioniByProdotto

	public List<Prenotazione> getPrenotazioniAttiveByProdotto(UUID prodottoId) {

		log.info("Recupero prenotazioni ATTIVE ordinate per data per prodottoId={}", prodottoId);

		ControlliUtils.controlloIdValido(prodottoId, "Prodotto");

		Prodotto prodotto = prodottoRepo.findById(prodottoId)
				.orElseThrow(() -> {
					log.error("Prodotto non trovato: {}", prodottoId);
					return new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO);
				});
		List<Prenotazione> prenotazioni =
				prenotazioneRepo.findByProdottoAndStatoOrderByDataPrenotazioneAsc(
						prodotto,
						Prenotazione.Stato.ATTIVA
				);

		log.info("Trovate {} prenotazioni ATTIVE ordinate per data per prodotto {}", prenotazioni.size(), prodottoId);

		return prenotazioni;

	}//getPrenotazioniAttiveByProdotto

	public List<Prenotazione> getPrenotazioniByStato(Prenotazione.Stato stato) {

		log.info("Recupero prenotazioni per stato={}", stato);

		if (!controlloStato(stato)) {
			log.error("Stato non valido: {}", stato);
			throw new ValoreNonValidoException("Stato non valido", ErroreCodice.PRENOTAZIONE_STATO_NON_VALIDO);
		}

		List<Prenotazione> prenotazioni = prenotazioneRepo.findByStato(stato);

		log.info("Trovate {} prenotazioni con stato {}", prenotazioni.size(), stato);

		return prenotazioni;

	}//getPrenotazioniByStato

	public List<Prenotazione> getPrenotazioniInArrivo(LocalDate data) {

		log.info("Recupero prenotazioni con disponibilità prevista prima di {}", data);

		if (data == null) {
			throw new ValoreNonValidoException("Data non valida", ErroreCodice.ERRORE_VALIDAZIONE);
		}

		List<Prenotazione> prenotazioni = prenotazioneRepo.findByDataPrevistaDisponibilitaBefore(data);

		log.info("Trovate {} prenotazioni in arrivo", prenotazioni.size());

		return prenotazioni;

	}//getPrenotazioniInArrivo

	@Transactional
	public Prenotazione annullaPrenotazione(UUID prenotazioneId) {

		log.info("Annullamento prenotazione id={}", prenotazioneId);

		ControlliUtils.controlloIdValido(prenotazioneId, "Prenotazione");

		Prenotazione prenotazione = prenotazioneRepo.findById(prenotazioneId)
				.orElseThrow(() -> {
					log.error("Prenotazione non trovata: {}", prenotazioneId);
					return new EntitaNonTrovataException(ErroreCodice.PRENOTAZIONE_NON_TROVATA);
				});

		if (prenotazione.getStato() != Prenotazione.Stato.ATTIVA) {
			log.error("Solo prenotazioni ATTIVE possono essere annullate");
			throw new OperazioneNonConsentitaException(
					"Solo prenotazioni ATTIVE possono essere annullate",
					ErroreCodice.PRENOTAZIONE_STATO_NON_VALIDO
			);
		}

		prenotazione.setStato(Prenotazione.Stato.ANNULLATA);

		Prenotazione aggiornata = prenotazioneRepo.save(prenotazione);

		log.info("Prenotazione annullata: {}", prenotazioneId);

		return aggiornata;

	}//annullaPrenotazione

	@Transactional
	public Prenotazione eseguiPrenotazione(UUID prenotazioneId) {

		log.info("Esecuzione prenotazione id={}", prenotazioneId);

		ControlliUtils.controlloIdValido(prenotazioneId, "Prenotazione");

		Prenotazione prenotazione = prenotazioneRepo.findById(prenotazioneId)
				.orElseThrow(() -> {
					log.error("Prenotazione non trovata: {}", prenotazioneId);
					return new EntitaNonTrovataException(ErroreCodice.PRENOTAZIONE_NON_TROVATA);
				});

		if (prenotazione.getStato() != Prenotazione.Stato.ATTIVA) {
			log.error("Prenotazione non eseguibile. Stato attuale: {}", prenotazione.getStato());
			throw new OperazioneNonConsentitaException(
					"Solo le prenotazioni ATTIVE possono essere eseguite",
					ErroreCodice.PRENOTAZIONE_STATO_NON_VALIDO
			);
		}

		prenotazione.setStato(Prenotazione.Stato.ESEGUITA);

		Prenotazione aggiornata = prenotazioneRepo.save(prenotazione);

		log.info("Prenotazione eseguita: {}", prenotazioneId);

		return aggiornata;

	}//eseguiPrenotazione

	// ===== CONTROLLI =====

	private boolean controlloQuantita(Integer quantita) {
		log.debug("Controllo quantità prenotazione: {}", quantita);
		return quantita != null && quantita > 0;
	}//controlloQuantita

	private boolean controlloStato(Prenotazione.Stato stato) {
		log.debug("Controllo stato prenotazione: {}", stato);
		return stato != null;
	}//controlloStato

	private boolean controlloDataPrevista(LocalDate data) {
		log.debug("Controllo data prevista disponibilità: {}", data);
		if (data == null) return true;
		return !data.isBefore(LocalDate.now());
	}//controlloDataPrevista

	private boolean controlloDataPrenotazione(LocalDateTime dataPrenotazione) {
		log.debug("Controllo data prenotazione: {}", dataPrenotazione);
		if (dataPrenotazione == null) {
			return true;
		}
		return !dataPrenotazione.isAfter(LocalDateTime.now());
	}//controlloDataPrenotazione

}//PrenotazioneService
