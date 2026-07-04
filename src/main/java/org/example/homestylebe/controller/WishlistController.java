package org.example.homestylebe.controller;

import org.example.homestylebe.dto.response.WishlistResponseDTO;
import org.example.homestylebe.entity.Wishlist;
import org.example.homestylebe.mapper.WishlistMapper;
import org.example.homestylebe.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione della "lista dei desideri" degli utenti.
 * Consente di aggiungere, rimuovere e modificare la priorità dei prodotti
 * che l'utente desidera tenere d'occhio.
 */
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final WishlistMapper wishlistMapper;

    /**
     * Recupera l'intera wishlist di un utente.
     *
     * @param idUtente L'ID dell'utente proprietario della wishlist.
     * @return Una lista di DTO contenenti i prodotti salvati e le loro priorità.
     */
    @GetMapping("/utente/{idUtente}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<WishlistResponseDTO>> getWishlistPerUtente(@PathVariable UUID idUtente) {
        return ResponseEntity.ok(
                wishlistMapper.toDTOs(wishlistService.trovaWishlistPerUtente(idUtente))
        );
    }

    /**
     * Aggiunge un prodotto alla wishlist di un utente.
     * Se il prodotto è già presente, la sua priorità verrà aggiornata.
     *
     * @param idUtente L'ID dell'utente.
     * @param idProdotto L'ID del prodotto da desiderare.
     * @param priorita (Opzionale) La priorità assegnata (es. ALTA, MEDIA, BASSA). Se omessa, il default è MEDIA.
     * @return L'elemento appena inserito o aggiornato nella wishlist.
     */
    @PostMapping("/utente/{idUtente}/prodotto/{idProdotto}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WishlistResponseDTO> aggiungiAWishlist(
            @PathVariable UUID idUtente,
            @PathVariable UUID idProdotto,
            @RequestParam(required = false) Wishlist.Priorita priorita) {

        Wishlist wishlist = wishlistService.aggiungiAWishlist(idUtente, idProdotto, priorita);
        return ResponseEntity.ok(wishlistMapper.toDTO(wishlist));
    }

    /**
     * Modifica la priorità di un elemento già presente nella wishlist.
     *
     * @param idWishlist L'ID dell'elemento nella wishlist.
     * @param nuovaPriorita La nuova priorità desiderata.
     * @return L'elemento della wishlist aggiornato.
     */
    @PutMapping("/{idWishlist}/priorita")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WishlistResponseDTO> aggiornaPriorita(
            @PathVariable UUID idWishlist,
            @RequestParam Wishlist.Priorita nuovaPriorita) {

        Wishlist wishlist = wishlistService.aggiornaPriorita(idWishlist, nuovaPriorita);
        return ResponseEntity.ok(wishlistMapper.toDTO(wishlist));
    }

    /**
     * Rimuove un singolo elemento dalla wishlist.
     *
     * @param idWishlist L'ID dell'elemento da eliminare.
     * @return Risposta vuota con status HTTP 204.
     */
    @DeleteMapping("/{idWishlist}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> rimuoviDaWishlist(@PathVariable UUID idWishlist) {
        wishlistService.rimuoviDaWishlist(idWishlist);
        return ResponseEntity.noContent().build();
    }

    /**
     * Svuota completamente la wishlist di un utente, rimuovendo tutti i prodotti salvati.
     *
     * @param idUtente L'ID dell'utente.
     * @return Risposta vuota con status HTTP 204.
     */
    @DeleteMapping("/utente/{idUtente}/svuota")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> svuotaWishlistPerUtente(@PathVariable UUID idUtente) {
        wishlistService.svuotaWishlistPerUtente(idUtente);
        return ResponseEntity.noContent().build();
    }
}
