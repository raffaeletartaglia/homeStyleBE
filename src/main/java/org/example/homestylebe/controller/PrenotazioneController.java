package org.example.homestylebe.controller;

import org.example.homestylebe.dto.request.PrenotazioneRequestDTO;
import org.example.homestylebe.dto.response.PrenotazioneResponseDTO;
import org.example.homestylebe.entity.Prenotazione;
import org.example.homestylebe.mapper.PrenotazioneMapper;
import org.example.homestylebe.service.PrenotazioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione delle prenotazioni di prodotti.
 * Consente agli utenti di prenotare prodotti esauriti o in arrivo,
 * e agli amministratori di gestire lo stato di queste prenotazioni.
 */
@RestController
@RequestMapping("/api/v1/prenotazione")
@RequiredArgsConstructor
public class PrenotazioneController {

        private final PrenotazioneMapper prenotazioneMapper;
        private final PrenotazioneService prenotazioneService;

        /**
         * Crea una nuova prenotazione per un prodotto da parte di un utente.
         *
         * @param requestDTO I dati della prenotazione (es. ID utente, ID prodotto).
         * @return La prenotazione salvata nel sistema (tipicamente in stato ATTIVA o
         *         IN_ATTESA).
         */
        @PostMapping
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<PrenotazioneResponseDTO> creaPrenotazione(
                        @RequestBody PrenotazioneRequestDTO requestDTO) {
                return ResponseEntity.ok(
                                prenotazioneMapper.toDTO(
                                                prenotazioneService.creaPrenotazione(
                                                                prenotazioneMapper.toEntity(requestDTO))));
        }

        /**
         * Restituisce tutte le prenotazioni effettuate da un determinato utente.
         *
         * @param utenteId L'ID dell'utente che ha fatto le prenotazioni.
         * @return Una lista di prenotazioni dell'utente.
         */
        @GetMapping("/utente/{utenteId}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<PrenotazioneResponseDTO>> getPrenotazioniByUtente(
                        @PathVariable UUID utenteId) {
                return ResponseEntity.ok(
                                prenotazioneMapper.toDTOs(
                                                prenotazioneService.getPrenotazioniByUtente(utenteId)));
        }

        /**
         * Restituisce tutte le prenotazioni effettuate per uno specifico prodotto,
         * indipendentemente dal loro stato o dall'utente.
         *
         * @param prodottoId L'ID del prodotto.
         * @return Una lista di prenotazioni associate al prodotto.
         */
        @GetMapping("/prodotto/{prodottoId}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<PrenotazioneResponseDTO>> getPrenotazioniByProdotto(
                        @PathVariable UUID prodottoId) {
                return ResponseEntity.ok(
                                prenotazioneMapper.toDTOs(
                                                prenotazioneService.getPrenotazioniByProdotto(prodottoId)));
        }

        /**
         * Restituisce solo le prenotazioni "attive" (ancora non evase o annullate) per
         * un prodotto.
         * Particolarmente utile per capire quanta domanda in sospeso c'è per un
         * articolo.
         *
         * @param prodottoId L'ID del prodotto.
         * @return Una lista di prenotazioni attive per il prodotto.
         */
        @GetMapping("/prodotto/{prodottoId}/attive")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<PrenotazioneResponseDTO>> getPrenotazioniAttiveByProdotto(
                        @PathVariable UUID prodottoId) {
                return ResponseEntity.ok(
                                prenotazioneMapper.toDTOs(
                                                prenotazioneService.getPrenotazioniAttiveByProdotto(prodottoId)));
        }

        /**
         * Filtra a livello globale tutte le prenotazioni in base al loro stato.
         * Operazione riservata agli admin per monitoraggio.
         *
         * @param stato Lo stato delle prenotazioni (es. IN_ATTESA, COMPLETATA,
         *              ANNULLATA).
         * @return Lista di prenotazioni che si trovano nello stato specificato.
         */
        @GetMapping("/stato/{stato}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<PrenotazioneResponseDTO>> getPrenotazioniByStato(
                        @PathVariable Prenotazione.Stato stato) {
                return ResponseEntity.ok(
                                prenotazioneMapper.toDTOs(
                                                prenotazioneService.getPrenotazioniByStato(stato)));
        }

        /**
         * Recupera le prenotazioni che prevedono un arrivo o un'evasione a partire da
         * una certa data.
         *
         * @param data La data di riferimento da controllare.
         * @return Lista di prenotazioni imminenti o in arrivo per quella data.
         */
        @GetMapping("/in-arrivo")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<PrenotazioneResponseDTO>> getPrenotazioniInArrivo(
                        @RequestParam LocalDate data) {
                return ResponseEntity.ok(
                                prenotazioneMapper.toDTOs(
                                                prenotazioneService.getPrenotazioniInArrivo(data)));
        }

        /**
         * Consente di annullare una prenotazione esistente.
         * Può essere chiamato dall'utente (se ci ripensa) o dall'admin.
         *
         * @param prenotazioneId L'ID della prenotazione da annullare.
         * @return La prenotazione con lo stato aggiornato ad ANNULLATA.
         */
        @PutMapping("/annulla/{prenotazioneId}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<PrenotazioneResponseDTO> annullaPrenotazione(
                        @PathVariable UUID prenotazioneId) {
                return ResponseEntity.ok(
                                prenotazioneMapper.toDTO(
                                                prenotazioneService.annullaPrenotazione(prenotazioneId)));
        }

        /**
         * Esegue effettivamente la prenotazione (es. quando il prodotto torna
         * disponibile,
         * la prenotazione viene trasformata in ordine o comunque "evasa").
         *
         * @param prenotazioneId L'ID della prenotazione da espletare.
         * @return La prenotazione con lo stato aggiornato.
         */
        @PutMapping("/esegui/{prenotazioneId}")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<PrenotazioneResponseDTO> eseguiPrenotazione(
                        @PathVariable UUID prenotazioneId) {
                return ResponseEntity.ok(
                                prenotazioneMapper.toDTO(
                                                prenotazioneService.eseguiPrenotazione(prenotazioneId)));
        }

}
