package org.example.homestylebe.controller;

import org.example.homestylebe.dto.request.ModalitaPagamentoRequestDTO;
import org.example.homestylebe.dto.response.ModalitaPagamentoResponseDTO;
import org.example.homestylebe.mapper.ModalitaPagamentoMapper;
import org.example.homestylebe.service.ModalitaPagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller per la gestione delle modalità di pagamento previste dal sistema
 * (es. Bonifico, PayPal, Contrassegno, ecc.).
 * Operazioni di lettura accessibili a tutti, scritture riservate ad ADMIN.
 */
@RestController
@RequestMapping("/api/v1/modalita-pagamento")
@RequiredArgsConstructor
public class ModalitaPagamentoController {

    private final ModalitaPagamentoMapper modalitaPagamentoMapper;
    private final ModalitaPagamentoService modalitaPagamentoService;

    /**
     * Restituisce tutte le modalità di pagamento configurate.
     *
     * @return Una lista di DTO rappresentanti le modalità di pagamento.
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ModalitaPagamentoResponseDTO>> getAllModalitaPagamento() {
        return ResponseEntity.ok(
                modalitaPagamentoMapper.toDTOs(modalitaPagamentoService.getAllModalitaPagamento())
        );
    }

    /**
     * Recupera una specifica modalità di pagamento tramite il suo ID.
     *
     * @param id L'identificativo univoco della modalità di pagamento.
     * @return I dettagli della modalità di pagamento.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ModalitaPagamentoResponseDTO> getModalitaPagamentoById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                modalitaPagamentoMapper.toDTO(
                        modalitaPagamentoService.getModalitaPagamentoById(id)
                )
        );
    }

    /**
     * Crea una nuova modalità di pagamento (riservato ADMIN).
     *
     * @param requestDTO I dati della nuova modalità da creare.
     * @return La modalità di pagamento appena creata.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModalitaPagamentoResponseDTO> creaModalitaPagamento(
            @RequestBody ModalitaPagamentoRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(
                modalitaPagamentoMapper.toDTO(
                        modalitaPagamentoService.creaModalitaPagamento(
                                modalitaPagamentoMapper.toEntity(requestDTO)
                        )
                )
        );
    }

    /**
     * Modifica una modalità di pagamento esistente (riservato ADMIN).
     *
     * @param id L'ID della modalità di pagamento da aggiornare.
     * @param requestDTO I nuovi dati della modalità di pagamento.
     * @return La modalità di pagamento aggiornata.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModalitaPagamentoResponseDTO> modificaModalitaPagamento(
            @PathVariable UUID id,
            @RequestBody ModalitaPagamentoRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(
                modalitaPagamentoMapper.toDTO(
                        modalitaPagamentoService.modificaModalitaPagamento(
                                id,
                                modalitaPagamentoMapper.toEntity(requestDTO)
                        )
                )
        );
    }

    /**
     * Elimina una modalità di pagamento dal sistema (riservato ADMIN).
     *
     * @param id L'ID della modalità di pagamento da rimuovere.
     * @return Risposta vuota con status HTTP 204.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteModalitaPagamento(
            @PathVariable UUID id
    ) {
        modalitaPagamentoService.deleteModalitaPagamento(id);
        return ResponseEntity.noContent().build();
    }

}

