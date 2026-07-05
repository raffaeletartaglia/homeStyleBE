package org.example.homestylebe.controller;

import org.example.homestylebe.dto.response.ResoResponseDTO;
import org.example.homestylebe.entity.Reso;
import org.example.homestylebe.mapper.ResoMapper;
import org.example.homestylebe.service.ResoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Controller REST per la gestione delle richieste di reso.
 * Permette agli utenti di effettuare resi per i prodotti acquistati
 * e di specificare orari e motivi per il ritiro.
 */
@RestController
@RequestMapping("/api/v1/resi")
@RequiredArgsConstructor
public class ResoController {

    private final ResoService resoService;
    private final ResoMapper resoMapper;

    /**
     * Recupera i dettagli di una specifica pratica di reso.
     *
     * @param idReso L'ID univoco del reso.
     * @return Il DTO con i dettagli del reso.
     */
    @GetMapping("/{idReso}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResoResponseDTO> getResoPerId(@PathVariable UUID idReso) {
        return ResponseEntity.ok(
                resoMapper.toDTO(resoService.trovaResoPerId(idReso))
        );
    }

    /**
     * Recupera tutti i resi nel sistema. (Solo Admin)
     */
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ResoResponseDTO>> getTuttiIResi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataResoPrevista").descending());
        return ResponseEntity.ok(
                resoService.trovaTuttiIResi(pageable).map(resoMapper::toDTO)
        );
    }

    /**
     * Recupera la pratica di reso associata a una specifica riga di un ordine (se esiste).
     *
     * @param idDettaglioOrdine L'ID del dettaglio ordine.
     * @return Il DTO del reso collegato a quel prodotto acquistato.
     */
    @GetMapping("/dettaglio-ordine/{idDettaglioOrdine}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResoResponseDTO> getResoPerDettaglioOrdine(@PathVariable UUID idDettaglioOrdine) {
        return ResponseEntity.ok(
                resoMapper.toDTO(resoService.trovaResoPerDettaglioOrdine(idDettaglioOrdine))
        );
    }

    // ============ CREAZIONE ============

    /**
     * Apre una nuova pratica di reso per una determinata riga di un ordine.
     *
     * @param idDettaglioOrdine La riga dell'ordine da restituire.
     * @param idIndirizzoReso L'ID dell'indirizzo dove il corriere effettuerà il ritiro.
     * @param dataResoPrevista La data desiderata per il ritiro.
     * @param oraRitiroReso L'ora desiderata per il ritiro.
     * @param motivo La causale del reso (es. "Prodotto difettoso").
     * @return Il DTO del reso appena creato.
     */
    @PostMapping("/dettaglio-ordine/{idDettaglioOrdine}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResoResponseDTO> creaReso(
            @PathVariable UUID idDettaglioOrdine,
            @RequestParam UUID idIndirizzoReso,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataResoPrevista,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime oraRitiroReso,
            @RequestParam String motivo) {

        Reso reso = resoService.creaReso(
                idDettaglioOrdine,
                idIndirizzoReso,
                dataResoPrevista,
                oraRitiroReso,
                motivo
        );

        return ResponseEntity.ok(resoMapper.toDTO(reso));
    }

    // ============ MODIFICHE ============

    /**
     * Aggiorna la data e l'ora previste per il ritiro di un reso.
     * Operazione consentita solo se il reso è ancora in uno stato che ammette modifiche (es. IN_ATTESA).
     *
     * @param idReso L'ID del reso da modificare.
     * @param dataRitiro La nuova data di ritiro.
     * @param oraRitiro La nuova ora di ritiro.
     * @return Il DTO del reso aggiornato.
     */
    @PutMapping("/{idReso}/ritiro")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResoResponseDTO> aggiornaDataOraRitiro(
            @PathVariable UUID idReso,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataRitiro,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime oraRitiro) {

        Reso reso = resoService.aggiornaDataOraRitiro(idReso, dataRitiro, oraRitiro);
        return ResponseEntity.ok(resoMapper.toDTO(reso));
    }

    /**
     * Modifica lo stato di un reso (Solo admin).
     */
    @PutMapping("/{idReso}/stato")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResoResponseDTO> modificaStatoReso(
            @PathVariable UUID idReso,
            @RequestParam Reso.StatoReso nuovoStato) {

        Reso reso = resoService.modificaStatoReso(idReso, nuovoStato);
        return ResponseEntity.ok(resoMapper.toDTO(reso));
    }

    /**
     * Annulla una pratica di reso.
     * Può essere usata dall'utente se cambia idea o dall'admin in caso di rifiuto del reso.
     *
     * @param idReso L'ID del reso da annullare.
     * @return Il DTO del reso aggiornato nello stato ANNULLATO.
     */
    @PostMapping("/{idReso}/annulla")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResoResponseDTO> annullaReso(@PathVariable UUID idReso) {
        Reso reso = resoService.annullaReso(idReso);
        return ResponseEntity.ok(resoMapper.toDTO(reso));
    }
}

