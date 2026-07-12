package org.example.homestylebe.service;

import org.example.homestylebe.exception.ErroreCodice;
import org.example.homestylebe.entity.Indirizzo;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.repository.IndirizzoRepository;
import org.example.homestylebe.repository.UtenteRepository;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.example.homestylebe.utils.ControlliUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndirizzoService {

    private final IndirizzoRepository inidirizziRepo;
    private final UtenteRepository utenteRepository;
    private final UtenteService utenteService;

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Trova un singolo indirizzo in base al suo id
     * 
     * @param idIndirizzo
     * @return
     */
    public Indirizzo trovareIndirizzoDallId(UUID idIndirizzo) {
        log.info("Cerco l'indirizzo con id: {}", idIndirizzo);
        ControlliUtils.controlloIdValido(idIndirizzo, "indirizzo");

        Indirizzo indirizzo = inidirizziRepo.findById(idIndirizzo).orElseThrow(
                () -> {
                    log.error("Indirizzo non trovato con id: {}", idIndirizzo);
                    return new EntitaNonTrovataException(ErroreCodice.INDIRIZZO_NON_TROVATO);
                });

        log.info("Indirizzo trovato");
        return indirizzo;
    }// trovareIndirizzoDallId

    private UUID resolveUtenteId(UUID idUtente) {
        log.info("resolveUtenteId chiamato con idUtente={}", idUtente);
        
        Utente loggedUser = utenteService.getFromLoggedUser();
        log.info("Utente loggato: id={}, keycloakId={}, ruolo={}", 
                loggedUser.getId(), loggedUser.getKeycloakId(), loggedUser.getRuolo());
        
        // Confronto null-safe: il frontend manda il keycloakId come UUID nel path
        String keycloakId = loggedUser.getKeycloakId();
        if (keycloakId != null && keycloakId.equals(idUtente.toString())) {
            log.info("Match tramite keycloakId, ritorno id DB: {}", loggedUser.getId());
            return loggedUser.getId();
        }
        
        if (loggedUser.getId().equals(idUtente)) {
            log.info("Match tramite id DB diretto");
            return loggedUser.getId();
        }
        
        log.warn("Nessun match diretto. keycloakId={}, idUtente passato={}, id DB={}",
                keycloakId, idUtente, loggedUser.getId());
        
        // Solo un admin può cercare altri utenti
        if (loggedUser.getRuolo() != org.example.homestylebe.entity.Utente.Ruolo.ADMIN) {
            log.error("Utente non admin tenta di accedere a indirizzi altrui");
            throw new org.springframework.security.access.AccessDeniedException("Non autorizzato a gestire indirizzi di altri utenti");
        }

        return utenteRepository.findUtenteByKeycloakId(idUtente.toString())
                .map(Utente::getId)
                .orElse(idUtente);
    }

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Trova tutti gli indirizzi associati a un utente in base all'id dell'utente
     * 
     * @param idUtente
     * @return
     */
    public List<Indirizzo> trovaTuttiGliIndirizziDallIdUtente(UUID paramIdUtente) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Cerco gli indirizzi in base all'id utente: {}", idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");
        Utente utente = utenteRepository.findById(idUtente).orElseThrow(
                () -> {
                    log.error("Utente con id: {}, non trovato", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
                });

        List<Indirizzo> indirizziUtente = inidirizziRepo.findByUtenteAndIsDeletedFalse(utente);

        log.info("Indirizzi dell'utente con id: {}, trovati", idUtente);
        return indirizziUtente;
    }

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Trova tutti gli indirizzi di un utente filtrati per tipo
     * 
     * @param idUtente
     * @param tipo
     * @return
     */
    public List<Indirizzo> trovareIndirizziPerTipo(UUID paramIdUtente, Indirizzo.Tipo tipo) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Cerco gli indirizzi di tipo: {} per l'utente con id: {}", tipo, idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");

        ControlliUtils.controlloTipoIndirizzo(tipo != null ? tipo.toString() : null);

        Utente utenteTrovato = utenteRepository.findById(idUtente).orElseThrow(
                () -> {
                    log.error("Utente con id: {}, non trovato", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
                });

        List<Indirizzo> indirizzi = inidirizziRepo.findByUtenteAndTipoAndIsDeletedFalse(utenteTrovato, tipo);

        log.info("Trovati {} indirizzi di tipo: {} per l'utente con id: {}", indirizzi.size(), tipo, idUtente);
        return indirizzi;
    }// trovareIndirizziPerTipo

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Trova tutti gli indirizzi di tutti gli utenti
     */
    public List<Indirizzo> prendiTuttiGliIndirizzi() {
        log.info("Cerco tutti gli indirizzi");

        List<Indirizzo> indirizzi = inidirizziRepo.findAll();

        log.info("Tutti gli indirizzi sono stati trovati");
        return indirizzi;
    }// prendiTuttiGliIndirizzi

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Aggiunge un singolo indirizzo a un utente
     */
    @Transactional
    public Indirizzo aggiungiUnIndirizzo(UUID paramIdUtente, Indirizzo indirizzo) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Inizio a creare il nuovo indirizzo dell'utente con id: {}", idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");

        Utente utenteTrovato = utenteRepository.findById(idUtente).orElseThrow(
                () -> {
                    log.error("Utente con id: {}, non trovato", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
                });

        log.info("Utente con id: {}, trovato", idUtente);
        ControlliUtils.controlloEsistenzaCampo(indirizzo, "Il nuovo indirizzo che vuoi aggiungere è null");
        ControlliUtils.controlloTipoIndirizzo(indirizzo.getTipo().toString());

        Indirizzo nuovoIndirizzo = generaIndirizzo(indirizzo, utenteTrovato);

        log.info("Nuovo indirizzo creato");
        Indirizzo indirizzoSalvato = inidirizziRepo.save(nuovoIndirizzo);
        
        if (indirizzoSalvato.isDefault()) {
            impostaAltriIndirizziComeNonDefault(utenteTrovato, indirizzoSalvato.getId());
        }
        
        log.info("Nuovo indirizzo salvato con id: {}", indirizzoSalvato.getId());
        return indirizzoSalvato;
    }

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Aggiunge una lista di indirizzi a un utente
     */
    @Transactional
    public List<Indirizzo> aggiungiIndirizzi(UUID paramIdUtente, List<Indirizzo> indirizzi) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Inizio a creare i nuovi indirizzi dell'utente con id: {}", idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");

        Utente utenteTrovato = utenteRepository.findById(idUtente).orElseThrow(
                () -> {
                    log.error("Utente con id: {}, non trovato", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
                });

        log.info("Utente con id: {}, trovato", idUtente);
        List<Indirizzo> nuoviIndirizzi = new ArrayList<>();
        for (Indirizzo indirizzo : indirizzi) {
            ControlliUtils.controlloEsistenzaCampo(indirizzo, "Il nuovo indirizzo che vuoi aggiungere è null");
            ControlliUtils.controlloTipoIndirizzo(indirizzo.getTipo().toString());
            nuoviIndirizzi.add(generaIndirizzo(indirizzo, utenteTrovato));
        }

        log.info("Lista di nuovi indirizzi creata");
        List<Indirizzo> indirizziSalvati = inidirizziRepo.saveAll(nuoviIndirizzi);
        log.info("Nuovi indirizzi salvati");
        return indirizziSalvati;
    }// aggiungiUnIndirizzo

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Modifica un indirizzo esistente
     */
    @Transactional
    public Indirizzo modificaIndirizzo(UUID idIndirizzo, UUID paramIdUtente, Indirizzo indirizzoModificato) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Inizio a modificare l'indirizzo con id: {}, dell'utente con id: {}", idIndirizzo, idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");
        ControlliUtils.controlloIdValido(idIndirizzo, "indirizzo");
        ControlliUtils.controlloEsistenzaCampo(indirizzoModificato, "L'indirizzo modificato è null");

        log.info("Cerco l'utente con id: {}", idUtente);
        Utente utenteTrovato = utenteRepository.findById(idUtente).orElseThrow(
                () -> {
                    log.error("Utente con id: {}, non trovato", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
                });

        log.info("Cerco l'indirizzo con id: {}", idIndirizzo);
        Indirizzo indirizzoTrovato = inidirizziRepo.findById(idIndirizzo).orElseThrow(
                () -> {
                    log.error("Indirizzo con id: {}, non trovato", idIndirizzo);
                    return new EntitaNonTrovataException(ErroreCodice.INDIRIZZO_NON_TROVATO);
                });

        if (!indirizzoTrovato.getUtente().getId().equals(utenteTrovato.getId())) {
            log.error("L'indirizzo con id: {} non appartiene all'utente con id: {}", idIndirizzo, idUtente);
            throw new IllegalArgumentException("L'indirizzo non appartiene all'utente specificato");
        }

        Indirizzo nuovoIndirizzo = generaIndirizzo(indirizzoModificato, utenteTrovato);
        nuovoIndirizzo.setId(indirizzoTrovato.getId());
        log.info("Nuovo indirizzo generato");

        Indirizzo indirizzoSalvato = inidirizziRepo.save(nuovoIndirizzo);
        
        if (indirizzoSalvato.isDefault()) {
            impostaAltriIndirizziComeNonDefault(utenteTrovato, indirizzoSalvato.getId());
        }
        
        log.info("Indirizzo con id: {}, modificato correttamente", indirizzoSalvato.getId());
        return indirizzoSalvato;
    }// modificaIndirizzo

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Elimina un singolo indirizzo in base all'id
     */
    @Transactional
    public void eliminaIndirizzo(UUID idIndirizzo) {
        log.info("Inizio a eliminare l'indirizzo con id: {}", idIndirizzo);

        ControlliUtils.controlloIdValido(idIndirizzo, "indirizzo");

        if (!inidirizziRepo.existsById(idIndirizzo)) {
            log.error("Indirizzo con id: {}, non trovato", idIndirizzo);
            throw new EntitaNonTrovataException(ErroreCodice.INDIRIZZO_NON_TROVATO);
        }

        inidirizziRepo.deleteById(idIndirizzo);
        log.info("Indirizzo con id: {}, eliminato con successo", idIndirizzo);
    }// eliminaIndirizzo

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Elimina uno specifico indirizzo di un utente verificando l'ownership
     */
    @Transactional
    public void eliminaUnIndirizzoDiUnUtente(UUID paramIdUtente, UUID idIndirizzo) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Inizio a eliminare l'indirizzo con id: {}, dell'utente con id: {}", idIndirizzo, idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");
        ControlliUtils.controlloIdValido(idIndirizzo, "indirizzo");

        Utente utenteTrovato = utenteRepository.findById(idUtente).orElseThrow(
                () -> {
                    log.error("Utente con id: {}, non trovato", idUtente);
                    return new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
                });

        Indirizzo indirizzoTrovato = inidirizziRepo.findById(idIndirizzo).orElseThrow(
                () -> {
                    log.error("Indirizzo con id: {}, non trovato", idIndirizzo);
                    return new EntitaNonTrovataException(ErroreCodice.INDIRIZZO_NON_TROVATO);
                });

        if (!indirizzoTrovato.getUtente().getId().equals(utenteTrovato.getId())) {
            log.error("L'indirizzo con id: {} non appartiene all'utente con id: {}", idIndirizzo, idUtente);
            throw new EntitaNonTrovataException(ErroreCodice.INDIRIZZO_NON_VALIDO);
        }

        indirizzoTrovato.setDeleted(true);
        indirizzoTrovato.setDefault(false);
        inidirizziRepo.save(indirizzoTrovato);
        log.info("Indirizzo con id: {} dell'utente: {}, eliminato (soft delete) con successo", idIndirizzo, idUtente);
    }// eliminaGliIndirizziDiUnUtente

    /**
     * Funziona correttamente. -- verificato 24/06/2026 --
     * Elimina tutti gli indirizzi di un utente
     */
    @Transactional
    public void eliminaTuttiGliIndirizziDiUnUtente(UUID paramIdUtente) {
        final UUID idUtente = resolveUtenteId(paramIdUtente);
        log.info("Inizio a eliminare tutti gli indirizzi dell'utente con id: {}", idUtente);

        ControlliUtils.controlloIdValido(idUtente, "utente");

        if (!utenteRepository.existsById(idUtente)) {
            log.error("Utente con id: {}, non trovato", idUtente);
            throw new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO);
        }

        Utente utenteTrovato = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO));

        List<Indirizzo> indirizzi = inidirizziRepo.findByUtenteAndIsDeletedFalse(utenteTrovato);
        for (Indirizzo ind : indirizzi) {
            ind.setDeleted(true);
            ind.setDefault(false);
        }
        inidirizziRepo.saveAll(indirizzi);
        
        log.info("Indirizzi dell'utente: {}, eliminati (soft delete) con successo", idUtente);
    }// eliminaTuttiGliIndirizziDiUnUtente

    /**
     * Metodo privato di utilità per generare un nuovo indirizzo
     * 
     * @param indirizzo
     * @param utente
     * @return
     */
    private Indirizzo generaIndirizzo(Indirizzo indirizzo, Utente utente) {
        log.info("Genero il nuovo indirizzo");
        Indirizzo nuovoIndirizzo = new Indirizzo();

        nuovoIndirizzo.setUtente(utente);
        nuovoIndirizzo.setNazione(indirizzo.getNazione());
        nuovoIndirizzo.setVia(indirizzo.getVia());
        nuovoIndirizzo.setNumeroCivico(indirizzo.getNumeroCivico());
        nuovoIndirizzo.setCitta(indirizzo.getCitta());
        nuovoIndirizzo.setProvincia(indirizzo.getProvincia());
        nuovoIndirizzo.setCap(indirizzo.getCap());
        nuovoIndirizzo.setTipo(indirizzo.getTipo());
        nuovoIndirizzo.setDefault(indirizzo.isDefault());

        log.info("Nuovo indirizzo generato");
        return nuovoIndirizzo;
    }// generaIndirizzo

    private void impostaAltriIndirizziComeNonDefault(Utente utente, UUID idIndirizzoEscluso) {
        List<Indirizzo> indirizziUtente = inidirizziRepo.findByUtenteAndIsDeletedFalse(utente);
        for (Indirizzo ind : indirizziUtente) {
            if (ind.isDefault() && !ind.getId().equals(idIndirizzoEscluso)) {
                ind.setDefault(false);
                inidirizziRepo.save(ind);
            }
        }
    }
}// IndirizzoService
