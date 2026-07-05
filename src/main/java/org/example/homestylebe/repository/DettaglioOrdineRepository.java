package org.example.homestylebe.repository;

import org.example.homestylebe.entity.DettaglioOrdine;
import org.example.homestylebe.entity.Ordine;
import org.example.homestylebe.entity.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DettaglioOrdineRepository extends JpaRepository<DettaglioOrdine, UUID> {

    // Trova tutti i dettagli di un ordine
    List<DettaglioOrdine> findByOrdine(Ordine ordine);

    // Trova il dettaglio di un prodotto specifico in un ordine
    Optional<DettaglioOrdine> findByOrdineAndProdotto(Ordine ordine, Prodotto prodotto);

    // Trova tutti i dettagli di un prodotto (utile per statistiche o report)
    List<DettaglioOrdine> findByProdotto(Prodotto prodotto);

    List<DettaglioOrdine> findByOrdine_Id(UUID ordineId);

    List<DettaglioOrdine> findByProdotto_Id(UUID prodottoId);

    boolean existsByProdottoIdAndOrdineStatoOrdineIn(UUID prodottoId, List<Ordine.StatoOrdine> inElaborazione);
}//DettaglioOrdineRepository
