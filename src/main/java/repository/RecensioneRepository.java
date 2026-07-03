package repository;

import entity.Recensione;
import entity.Utente;
import entity.DettaglioOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, UUID> {

    // Trova tutte le recensioni di un utente
    List<Recensione> findByUtente(Utente utente);

    // Trova tutte le recensioni di un dettaglio ordine (cioè di un prodotto specifico in un ordine)
    List<Recensione> findByDettaglioOrdine(DettaglioOrdine dettaglioOrdine);

    // Trova tutte le recensioni di un prodotto dato tramite il dettaglio ordine
    List<Recensione> findByDettaglioOrdine_ProdottoId(UUID prodottoId);

    boolean existsByDettaglioOrdine_Id(UUID id);

    List<Recensione> findByDettaglioOrdine_Id(UUID dettaglioOrdineId);

    List<Recensione> findByUtente_Id(UUID utenteId);

    List<Recensione> findByDettaglioOrdineProdotto_Id(UUID prodottoId);
}//RecensioneRepository
