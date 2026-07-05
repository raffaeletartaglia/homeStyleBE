package org.example.homestylebe.controller;

import org.example.homestylebe.dto.response.MovimentoMagazzinoResponseDTO;
import org.example.homestylebe.mapper.MovimentoMagazzinoMapper;
import org.example.homestylebe.service.MovimentoMagazzinoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller per la tracciabilità dei movimenti di magazzino.
 * Offre endpoint riservati agli amministratori per monitorare lo storico
 * dei movimenti associati a prodotti, ordini o resi.
 */
@RestController
@RequestMapping("/api/v1/movimento-magazzino")
@RequiredArgsConstructor
public class MovimentoMagazzinoController {

    private final MovimentoMagazzinoMapper movimentoMagazzinoMapper;
    private final MovimentoMagazzinoService movimentoMagazzinoService;

    /**
     * Recupera lo storico dei movimenti di magazzino per un determinato prodotto.
     *
     * @param prodottoId L'identificativo del prodotto da analizzare.
     * @return Lista dei movimenti di magazzino (es. carichi/scarichi) relativi al prodotto.
     */
    @GetMapping("/prodotto/{prodottoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovimentoMagazzinoResponseDTO>> getMovimentiByProdotto(
            @PathVariable UUID prodottoId
    ) {
        return ResponseEntity.ok(
                movimentoMagazzinoMapper.toDTOs(
                        movimentoMagazzinoService.getMovimentiByProdotto(prodottoId)
                )
        );
    }

    /**
     * Recupera lo storico dei movimenti di magazzino innescati da un ordine.
     *
     * @param ordineId L'identificativo dell'ordine.
     * @return Lista dei movimenti legati all'evasione dell'ordine.
     */
    @GetMapping("/ordine/{ordineId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovimentoMagazzinoResponseDTO>> getMovimentiByOrdine(
            @PathVariable UUID ordineId
    ) {
        return ResponseEntity.ok(
                movimentoMagazzinoMapper.toDTOs(
                        movimentoMagazzinoService.getMovimentiByOrdine(ordineId)
                )
        );
    }

    /**
     * Recupera lo storico dei movimenti di magazzino causati da un reso.
     *
     * @param resoId L'identificativo del reso.
     * @return Lista dei movimenti (es. re-ingresso in magazzino) generati dal reso.
     */
    @GetMapping("/reso/{resoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovimentoMagazzinoResponseDTO>> getMovimentiByReso(
            @PathVariable UUID resoId
    ) {
        return ResponseEntity.ok(
                movimentoMagazzinoMapper.toDTOs(
                        movimentoMagazzinoService.getMovimentiByReso(resoId)
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<org.springframework.data.domain.Page<MovimentoMagazzinoResponseDTO>> getTuttiMovimenti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                movimentoMagazzinoService.getTuttiMovimenti(page, size)
                        .map(movimentoMagazzinoMapper::toDTO)
        );
    }

    @PostMapping("/rifornimento")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovimentoMagazzinoResponseDTO> aggiungiRifornimentoManuale(
            @RequestParam UUID prodottoId,
            @RequestParam Integer quantita,
            @RequestParam(required = false) String note
    ) {
        return ResponseEntity.ok(
                movimentoMagazzinoMapper.toDTO(
                        movimentoMagazzinoService.aggiungiRifornimentoManuale(prodottoId, quantita, note)
                )
        );
    }

}
