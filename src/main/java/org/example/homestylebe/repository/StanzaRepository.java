package org.example.homestylebe.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.example.homestylebe.entity.AreaCasa;
import org.example.homestylebe.entity.Stanza;

import java.util.Optional;

@Repository
public interface StanzaRepository extends JpaRepository<Stanza, UUID> {

    boolean existsByTipologia(AreaCasa tipologia);

    Optional<Stanza> findByTipologia(AreaCasa tipologia);
}
