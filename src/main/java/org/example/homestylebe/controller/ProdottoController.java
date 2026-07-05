package org.example.homestylebe.controller;

import org.example.homestylebe.dto.request.ProdottoRequestDTO;
import org.example.homestylebe.dto.response.ProdottoResponseDTO;
import org.example.homestylebe.mapper.ProdottoMapper;
import org.example.homestylebe.service.ProdottoService;
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
 * Controller REST per la gestione del catalogo prodotti.
 * Permette la consultazione dei prodotti a tutti gli utenti (e admin),
 * riservando le operazioni di modifica (creazione, aggiornamento,
 * cancellazione)
 * agli amministratori.
 */
@RestController
@RequestMapping("/api/v1/prodotto")
@RequiredArgsConstructor
public class ProdottoController {

        private final ProdottoMapper prodottoMapper;
        private final ProdottoService prodottoService;

        /**
         * Inserisce un nuovo prodotto nel catalogo.
         * Operazione riservata agli amministratori.
         *
         * @param requestDTO I dati del nuovo prodotto (nome, descrizione, prezzo,
         *                   ecc.).
         * @return Il prodotto appena creato.
         */
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ProdottoResponseDTO> creaProdotto(
                        @RequestBody ProdottoRequestDTO requestDTO) {
                return ResponseEntity.ok(
                                prodottoMapper.toDTO(
                                                prodottoService.creaProdotto(
                                                                prodottoMapper.toEntity(requestDTO))));
        }

        /**
         * Recupera i dettagli di un singolo prodotto tramite il suo ID.
         *
         * @param prodottoId L'identificativo univoco del prodotto.
         * @return Le informazioni dettagliate del prodotto.
         */
        @GetMapping("/{prodottoId}")
        public ResponseEntity<ProdottoResponseDTO> getProdottoById(
                        @PathVariable UUID prodottoId) {
                return ResponseEntity.ok(
                                prodottoMapper.toDTO(
                                                prodottoService.getProdottoById(prodottoId)));
        }

        /**
         * Recupera l'elenco completo di tutti i prodotti presenti a catalogo.
         *
         * @return Una lista contenente tutti i prodotti.
         */
        @GetMapping
        public ResponseEntity<Page<ProdottoResponseDTO>> getAllProdotti(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Pageable pageable = PageRequest.of(page, size);
                return ResponseEntity.ok(
                                prodottoService.getAllProdotti(pageable).map(prodottoMapper::toDTO));
        }

        /**
         * Modifica i dati di un prodotto esistente (es. per aggiornare il prezzo o la
         * disponibilità).
         * Operazione riservata agli amministratori.
         *
         * @param prodottoId L'ID del prodotto da modificare.
         * @param requestDTO I nuovi dati del prodotto.
         * @return Il prodotto aggiornato.
         */
        @PutMapping("/{prodottoId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ProdottoResponseDTO> modificaProdotto(
                        @PathVariable UUID prodottoId,
                        @RequestBody ProdottoRequestDTO requestDTO) {
                return ResponseEntity.ok(
                                prodottoMapper.toDTO(
                                                prodottoService.modificaProdotto(
                                                                prodottoId,
                                                                prodottoMapper.toEntity(requestDTO))));
        }

        /**
         * Elimina un prodotto dal catalogo.
         * Operazione riservata agli amministratori.
         *
         * @param prodottoId L'ID del prodotto da eliminare.
         * @return Risposta HTTP vuota (No Content).
         */
        @DeleteMapping("/{prodottoId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteProdotto(
                        @PathVariable UUID prodottoId) {
                prodottoService.deleteProdotto(prodottoId);
                return ResponseEntity.noContent().build();
        }

        /**
         * Ricerca globale prodotti per suggerimenti
         *
         * @param query Testo da cercare
         * @return Lista di suggerimenti prodotti
         */
        @GetMapping("/ricerca-suggerimenti")
        public ResponseEntity<List<org.example.homestylebe.dto.response.ProdottoSuggerimentoDTO>> ricercaSuggerimenti(
                        @RequestParam String query) {
                return ResponseEntity.ok(prodottoService.ricercaSuggerimenti(query));
        }

        /**
         * Filtra i prodotti per stanza, categoria e/o testo di ricerca.
         * Tutti i parametri sono opzionali e combinabili.
         *
         * @param stanzaId    ID della stanza (opzionale)
         * @param categoriaId ID della categoria (opzionale)
         * @param query       Testo di ricerca libero (opzionale)
         * @param page        Numero di pagina (default 0)
         * @param size        Dimensione pagina (default 12)
         * @return Pagina di prodotti filtrati
         */
        @GetMapping("/filtra")
        public ResponseEntity<Page<ProdottoResponseDTO>> filtraProdotti(
                        @RequestParam(required = false) UUID stanzaId,
                        @RequestParam(required = false) UUID categoriaId,
                        @RequestParam(required = false) String query,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size) {
                Pageable pageable = PageRequest.of(page, size);
                return ResponseEntity.ok(
                                prodottoService.getProdottiFiltrati(stanzaId, categoriaId, query, pageable)
                                                .map(prodottoMapper::toDTO));
        }

}
