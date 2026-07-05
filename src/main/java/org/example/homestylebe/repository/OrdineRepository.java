package org.example.homestylebe.repository;

import org.example.homestylebe.entity.Ordine;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.entity.Indirizzo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, UUID> {

    // Trova tutti gli ordini di un utente
    List<Ordine> findByUtente(Utente utente);

    // Trova tutti gli ordini di un utente filtrando per stato
    List<Ordine> findByUtente_IdAndStatoOrdine(UUID idUtente, Ordine.StatoOrdine statoOrdine);

    // Trova tutti gli ordini con uno stato specifico
    List<Ordine> findByStatoOrdine(Ordine.StatoOrdine statoOrdine);

    // Trova tutti gli ordini con data ordine maggiore o uguale a una certa data
    List<Ordine> findByDataOrdineAfter(LocalDateTime data);

    // Trova tutti gli ordini per un certo indirizzo di spedizione
    List<Ordine> findByIndirizzoSpedizione(Indirizzo indirizzo);

    List<Ordine> findByUtente_Id(UUID utenteId);

    // Versione impaginata
    Page<Ordine> findByUtente_Id(UUID utenteId, Pageable pageable);
}//OrdineRepository
