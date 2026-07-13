package org.example.homestylebe.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import org.example.homestylebe.entity.Stanza;
import org.example.homestylebe.repository.StanzaRepository;

import org.example.homestylebe.exception.CampoNullException;
import org.example.homestylebe.exception.BusinessException;
import org.example.homestylebe.exception.ErroreCodice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StanzaService {

    private final StanzaRepository stanzaRepo;

    /**
     * Funziona correttamente. -- verificato 25/06/2026 --
     * Crea una nuova stanza.
     * Effettua la validazione per assicurare che la stanza e la sua tipologia non siano null,
     * che la tipologia sia valida e che non esista già una stanza con la stessa tipologia.
     *
     * @param stanza L'oggetto Stanza da creare.
     * @return La stanza appena salvata nel database.
     * @throws CampoNullException se l'oggetto stanza o la tipologia sono nulli o non validi, o se la stanza esiste già.
     */
    public Stanza creaUnaStanza(Stanza stanza) {
        log.info("Creazione di una stanza");

        if (stanza == null) {
            log.error("Stanza null");
            throw new CampoNullException("Stanza null");
        }

        if (stanza.getTipologia() == null || stanza.getTipologia().trim().isEmpty()) {
            log.error("Tipologia stanza null o vuota");
            throw new CampoNullException("Campo tipologia null o vuoto");
        }

        if (stanzaRepo.existsByTipologia(stanza.getTipologia())) {
            log.error("Stanza già esistente");
            throw new BusinessException(ErroreCodice.STANZA_DUPLICATA);
        }

        Stanza stanzaSalvate = stanzaRepo.save(stanza);
        log.info("Stanza creata con id: {}", stanzaSalvate.getId());

        return stanzaSalvate;
    }

    /**
     * Funziona correttamente. -- verificato 25/06/2026 --
     * Recupera una stanza dato il suo identificativo.
     * 
     * @param id L'UUID della stanza da cercare.
     * @return L'oggetto Stanza trovato.
     * @throws CampoNullException se l'id è nullo o se la stanza non viene trovata.
     */
    @Transactional(readOnly = true)
    public Stanza prendiStanza(UUID id) {
        log.info("Recupero stanza con id: {}", id);

        if (id == null) {
            log.error("Id null");
            throw new CampoNullException("Id null");
        }

        return stanzaRepo.findById(id).orElseThrow(() -> new CampoNullException("Stanza non trovata"));
    }

    /**
     * Funziona correttamente. -- verificato 25/06/2026 --
     * Recupera l'elenco di tutte le stanze presenti nel sistema (paginato).
     *
     * @param pageable Parametri di paginazione.
     * @return Una pagina contenente tutte le stanze.
     */
    @Transactional(readOnly = true)
    public Page<Stanza> prendiTutteLeStanze(Pageable pageable) {
        log.info("Recupero tutte le stanze (paginato)");
        return stanzaRepo.findAll(pageable);
    }

    /**
     * Funziona correttamente. -- verificato 25/06/2026 --
     * Aggiorna i dati di una stanza esistente.
     * Effettua la validazione per controllare che i nuovi dati siano corretti e
     * che non si stia cercando di assegnare una tipologia già assegnata a un'altra stanza.
     * 
     * @param id L'UUID della stanza da aggiornare.
     * @param stanza L'oggetto Stanza contenente i nuovi dati.
     * @return La stanza aggiornata.
     * @throws CampoNullException se i parametri sono nulli, se la stanza non viene trovata
     *                            o se esiste già un'altra stanza con la stessa tipologia.
     */
    public Stanza aggiornaStanza(UUID id, Stanza stanza) {
        log.info("Aggiornamento stanza con id: {}", id);

        if (stanza == null) {
            log.error("Stanza null");
            throw new CampoNullException("Stanza null");
        }

        if (stanza.getTipologia() == null || stanza.getTipologia().trim().isEmpty()) {
            log.error("Tipologia stanza null o vuota");
            throw new CampoNullException("Campo tipologia null o vuoto");
        }

        Stanza vecchiaStanza = stanzaRepo.findById(id).orElseThrow(() -> new CampoNullException("Stanza non trovata"));

        if (!vecchiaStanza.getTipologia().equals(stanza.getTipologia()) && stanzaRepo.existsByTipologia(stanza.getTipologia())) {
            log.error("Stanza già esistente");
            throw new BusinessException(ErroreCodice.STANZA_DUPLICATA);
        }

        vecchiaStanza.setTipologia(stanza.getTipologia());
        Stanza stanzaSalvata = stanzaRepo.save(vecchiaStanza);
        log.info("Stanza aggiornata con id: {}", stanzaSalvata.getId());

        return stanzaSalvata;
    }

    /**
     * Funziona correttamente. -- verificato 25/06/2026 --
     * Elimina una stanza esistente in base al suo id.
     * 
     * @param id L'UUID della stanza da eliminare.
     */
    public void eliminaStanza(UUID id) {
        log.info("Eliminazione stanza con id: {}", id);
        stanzaRepo.deleteById(id);
    }
}
