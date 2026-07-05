package org.example.homestylebe.controller;

import org.example.homestylebe.dto.response.SpedizioneResponseDTO;
import org.example.homestylebe.entity.Spedizione;
import org.example.homestylebe.mapper.SpedizioneMapper;
import org.example.homestylebe.service.SpedizioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione delle spedizioni.
 * Offre funzionalità per monitorare lo stato delle spedizioni da parte degli utenti
 * e per la loro gestione operativa (creazione, aggiornamento tracking) da parte degli admin.
 */
@RestController
@RequestMapping("/api/v1/spedizioni")
@RequiredArgsConstructor
public class SpedizioneController {

    private final SpedizioneService spedizioneService;
    private final SpedizioneMapper spedizioneMapper;

    /**
     * Recupera i dettagli di una spedizione specifica tramite il suo ID.
     *
     * @param idSpedizione L'ID univoco della spedizione.
     * @return DTO contenente le informazioni della spedizione.
     */
    @GetMapping("/{idSpedizione}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SpedizioneResponseDTO> getSpedizionePerId(@PathVariable UUID idSpedizione) {
        return ResponseEntity.ok(
                spedizioneMapper.toDTO(spedizioneService.trovaSpedizionePerId(idSpedizione))
        );
    }

    /**
     * Restituisce tutte le spedizioni collegate a un determinato ordine (storico spedizioni o colli multipli).
     *
     * @param idOrdine L'ID dell'ordine.
     * @return Lista di spedizioni associate all'ordine.
     */
    @GetMapping("/ordine/{idOrdine}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SpedizioneResponseDTO>> getSpedizioniPerOrdine(@PathVariable UUID idOrdine) {
        return ResponseEntity.ok(
                spedizioneMapper.toDTOs(spedizioneService.trovaSpedizioniPerOrdine(idOrdine))
        );
    }

    // ================== CREAZIONE ==================

    /**
     * Crea una nuova spedizione (es. genera la lettera di vettura).
     * L'operazione è riservata agli amministratori e imposta lo stato iniziale (tipicamente PREPARAZIONE).
     *
     * @param idOrdine L'ordine a cui associare la spedizione.
     * @param corriere Il nome del corriere incaricato (es. "Bartolini", "DHL").
     * @param codiceTracking (Opzionale) Il codice di tracciamento.
     * @return DTO della spedizione appena creata.
     */
    @PostMapping("/ordine/{idOrdine}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpedizioneResponseDTO> creaSpedizione(
            @PathVariable UUID idOrdine,
            @RequestParam String corriere,
            @RequestParam(required = false) String codiceTracking) {

        Spedizione spedizione = spedizioneService.creaSpedizione(idOrdine, corriere, codiceTracking);
        return ResponseEntity.ok(spedizioneMapper.toDTO(spedizione));
    }

    // ================== MODIFICA STATO ==================

    /**
     * Aggiorna lo stato operativo di una spedizione (es. SPEDITO, CONSEGNATO).
     * Riservato agli admin.
     * Vincoli:
     * - Annullamento concesso solo se la spedizione è ancora in PREPARAZIONE.
     * - Impossibile modificare spedizioni già in stato ANNULLATA o CONSEGNATA.
     *
     * @param idSpedizione L'ID della spedizione.
     * @param nuovoStato Il nuovo stato da assegnare.
     * @return DTO della spedizione aggiornata.
     */
    @PutMapping("/{idSpedizione}/stato")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpedizioneResponseDTO> aggiornaStatoSpedizione(
            @PathVariable UUID idSpedizione,
            @RequestParam Spedizione.StatoSpedizione nuovoStato) {

        Spedizione spedizione = spedizioneService.aggiornaStatoSpedizione(idSpedizione, nuovoStato);
        return ResponseEntity.ok(spedizioneMapper.toDTO(spedizione));
    }

    /**
     * Recupera tutte le spedizioni (paginato).
     *
     * @param page Pagina da recuperare.
     * @param size Numero di elementi per pagina.
     * @return Pagina di spedizioni.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<org.springframework.data.domain.Page<SpedizioneResponseDTO>> getTutteSpedizioni(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                spedizioneService.getTutteSpedizioni(page, size)
                        .map(spedizioneMapper::toDTO)
        );
    }

    /**
     * Aggiorna i dettagli di una spedizione (corriere, tracking).
     *
     * @param idSpedizione ID della spedizione.
     * @param corriere Nuovo corriere.
     * @param codiceTracking Nuovo tracking.
     * @return Spedizione aggiornata.
     */
    @PutMapping("/{idSpedizione}/dettagli")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpedizioneResponseDTO> aggiornaDettagliSpedizione(
            @PathVariable UUID idSpedizione,
            @RequestParam(required = false) String corriere,
            @RequestParam(required = false) String codiceTracking) {
        Spedizione spedizione = spedizioneService.aggiornaDettagliSpedizione(idSpedizione, corriere, codiceTracking);
        return ResponseEntity.ok(spedizioneMapper.toDTO(spedizione));
    }
}

