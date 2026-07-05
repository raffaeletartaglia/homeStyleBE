package org.example.homestylebe.controller;

import org.example.homestylebe.service.CarrelloProdottoService;
import org.example.homestylebe.service.CarrelloService;
import lombok.RequiredArgsConstructor;
import org.example.homestylebe.mapper.CarrelloMapper;
import org.example.homestylebe.mapper.CarrelloProdottoMapper;
import org.example.homestylebe.dto.request.CarrelloProdottoRequestDTO;
import org.example.homestylebe.dto.response.CarrelloResponseDTO;
import org.example.homestylebe.dto.response.CarrelloProdottoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Controller per la gestione del carrello degli utenti.
 * Permette di recuperare, svuotare o eliminare il carrello,
 * nonché di gestire l'aggiunta, l'aggiornamento e la rimozione di prodotti.
 */
@RestController
@RequestMapping("api/v1/carrello")
@RequiredArgsConstructor
public class CarrelloController {

    private final CarrelloService carrelloService;
    private final CarrelloProdottoService carrelloProdottoService;

    private final CarrelloMapper carrelloMapper;
    private final CarrelloProdottoMapper carrelloProdottoMapper;

    /**
     * Recupera il carrello attivo associato a uno specifico utente.
     * 
     * @param idUtente L'identificativo univoco dell'utente.
     * @return Una ResponseEntity contenente il DTO del carrello dell'utente.
     */
    @GetMapping("/utente/{idUtente}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CarrelloResponseDTO> getCarrelloAttivoPerUtente(@PathVariable UUID idUtente) {
        // Recupero il carrello dal DB tramite service e lo mappo nel DTO di risposta
        return ResponseEntity.ok(carrelloMapper.toDTO(carrelloService.trovaCarelloPerUtente(idUtente)));
    }

    /**
     * Recupera un carrello specifico tramite il suo identificativo univoco.
     * 
     * @param idCarrello L'identificativo univoco del carrello da recuperare.
     * @return Una ResponseEntity contenente il DTO del carrello richiesto.
     */
    @GetMapping("/{idCarrello}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CarrelloResponseDTO> getCarrelloPerId(@PathVariable UUID idCarrello) {
        return ResponseEntity.ok(carrelloMapper.toDTO(carrelloService.trovaCarrelloPerId(idCarrello)));
    }

    /**
     * Svuota completamente un carrello rimuovendo tutti i prodotti al suo interno.
     * 
     * @param idCarrello L'identificativo del carrello da svuotare.
     * @return Una ResponseEntity vuota con status HTTP 204 (No Content).
     */
    @DeleteMapping("/{idCarrello}/svuota")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> svuotaCarrello(@PathVariable UUID idCarrello) {
        // Il service si occuperà di cancellare tutti gli elementi associati al carrello
        carrelloService.svuotaCarrello(idCarrello);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina fisicamente un carrello dal database.
     * Questa operazione è riservata agli amministratori.
     * 
     * @param idCarrello L'identificativo del carrello da eliminare.
     * @return Una ResponseEntity vuota con status HTTP 204 (No Content).
     */
    @DeleteMapping("/{idCarrello}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminaCarrello(@PathVariable UUID idCarrello) {
        carrelloService.eliminaCarello(idCarrello);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calcola e restituisce il prezzo totale dei prodotti presenti nel carrello.
     * 
     * @param idCarrello L'identificativo del carrello.
     * @return L'importo totale del carrello come BigDecimal.
     */
    @GetMapping("/{idCarrello}/totale")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> calcolaTotale(@PathVariable UUID idCarrello) {
        return ResponseEntity.ok(carrelloProdottoService.calcolaTotaleCarrello(idCarrello));
    }

    /**
     * Recupera la lista dettagliata dei prodotti (e relative quantità) presenti in
     * un carrello, impaginati.
     * 
     * @param idCarrello L'identificativo del carrello.
     * @return Una pagina di DTO rappresentanti le righe del carrello.
     */
    @GetMapping("/{idCarrello}/prodotti")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<CarrelloProdottoResponseDTO>> getProdottiDelCarrello(
            @PathVariable UUID idCarrello,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity
                .ok(carrelloProdottoService.trovaProdottiDelCarrello(idCarrello, pageable).map(carrelloProdottoMapper::toDTO));
    }

    /**
     * Aggiunge uno o più prodotti al carrello di un utente.
     * 
     * @param idUtente   L'identificativo dell'utente proprietario del carrello.
     * @param requestDTO La lista di DTO contenenti gli identificativi dei prodotti
     *                   e le relative quantità da aggiungere.
     * @return Il carrello aggiornato dopo l'aggiunta.
     */
    @PostMapping("/utente/{idUtente}/prodotti")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CarrelloResponseDTO> aggiungiProdottiAlCarrello(
            @PathVariable UUID idUtente,
            @RequestBody List<CarrelloProdottoRequestDTO> requestDTO) {

        // Mappa i DTO in entità e le passa al service per l'inserimento
        return ResponseEntity.ok(
                carrelloMapper.toDTO(
                        carrelloService.aggiuntaProdottiCarrello(
                                idUtente, carrelloProdottoMapper.toEntities(requestDTO))));
    }

    /**
     * Aggiorna la quantità di uno specifico prodotto all'interno del carrello.
     * 
     * @param idCarrelloProdotto L'identificativo della riga del carrello
     *                           (associazione carrello-prodotto).
     * @param quantita           La nuova quantità da impostare.
     * @return La riga del carrello aggiornata.
     */
    @PutMapping("/prodotti/{idCarrelloProdotto}/quantita")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CarrelloProdottoResponseDTO> aggiornaQuantita(
            @PathVariable UUID idCarrelloProdotto,
            @RequestParam Integer quantita) {

        return ResponseEntity.ok(
                carrelloProdottoMapper.toDTO(
                        (carrelloProdottoService.aggiornaQuantita(idCarrelloProdotto, quantita))));
    }

    /**
     * Rimuove un singolo prodotto dal carrello.
     * 
     * @param idCarrelloProdotto L'identificativo della riga del carrello da
     *                           rimuovere.
     * @return Una ResponseEntity vuota con status HTTP 204 (No Content).
     */
    @DeleteMapping("/prodotti/{idCarrelloProdotto}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> rimuoviProdottoDalCarrello(@PathVariable UUID idCarrelloProdotto) {
        carrelloProdottoService.rimuoviProdottoDalCarrello(idCarrelloProdotto);
        return ResponseEntity.noContent().build();
    }
}
