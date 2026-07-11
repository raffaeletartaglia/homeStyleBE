package org.example.homestylebe.controller;

import org.example.homestylebe.dto.request.UtenteRequestDTO;
import org.example.homestylebe.dto.response.UtenteResponseDTO;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.mapper.UtenteMapper;
import org.example.homestylebe.service.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST per la gestione del profilo dell'utente autenticato.
 *
 * Espone endpoint per recuperare e aggiornare i dati del profilo
 * collegati all'utente autenticato tramite Keycloak.
 */
@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;
    private final UtenteMapper utenteMapper;

    /**
     * Crea o aggiorna il profilo dell'utente autenticato.
     * <p>
     * I dati dell'utente (nome, cognome, ecc.) vengono presi dal body della
     * richiesta,
     * mentre l'identità (Keycloak user id, email, ecc.) viene estratta dal token
     * JWT
     * dell'utente autenticato.
     *
     * @param jwt              il token JWT dell'utente autenticato
     * @param utenteRequestDTO DTO con i dati del profilo da creare/aggiornare
     * @return ResponseEntity contenente il profilo aggiornato dell'utente
     */
    @PostMapping("/me")
    public ResponseEntity<UtenteResponseDTO> salvaProfilo(@AuthenticationPrincipal Jwt jwt,
            @RequestBody UtenteRequestDTO utenteRequestDTO) {
        return ResponseEntity.ok(
                utenteMapper.toDTO(utenteService.creaOAggiornaProfilo(jwt, utenteMapper.toEntity(utenteRequestDTO))));
    }

    /**
     * Recupera il profilo dell'utente autenticato dal database.
     *
     * @return ResponseEntity contenente il profilo dell'utente
     */
    @GetMapping("/me")
    public ResponseEntity<UtenteResponseDTO> getProfilo() {
        return ResponseEntity.ok(utenteMapper.toDTO(utenteService.getFromLoggedUser()));
    }

    /**
     * Elimina il profilo dell'utente autenticato sia dal database locale che da Keycloak.
     *
     * @return ResponseEntity vuota
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> eliminaProfilo() {
        utenteService.eliminaProfilo();
        return ResponseEntity.noContent().build();
    }
}// UtenteController
