package org.example.homestylebe.controller;

import org.example.homestylebe.dto.response.CarrelloProdottoResponseDTO;
import org.example.homestylebe.mapper.CarrelloProdottoMapper;
import org.example.homestylebe.service.CarrelloProdottoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione delle righe di prodotto all'interno del carrello.
 * Permette di consultare, aggiornare la quantità e rimuovere i singoli item,
 * nonché di calcolare il totale del carrello.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/carrello-prodotto")
@RequiredArgsConstructor
public class CarrelloProdottoController {

    // FIX BUG 1: aggiunto 'final' per consentire l'iniezione tramite @RequiredArgsConstructor
    private final CarrelloProdottoMapper carrelloProdottoMapper;
    private final CarrelloProdottoService carrelloProdottoService;

    /**
     * Recupera il dettaglio di una singola riga carrello tramite il suo ID.
     *
     * @param idCarrelloProdotto L'ID univoco della riga carrello.
     * @return Il DTO della riga carrello trovata.
     */
    @GetMapping("/{idCarrelloProdotto}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CarrelloProdottoResponseDTO> trovaCarrelloProdottoPerId(
            @PathVariable UUID idCarrelloProdotto) {
        log.info("Richiesta trovare carrello-prodotto per id: {}", idCarrelloProdotto);
        return ResponseEntity.ok(
                carrelloProdottoMapper.toDTO(
                        carrelloProdottoService.trovaCarrelloProdottoPerId(idCarrelloProdotto)));
    }

    /**
     * Recupera tutti i prodotti presenti in un carrello tramite l'ID del carrello.
     *
     * @param idCarrello L'ID del carrello di cui listare i prodotti.
     * @return La lista di righe carrello associate al carrello specificato.
     */
    @GetMapping("/prodotti/{idCarrello}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<org.springframework.data.domain.Page<CarrelloProdottoResponseDTO>> trovaProdottiDelCarrello(
            @PathVariable UUID idCarrello,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Richiesta prodotti del carrello con id: {}", idCarrello);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(
                carrelloProdottoService.trovaProdottiDelCarrello(idCarrello, pageable).map(carrelloProdottoMapper::toDTO));
    }

    /**
     * Aggiorna la quantità di un prodotto già presente nel carrello.
     * Se la nuova quantità è 0, il prodotto viene rimosso automaticamente.
     *
     * @param idCarrelloProdotto L'ID della riga carrello da aggiornare.
     * @param quantita           La nuova quantità desiderata (0 = rimozione).
     * @return La riga carrello aggiornata.
     */
    @PutMapping("/{idCarrelloProdotto}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CarrelloProdottoResponseDTO> aggiornaQuantita(
            @PathVariable UUID idCarrelloProdotto,
            @RequestParam Integer quantita) {
        log.info("Aggiornamento quantità per carrello-prodotto id: {}, nuova quantità: {}",
                idCarrelloProdotto, quantita);
        return ResponseEntity.ok(
                carrelloProdottoMapper.toDTO(
                        carrelloProdottoService.aggiornaQuantita(idCarrelloProdotto, quantita)));
    }

    /**
     * Rimuove fisicamente una riga prodotto dal carrello.
     *
     * @param idCarrelloProdotto L'ID della riga carrello da eliminare.
     * @return Risposta vuota con status HTTP 204 (No Content).
     */
    // FIX BUG 2: firma corretta da ResponseEntity<CarrelloProdottoResponseDTO> a ResponseEntity<Void>
    @DeleteMapping("/{idCarrelloProdotto}/rimuovi")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> rimuoviProdottoDalCarrello(
            @PathVariable UUID idCarrelloProdotto) {
        log.info("Rimozione prodotto dal carrello, carrello-prodotto id: {}", idCarrelloProdotto);
        carrelloProdottoService.rimuoviProdottoDalCarrello(idCarrelloProdotto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calcola il totale economico di un carrello sommando (prezzo × quantità) per ogni riga.
     *
     * @param idCarrello L'ID del carrello di cui calcolare il totale.
     * @return Il totale del carrello come BigDecimal.
     */
    // FIX BUG 3: path corretto da /{idUtente} a /{idCarrello}, variabile rinominata coerentemente
    @GetMapping("/{idCarrello}/totale")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> calcolaTotaleCarrello(@PathVariable UUID idCarrello) {
        log.info("Calcolo totale per carrello con id: {}", idCarrello);
        return ResponseEntity.ok(
                carrelloProdottoService.calcolaTotaleCarrello(idCarrello));
    }

}// CarrelloProdottoController
