package repository;

import entity.Indirizzo;
import entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface IndirizzoRepository extends JpaRepository<Indirizzo, UUID> {
	
	// Trova tutti gli indirizzi di un utente
    List<Indirizzo> findByUtente(Utente utente);

    // Trova tutti gli indirizzi di un utente filtrando per tipo
    List<Indirizzo> findByUtenteAndTipo(Utente utente, Indirizzo.Tipo tipo);

    // Trova tutti gli indirizzi di un tipo specifico
    List<Indirizzo> findByTipo(Indirizzo.Tipo tipo);

    // Elimina un indirizzo in base all'id di un utente
    void deleteByUtente(Utente utente);

    // Elimina tutti gli indirizzi di un utente
    void deleteAllByUtente(Utente utente);
}//IndirizzoRepository
