package repository;

import com.homestyle.demo.entity.Carrello;
import com.homestyle.demo.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello, UUID> {

    // Trova il carrello di un utente
    Optional<Carrello> findByUtente_Id(UUID idUtente);

    Optional<Carrello> findByUtente_IdAndStato(UUID utente_id, Carrello.Stato stato);


    boolean existsByUtente_IdAndStato(UUID idUtente, Carrello.Stato stato);
}//CarrelloRepository
