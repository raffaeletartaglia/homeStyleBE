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
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;

    private UUID resolveUtenteId(UUID idUtente) {
        return utenteRepository.findUtenteByKeycloakId(idUtente.toString())
                .map(Utente::getId)
                .orElse(idUtente);
    }

    @Transactional(readOnly = true)
    public Wishlist trovaWishlistPerUtente(UUID paramIdUtente) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Recupero wishlist per utente id: {}", idUtente);
        ControlliUtils.controlloIdValido(idUtente, "utente");

        return wishlistRepository.findByUtente_Id(idUtente).orElseGet(() -> {
            log.info("Nessuna wishlist trovata per l'utente, verrà creata dinamicamente se necessario");
            return null; // Il controller o il service creerà la wishlist all'occorrenza
        });
    }

    @Transactional
    public Wishlist aggiungiAWishlist(UUID paramIdUtente, UUID idProdotto) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Aggiunta prodotto id: {} in wishlist utente id: {}", idProdotto, idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");
        ControlliUtils.controlloIdValido(idProdotto, "prodotto");

        Utente utente = utenteRepository.findById(idUtente).orElseThrow(
                () -> new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO)
        );

        Prodotto prodotto = prodottoRepository.findById(idProdotto).orElseThrow(
                () -> new EntitaNonTrovataException(ErroreCodice.PRODOTTO_NON_TROVATO)
        );

        // Cerco o creo la wishlist per l'utente
        Wishlist wishlist = wishlistRepository.findByUtente_Id(idUtente).orElseGet(() -> {
            Wishlist w = new Wishlist();
            w.setUtente(utente);
            return w;
        });

        if (wishlist.getProdotti().contains(prodotto)) {
            log.warn("Prodotto id: {} già presente in wishlist utente id: {}", idProdotto, idUtente);
            return wishlist; // Già presente, non facciamo nulla
        }

        wishlist.getProdotti().add(prodotto);
        Wishlist salvata = wishlistRepository.save(wishlist);
        log.info("Prodotto aggiunto alla wishlist. Elementi totali: {}", salvata.getProdotti().size());
        return salvata;
    }

    @Transactional
    public Wishlist rimuoviDaWishlist(UUID paramIdUtente, UUID idProdotto) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Rimozione prodotto id: {} dalla wishlist utente id: {}", idProdotto, idUtente);
        ControlliUtils.controlloIdValido(idUtente, "utente");
        ControlliUtils.controlloIdValido(idProdotto, "prodotto");

        Wishlist wishlist = wishlistRepository.findByUtente_Id(idUtente).orElseThrow(
                () -> new EntitaNonTrovataException(ErroreCodice.WISHLIST_ITEM_NON_TROVATO)
        );

        boolean rimosso = wishlist.getProdotti().removeIf(p -> p.getId().equals(idProdotto));
        
        if (!rimosso) {
            log.warn("Il prodotto {} non era presente nella wishlist dell'utente {}", idProdotto, idUtente);
        }

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public void svuotaWishlistPerUtente(UUID paramIdUtente) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Svuotamento wishlist per utente id: {}", idUtente);
        ControlliUtils.controlloIdValido(idUtente, "utente");

        wishlistRepository.findByUtente_Id(idUtente).ifPresent(w -> {
            w.getProdotti().clear();
            wishlistRepository.save(w);
            log.info("Wishlist svuotata per utente id: {}", idUtente);
        });
    }

    @Transactional(readOnly = true)
    public List<org.example.homestylebe.dto.response.ProdottoSuggerimentoDTO> ricercaSuggerimentiWishlist(UUID paramIdUtente, String query, UUID categoriaId) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Ricerca suggerimenti wishlist per utente id: {} con query: {} e categoria: {}", idUtente, query, categoriaId);
        ControlliUtils.controlloIdValido(idUtente, "utente");

        String likeQuery = (query != null && !query.trim().isEmpty()) ? "%" + query.trim().toLowerCase() + "%" : null;
        List<Prodotto> prodotti = wishlistRepository.ricercaProdottiInWishlist(idUtente, likeQuery, categoriaId);
        
        return prodotti.stream().map(p -> {
            org.example.homestylebe.dto.response.ProdottoSuggerimentoDTO dto = new org.example.homestylebe.dto.response.ProdottoSuggerimentoDTO();
            dto.setId(p.getId());
            dto.setNomeProdotto(p.getNomeProdotto());
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> ricercaProdottiWishlist(UUID paramIdUtente, String query, UUID categoriaId) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Ricerca prodotti wishlist per utente id: {} con query: {} e categoria: {}", idUtente, query, categoriaId);
        ControlliUtils.controlloIdValido(idUtente, "utente");

        String likeQuery = (query != null && !query.trim().isEmpty()) ? "%" + query.trim().toLowerCase() + "%" : null;
        return wishlistRepository.ricercaProdottiInWishlist(idUtente, likeQuery, categoriaId);
    }
}


