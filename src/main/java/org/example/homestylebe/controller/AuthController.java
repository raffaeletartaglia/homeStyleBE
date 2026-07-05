package org.example.homestylebe.controller;

import org.example.homestylebe.service.TokenBlackListService;
import org.example.homestylebe.exception.TokenNonValidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.example.homestylebe.dto.request.LogoutRequestDTO;

/**
 * Controller che gestisce le operazioni di autenticazione e autorizzazione
 * dell'utente.
 * Permette di recuperare i dati dell'utente loggato e di effettuare il logout.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenBlackListService tokenBlackListService;

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Endpoint per recuperare le informazioni dell'utente attualmente autenticato.
     * Estrae i dati dal token JWT fornito nella richiesta.
     *
     * @param autenticazione L'oggetto di autenticazione contenente il token JWT.
     * @return Una mappa contenente i dettagli dell'utente (id, username, email,
     *         nome, cognome, ruoli).
     * @throws TokenNonValidoException Se il token non è presente o non è valido.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication autenticazione) {
        log.info("Richiesta /me per l'utente: {}", autenticazione.getName());

        // Recupero il JWT dall'oggetto di autenticazione
        Jwt jwt = (Jwt) autenticazione.getPrincipal();

        // Controllo validità del token
        if (jwt == null) {
            log.error("Token dell'utente {} non valido", autenticazione.getName());
            throw new TokenNonValidoException("Il token non è valido");
        }

        log.info("Caricamento delle credenziali");
        // Mappatura dei claims del token per costruire la risposta con le info utente
        Map<String, Object> userInfo = Map.of(
                "id", jwt.getSubject(),
                "username", jwt.getClaimAsString("preferred_username"),
                "email", jwt.getClaimAsString("email") != null ? jwt.getClaimAsString("email") : "N/A",
                "nome", jwt.getClaimAsString("given_name") != null ? jwt.getClaimAsString("given_name") : "N/A",
                "cognome", jwt.getClaimAsString("family_name") != null ? jwt.getClaimAsString("family_name") : "N/A",
                "ruoli", autenticazione.getAuthorities());

        log.info("Richiesta di verifica sull'autenticazione ritornata con successo");
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Endpoint per effettuare il logout dell'utente.
     * Invalida i token di accesso e di refresh forniti, inserendoli in una
     * blacklist.
     *
     * @param logoutRequest  Il DTO contenente l'access token e il refresh token da
     *                       revocare.
     * @param authentication L'oggetto di autenticazione dell'utente che effettua il
     *                       logout.
     * @return Una mappa con un messaggio di conferma del logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody LogoutRequestDTO logoutRequest,
            Authentication authentication) {

        log.info("Richiesta di logout per l'utente: {}", authentication.getName());

        // Revoca i token aggiungendoli alla blacklist tramite il service dedicato
        tokenBlackListService.revokeTokens(
                logoutRequest.getAccessToken(),
                logoutRequest.getRefreshToken());

        log.info("Logout completato per l'utente: {}", authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Logout effettuato con successo"));
    }
}
