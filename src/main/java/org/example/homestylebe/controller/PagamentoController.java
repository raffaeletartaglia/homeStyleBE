package org.example.homestylebe.controller;

import org.example.homestylebe.dto.request.PagamentoRequestDTO;
import org.example.homestylebe.dto.response.PagamentoResponseDTO;
import org.example.homestylebe.mapper.PagamentoMapper;
import org.example.homestylebe.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.example.homestylebe.entity.Pagamento;
/**
 * Controller REST per la gestione delle transazioni di pagamento.
 * Permette la creazione, l'annullamento e l'esecuzione di pagamenti
 * associati agli ordini, compresa la gestione delle rate.
 */
@RestController
@RequestMapping("/api/v1/pagamento")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoMapper pagamentoMapper;
    private final PagamentoService pagamentoService;

    /**
     * Crea un nuovo record di pagamento (in stato IN_ATTESA).
     *
     * @param requestDTO Il DTO contenente i dettagli della richiesta di pagamento (es. importo, metodo).
     * @return Il DTO del pagamento appena creato.
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PagamentoResponseDTO> creaPagamento(
            @RequestBody PagamentoRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(
                pagamentoMapper.toDTO(
                        pagamentoService.creaPagamento(
                                pagamentoMapper.toEntity(requestDTO)
                        )
                )
        );
    }

    /**
     * Recupera le informazioni dettagliate di un singolo pagamento tramite il suo ID.
     *
     * @param pagamentoId L'identificativo univoco del pagamento.
     * @return Il DTO con le informazioni del pagamento.
     */
    @GetMapping("/{pagamentoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PagamentoResponseDTO> getPagamentoById(
            @PathVariable UUID pagamentoId
    ) {
        return ResponseEntity.ok(
                pagamentoMapper.toDTO(
                        pagamentoService.getPagamentoById(pagamentoId)
                )
        );
    }

    /**
     * Recupera il pagamento associato a un determinato ordine.
     * Utile per controllare lo stato della transazione relativa a un acquisto.
     *
     * @param ordineId L'ID dell'ordine di cui cercare il pagamento.
     * @return Il DTO del pagamento corrispondente all'ordine.
     */
    @GetMapping("/ordine/{ordineId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PagamentoResponseDTO> getPagamentoByOrdine(
            @PathVariable UUID ordineId
    ) {
        return ResponseEntity.ok(
                pagamentoMapper.toDTO(
                        pagamentoService.getPagamentoByOrdine(ordineId)
                )
        );
    }

    /**
     * Annulla un pagamento, portandolo in stato ANNULLATO o FALLITO.
     * Tipicamente chiamato se un utente annulla l'ordine prima della transazione effettiva.
     *
     * @param pagamentoId L'ID del pagamento da annullare.
     * @return Il DTO del pagamento aggiornato.
     */
    @PutMapping("/annulla/{pagamentoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PagamentoResponseDTO> annullaPagamento(
            @PathVariable UUID pagamentoId
    ) {
        return ResponseEntity.ok(
                pagamentoMapper.toDTO(
                        pagamentoService.annullaPagamento(pagamentoId)
                )
        );
    }

    /**
     * Effettua o conferma il pagamento. Questa operazione cambia lo stato
     * della transazione (es. in COMPLETATO) previa validazione della disponibilità fondi (simulata).
     *
     * @param pagamentoId L'ID del pagamento da confermare.
     * @return Il DTO del pagamento aggiornato.
     */
    @PutMapping("/effettua/{pagamentoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PagamentoResponseDTO> effettuaPagamento(
            @PathVariable UUID pagamentoId
    ) {
        return ResponseEntity.ok(
                pagamentoMapper.toDTO(
                        pagamentoService.effettuaPagamento(pagamentoId)
                )
        );
    }

    /**
     * Simula o gestisce il pagamento di una specifica "rata", utile per sistemi buy-now-pay-later.
     *
     * @param pagamentoId L'ID del pagamento rateale.
     * @return Il DTO del pagamento aggiornato.
     */
    @PutMapping("/rata/{pagamentoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PagamentoResponseDTO> pagaRata(
            @PathVariable UUID pagamentoId
    ) {
        return ResponseEntity.ok(
                pagamentoMapper.toDTO(
                        pagamentoService.pagaRata(pagamentoId)
                )
        );
    }

}

