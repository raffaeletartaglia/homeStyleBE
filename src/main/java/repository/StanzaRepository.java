package repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.AreaCasa;
import entity.Stanza;

import java.util.Optional;

@Repository
public interface StanzaRepository extends JpaRepository<Stanza, UUID> {

    boolean existsByTipologia(AreaCasa tipologia);

    Optional<Stanza> findByTipologia(AreaCasa tipologia);
}
