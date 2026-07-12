package org.example.homestylebe.repository;

import org.example.homestylebe.entity.Indirizzo;
import org.example.homestylebe.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface IndirizzoRepository extends JpaRepository<Indirizzo, UUID> {
	
	// Trova tutti gli indirizzi di un utente
    List<Indirizzo> findByUtenteAndIsDeletedFalse(Utente utente);

    // Trova tutti gli indirizzi di un utente filtrando per tipo
    List<Indirizzo> findByUtenteAndTipoAndIsDeletedFalse(Utente utente, Indirizzo.Tipo tipo);

    // Trova tutti gli indirizzi di un tipo specifico
    List<Indirizzo> findByTipoAndIsDeletedFalse(Indirizzo.Tipo tipo);

}//IndirizzoRepository
