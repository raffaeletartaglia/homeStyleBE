package org.example.homestylebe.repository;

import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.entity.Utente.Ruolo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, UUID> {

    Optional<Utente> findUtenteByKeycloakId(String keycloakId);

    Optional<Utente> findUtenteByEmail(String email);
}// UtenteRepository
