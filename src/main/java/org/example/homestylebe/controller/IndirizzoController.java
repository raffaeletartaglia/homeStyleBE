package org.example.homestylebe.controller;

import org.example.homestylebe.dto.request.IndirizzoRequestDTO;
import org.example.homestylebe.dto.response.IndirizzoResponseDTO;
import org.example.homestylebe.entity.Indirizzo;
import org.example.homestylebe.entity.Indirizzo.Tipo;
import org.example.homestylebe.mapper.IndirizzoMapper;
import org.example.homestylebe.service.IndirizzoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione degli indirizzi (di spedizione o
 * fatturazione).
 * Consente ad utenti e amministratori di ricercare, aggiungere, modificare
 * ed eliminare gli indirizzi memorizzati nel sistema.
 */
@RestController
@RequestMapping("/api/v1/indirizzi")
@RequiredArgsConstructor
public class IndirizzoController {

        private final IndirizzoMapper indirizzoMapper;
        private final IndirizzoService indirizzoService;

        /**
         * Recupera i dettagli di uno specifico indirizzo tramite il suo ID.
         *
         * @param idIndirizzo L'ID univoco dell'indirizzo.
         * @return Il DTO dell'indirizzo richiesto.
         */
        @GetMapping("/{idIndirizzo}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<IndirizzoResponseDTO> trovareIndirizzoDallId(
                        @PathVariable UUID idIndirizzo) {
                return ResponseEntity.ok(
                                indirizzoMapper.toDTO(
                                                indirizzoService.trovareIndirizzoDallId(idIndirizzo)));
        }

        /**
         * Restituisce tutti gli indirizzi associati a un determinato utente.
         *
         * @param idUtente L'ID dell'utente.
         * @return Una lista di tutti gli indirizzi dell'utente.
         */
        @GetMapping("/utente/{idUtente}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<IndirizzoResponseDTO>> trovaTuttiGliIndirizziDallIdUtente(
                        @PathVariable UUID idUtente) {
                return ResponseEntity.ok(
                                indirizzoMapper.toDTOs(
                                                indirizzoService.trovaTuttiGliIndirizziDallIdUtente(idUtente)));
        }

        /**
         * Filtra gli indirizzi di un utente in base al tipo (es. SPEDIZIONE o
         * FATTURAZIONE).
         *
         * @param idUtente L'ID dell'utente.
         * @param tipo     Il tipo di indirizzo da cercare.
         * @return Una lista filtrata di indirizzi.
         */
        @GetMapping("/utente/{idUtente}/{tipo}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<IndirizzoResponseDTO>> trovareIndirizziPerTipo(
                        @PathVariable UUID idUtente,
                        @PathVariable String tipo) {
                return ResponseEntity.ok(
                                indirizzoMapper.toDTOs(
                                                indirizzoService.trovareIndirizziPerTipo(
                                                                idUtente,
                                                                Tipo.valueOf(tipo))));
        }

        /**
         * Recupera l'elenco globale di tutti gli indirizzi a sistema.
         * Operazione riservata agli amministratori.
         *
         * @return Lista completa di tutti gli indirizzi nel database.
         */
        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<IndirizzoResponseDTO>> prendiTuttiGliIndirizzi() {
                return ResponseEntity.ok(
                                indirizzoMapper.toDTOs(
                                                indirizzoService.prendiTuttiGliIndirizzi()));
        }

        /**
         * Crea un nuovo indirizzo e lo associa a un utente.
         *
         * @param idUtente   L'utente a cui assegnare il nuovo indirizzo.
         * @param requestDTO I dati del nuovo indirizzo.
         * @return L'indirizzo salvato.
         */
        @PostMapping("/utente/{idUtente}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<IndirizzoResponseDTO> aggiungiUnIndirizzo(
                        @PathVariable UUID idUtente,
                        @RequestBody IndirizzoRequestDTO requestDTO) {
                return ResponseEntity.ok(
                                indirizzoMapper.toDTO(
                                                indirizzoService.aggiungiUnIndirizzo(
                                                                idUtente,
                                                                indirizzoMapper.toEntity(requestDTO))));
        }

        /**
         * Aggiunge una lista multipla di indirizzi a un utente in una sola operazione.
         *
         * @param idUtente    L'utente a cui assegnare gli indirizzi.
         * @param requestDTOs Lista di indirizzi da creare.
         * @return La lista degli indirizzi creati, o BadRequest se l'input è
         *         nullo/vuoto.
         */
        @PostMapping("/utente/{idUtente}/lista")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<IndirizzoResponseDTO>> aggiungiIndirizzi(
                        @PathVariable UUID idUtente,
                        @RequestBody List<IndirizzoRequestDTO> requestDTOs) {
                if (requestDTOs == null || requestDTOs.isEmpty()) {
                        return ResponseEntity.badRequest().build();
                }
                return ResponseEntity.ok(
                                indirizzoMapper.toDTOs(
                                                indirizzoService.aggiungiIndirizzi(
                                                                idUtente,
                                                                requestDTOs.stream()
                                                                                .map(indirizzoMapper::toEntity)
                                                                                .toList())));
        }

        /**
         * Modifica le informazioni di un indirizzo esistente appartenente a un utente.
         * 
         * @param idUtente    L'utente proprietario dell'indirizzo.
         * @param idIndirizzo L'ID dell'indirizzo da modificare.
         * @param requestDTO  I nuovi dati.
         * @return L'indirizzo aggiornato.
         */
        @PutMapping("/utente/{idUtente}/indirizzo/{idIndirizzo}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<IndirizzoResponseDTO> modificaIndirizzo(
                        @PathVariable UUID idUtente,
                        @PathVariable UUID idIndirizzo,
                        @RequestBody IndirizzoRequestDTO requestDTO) {
                return ResponseEntity.ok(
                                indirizzoMapper.toDTO(
                                                indirizzoService.modificaIndirizzo(
                                                                idIndirizzo,
                                                                idUtente,
                                                                indirizzoMapper.toEntity(requestDTO))));
        }

        /**
         * Elimina fisicamente un indirizzo dal database in base al suo ID.
         *
         * @param idIndirizzo L'ID dell'indirizzo da cancellare.
         * @return Risposta vuota con status HTTP 204.
         */
        @DeleteMapping("/{idIndirizzo}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<Void> eliminaIndirizzo(
                        @PathVariable UUID idIndirizzo) {
                indirizzoService.eliminaIndirizzo(idIndirizzo);
                return ResponseEntity.noContent().build();
        }

        /**
         * Elimina un indirizzo assicurandosi che appartenga allo specifico utente
         * indicato.
         *
         * @param idUtente    L'ID dell'utente.
         * @param idIndirizzo L'ID dell'indirizzo.
         * @return Risposta vuota con status HTTP 204.
         */
        @DeleteMapping("/utente/{idUtente}/indirizzo/{idIndirizzo}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<Void> eliminaGliIndirizziDiUnUtente(
                        @PathVariable UUID idUtente,
                        @PathVariable UUID idIndirizzo) {
                indirizzoService.eliminaUnIndirizzoDiUnUtente(
                                idUtente,
                                idIndirizzo);
                return ResponseEntity.noContent().build();
        }

        /**
         * Cancella tutti gli indirizzi salvati per un singolo utente.
         *
         * @param idUtente L'utente di cui svuotare la rubrica indirizzi.
         * @return Risposta vuota con status HTTP 204.
         */
        @DeleteMapping("/utente/{idUtente}")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<Void> eliminaTuttiGliIndirizziDiUnUtente(
                        @PathVariable UUID idUtente) {
                indirizzoService.eliminaTuttiGliIndirizziDiUnUtente(idUtente);
                return ResponseEntity.noContent().build();
        }

}
