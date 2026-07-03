package repository;

import entity.Spedizione;
import entity.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpedizioneRepository extends JpaRepository<Spedizione, UUID> {

    // Trova la spedizione di un ordine specifico
    Spedizione findByOrdine(Ordine ordine);

    // Trova tutte le spedizioni di un certo stato
    List<Spedizione> findByStatoSpedizione(Spedizione.StatoSpedizione statoSpedizione);

    // Trova tutte le spedizioni di un corriere specifico
    List<Spedizione> findByCorriere(String corriere);

    List<Spedizione> findByOrdine_Id(UUID idOrdine);
    
}//SpedizioneRepository
