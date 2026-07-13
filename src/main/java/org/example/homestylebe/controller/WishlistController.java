package org.example.homestylebe.controller;

import org.example.homestylebe.dto.response.WishlistResponseDTO;
import org.example.homestylebe.dto.response.ProdottoResponseDTO;
import org.example.homestylebe.dto.response.ProdottoSuggerimentoDTO;
import org.example.homestylebe.entity.Wishlist;
import org.example.homestylebe.mapper.WishlistMapper;
import org.example.homestylebe.mapper.ProdottoMapper;
import org.example.homestylebe.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Controller REST per la gestione della "lista dei desideri" degli utenti.
 * Consente di aggiungere, rimuovere e modificare la priorità dei prodotti
 * che l'utente desidera tenere d'occhio.
 */
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistMapper wishlistMapper;
    private final ProdottoMapper prodottoMapper;
    private final WishlistService wishlistService;

    @GetMapping("/utente/{idUtente}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WishlistResponseDTO> getWishlistPerUtente(@PathVariable UUID idUtente) {
        Wishlist w = wishlistService.trovaWishlistPerUtente(idUtente);
        if (w == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(wishlistMapper.toDTO(w));
    }

    @PostMapping("/utente/{idUtente}/prodotto/{idProdotto}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WishlistResponseDTO> aggiungiAWishlist(
            @PathVariable UUID idUtente,
            @PathVariable UUID idProdotto) {

        Wishlist wishlist = wishlistService.aggiungiAWishlist(idUtente, idProdotto);
        return ResponseEntity.ok(wishlistMapper.toDTO(wishlist));
    }

    @DeleteMapping("/utente/{idUtente}/prodotto/{idProdotto}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WishlistResponseDTO> rimuoviDaWishlist(
            @PathVariable UUID idUtente,
            @PathVariable UUID idProdotto) {
        
        Wishlist wishlist = wishlistService.rimuoviDaWishlist(idUtente, idProdotto);
        return ResponseEntity.ok(wishlistMapper.toDTO(wishlist));
    }

    @DeleteMapping("/utente/{idUtente}/svuota")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> svuotaWishlistPerUtente(@PathVariable UUID idUtente) {
        wishlistService.svuotaWishlistPerUtente(idUtente);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/utente/{idUtente}/ricerca-suggerimenti")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProdottoSuggerimentoDTO>> ricercaSuggerimentiWishlist(
            @PathVariable UUID idUtente,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID categoriaId) {
        return ResponseEntity.ok(wishlistService.ricercaSuggerimentiWishlist(idUtente, query, categoriaId));
    }

    @GetMapping("/utente/{idUtente}/ricerca")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProdottoResponseDTO>> ricercaProdottiWishlist(
            @PathVariable UUID idUtente,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID categoriaId) {
        
        List<org.example.homestylebe.entity.Prodotto> prodotti = wishlistService.ricercaProdottiWishlist(idUtente, query, categoriaId);
        return ResponseEntity.ok(prodottoMapper.toDTOs(prodotti));
    }
}
