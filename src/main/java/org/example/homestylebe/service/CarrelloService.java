package org.example.homestylebe.service;

import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.entity.Carrello;
import org.example.homestylebe.entity.CarrelloProdotto;
import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.repository.CarrelloProdottoRepository;
import org.example.homestylebe.repository.CarrelloRepository;
import org.example.homestylebe.repository.ProdottoRepository;
import org.example.homestylebe.repository.UtenteRepository;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import org.example.homestylebe.exception.ValoreNonValidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.homestylebe.utils.ControlliUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarrelloService {

    private final CarrelloRepository carrelloRepo;
    private final UtenteRepository utenteRepo;
    private final ProdottoRepository prodottoRepo;

    /**
     * Trova un carrello tramite il suo ID.
     * Funziona correttamente. -- verificato 26/06/2026 --
     *
     * @param idCarello id del carrello
     * @return il carrello trovato
     */
    @Transactional(readOnly = true)
    public Carrello trovaCarrelloPerId(UUID idCarello){
        log.info("Inizio a cercare il carello con id: {}", idCarello);

        ControlliUtils.controlloIdValido(idCarello, "carrello");

        Carrello carrelloTrovato = carrelloRepo.findById(idCarello).orElseThrow(
                () -> {
                    log.error("Carrello con id: {}, non trovato", idCarello);
                    return new EntitaNonTrovataException(ErroreCodice.CARRELLO_NON_TROVATO);
                }
        );

        log.info("Trovato carrello con id: {}", carrelloTrovato.getId());
        return carrelloTrovato;
    }

    private UUID resolveUtenteId(UUID idUtente) {
        return utenteRepo.findUtenteByKeycloakId(idUtente.toString())
                .map(Utente::getId)
                .orElse(idUtente);
    }

    /**
     * Trova il carrello attivo di un utente.
     * Funziona correttamente. -- verificato 26/06/2026 --
     *
     * @param idUtente id dell'utente
     * @return il carrello attivo dell'utente
     */
    @Transactional
    public Carrello trovaCarelloPerUtente(UUID paramIdUtente){
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Inizio a cercare il carrello dell'utente con id: {}", idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");

        Carrello carrelloTrovato = carrelloRepo.findByUtente_IdAndStato(idUtente, Carrello.Stato.ATTIVO).orElseGet(
                () -> {
                    log.info("Nessun carrello attivo trovato per l'utente con id: {}, procedo alla creazione automatica", idUtente);
                    return creaCarrello(idUtente);
                }
        );

        log.info("Carrello trovato/creato con id: {}, dell'utente con id: {}", carrelloTrovato.getId(), idUtente);
        return carrelloTrovato;
    }


    /**
     * Aggiunge una lista di prodotti al carrello dell'utente.
     * Funziona correttamente. -- verificato 26/06/2026 --
     *
     * @param idUtente id dell'utente
     * @param nuoviProdotti lista dei prodotti da aggiungere
     * @return il carrello aggiornato
     */
    @Transactional
    public Carrello aggiuntaProdottiCarrello(UUID paramIdUtente, List<CarrelloProdotto> nuoviProdotti) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Aggiunta prodotti al carrello per utente id: {}", idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");

        // Validazione lista
        if (nuoviProdotti == null || nuoviProdotti.isEmpty()) {
            log.error("Lista prodotti vuota per utente id: {}", idUtente);
            throw new IllegalArgumentException("La lista dei prodotti non può essere vuota");
        }

        // Cerca carrello attivo, se non esiste lo crea implicitamente
        UUID finalIdUtente = idUtente;
        Carrello carrello = carrelloRepo
                .findByUtente_IdAndStato(idUtente, Carrello.Stato.ATTIVO)
                .orElseGet(() -> {
                    log.info("Nessun carrello attivo, ne creo uno per utente id: {}", finalIdUtente);
                    return creaCarrello(finalIdUtente); // metodo privato
                });

        for (CarrelloProdotto carrelloProdotto : nuoviProdotti) {
            ControlliUtils.controlloEsistenzaCampo(carrelloProdotto, "carrello prodotto");

            Prodotto prodotto = prodottoRepo.findById(carrelloProdotto.getProdotto().getId())
                    .orElseThrow(() -> {
                        log.error("Prodotto con id: {} non trovato", carrelloProdotto.getProdotto().getId());
                        return new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO);
                    });

            if (carrelloProdotto.getQuantita() <= 0) {
                log.error("Quantità non valida: {}", carrelloProdotto.getQuantita());
                throw new ValoreNonValidoException(
                        "La quantità deve essere maggiore di zero",
                        ErroreCodice.CARRELLO_PRODOTTO_QUANTITA_NON_VALIDA
                );
            }

            int quantitaGiaPresente = carrello.getProdotti().stream()
                    .filter(cp -> cp.getProdotto().getId().equals(prodotto.getId()))
                    .mapToInt(CarrelloProdotto::getQuantita)
                    .sum();

            int quantitaTotaleRichiesta = quantitaGiaPresente + carrelloProdotto.getQuantita();
            int giacenza = prodotto.getGiacenza() != null ? prodotto.getGiacenza() : 0;

            if (quantitaTotaleRichiesta > giacenza) {
                log.error("Quantità totale richiesta {} > giacenza disponibile {}", quantitaTotaleRichiesta, giacenza);
                throw new ValoreNonValidoException(
                        "Quantità richiesta superiore alla disponibilità in magazzino",
                        ErroreCodice.PRODOTTO_STOCK_INSUFFICIENTE
                );
            }
            
            // Assegna l'entità Prodotto gestita da Hibernate (managed entity)
            // per evitare l'eccezione "Detached entity with generated id... has an uninitialized version value 'null'"
            carrelloProdotto.setProdotto(prodotto);
        }

        aggiungiProdottiAlCarrello(carrello, nuoviProdotti);
        log.info("Prodotti aggiornati nel carrello id: {}", carrello.getId());

        Carrello carrelloSalvato = carrelloRepo.save(carrello);
        log.info("Carrello id: {} salvato con {} prodotti", carrelloSalvato.getId(), carrelloSalvato.getProdotti().size());

        return carrelloSalvato;
    }

     Carrello creaCarrello(UUID paramIdUtente) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Creazione carrello per utente id: {}", idUtente);

        // 1. Utente esiste?
        Utente utente = utenteRepo.findById(idUtente)
                .orElseThrow(() -> {
                    log.error("Utente id: {} non trovato", idUtente);
                    return new IllegalArgumentException("Utente non trovato");
                });

        // 2. Ha già un carrello attivo?
        if (carrelloRepo.existsByUtente_IdAndStato(idUtente, Carrello.Stato.ATTIVO)) {
            log.error("Utente id: {} ha già un carrello attivo", idUtente);
            throw new ValoreNonValidoException(
                    "Esiste già un carrello attivo per questo utente",
                    ErroreCodice.CARRELLO_GIA_ESISTENTE_PER_UTENTE
            );
        }

        // 3. Crea e salva
        Carrello nuovoCarrello = new Carrello();
        nuovoCarrello.setUtente(utente);
        nuovoCarrello.setStato(Carrello.Stato.ATTIVO);

        Carrello salvato = carrelloRepo.save(nuovoCarrello);
        log.info("Carrello id: {} creato per utente id: {}", salvato.getId(), idUtente);

        return salvato;
    }



    /**
     * Svuota il carrello rimuovendo tutti i prodotti al suo interno.
     * Funziona correttamente. -- verificato 26/06/2026 --
     *
     * @param idCarrello id del carrello da svuotare
     */
    @Transactional
    public void svuotaCarrello(UUID idCarrello) {
        log.info("Inizio a svuotare il carrello con id: {}", idCarrello);
        // carrello.getProdotti().clear()
        ControlliUtils.controlloIdValido(idCarrello, "carrello");

        Carrello carrelloTrovato = carrelloRepo.findById(idCarrello).orElseThrow(
                () -> {
                    log.error("Carrello non trovato con id: {}", idCarrello);
                    return new EntitaNonTrovataException(ErroreCodice.CARRELLO_NON_TROVATO);
                }
        );
        // orphanRemoval fa il resto, nessuna query manuale necessaria
        carrelloTrovato.getProdotti().clear();
    }

    /**
     * Elimina fisicamente il carrello.
     * Funziona correttamente. -- verificato 26/06/2026 --
     *
     * @param idCarello id del carrello da eliminare
     */
    @Transactional
    public void eliminaCarello(UUID idCarello){
        log.info("Inizio a eliminare il carrello con id: {}", idCarello);

        ControlliUtils.controlloIdValido(idCarello, "carrello");
        if (!carrelloRepo.existsById(idCarello)) {
            log.error("Carrello con id: {}, non trovato", idCarello);
            throw new EntitaNonTrovataException(ErroreCodice.CARRELLO_NON_TROVATO);
        }

        log.info("Carrello con id: {}, trovato", idCarello);
        carrelloRepo.deleteById(idCarello);
        log.info("Carrello con id: {}, eliminato con successo", idCarello);
    }

    private void aggiungiProdottiAlCarrello(Carrello carrello, List<CarrelloProdotto> nuoviOrdini) {
        // Costruiamo un Set degli id già presenti → lookup O(1) invece di O(n)
        Set<UUID> prodottiEsistenti = carrello.getProdotti()
                .stream()
                .map(cp -> cp.getProdotto().getId())
                .collect(Collectors.toSet());

        for (CarrelloProdotto nuovoOrdine : nuoviOrdini) {
            UUID idNuovoProdotto = nuovoOrdine.getProdotto().getId();

            if (prodottiEsistenti.contains(idNuovoProdotto)) {
                // Prodotto già presente → aggiorna solo la quantità
                carrello.getProdotti().stream()
                        .filter(cp -> cp.getProdotto().getId().equals(idNuovoProdotto))
                        .findFirst()
                        .ifPresent(cp -> cp.setQuantita(cp.getQuantita() + nuovoOrdine.getQuantita()));
            } else {
                // Prodotto nuovo → collegalo al carrello e aggiungilo
                nuovoOrdine.setCarrello(carrello);
                carrello.getProdotti().add(nuovoOrdine);
                prodottiEsistenti.add(idNuovoProdotto);
            }
        }
    }

}
