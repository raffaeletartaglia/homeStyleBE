package org.example.homestylebe.service;

import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.entity.*;
import org.example.homestylebe.repository.*;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import org.example.homestylebe.exception.OrdineGiaAnnullatoException;
import org.example.homestylebe.exception.OrdineGiaConsegnatoException;
import org.example.homestylebe.exception.ValoreNonValidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.homestylebe.utils.ControlliUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdineService {

    private final OrdineRepository ordineRepository;
    private final UtenteRepository utenteRepository;
    private final CarrelloRepository carrelloRepository;
    private final ProdottoRepository prodottoRepository;
    private final IndirizzoRepository indirizzoRepository;

    private UUID resolveUtenteId(UUID idUtente) {
        return utenteRepository.findUtenteByKeycloakId(idUtente.toString())
                .map(Utente::getId)
                .orElse(idUtente);
    }

    @Transactional(readOnly = true)
    public Ordine trovaOrdinePerId(UUID idOrdine) {
        log.info("Inizio a trovare l'ordine per id: {}", idOrdine);

        ControlliUtils.controlloIdValido(idOrdine, "ordine");

        Ordine ordineTrovato = ordineRepository.findById(idOrdine).orElseThrow(
                () -> {
                    log.error("Ordine con id: {}, non trovato", idOrdine);
                    return new EntitaNonTrovataException(ErroreCodice.ORDINE_NON_TROVATO);
                }
        );

        log.info("Ordine con id: {}, trovato", idOrdine);
        return ordineTrovato;
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Ordine> trovaOrdiniPerIdUtente(UUID paramIdUtente, org.springframework.data.domain.Pageable pageable) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Recupero ordini per l'utente con id: {}", idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");

        if (!utenteRepository.existsById(idUtente)) {
            log.error("Utente con id: {} non trovato", idUtente);
            throw new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
        }

        org.springframework.data.domain.Page<Ordine> ordiniTrovati = ordineRepository.findByUtente_Id(idUtente, pageable);
        if (ordiniTrovati.isEmpty()) {
            log.warn("Nessun ordine trovato per l'utente con id: {}", idUtente);
        } else {
            log.info("Ordini trovati con successo: {}", ordiniTrovati.getTotalElements());
        }

        return ordiniTrovati;
    }

    @Transactional(readOnly = true)
    public Page<Ordine> trovaTuttiGliOrdini(Pageable pageable) {
        log.info("Inizio a trovare tutti gli ordini (Admin)");
        Page<Ordine> ordiniTrovati = ordineRepository.findAll(pageable);
        log.info("Trovati {} ordini in totale nella pagina", ordiniTrovati.getNumberOfElements());
        return ordiniTrovati;
    }

    @Transactional(readOnly = true)
    public List<Ordine> trovaOrdiniPerIdUtenteEStatoOrdine(UUID paramIdUtente, Ordine.StatoOrdine statoOrdine) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Inizio a trovare gli ordini, per l'utente con id: {}, con stato: {}", idUtente, statoOrdine);

        ControlliUtils.controlloIdValido(idUtente, "utente");
        ControlliUtils.controlloStatoOrdineValido(statoOrdine);

        if (!utenteRepository.existsById(idUtente)) {
            log.error("Utente con id: {}, non trovato", idUtente);
            throw new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
        }

        List<Ordine> ordiniTrovati =
                ordineRepository.findByUtente_IdAndStatoOrdine(idUtente, statoOrdine);

        log.info("Trovati {} ordini per utente id: {} con stato: {}",
                ordiniTrovati.size(), idUtente, statoOrdine);
        return ordiniTrovati;
    }

    @Transactional(readOnly = true)
    public List<Ordine> trovaOrdiniPerStato(Ordine.StatoOrdine statoOrdine) {
        log.info("Inizio a cercare gli ordini con stato: {}", statoOrdine);

        ControlliUtils.controlloStatoOrdineValido(statoOrdine);

        List<Ordine> ordiniTrovati = ordineRepository.findByStatoOrdine(statoOrdine);
        log.info("Trovati {} ordini con stato: {}", ordiniTrovati.size(), statoOrdine);
        return ordiniTrovati;
    }

    @Transactional
    public Ordine creaOrdine(UUID paramIdUtente, UUID idIndirizzo) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Inizio a creare l'ordine per l'utente con id: {}, spedito all'indirizzo con id: {}",
                idUtente, idIndirizzo);

        ControlliUtils.controlloIdValido(idUtente, "utente");
        ControlliUtils.controlloIdValido(idIndirizzo, "indirizzo");

        // Utente
        log.info("Cerco l'utente con id: {}", idUtente);
        Utente utente = utenteRepository.findById(idUtente).orElseThrow(
                () -> {
                    log.error("Utente con id: {}, non trovato", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
                }
        );
        log.info("Trovato utente con id: {}", idUtente);

        // Carrello attivo
        log.info("Cerco il carrello attivo dell'utente con id: {}", idUtente);
        Carrello carrello = carrelloRepository
                .findByUtente_IdAndStato(idUtente, Carrello.Stato.ATTIVO)
                .orElseThrow(() -> {
                    log.error("Nessun carrello attivo trovato per l'utente con id: {}", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.CARRELLO_NON_TROVATO);
                });
        log.info("Trovato carrello attivo con id: {} per utente con id: {}", carrello.getId(), idUtente);

        if (carrello.getProdotti().isEmpty()) {
            log.error("Il carrello attivo con id: {} dell'utente con id: {}, è vuoto",
                    carrello.getId(), idUtente);
            throw new ValoreNonValidoException(
                    "Il carrello è vuoto", ErroreCodice.CARRELLO_VUOTO
            );
        }

        // Verifica disponibilità
        log.info("Verifico disponibilità");
        for (CarrelloProdotto cp : carrello.getProdotti()) {
            Prodotto prodotto = cp.getProdotto();

            // FIX BUG 5: usa giacenza (stock reale) invece di quantitaRiordinoStandard (logistico)
            if (cp.getQuantita() > prodotto.getGiacenza()) {
                log.error("Disponibilità insufficiente per prodotto id: {}. Disponibile: {}, richiesto: {}",
                        prodotto.getId(), prodotto.getGiacenza(), cp.getQuantita());
                throw new ValoreNonValidoException(
                        "Disponibilità insufficiente per il prodotto", ErroreCodice.PRODOTTO_STOCK_INSUFFICIENTE
                );
            }
        }
        log.info("Verifica disponibilità terminata con successo");

        // Indirizzo
        log.info("Ricerca indirizzo di spedizione id: {}", idIndirizzo);
        Indirizzo indirizzo = indirizzoRepository.findById(idIndirizzo).orElseThrow(
                () -> {
                    log.error("Indirizzo con id: {}, non trovato", idIndirizzo);
                    return new EntitaNonTrovataException(ErroreCodice.INDIRIZZO_NON_TROVATO);
                }
        );
        log.info("Indirizzo con id: {}, trovato con successo", idIndirizzo);

        // Ordine
        log.info("Creazione ordine");
        Ordine ordine = new Ordine();
        ordine.setUtente(utente);
        ordine.setStatoOrdine(Ordine.StatoOrdine.IN_ELABORAZIONE);
        ordine.setIndirizzoSpedizione(indirizzo);
        log.info("Ordine creato (non ancora salvato)");

        // Dettagli ordine + aggiornamento magazzino + calcolo totale
        log.info("Creazione dettagli ordine, aggiornamento magazzino e calcolo totale");
        List<DettaglioOrdine> dettagli = new ArrayList<>();
        BigDecimal totale = BigDecimal.ZERO;

        for (CarrelloProdotto cp : carrello.getProdotti()) {
            Prodotto prodotto = cp.getProdotto();

            DettaglioOrdine dettaglio = new DettaglioOrdine();
            dettaglio.setOrdine(ordine);
            dettaglio.setProdotto(prodotto);
            dettaglio.setQuantita(cp.getQuantita());
            dettaglio.setPrezzoUnitario(prodotto.getPrezzo());
            dettagli.add(dettaglio);

            // FIX BUG 5: decrementa giacenza (stock reale) invece di quantitaRiordinoStandard
            prodotto.setGiacenza(
                    prodotto.getGiacenza() - cp.getQuantita()
            );

            BigDecimal subtotale = prodotto.getPrezzo()
                    .multiply(BigDecimal.valueOf(cp.getQuantita()));
            totale = totale.add(subtotale);
        }

        ordine.setDettagliOrdine(dettagli);
        ordine.setPrezzoTotale(totale);
        log.info("Dettagli ordine creati con successo, totale ordine: {}", totale);

        // Salva ordine
        log.info("Salvataggio ordine");
        Ordine ordineSalvato = ordineRepository.save(ordine);
        log.info("Ordine salvato con successo, idOrdine: {}", ordineSalvato.getId());

        // Aggiorna prodotti a magazzino
        log.info("Aggiornamento prodotti e magazzino");
        prodottoRepository.saveAll(
                dettagli.stream()
                        .map(DettaglioOrdine::getProdotto)
                        .toList()
        );
        log.info("Prodotti e magazzino aggiornati con successo");

        // Aggiorna carrello
        log.info("Aggiornamento carrello con id: {}", carrello.getId());
        carrello.setStato(Carrello.Stato.CONVERTITO);
        carrello.getProdotti().clear();
        carrelloRepository.save(carrello);
        log.info("Modifiche del carrello con id: {}, avvenute con successo", carrello.getId());

        return ordineSalvato;
    }

    @Transactional
    public Ordine modificaIndirizzoSpedizione(UUID idOrdine, UUID idNuovoIndirizzo) {
        log.info("Modifica indirizzo spedizione ordine id: {}", idOrdine);

        ControlliUtils.controlloIdValido(idOrdine, "ordine");
        ControlliUtils.controlloIdValido(idNuovoIndirizzo, "indirizzo");

        log.info("Cerco l'ordine con id: {}", idOrdine);
        Ordine ordine = ordineRepository.findById(idOrdine).orElseThrow(
                () -> {
                    log.error("Ordine con id: {}, non trovato", idOrdine);
                    return new EntitaNonTrovataException(ErroreCodice.ORDINE_NON_TROVATO);
                }
        );
        log.info("Ordine con id: {}, trovato", idOrdine);

        log.info("Controllo stato ordine");
        if (ordine.getStatoOrdine() != Ordine.StatoOrdine.IN_ELABORAZIONE) {
            log.error("Non si può modificare l'indirizzo per ordine id: {}, stato: {}",
                    idOrdine, ordine.getStatoOrdine());
            throw new ValoreNonValidoException(
                    "L'indirizzo è modificabile solo per ordini IN_ELABORAZIONE", ErroreCodice.INDIRIZZO_NON_VALIDO
            );
        }
        log.info("Stato dell'ordine {} valido per la modifica indirizzo", ordine.getStatoOrdine());

        log.info("Cerco nuovo indirizzo di spedizione id: {}", idNuovoIndirizzo);
        Indirizzo nuovoIndirizzo = indirizzoRepository.findById(idNuovoIndirizzo).orElseThrow(
                () -> {
                    log.error("Indirizzo con id: {}, non trovato", idNuovoIndirizzo);
                    return new EntitaNonTrovataException(ErroreCodice.INDIRIZZO_NON_TROVATO);
                }
        );
        log.info("Nuovo indirizzo di spedizione con id: {}, trovato", idNuovoIndirizzo);

        ordine.setIndirizzoSpedizione(nuovoIndirizzo);
        log.info("Indirizzo di spedizione aggiornato, salvataggio ordine");

        Ordine ordineSalvato = ordineRepository.save(ordine);
        log.info("Ordine con id: {}, salvato con successo", ordineSalvato.getId());
        return ordineSalvato;
    }

    @Transactional
    public Ordine modificaStatoOrdine(UUID idOrdine, Ordine.StatoOrdine nuovoStato) {
        log.info("Inizio modifica stato ordine con id: {}, a nuovo stato: {}", idOrdine, nuovoStato);

        ControlliUtils.controlloIdValido(idOrdine, "ordine");
        ControlliUtils.controlloStatoOrdineValido(nuovoStato);

        log.info("Cerco ordine con id: {}", idOrdine);
        Ordine ordine = ordineRepository.findById(idOrdine).orElseThrow(
                () -> {
                    log.error("Ordine con id: {}, non trovato", idOrdine);
                    return new EntitaNonTrovataException(ErroreCodice.ORDINE_NON_TROVATO);
                }
        );
        log.info("Ordine con id: {}, trovato con successo", idOrdine);

        // se ControlliUtils.controlloTransizioneStatoValida lancia, idealmente usi ValoreNonValidoException con ORDINE_STATO_NON_VALIDO
        ControlliUtils.controlloTransizioneStatoValida(ordine.getStatoOrdine(), nuovoStato);

        log.info("Cambio stato ordine id: {} da {} a {}",
                idOrdine, ordine.getStatoOrdine(), nuovoStato);
        ordine.setStatoOrdine(nuovoStato);

        Ordine ordineSalvato = ordineRepository.save(ordine);
        log.info("Ordine con id: {}, salvato con nuovo stato: {}", ordineSalvato.getId(),
                ordineSalvato.getStatoOrdine());

        return ordineSalvato;
    }

    @Transactional
    public Ordine annullaOrdine(UUID idOrdine) {
        log.info("Inizio annullamento ordine id: {}", idOrdine);

        ControlliUtils.controlloIdValido(idOrdine, "ordine");

        log.info("Cerco l'ordine con id: {}", idOrdine);
        Ordine ordine = ordineRepository.findById(idOrdine).orElseThrow(
                () -> {
                    log.error("Ordine con id: {}, non trovato", idOrdine);
                    return new EntitaNonTrovataException(ErroreCodice.ORDINE_NON_TROVATO);
                }
        );
        log.info("Ordine con id: {}, trovato", idOrdine);

        log.info("Controllo stato ordine");
        if (ordine.getStatoOrdine() == Ordine.StatoOrdine.ANNULLATO) {
            log.error("Impossibile annullare un ordine già ANNULLATO");
            throw new OrdineGiaAnnullatoException();
        }
        if (ordine.getStatoOrdine() == Ordine.StatoOrdine.CONSEGNATO) {
            log.error("Impossibile annullare un ordine già CONSEGNATO");
            throw new OrdineGiaConsegnatoException();
        }
        log.info("Controllo stato ordine superato, procedo con il ripristino magazzino");

        // FIX BUG 5: ripristino giacenza (stock reale) invece di quantitaRiordinoStandard
        for (DettaglioOrdine det : ordine.getDettagliOrdine()) {
            Prodotto p = det.getProdotto();
            p.setGiacenza(p.getGiacenza() + det.getQuantita());
        }
        prodottoRepository.saveAll(
                ordine.getDettagliOrdine()
                        .stream()
                        .map(DettaglioOrdine::getProdotto)
                        .toList()
        );
        log.info("Modifica magazzino avvenuta con successo");

        ordine.setStatoOrdine(Ordine.StatoOrdine.ANNULLATO);

        Ordine ordineSalvato = ordineRepository.save(ordine);
        log.info("Ordine con id: {}, salvato con stato ANNULLATO", ordineSalvato.getId());
        return ordineSalvato;
    }

}
