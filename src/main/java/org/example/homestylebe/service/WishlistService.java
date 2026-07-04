package org.example.homestylebe.service;

import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.entity.Wishlist;
import org.example.homestylebe.repository.ProdottoRepository;
import org.example.homestylebe.repository.UtenteRepository;
import org.example.homestylebe.repository.WishlistRepository;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.homestylebe.utils.ControlliUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;

    @Transactional(readOnly = true)
    public List<Wishlist> trovaWishlistPerUtente(UUID idUtente) {
        log.info("Cerco wishlist per utente id: {}", idUtente);
        ControlliUtils.controlloIdValido(idUtente, "utente");

        List<Wishlist> wishlists = wishlistRepository.findByUtente_IdOrderByDataAggiuntaDesc(idUtente);
        if (wishlists.isEmpty()) {
            log.warn("Wishlist dell'utente: {}, vuota", idUtente);
        }
        log.info("Wishlist trovata con successo");
        return wishlists;
    }

    @Transactional
    public Wishlist aggiungiAWishlist(UUID idUtente,
                                      UUID idProdotto,
                                      Wishlist.Priorita priorita) {
        log.info("Aggiunta prodotto id: {} in wishlist utente id: {}", idProdotto, idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");
        ControlliUtils.controlloIdValido(idProdotto, "prodotto");

        log.info("Cerco l'utente con id: {}", idUtente);
        Utente utente = utenteRepository.findById(idUtente).orElseThrow(
                () -> {
                    log.error("Utente con id: {}, non trovato", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
                }
        );
        log.info("Utente con id: {}, trovato", idUtente);

        log.info("Cerco prodotto con id: {}", idProdotto);
        Prodotto prodotto = prodottoRepository.findById(idProdotto).orElseThrow(
                () -> {
                    log.error("Prodotto con id: {}, non trovato", idProdotto);
                    return new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO);
                }
        );
        log.info("Prodotto con id: {}, trovato", idProdotto);

        log.info("Verifico se il prodotto è già in wishlist");
        Wishlist esistente = wishlistRepository
                .findByUtente_IdAndProdotto_Id(idUtente, idProdotto)
                .orElse(null);

        if (esistente != null) {
            log.warn("Prodotto id: {} già presente in wishlist utente id: {}", idProdotto, idUtente);
            // opzionale: aggiorno solo la priorità se è stata passata ed è diversa
            if (priorita != null && priorita != esistente.getPriorita()) {
                esistente.setPriorita(priorita);
                Wishlist salvata = wishlistRepository.save(esistente);
                log.info("Priorità aggiornata per wishlist id: {}", salvata.getId());
                return salvata;
            }
            // se non aggiorni, puoi anche decidere di lanciare un'eccezione:
            // throw new WishlistItemGiaEsistenteException();
            return esistente;
        }

        log.info("Creazione nuova wishlist");
        Wishlist wishlist = new Wishlist();
        wishlist.setUtente(utente);
        wishlist.setProdotto(prodotto);
        wishlist.setPriorita(priorita != null ? priorita : Wishlist.Priorita.MEDIA);

        Wishlist salvata = wishlistRepository.save(wishlist);
        log.info("Wishlist item creato con id: {}", salvata.getId());
        return salvata;
    }

    @Transactional
    public Wishlist aggiornaPriorita(UUID idWishlist, Wishlist.Priorita nuovaPriorita) {
        log.info("Aggiornamento priorità wishlist id: {} -> {}", idWishlist, nuovaPriorita);
        ControlliUtils.controlloIdValido(idWishlist, "wishlist");

        Wishlist w = wishlistRepository.findById(idWishlist).orElseThrow(
                () -> {
                    log.error("Wishlist id: {} non trovata", idWishlist);
                    return new EntitaNonTrovataException(ErroreCodice.WISHLIST_ITEM_NON_TROVATO);
                }
        );

        w.setPriorita(nuovaPriorita);
        Wishlist salvata = wishlistRepository.save(w);
        log.info("Wishlist id: {} aggiornata a priorità: {}", salvata.getId(), salvata.getPriorita());
        return salvata;
    }

    @Transactional
    public void rimuoviDaWishlist(UUID idWishlist) {
        log.info("Rimozione elemento wishlist id: {}", idWishlist);
        ControlliUtils.controlloIdValido(idWishlist, "wishlist");

        if (!wishlistRepository.existsById(idWishlist)) {
            log.error("Wishlist id: {} non trovata", idWishlist);
            throw new EntitaNonTrovataException(ErroreCodice.WISHLIST_ITEM_NON_TROVATO);
        }

        wishlistRepository.deleteById(idWishlist);
        log.info("Wishlist id: {} rimossa con successo", idWishlist);
    }

    @Transactional
    public void svuotaWishlistPerUtente(UUID idUtente) {
        log.info("Svuotamento wishlist per utente id: {}", idUtente);
        ControlliUtils.controlloIdValido(idUtente, "utente");

        wishlistRepository.deleteByUtente_Id(idUtente);
        log.info("Wishlist svuotata per utente id: {}", idUtente);
    }
}


