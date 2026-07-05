package org.example.homestylebe.controller;
import org.example.homestylebe.dto.request.CartaPagamentoRequestDTO;
import org.example.homestylebe.dto.response.CartaPagamentoResponseDTO;
import org.example.homestylebe.entity.CartaPagamento;
import org.example.homestylebe.mapper.CartaPagamentoMapper;
import org.example.homestylebe.service.CartaPagamentoService;
import org.example.homestylebe.service.UtenteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione delle carte di pagamento degli utenti.
 * Consente di aggiungere, recuperare ed eliminare le carte di credito/debito.
 */
@RestController
@RequestMapping("/api/v1/carta-pagamento")
@RequiredArgsConstructor
@Slf4j
public class CartaPagamentoController {

    private final CartaPagamentoMapper cartaPagamentoMapper;
    private final CartaPagamentoService cartaPagamentoService;
    private final UtenteService utenteService;

    /**
     * Aggiunge una nuova carta di pagamento per l'utente.
     *
     * @param requestDTO Il DTO contenente i dati della nuova carta.
     * @return La carta di pagamento appena creata e salvata.
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartaPagamentoResponseDTO> addCartaPagamento(
            @RequestBody CartaPagamentoRequestDTO requestDTO
    ) {
        log.info("Ricevuta richiesta per aggiungere carta: {}", requestDTO);
        CartaPagamento entity = cartaPagamentoMapper.toEntity(requestDTO);
        
        var loggedUser = utenteService.getFromLoggedUser();
        log.info("Utente recuperato dal token: id={}, email={}", loggedUser.getId(), loggedUser.getEmail());
        
        // Forza l'utente autenticato per evitare manipolazioni
        entity.setUtente(loggedUser);

        return ResponseEntity.ok(
                cartaPagamentoMapper.toDTO(
                        cartaPagamentoService.addCartaPagamento(entity)
                )
        );
    }

    /**
     * Recupera tutte le carte di pagamento associate a un utente.
     *
     * @param utenteId L'ID dell'utente di cui recuperare le carte.
     * @return Una lista di carte di pagamento appartenenti all'utente.
     */
    @GetMapping("/utente/{utenteId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CartaPagamentoResponseDTO>> getCarteByUtente(
            @PathVariable UUID utenteId
    ) {
        // Risolve il keycloakId in id del DB (il frontend manda il sub del JWT)
        var loggedUser = utenteService.getFromLoggedUser();
        UUID resolvedId = (loggedUser.getKeycloakId() != null
                && loggedUser.getKeycloakId().equals(utenteId.toString()))
                ? loggedUser.getId()
                : utenteId;
        log.info("getCarteByUtente: utenteId ricevuto={}, resolvedId={}", utenteId, resolvedId);
        return ResponseEntity.ok(
                cartaPagamentoMapper.toDTOs(
                        cartaPagamentoService.getCarteByUtente(resolvedId)
                )
        );
    }

    /**
     * Recupera i dettagli di una singola carta di pagamento tramite il suo ID.
     *
     * @param cartaId L'identificativo univoco della carta.
     * @return I dettagli della carta di pagamento.
     */
    @GetMapping("/{cartaId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CartaPagamentoResponseDTO> getCartaById(
            @PathVariable UUID cartaId
    ) {
        return ResponseEntity.ok(
                cartaPagamentoMapper.toDTO(
                        cartaPagamentoService.getCartaById(cartaId)
                )
        );
    }

    /**
     * Elimina fisicamente una carta di pagamento dal sistema.
     *
     * @param idCartaPagamento L'identificativo della carta da eliminare.
     * @return Una ResponseEntity vuota con status HTTP 204 (No Content).
     */
    @DeleteMapping("/{idCartaPagamento}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteCartaPagamento(@PathVariable UUID idCartaPagamento) {
        cartaPagamentoService.deleteCartaPagamento(idCartaPagamento);
        return ResponseEntity.noContent().build();
    }
    /**
     * Imposta o rimuove una carta di pagamento come predefinita.
     * Usa PATCH per aggiornare solo il campo isDefault.
     *
     * @param idCartaPagamento L'identificativo della carta.
     * @param predefinita true per impostare come predefinita, false per rimuoverla.
     * @return La carta aggiornata.
     */
    @PatchMapping("/{idCartaPagamento}/predefinita")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartaPagamentoResponseDTO> togglePredefinita(
            @PathVariable UUID idCartaPagamento,
            @RequestParam(defaultValue = "true") boolean predefinita
    ) {
        log.info("Toggle predefinita carta={} -> {}", idCartaPagamento, predefinita);
        return ResponseEntity.ok(
                cartaPagamentoMapper.toDTO(
                        cartaPagamentoService.updateCartaPagamento(idCartaPagamento, predefinita)
                )
        );
    }

    /**
     * Modifica una carta di pagamento esistente.
     *
     * @param idCartaPagamento L'identificativo della carta.
     * @param requestDTO Il DTO con i dati aggiornati.
     * @return La carta aggiornata.
     */
    @PutMapping("/{idCartaPagamento}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartaPagamentoResponseDTO> updateCartaPagamento(
            @PathVariable UUID idCartaPagamento,
            @RequestBody CartaPagamentoRequestDTO requestDTO
    ) {
        CartaPagamento entity = cartaPagamentoMapper.toEntity(requestDTO);
        return ResponseEntity.ok(
                cartaPagamentoMapper.toDTO(
                        cartaPagamentoService.updateCartaPagamento(idCartaPagamento, entity)
                )
        );
    }

}
