package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "token_revocato")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Questa entity serve per tenere traccia dei token che non sono più validi
 * Serve per aiutare il BE a tenere traccia degli utenti che non sono più in una sessione attiva
 * Una volta che l'utente esegue il logout i token verranno salvati nella tabella
 * Che funge da blackList, un'area dove i token invalidati vengono salvati
 * Questo impedisce a un attaccante di sfruttare liberamente il token
 */
public class TokenRevocato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "jti", nullable = false, unique = true)
    private String jti; // JWT ID (claim "jti") del access token

    @Column(name = "refresh_jti")
    private String refreshJti; // JTI del refresh token (opzionale)

    @Column(name = "subject", nullable = false)
    private String subject; // UUID utente (claim "sub")

    @Column(name = "revoked_at", nullable = false)
    private Instant revokedAt;

    @Column(name = "expires_at")
    private Instant expiresAt; // per cleanup automatico
}
