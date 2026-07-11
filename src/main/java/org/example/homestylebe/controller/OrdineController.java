package org.example.homestylebe.controller;

import org.example.homestylebe.dto.response.OrdineResponseDTO;
import org.example.homestylebe.entity.Ordine;
import org.example.homestylebe.mapper.OrdineMapper;
import org.example.homestylebe.service.OrdineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Controller REST per la gestione degli ordini.
 * Offre endpoint per la creazione, consultazione e modifica degli ordini,
 * con permessi differenziati per utenti normali e amministratori.
 */
@RestController
@RequestMapping("/api/v1/ordini")
@RequiredArgsConstructor
public class OrdineController {

    private final OrdineService ordineService;
    private final OrdineMapper ordineMapper;

    /**
     * Recupera tutti gli ordini presenti a sistema (senza filtri).
     * Operazione riservata agli amministratori.
     *
     * @return Lista di tutti gli ordini.
     */
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrdineResponseDTO>> getTuttiGliOrdini(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataOrdine").descending());
        return ResponseEntity.ok(
                ordineService.trovaTuttiGliOrdini(pageable).map(ordineMapper::toDTO)
        );
    }

    /**
     * Recupera i dettagli di uno specifico ordine tramite il suo identificativo univoco.
     *
     * @param idOrdine L'ID dell'ordine da recuperare.
     * @return Il DTO dell'ordine richiesto.
     */
    @GetMapping("/{idOrdine}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrdineResponseDTO> getOrdinePerId(@PathVariable UUID idOrdine) {
        return ResponseEntity.ok(
                ordineMapper.toDTO(ordineService.trovaOrdinePerId(idOrdine))
        );
    }

    /**
     * Restituisce lo storico completo degli ordini effettuati da un determinato utente in formato paginato.
     *
     * @param idUtente L'identificativo dell'utente.
     * @return Una pagina di DTO rappresentanti gli ordini dell'utente.
     */
    @GetMapping("/utente/{idUtente}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrdineResponseDTO>> getOrdiniPerUtente(
            @PathVariable UUID idUtente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataOrdine").descending());
        return ResponseEntity.ok(
                ordineService.trovaOrdiniPerIdUtente(idUtente, pageable).map(ordineMapper::toDTO)
        );
    }

    /**
     * Filtra lo storico ordini di un utente in base a uno specifico stato (es. SPEDITO, IN_ELABORAZIONE).
     *
     * @param idUtente L'identificativo dell'utente.
     * @param stato Lo stato dell'ordine per cui filtrare.
     * @return Una lista di DTO degli ordini che corrispondono ai criteri.
     */
    @GetMapping("/utente/{idUtente}/stato")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<OrdineResponseDTO>> getOrdiniPerUtenteEStato(
            @PathVariable UUID idUtente,
            @RequestParam Ordine.StatoOrdine stato) {

        return ResponseEntity.ok(
                ordineMapper.toDTOs(
                        ordineService.trovaOrdiniPerIdUtenteEStatoOrdine(idUtente, stato)
                )
        );
    }

    /**
     * Recupera tutti gli ordini presenti a sistema filtrati per stato.
     * Questa operazione è utile agli amministratori (es. per vedere tutti gli ordini IN_ELABORAZIONE).
     *
     * @param stato Lo stato degli ordini da cercare.
     * @return Lista di ordini corrispondenti allo stato.
     */
    @GetMapping("/stato")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrdineResponseDTO>> getOrdiniPerStato(
            @RequestParam Ordine.StatoOrdine stato) {

        return ResponseEntity.ok(
                ordineMapper.toDTOs(ordineService.trovaOrdiniPerStato(stato))
        );
    }

    /**
     * Crea un nuovo ordine a partire dagli elementi attualmente presenti nel carrello dell'utente.
     * Richiede un indirizzo di spedizione.
     *
     * @param idUtente L'utente che sta effettuando il checkout.
     * @param idIndirizzoSpedizione L'ID dell'indirizzo a cui spedire i prodotti.
     * @return Il DTO del nuovo ordine generato.
     */
    @PostMapping("/utente/{idUtente}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrdineResponseDTO> creaOrdine(
            @PathVariable UUID idUtente,
            @RequestParam UUID idIndirizzoSpedizione) {

        return ResponseEntity.ok(
                ordineMapper.toDTO(ordineService.creaOrdine(idUtente, idIndirizzoSpedizione)
            )
        );
    }

    /**
     * Crea un nuovo ordine per un singolo prodotto senza svuotare il carrello.
     */
    @PostMapping("/utente/{idUtente}/singolo")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrdineResponseDTO> creaOrdineSingolo(
            @PathVariable UUID idUtente,
            @RequestParam UUID idIndirizzoSpedizione,
            @RequestParam UUID idProdotto,
            @RequestParam int quantita) {

        return ResponseEntity.ok(
                ordineMapper.toDTO(ordineService.creaOrdineSingolo(idUtente, idIndirizzoSpedizione, idProdotto, quantita))
        );
    }

    /**
     * Modifica l'indirizzo di spedizione di un ordine.
     * Nota: l'operazione è concessa solo se l'ordine è in stato IN_ELABORAZIONE.
     *
     * @param idOrdine L'ID dell'ordine da modificare.
     * @param idNuovoIndirizzo L'ID del nuovo indirizzo di spedizione.
     * @return L'ordine aggiornato con il nuovo indirizzo.
     */
    @PutMapping("/{idOrdine}/indirizzo")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrdineResponseDTO> modificaIndirizzoSpedizione(
            @PathVariable UUID idOrdine,
            @RequestParam UUID idNuovoIndirizzo) {

        return ResponseEntity.ok(
                ordineMapper.toDTO(ordineService.modificaIndirizzoSpedizione(idOrdine, idNuovoIndirizzo)
                )
        );
    }

    /**
     * Modifica manualmente lo stato di un ordine (es. da IN_ELABORAZIONE a SPEDITO).
     * Questa funzione utilizza una logica di transizione sicura nel service e spetta agli amministratori.
     *
     * @param idOrdine L'ID dell'ordine.
     * @param nuovoStato Il nuovo stato da assegnare.
     * @return L'ordine aggiornato con il nuovo stato.
     */
    @PutMapping("/{idOrdine}/stato")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrdineResponseDTO> modificaStatoOrdine(
            @PathVariable UUID idOrdine,
            @RequestParam Ordine.StatoOrdine nuovoStato) {

        return ResponseEntity.ok(
                ordineMapper.toDTO(
                        ordineService.modificaStatoOrdine(idOrdine, nuovoStato)
                )
        );
    }

    /**
     * Annulla un ordine esistente.
     * Questa operazione è permessa solo se l'ordine non è ancora stato CONSEGNATO o SPEDITO.
     *
     * @param idOrdine L'ID dell'ordine da annullare.
     * @return L'ordine aggiornato in stato ANNULLATO.
     */
    @PostMapping("/{idOrdine}/annulla")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrdineResponseDTO> annullaOrdine(@PathVariable UUID idOrdine) {
        return ResponseEntity.ok(ordineMapper.toDTO(ordineService.annullaOrdine(idOrdine)));
    }
}

