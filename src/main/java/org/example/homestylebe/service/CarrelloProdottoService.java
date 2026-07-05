package org.example.homestylebe.service;

import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.entity.Carrello;
import org.example.homestylebe.entity.CarrelloProdotto;
import org.example.homestylebe.repository.CarrelloProdottoRepository;
import org.example.homestylebe.repository.CarrelloRepository;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import org.example.homestylebe.exception.StockInsufficienteException;
import org.example.homestylebe.exception.ValoreNonValidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.homestylebe.utils.ControlliUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarrelloProdottoService {

    private final CarrelloProdottoRepository carrelloProdottoRepo;
    private final CarrelloRepository carrelloRepo;

    @Transactional(readOnly = true)
    public CarrelloProdotto trovaCarrelloProdottoPerId(UUID idCarrelloProdotto) {
        log.info("Inizio a cercare il carrello prodotto con id: {}", idCarrelloProdotto);

        ControlliUtils.controlloIdValido(idCarrelloProdotto, "carrello prodotto");

        CarrelloProdotto carrelloProdottoTrovato = carrelloProdottoRepo.findById(idCarrelloProdotto).orElseThrow(
                () -> {
                    log.error("Carrello prodotto con id: {} non trovato", idCarrelloProdotto);
                    return new EntitaNonTrovataException(ErroreCodice.CARRELLO_PRODOTTO_NON_TROVATO);
                }
        );
        log.info("Carrello prodotto con id: {} trovato", idCarrelloProdotto);

        return carrelloProdottoTrovato;
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<CarrelloProdotto> trovaProdottiDelCarrello(UUID idCarrello, org.springframework.data.domain.Pageable pageable) {
        log.info("Inizio a cercare i prodotti del carrello con id: {}", idCarrello);

        ControlliUtils.controlloIdValido(idCarrello, "carrello");

        if (!carrelloRepo.existsById(idCarrello)) {
            log.error("Carrello con id: {} non trovato", idCarrello);
            throw new EntitaNonTrovataException(ErroreCodice.CARRELLO_NON_TROVATO);
        }

        org.springframework.data.domain.Page<CarrelloProdotto> carrelloProdottoTrovato = carrelloProdottoRepo.findByCarrello_Id(idCarrello, pageable);
        if (carrelloProdottoTrovato.isEmpty()) {
            log.warn("Carrello prodotto di carrello con id: {} vuoto", idCarrello);
        } else {
            log.info("Trovati {} prodotti per il carrello con id: {}", carrelloProdottoTrovato.getTotalElements(), idCarrello);
        }

        return carrelloProdottoTrovato;
    }

    @Transactional
    public CarrelloProdotto aggiornaQuantita(UUID idCarrelloProdotto, Integer nuovaQuantita) {
        log.info("Inizio a modificare la quantità del carrello prodotto con id: {}", idCarrelloProdotto);
        ControlliUtils.controlloIdValido(idCarrelloProdotto, "carrello prodotto");

        CarrelloProdotto carrelloProdotto = carrelloProdottoRepo.findById(idCarrelloProdotto).orElseThrow(
                () -> {
                    log.error("Carrello prodotto con id: {} non trovato", idCarrelloProdotto);
                    return new EntitaNonTrovataException(ErroreCodice.CARRELLO_PRODOTTO_NON_TROVATO);
                }
        );
        log.info("Carrello prodotto con id: {} trovato", idCarrelloProdotto);

        if (nuovaQuantita == null || nuovaQuantita < 0) {
            log.error("Quantità inserita non valida: {}", nuovaQuantita);
            throw new ValoreNonValidoException(
                    "Quantità inserita non valida", ErroreCodice.CARRELLO_PRODOTTO_QUANTITA_NON_VALIDA
            );
        }

        if (nuovaQuantita == 0) {
            log.info("Quantità = 0, rimuovo prodotto dal carrello");
            carrelloProdotto.getCarrello().getProdotti().remove(carrelloProdotto);
            carrelloProdottoRepo.delete(carrelloProdotto);
            return carrelloProdotto; // o null, in base a come vuoi gestire la risposta
        }

        // FIX PROB 6: usa giacenza (stock reale) invece di quantitaRiordinoStandard (logistico)
        if (nuovaQuantita > carrelloProdotto.getProdotto().getGiacenza()) {
            log.error("Quantità richiesta: {}, superiore alla giacenza disponibile: {}",
                    nuovaQuantita, carrelloProdotto.getProdotto().getGiacenza());
            throw new StockInsufficienteException();
        }

        carrelloProdotto.setQuantita(nuovaQuantita);
        CarrelloProdotto salvato = carrelloProdottoRepo.save(carrelloProdotto);
        log.info("Quantità aggiornata per carrello prodotto id: {}", salvato.getId());
        return salvato;
    }

    @Transactional
    public void rimuoviProdottoDalCarrello(UUID idCarrelloProdotto) {
        log.info("Rimozione prodotto dal carrello, CarrelloProdotto id: {}", idCarrelloProdotto);

        ControlliUtils.controlloIdValido(idCarrelloProdotto, "carrello prodotto");

        CarrelloProdotto carrelloProdotto = carrelloProdottoRepo.findById(idCarrelloProdotto)
                .orElseThrow(() -> {
                    log.error("CarrelloProdotto id: {} non trovato", idCarrelloProdotto);
                    return new EntitaNonTrovataException(ErroreCodice.CARRELLO_PRODOTTO_NON_TROVATO);
                });

        log.info("CarrelloProdotto trovato, lo rimuovo dalla lista del carrello");
        carrelloProdotto.getCarrello().getProdotti().remove(carrelloProdotto);
        carrelloProdottoRepo.delete(carrelloProdotto);
        log.info("Prodotto rimosso con successo dal carrello id: {}", carrelloProdotto.getCarrello().getId());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcolaTotaleCarrello(UUID idCarrello) {
        log.info("Calcolo totale carrello id: {}", idCarrello);

        ControlliUtils.controlloIdValido(idCarrello, "carrello");

        Carrello carrello = carrelloRepo.findById(idCarrello)
                .orElseThrow(() -> {
                    log.error("Carrello id: {} non trovato", idCarrello);
                    return new EntitaNonTrovataException(ErroreCodice.CARRELLO_NON_TROVATO);
                });

        BigDecimal totale = carrello.getProdotti()
                .stream()
                .map(cp -> cp.getProdotto().getPrezzo()
                        .multiply(BigDecimal.valueOf(cp.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Totale carrello id: {} = {}", idCarrello, totale);
        return totale;
    }
}
