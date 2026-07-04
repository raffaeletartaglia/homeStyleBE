package org.example.homestylebe.repository;

import org.example.homestylebe.entity.TokenRevocato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRevocatoRepository extends JpaRepository<TokenRevocato, UUID> {

    boolean existsByJti(String jti);

    Optional<TokenRevocato> findByJti(String jti);

    // Pulizia periodica dei token scaduti
    @Modifying
    @Query("DELETE FROM TokenRevocato r WHERE r.expiresAt < :now")
    void deleteExpiredTokens(Instant now);
}
