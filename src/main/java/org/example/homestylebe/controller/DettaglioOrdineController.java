package org.example.homestylebe.controller;

import org.example.homestylebe.dto.response.DettaglioOrdineResponseDTO;
import org.example.homestylebe.mapper.DettaglioOrdineMapper;
import org.example.homestylebe.service.DettaglioOrdineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione dei dettagli ordine.
 * Permette di recuperare i dettagli di un ordine specifico, di un prodotto, o per ID.
 */
@RestController
@RequestMapping("/api/v1/dettaglio-ordine")
@RequiredArgsConstructor
public class DettaglioOrdineController {

    private final DettaglioOrdineMapper dettaglioOrdineMapper;
    private final DettaglioOrdineService dettaglioOrdineService;

    /**
     * Recupera un singolo dettaglio ordine tramite il suo identificativo univoco.
     *
     * @param idDettaglioOrdine L'identificativo del dettaglio ordine.
     * @return Una ResponseEntity contenente il DTO del dettaglio ordine trovato.
     */
    @GetMapping("/{idDettaglio}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DettaglioOrdineResponseDTO> trovaDettaglioPerId(
            @PathVariable("idDettaglio") UUID idDettaglioOrdine) {
        return ResponseEntity.ok(
                dettaglioOrdineMapper.toDTO(
                        dettaglioOrdineService.trovaDettaglioPerId(idDettaglioOrdine)
                )
        );
    }

    /**
     * Recupera tutti i dettagli associati a un determinato ordine.
     *
     * @param idOrdine L'ID dell'ordine di cui recuperare i dettagli.
     * @return Una lista di DTO dei dettagli ordine.
     */
    // FIX BUG 4: convertito da GET /ordine + @RequestBody a GET /ordine/{idOrdine} + @PathVariable
    @GetMapping("/ordine/{idOrdine}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<DettaglioOrdineResponseDTO>> trovaDettagliPerOrdine(
            @PathVariable UUID idOrdine) {
        return ResponseEntity.ok(
                dettaglioOrdineMapper.toDTOs(
                        dettaglioOrdineService.trovaDettagliPerOrdine(idOrdine)
                )
        );
    }

    /**
     * Recupera tutti i dettagli ordine che contengono un determinato prodotto.
     *
     * @param idProdotto L'ID del prodotto.
     * @return Una lista di DTO dei dettagli ordine contenenti il prodotto specificato.
     */
    // FIX BUG 4: convertito da GET /prodotto + @RequestBody a GET /prodotto/{idProdotto} + @PathVariable
    @GetMapping("/prodotto/{idProdotto}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<DettaglioOrdineResponseDTO>> trovaDettagliPerProdotto(
            @PathVariable UUID idProdotto) {
        return ResponseEntity.ok(
                dettaglioOrdineMapper.toDTOs(
                        dettaglioOrdineService.trovaDettagliPerProdotto(idProdotto)
                )
        );
    }
}
