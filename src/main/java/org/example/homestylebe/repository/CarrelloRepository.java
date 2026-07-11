package org.example.homestylebe.repository;

import org.example.homestylebe.entity.Carrello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello, UUID> {

    // Trova il carrello di un utente
    Optional<Carrello> findByUtente_Id(UUID idUtente);

    boolean existsByUtente_Id(UUID idUtente);
}//CarrelloRepository
