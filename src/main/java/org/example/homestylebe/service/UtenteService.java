package org.example.homestylebe.service;

import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.entity.Utente.Ruolo;

import org.example.homestylebe.exception.UtenteException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import org.example.homestylebe.repository.UtenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtenteService {

    private final UtenteRepository utenteRepo;

    /**
     * Recupera il profilo dell'utente autenticato a partire dal SecurityContext.
     *
     * @return DTO con i dati del profilo dell'utente autenticato
     */
    public Utente getFromLoggedUser() {
        log.info("Inizio a cercare i dati dell'utente registrato.");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new UtenteException(ErroreCodice.UTENTE_NON_TROVATO, "Utente non autenticato o token JWT assente");
        }

        String keycloakId = jwt.getSubject();
        return utenteRepo.findUtenteByKeycloakId(keycloakId).orElseGet(() -> {
            log.info("Utente non trovato nel database locale. Creazione automatica da token Keycloak.");
            Utente nuovo = new Utente();
            nuovo.setKeycloakId(keycloakId);
            nuovo.setEmail(jwt.getClaimAsString("email"));
            nuovo.setNome(jwt.getClaimAsString("given_name"));
            nuovo.setCognome(jwt.getClaimAsString("family_name"));
            
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(auth -> auth.equals("ROLE_ADMIN") || auth.equals("ADMIN"));
            nuovo.setRuolo(isAdmin ? Ruolo.ADMIN : Ruolo.USER);
            
            return utenteRepo.save(nuovo);
        });
    }

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Crea un nuovo profilo utente oppure aggiorna quello esistente
     * in base all'identificativo dell'utente presente nel token JWT.
     *
     * @param jwt    token JWT dell'utente autenticato (Keycloak)
     * @param utente entità Utente con i dati del profilo provenienti dal client
     * @return DTO con i dati del profilo dopo salvataggio
     */
    public Utente creaOAggiornaProfilo(Jwt jwt, Utente utente) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Inizio a creare/trovare l'utente");

        String keycloakId = jwt.getSubject();
        if (keycloakId == null) {
            throw new UtenteException(ErroreCodice.UTENTE_NON_TROVATO, "Utente non autenticato");
        }

        String email = jwt.getClaim("email");
        if (email == null) {
            throw new UtenteException(ErroreCodice.UTENTE_NON_TROVATO, "Email non presente nel token");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN") || auth.equals("ADMIN"));

        Utente utenteTrovatoOCreato = utenteRepo.findUtenteByKeycloakId(keycloakId)
                .orElseGet(() -> utenteRepo.findUtenteByEmail(email)
                        .map(esistente -> {
                            log.info("Utente trovato tramite email, aggiorno il keycloakId");
                            esistente.setKeycloakId(keycloakId);
                            return esistente;
                        })
                        .orElseGet(() -> {
                            Utente nuovo = new Utente();
                            nuovo.setKeycloakId(keycloakId);
                            nuovo.setEmail(email); // se vuoi tenerla anche lato app
                            nuovo.setRuolo(isAdmin ? Ruolo.ADMIN : Ruolo.USER);
                            nuovo.setNumeroTelefono(utente.getNumeroTelefono());
                            return nuovo;
                        }));

        utenteTrovatoOCreato.setNome(utente.getNome());
        utenteTrovatoOCreato.setCognome(utente.getCognome());
        utenteTrovatoOCreato.setNumeroTelefono(utente.getNumeroTelefono());

        log.info("Utente creato/trovato.");
        return utenteRepo.save(utenteTrovatoOCreato);
    }

}// UtenteService
