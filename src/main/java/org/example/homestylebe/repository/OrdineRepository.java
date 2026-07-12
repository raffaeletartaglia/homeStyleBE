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

import org.example.homestylebe.entity.Reso;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

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

    @Query("SELECT DISTINCT o FROM Ordine o " +
           "LEFT JOIN o.dettagliOrdine d " +
           "LEFT JOIN d.reso r " +
           "WHERE o.utente.id = :utenteId " +
           "AND (:statoOrdine IS NULL OR o.statoOrdine = :statoOrdine) " +
           "AND (" +
           "  (:statoResoStr IS NULL) " +
           "  OR (:statoResoStr = 'NESSUNO' AND NOT EXISTS (SELECT 1 FROM DettaglioOrdine d2 WHERE d2.ordine = o AND d2.reso IS NOT NULL)) " +
           "  OR (:statoResoStr != 'NESSUNO' AND r.statoReso = :statoResoEnum) " +
           ")")
    Page<Ordine> findOrdiniFiltrati(
            @Param("utenteId") UUID utenteId, 
            @Param("statoOrdine") Ordine.StatoOrdine statoOrdine, 
            @Param("statoResoStr") String statoResoStr,
            @Param("statoResoEnum") Reso.StatoReso statoResoEnum,
            Pageable pageable);
}//OrdineRepository
