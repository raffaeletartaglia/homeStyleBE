package org.example.homestylebe.service; 
import java.util.UUID; 
import java.time.LocalDateTime; 
import java.util.List; 
import java.util.Optional;

import org.example.homestylebe.exception.ErroreCodice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor; 
import lombok.extern.slf4j.Slf4j; 
import org.example.homestylebe.utils.ControlliUtils; 
import org.example.homestylebe.exception.*; 
import org.example.homestylebe.repository.MovimentoMagazzinoRepository; 
import org.example.homestylebe.repository.ProdottoRepository; 
import org.example.homestylebe.repository.OrdineRepository; 
import org.example.homestylebe.repository.ResoRepository; 
import org.example.homestylebe.entity.MovimentoMagazzino; 
import org.example.homestylebe.entity.Prodotto; 
import org.example.homestylebe.entity.Ordine; 
import org.example.homestylebe.entity.Reso;


@Service
@RequiredArgsConstructor
@Slf4j
public class MovimentoMagazzinoService {

    private final MovimentoMagazzinoRepository movimentoMagazzinoRepo;
    private final ProdottoRepository prodottoRepo;
    private final OrdineRepository ordineRepo;
    private final ResoRepository resoRepo;

    @Transactional
    public MovimentoMagazzino creaMovimento(MovimentoMagazzino movimento) {
        log.info("Creazione nuovo movimento magazzino");

        if (!controlloTipoMovimento(movimento.getTipoMovimento())) {
            log.error("Tipo movimento non valido: {}", movimento.getTipoMovimento());
            throw new ValoreNonValidoException("Tipo movimento non valido", ErroreCodice.MOVIMENTO_MAGAZZINO_NON_VALIDO);
        }

        if (!controlloQuantita(movimento.getQuantita())) {
            log.error("Quantità non valida: {}", movimento.getQuantita());
            throw new ValoreNonValidoException("Quantità non valida", ErroreCodice.PRODOTTO_STOCK_INSUFFICIENTE);
        }

        if (!controlloNote(movimento.getNote())) {
            log.error("Note non valide");
            throw new ValoreNonValidoException("Note non valide", ErroreCodice.MOVIMENTO_MAGAZZINO_NOTE_NON_VALIDE);
        }

        if (!controlloDataMovimento(movimento.getDataMovimento())) {
            log.error("Data movimento non valida: {}", movimento.getDataMovimento());
            throw new ValoreNonValidoException("Data movimento non valida", ErroreCodice.MOVIMENTO_MAGAZZINO_DATA_NON_VALIDA);
        }

        ControlliUtils.controlloIdValido(movimento.getProdotto().getId(), "Prodotto");

        Prodotto prodotto = prodottoRepo.findById(movimento.getProdotto().getId())
                .orElseThrow(() -> {
                    log.error("Prodotto non trovato id={}", movimento.getProdotto().getId());
                    return new EntitaNonTrovataException("Prodotto non trovato");
                });

        movimento.setProdotto(prodotto);
        log.info("Prodotto associato id={}", prodotto.getId());

        if (movimentoRichiedeOrdine(movimento.getTipoMovimento())) {
            ControlliUtils.controlloIdValido(movimento.getOrdine().getId(), "Ordine");

            Ordine ordine = ordineRepo.findById(movimento.getOrdine().getId())
                    .orElseThrow(() -> {
                        log.error("Ordine non trovato id={}", movimento.getOrdine().getId());
                        return new EntitaNonTrovataException("Ordine non trovato");
                    });

            movimento.setOrdine(ordine);
            log.info("Ordine associato id={}", ordine.getId());

        } else {
            movimento.setOrdine(null);
        }

        if (movimentoRichiedeReso(movimento.getTipoMovimento())) {
            ControlliUtils.controlloIdValido(movimento.getReso().getId(), "Reso");

            Reso reso = resoRepo.findById(movimento.getReso().getId())
                    .orElseThrow(() -> {
                        log.error("Reso non trovato id={}", movimento.getReso().getId());
                        return new EntitaNonTrovataException("Reso non trovato");
                    });

            movimento.setReso(reso);
            log.info("Reso associato id={}", reso.getId());

        } else {
            movimento.setReso(null);
        }

        MovimentoMagazzino salvato = movimentoMagazzinoRepo.save(movimento);

        log.info("Movimento salvato, aggiorno quantità prodotto id={}", prodotto.getId());

        aggiornaQuantitaProdotto(prodotto, movimento.getTipoMovimento(), movimento.getQuantita());

        log.info("Movimento creato con id={}", salvato.getId());

        return salvato;
    }//creaMovimento


    @Transactional
    public MovimentoMagazzino modificaMovimento(UUID movimentoId, MovimentoMagazzino movimentoAggiornato) {
        log.info("Modifica movimento magazzino id={}", movimentoId);

        ControlliUtils.controlloIdValido(movimentoId, "MovimentoMagazzino");

        MovimentoMagazzino esistente = movimentoMagazzinoRepo.findById(movimentoId)
                .orElseThrow(() -> {
                    log.error("Movimento non trovato id={}", movimentoId);
                    return new EntitaNonTrovataException("Movimento non trovato");
                });

        
        if (!controlloTipoMovimento(movimentoAggiornato.getTipoMovimento())) {
            log.error("Tipo movimento non valido: {}", movimentoAggiornato.getTipoMovimento());
            throw new ValoreNonValidoException("Tipo movimento non valido", ErroreCodice.MOVIMENTO_MAGAZZINO_TIPO_NON_VALIDO);
        }

        if (!controlloQuantita(movimentoAggiornato.getQuantita())) {
            log.error("Quantità non valida: {}", movimentoAggiornato.getQuantita());
            throw new ValoreNonValidoException("Quantità non valida", ErroreCodice.MOVIMENTO_MAGAZZINO_QUANTITA_NON_VALIDA);
        }

        if (!controlloNote(movimentoAggiornato.getNote())) {
            log.error("Note non valide");
            throw new ValoreNonValidoException("Note non valide", ErroreCodice.MOVIMENTO_MAGAZZINO_NOTE_NON_VALIDE);
        }

        if (!controlloDataMovimento(movimentoAggiornato.getDataMovimento())) {
            log.error("Data movimento non valida: {}", movimentoAggiornato.getDataMovimento());
            throw new ValoreNonValidoException("Data movimento non valida", ErroreCodice.MOVIMENTO_MAGAZZINO_DATA_NON_VALIDA);
        }

        
        log.info("Inversione quantità vecchio movimento id={}", movimentoId);
        invertiQuantitaProdotto(esistente.getProdotto(), esistente.getTipoMovimento(), esistente.getQuantita());

        
        esistente.setTipoMovimento(movimentoAggiornato.getTipoMovimento());
        esistente.setQuantita(movimentoAggiornato.getQuantita());
        esistente.setNote(movimentoAggiornato.getNote());
        esistente.setDataMovimento(movimentoAggiornato.getDataMovimento());

        
        ControlliUtils.controlloIdValido(movimentoAggiornato.getProdotto().getId(), "Prodotto");

        Prodotto nuovoProdotto = prodottoRepo.findById(movimentoAggiornato.getProdotto().getId())
                .orElseThrow(() -> {
                    log.error("Prodotto non trovato id={}", movimentoAggiornato.getProdotto().getId());
                    return new EntitaNonTrovataException("Prodotto non trovato");
                });

        if (!esistente.getProdotto().getId().equals(nuovoProdotto.getId())) {
            log.info("Cambio prodotto nel movimento id={} da {} a {}", 
                     movimentoId, esistente.getProdotto().getId(), nuovoProdotto.getId());
        }

        esistente.setProdotto(nuovoProdotto);

       
        if (movimentoRichiedeOrdine(movimentoAggiornato.getTipoMovimento())) {
            ControlliUtils.controlloIdValido(movimentoAggiornato.getOrdine().getId(), "Ordine");

            Ordine ordine = ordineRepo.findById(movimentoAggiornato.getOrdine().getId())
                    .orElseThrow(() -> {
                        log.error("Ordine non trovato id={}", movimentoAggiornato.getOrdine().getId());
                        return new EntitaNonTrovataException("Ordine non trovato");
                    });

            esistente.setOrdine(ordine);
            log.info("Ordine aggiornato id={}", ordine.getId());

        } else {
            esistente.setOrdine(null);
        }

        // ===== RESO =====
        if (movimentoRichiedeReso(movimentoAggiornato.getTipoMovimento())) {
            ControlliUtils.controlloIdValido(movimentoAggiornato.getReso().getId(), "Reso");

            Reso reso = resoRepo.findById(movimentoAggiornato.getReso().getId())
                    .orElseThrow(() -> {
                        log.error("Reso non trovato id={}", movimentoAggiornato.getReso().getId());
                        return new EntitaNonTrovataException("Reso non trovato");
                    });

            esistente.setReso(reso);
            log.info("Reso aggiornato id={}", reso.getId());

        } else {
            esistente.setReso(null);
        }

       
        log.info("Aggiornamento quantità prodotto id={}", nuovoProdotto.getId());
        aggiornaQuantitaProdotto(nuovoProdotto, esistente.getTipoMovimento(), esistente.getQuantita());

        MovimentoMagazzino salvato = movimentoMagazzinoRepo.save(esistente);

        log.info("Movimento modificato con successo id={}", salvato.getId());

        return salvato;
    }//ModificaMovimento


    @Transactional
    public void annullaMovimento(UUID movimentoId) {
        log.info("Annullamento movimento id={}", movimentoId);

        ControlliUtils.controlloIdValido(movimentoId, "MovimentoMagazzino");

        MovimentoMagazzino movimento = movimentoMagazzinoRepo.findById(movimentoId)
                .orElseThrow(() -> {
                    log.error("Movimento non trovato id={}", movimentoId);
                    return new EntitaNonTrovataException("Movimento non trovato");
                });

        invertiQuantitaProdotto(movimento.getProdotto(), movimento.getTipoMovimento(), movimento.getQuantita());

        movimentoMagazzinoRepo.delete(movimento);

        log.info("Movimento annullato id={} e quantità ripristinata", movimentoId);
    }//annullaMovimento


    public List<MovimentoMagazzino> getMovimentiByProdotto(UUID prodottoId) {
        log.info("Recupero movimenti per prodottoId={}", prodottoId);

        ControlliUtils.controlloIdValido(prodottoId, "Prodotto");

        Prodotto prodotto = prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> {
                    log.error("Prodotto non trovato id={}", prodottoId);
                    return new EntitaNonTrovataException("Prodotto non trovato");
                });

        List<MovimentoMagazzino> lista = movimentoMagazzinoRepo.findByProdotto(prodotto);

        log.info("Trovati {} movimenti per prodottoId={}", lista.size(), prodottoId);

        return lista;
    }//getMovimentiByProdotto

    public org.springframework.data.domain.Page<MovimentoMagazzino> getTuttiMovimenti(int page, int size) {
        log.info("Recupero tutti i movimenti (page={}, size={})", page, size);
        return movimentoMagazzinoRepo.findAll(org.springframework.data.domain.PageRequest.of(page, size));
    }

    @Transactional
    public MovimentoMagazzino aggiungiRifornimentoManuale(UUID prodottoId, Integer quantita, String note) {
        log.info("Aggiunta rifornimento manuale per prodottoId={}, qtà={}", prodottoId, quantita);

        ControlliUtils.controlloIdValido(prodottoId, "Prodotto");

        Prodotto prodotto = prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> {
                    log.error("Prodotto non trovato id={}", prodottoId);
                    return new EntitaNonTrovataException("Prodotto non trovato");
                });

        MovimentoMagazzino mov = new MovimentoMagazzino();
        mov.setProdotto(prodotto);
        mov.setQuantita(quantita);
        mov.setTipoMovimento(MovimentoMagazzino.TipoMovimento.PRODUZIONE); // Uso PRODUZIONE per rifornimento generico
        mov.setDataMovimento(LocalDateTime.now());
        mov.setNote(note != null ? note : "Rifornimento manuale da Admin");

        return creaMovimento(mov);
    }


    public List<MovimentoMagazzino> getMovimentiByOrdine(UUID ordineId) {
        log.info("Recupero movimenti per ordineId={}", ordineId);

        ControlliUtils.controlloIdValido(ordineId, "Ordine");

        Ordine ordine = ordineRepo.findById(ordineId)
                .orElseThrow(() -> {
                    log.error("Ordine non trovato id={}", ordineId);
                    return new EntitaNonTrovataException("Ordine non trovato");
                });

        List<MovimentoMagazzino> lista = movimentoMagazzinoRepo.findByOrdine(ordine);

        log.info("Trovati {} movimenti per ordineId={}", lista.size(), ordineId);

        return lista;
    }//getMovimentiByOrdine


    public List<MovimentoMagazzino> getMovimentiByReso(UUID resoId) {
        log.info("Recupero movimenti per resoId={}", resoId);

        ControlliUtils.controlloIdValido(resoId, "Reso");

        Reso reso = resoRepo.findById(resoId)
                .orElseThrow(() -> {
                    log.error("Reso non trovato id={}", resoId);
                    return new EntitaNonTrovataException("Reso non trovato");
                });

        List<MovimentoMagazzino> lista = movimentoMagazzinoRepo.findByReso(reso);

        log.info("Trovati {} movimenti per resoId={}", lista.size(), resoId);

        return lista;
    }//getMovimentiByReso


    // ===== CONTROLLI =====

    private boolean controlloTipoMovimento(MovimentoMagazzino.TipoMovimento tipo) {
        log.debug("Controllo tipo movimento: {}", tipo);
        if (tipo == null) return false;
        ControlliUtils.controlloValoreEnum(
                MovimentoMagazzino.TipoMovimento.class,
                tipo.name(),
                "Tipo movimento"
        );
        return true;
    }

    private boolean controlloQuantita(Integer q){ 
        log.debug("Controllo quantità: {}", q);
        return q != null && q > 0; 
    }

    private boolean controlloNote(String note){ 
        log.debug("Controllo note");
        return note == null || note.length() <= 500; 
    }

    private boolean controlloDataMovimento(LocalDateTime data) {
        log.debug("Controllo data movimento: {}", data);
        return data == null || !data.isAfter(LocalDateTime.now());
    }

    private boolean movimentoRichiedeOrdine(MovimentoMagazzino.TipoMovimento tipo) {
        return tipo == MovimentoMagazzino.TipoMovimento.SCARICO_VENDITA
            || tipo == MovimentoMagazzino.TipoMovimento.ANNULLAMENTO_ORDINE;
    }

    private boolean movimentoRichiedeReso(MovimentoMagazzino.TipoMovimento tipo) {
        return tipo == MovimentoMagazzino.TipoMovimento.RESO_CLIENTE;
    }

    private void aggiornaQuantitaProdotto(Prodotto prodotto, MovimentoMagazzino.TipoMovimento tipo, Integer quantita) {
        if (prodotto == null || tipo == null || quantita == null) return;

        int attuale = Optional.ofNullable(prodotto.getGiacenza()).orElse(0);

        switch (tipo) {
            case PRODUZIONE, RESO_CLIENTE, ANNULLAMENTO_ORDINE ->
                prodotto.setGiacenza(attuale + quantita);
            case SCARICO_VENDITA ->
                prodotto.setGiacenza(Math.max(0, attuale - quantita));
            case RETTIFICA_INVENTARIO ->
                prodotto.setGiacenza(quantita);
            default -> {
                log.error("Tipo movimento non gestito: {}", tipo);
                throw new ValoreNonValidoException("Tipo movimento non gestito", ErroreCodice.MOVIMENTO_MAGAZZINO_TIPO_NON_GESTITO);
            }
        }

        prodottoRepo.save(prodotto);

        log.debug("Quantità aggiornata prodotto {}: {}", prodotto.getId(), prodotto.getGiacenza());
    }


    private void invertiQuantitaProdotto(Prodotto prodotto, MovimentoMagazzino.TipoMovimento tipo, Integer quantita) {
        if (prodotto == null || tipo == null || quantita == null) return;

        int attuale = Optional.ofNullable(prodotto.getGiacenza()).orElse(0);

        switch (tipo) {
            case PRODUZIONE, RESO_CLIENTE, ANNULLAMENTO_ORDINE ->
                prodotto.setGiacenza(Math.max(0, attuale - quantita));
            case SCARICO_VENDITA ->
                prodotto.setGiacenza(attuale + quantita);
            case RETTIFICA_INVENTARIO ->
                prodotto.setGiacenza(Math.max(0, attuale - quantita));
            default -> {
                log.error("Tipo movimento sconosciuto per inversione: {}", tipo);
                throw new ValoreNonValidoException("Tipo movimento sconosciuto per inversione quantità", ErroreCodice.MOVIMENTO_MAGAZZINO_NON_TROVATO);
            }
        }

        prodottoRepo.save(prodotto);

        log.debug("Quantità invertita prodotto {}: {}", prodotto.getId(), prodotto.getGiacenza());
    }

}//MovimentoMagazzinoService
	
	
	
	

