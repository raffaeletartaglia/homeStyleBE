package org.example.homestylebe.service;

import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.entity.DettaglioOrdine;
import org.example.homestylebe.entity.Indirizzo;
import org.example.homestylebe.entity.Reso;
import org.example.homestylebe.repository.DettaglioOrdineRepository;
import org.example.homestylebe.repository.IndirizzoRepository;
import org.example.homestylebe.repository.ResoRepository;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import org.example.homestylebe.exception.OperazioneNonConsentitaException;
import org.example.homestylebe.exception.ResoGiaEsistenteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.homestylebe.utils.ControlliUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.math.BigDecimal;

import org.example.homestylebe.entity.MovimentoMagazzino;
import org.example.homestylebe.entity.Pagamento;
import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.repository.MovimentoMagazzinoRepository;
import org.example.homestylebe.repository.PagamentoRepository;
import org.example.homestylebe.repository.ProdottoRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResoService {

    private final ResoRepository resoRepository;
    private final DettaglioOrdineRepository dettaglioOrdineRepository;
    private final IndirizzoRepository indirizzoRepository;
    private final ProdottoRepository prodottoRepository;
    private final MovimentoMagazzinoRepository movimentoMagazzinoRepository;
    private final PagamentoRepository pagamentoRepository;

    @Transactional(readOnly = true)
    public Reso trovaResoPerId(UUID idReso) {
        log.info("Cerco reso con id: {}", idReso);
        ControlliUtils.controlloIdValido(idReso, "reso");

        return resoRepository.findById(idReso).orElseThrow(
                () -> {
                    log.error("Reso con id: {} non trovato", idReso);
                    return new EntitaNonTrovataException(ErroreCodice.RESO_NON_TROVATO);
                }
        );
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Reso> trovaTuttiIResi(org.springframework.data.domain.Pageable pageable) {
        log.info("Cerco tutti i resi paginati");
        return resoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Reso trovaResoPerDettaglioOrdine(UUID idDettaglioOrdine) {
        log.info("Cerco reso per dettaglio ordine id: {}", idDettaglioOrdine);
        ControlliUtils.controlloIdValido(idDettaglioOrdine, "dettaglio ordine");

        return resoRepository.findByDettaglioOrdine_Id(idDettaglioOrdine).orElseThrow(
                () -> {
                    log.error("Nessun reso trovato per dettaglio ordine id: {}", idDettaglioOrdine);
                    return new EntitaNonTrovataException(ErroreCodice.RESO_NON_TROVATO);
                }
        );
    }

    @Transactional
    public Reso creaReso(UUID idDettaglioOrdine,
                         UUID idIndirizzoReso,
                         LocalDate dataResoPrevista,
                         LocalTime oraRitiroReso,
                         String motivo) {

        log.info("Creazione reso per dettaglio ordine id: {}", idDettaglioOrdine);

        ControlliUtils.controlloIdValido(idDettaglioOrdine, "dettaglio ordine");
        ControlliUtils.controlloIdValido(idIndirizzoReso, "indirizzo");

        // 1. DettaglioOrdine esiste?
        DettaglioOrdine dettaglio = dettaglioOrdineRepository.findById(idDettaglioOrdine)
                .orElseThrow(() -> {
                    log.error("Dettaglio ordine id: {} non trovato", idDettaglioOrdine);
                    return new EntitaNonTrovataException(ErroreCodice.DETTAGLIO_ORDINE_NON_TROVATO);
                });

        // 2. Esiste già un reso per questo dettaglio? (1:1)
        if (dettaglio.getReso() != null) {
            log.error("Esiste già un reso per dettaglio ordine id: {}", idDettaglioOrdine);
            throw new ResoGiaEsistenteException();
        }

        // 3. Indirizzo reso
        Indirizzo indirizzo = indirizzoRepository.findById(idIndirizzoReso)
                .orElseThrow(() -> {
                    log.error("Indirizzo reso id: {} non trovato", idIndirizzoReso);
                    return new EntitaNonTrovataException(ErroreCodice.INDIRIZZO_NON_TROVATO);
                });

        // 4. Creazione reso
        Reso reso = new Reso();
        reso.setDettaglioOrdine(dettaglio);
        reso.setIndirizzoReso(indirizzo);
        reso.setDataResoPrevista(dataResoPrevista);
        reso.setOraRitiroReso(oraRitiroReso);
        reso.setMotivo(motivo);
        reso.setStatoReso(Reso.StatoReso.IN_PREPARAZIONE); // se vuoi uno stato iniziale

        Reso salvato = resoRepository.save(reso);
        log.info("Reso creato con successo, id: {}", salvato.getId());
        return salvato;
    }

    @Transactional
    public Reso aggiornaDataOraRitiro(UUID idReso,
                                      LocalDate nuovaData,
                                      LocalTime nuovaOra) {
        log.info("Aggiornamento data/ora ritiro per reso id: {}", idReso);
        ControlliUtils.controlloIdValido(idReso, "reso");

        Reso reso = resoRepository.findById(idReso).orElseThrow(
                () -> {
                    log.error("Reso con id: {} non trovato", idReso);
                    return new EntitaNonTrovataException(ErroreCodice.RESO_NON_TROVATO);
                }
        );

        reso.setDataResoPrevista(nuovaData);
        reso.setOraRitiroReso(nuovaOra);

        Reso salvato = resoRepository.save(reso);
        log.info("Reso id: {} aggiornato con nuova data/ora ritiro", salvato.getId());
        return salvato;
    }

    @Transactional
    public Reso annullaReso(UUID idReso) {
        log.info("Inizio annullamento reso id: {}", idReso);
        ControlliUtils.controlloIdValido(idReso, "reso");

        Reso reso = resoRepository.findById(idReso).orElseThrow(
                () -> {
                    log.error("Reso con id: {} non trovato", idReso);
                    return new EntitaNonTrovataException(ErroreCodice.RESO_NON_TROVATO);
                }
        );

        // Non puoi annullare un reso già annullato o già ritirato
        if (reso.getStatoReso() == Reso.StatoReso.ANNULLATO || reso.getStatoReso() == Reso.StatoReso.RITIRATO) {
            log.error("Impossibile annullare reso id: {} con stato: {}", idReso, reso.getStatoReso());
            throw new OperazioneNonConsentitaException(
                    "Impossibile annullare un reso già " + reso.getStatoReso(),
                    ErroreCodice.PRENOTAZIONE_STATO_NON_VALIDO);
        }

        reso.setStatoReso(Reso.StatoReso.ANNULLATO);
        Reso salvato = resoRepository.save(reso);
        log.info("Reso id: {} annullato con successo", salvato.getId());
        return salvato;
    }

    @Transactional
    public Reso modificaStatoReso(UUID idReso, Reso.StatoReso nuovoStato) {
        log.info("Inizio modifica stato reso id: {} a nuovo stato: {}", idReso, nuovoStato);
        ControlliUtils.controlloIdValido(idReso, "reso");

        Reso reso = resoRepository.findById(idReso).orElseThrow(
                () -> {
                    log.error("Reso con id: {} non trovato", idReso);
                    return new EntitaNonTrovataException(ErroreCodice.RESO_NON_TROVATO);
                }
        );

        if (reso.getStatoReso() == Reso.StatoReso.ANNULLATO || reso.getStatoReso() == Reso.StatoReso.RITIRATO) {
            log.error("Impossibile modificare un reso già chiuso con stato: {}", reso.getStatoReso());
            throw new OperazioneNonConsentitaException(
                    "Impossibile modificare un reso già " + reso.getStatoReso(),
                    ErroreCodice.PRENOTAZIONE_STATO_NON_VALIDO);
        }

        reso.setStatoReso(nuovoStato);

        // Automazione RITIRATO: Reintegro Magazzino + Rimborso Pagamento
        if (nuovoStato == Reso.StatoReso.RITIRATO) {
            log.info("Avvio procedura automatica di magazzino e rimborso per reso id: {}", idReso);
            
            DettaglioOrdine dettaglio = reso.getDettaglioOrdine();
            Prodotto prodotto = dettaglio.getProdotto();
            int quantitaResa = dettaglio.getQuantita();

            // 1. Aggiorna giacenza
            prodotto.setGiacenza(prodotto.getGiacenza() + quantitaResa);
            prodottoRepository.save(prodotto);
            log.info("Giacenza prodotto id: {} incrementata di {}", prodotto.getId(), quantitaResa);

            // 2. Crea Movimento Magazzino
            MovimentoMagazzino movimento = new MovimentoMagazzino();
            movimento.setProdotto(prodotto);
            movimento.setTipoMovimento(MovimentoMagazzino.TipoMovimento.RESO_CLIENTE);
            movimento.setQuantita(quantitaResa);
            movimento.setReso(reso);
            movimento.setOrdine(dettaglio.getOrdine());
            movimento.setNote("Rientro automatico per reso ritirato");
            movimentoMagazzinoRepository.save(movimento);
            log.info("Creato movimento di magazzino per reso ritirato");

            // 3. Esegui rimborso (Pagamento)
            pagamentoRepository.findByOrdine(dettaglio.getOrdine()).ifPresent(pagamento -> {
                BigDecimal importoDaRimborsare = dettaglio.getPrezzoUnitario().multiply(BigDecimal.valueOf(quantitaResa));
                BigDecimal rimborsatoAttuale = pagamento.getImportoRimborsato() != null ? pagamento.getImportoRimborsato() : BigDecimal.ZERO;
                
                pagamento.setImportoRimborsato(rimborsatoAttuale.add(importoDaRimborsare));
                pagamento.setDataRimborso(LocalDateTime.now());
                pagamentoRepository.save(pagamento);
                log.info("Effettuato storno economico di {} per l'ordine id: {}", importoDaRimborsare, dettaglio.getOrdine().getId());
            });
        }

        Reso salvato = resoRepository.save(reso);
        log.info("Stato reso id: {} modificato in {}", salvato.getId(), nuovoStato);
        return salvato;
    }

}
