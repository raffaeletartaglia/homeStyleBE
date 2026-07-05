package org.example.homestylebe.controller;

import org.example.homestylebe.dto.request.RecensioneRequestDTO;
import org.example.homestylebe.dto.response.RecensioneResponseDTO;
import org.example.homestylebe.mapper.RecensioneMapper;
import org.example.homestylebe.service.RecensioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione delle recensioni (feedback e valutazioni) sui prodotti.
 * Permette agli utenti di lasciare recensioni su prodotti acquistati,
 * e agli altri utenti di consultarle.
 */
@RestController
@RequestMapping("/api/v1/recensione")
@RequiredArgsConstructor
public class RecensioneController {

    private final RecensioneMapper recensioneMapper;
    private final RecensioneService recensioneService;

    /**
     * Inserisce una nuova recensione.
     * Spesso la logica di business richiederà che l'utente abbia effettivamente acquistato il prodotto.
     *
     * @param requestDTO Il contenuto della recensione (voto, commento, ID prodotto).
     * @return La recensione appena salvata.
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RecensioneResponseDTO> creaRecensione(
            @RequestBody RecensioneRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(
                recensioneMapper.toDTO(
                        recensioneService.creaRecensione(
                                recensioneMapper.toEntity(requestDTO)
                        )
                )
        );
    }

    /**
     * Recupera tutte le recensioni associate a un determinato prodotto.
     * Utile per mostrare i feedback nella pagina di dettaglio dell'articolo.
     *
     * @param prodottoId L'ID del prodotto.
     * @return Una lista di recensioni lasciate dagli utenti per quel prodotto.
     */
    @GetMapping("/prodotto/{prodottoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<RecensioneResponseDTO>> getRecensioniByProdotto(
            @PathVariable UUID prodottoId
    ) {
        return ResponseEntity.ok(
                recensioneMapper.toDTOs(
                        recensioneService.getRecensioniByProdotto(prodottoId)
                )
        );
    }

    /**
     * Recupera tutte le recensioni scritte da un determinato utente.
     * Utile per la sezione "Il mio profilo / Le mie recensioni".
     *
     * @param utenteId L'ID dell'utente.
     * @return Lista delle recensioni pubblicate dall'utente.
     */
    @GetMapping("/utente/{utenteId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<RecensioneResponseDTO>> getRecensioniByUtente(
            @PathVariable UUID utenteId
    ) {
        return ResponseEntity.ok(
                recensioneMapper.toDTOs(
                        recensioneService.getRecensioniByUtente(utenteId)
                )
        );
    }

    /**
     * Recupera la recensione associata a una specifica riga di un ordine (Dettaglio Ordine).
     * Utile se si permette una sola recensione per ogni singola transazione/acquisto.
     *
     * @param dettaglioOrdineId L'ID del dettaglio ordine.
     * @return Lista di recensioni (solitamente una) per quella voce d'ordine.
     */
    @GetMapping("/dettaglio-ordine/{dettaglioOrdineId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<RecensioneResponseDTO>> getRecensioneByDettaglioOrdine(
            @PathVariable UUID dettaglioOrdineId
    ) {
        return ResponseEntity.ok(
                recensioneMapper.toDTOs(
                        recensioneService.getRecensioneByDettaglioOrdine(dettaglioOrdineId)
                )
        );
    }

    /**
     * Permette all'utente di modificare una propria recensione.
     *
     * @param recensioneId L'ID della recensione da modificare.
     * @param requestDTO Il nuovo contenuto e/o voto.
     * @return La recensione aggiornata.
     */
    @PutMapping("/{recensioneId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RecensioneResponseDTO> modificaRecensione(
            @PathVariable UUID recensioneId,
            @RequestBody RecensioneRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(
                recensioneMapper.toDTO(
                        recensioneService.modificaRecensione(
                                recensioneId,
                                recensioneMapper.toEntity(requestDTO)
                        )
                )
        );
    }

    /**
     * Permette all'utente di eliminare una propria recensione (o all'admin di moderarla).
     *
     * @param recensioneId L'ID della recensione da rimuovere.
     * @return Risposta vuota (No Content).
     */
    @DeleteMapping("/{recensioneId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> eliminaRecensione(
            @PathVariable UUID recensioneId
    ) {
        recensioneService.eliminaRecensione(recensioneId);
        return ResponseEntity.noContent().build();
    }

}

