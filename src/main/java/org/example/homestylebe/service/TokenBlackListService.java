package org.example.homestylebe.service;

import org.example.homestylebe.entity.TokenRevocato;
import org.example.homestylebe.repository.TokenRevocatoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlackListService {

    private final TokenRevocatoRepository tokenRevocatoRepository;
    private final JwtDecoder jwtDecoder;

    @Transactional
    public void revokeTokens(String rawAccessToken, String rawRefreshToken) {
        // Decodifica l'access token per estrarne jti, sub, exp
        Jwt accessJwt = jwtDecoder.decode(rawAccessToken);

        String jti = accessJwt.getId(); // claim "jti"
        if (jti == null) {
            throw new IllegalArgumentException("Il token non contiene il claim 'jti'. Verifica la config Keycloak.");
        }

        if (tokenRevocatoRepository.existsByJti(jti)) {
            log.info("Token {} già revocato, skip", jti);
            return;
        }

        String refreshJti = null;
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            try {
                Jwt refreshJwt = jwtDecoder.decode(rawRefreshToken);
                refreshJti = refreshJwt.getId();
            } catch (Exception e) {
                log.warn("Impossibile decodificare il refresh token: {}", e.getMessage());
            }
        }

        TokenRevocato revoked = TokenRevocato.builder()
                .jti(jti)
                .refreshJti(refreshJti)
                .subject(accessJwt.getSubject())
                .revokedAt(Instant.now())
                .expiresAt(accessJwt.getExpiresAt())
                .build();

        tokenRevocatoRepository.save(revoked);
        log.info("Token revocato per utente {}: jti={}", accessJwt.getSubject(), jti);
    }

    public boolean isRevoked(String jti) {
        return tokenRevocatoRepository.existsByJti(jti);
    }

    // Cleanup automatico ogni ora — rimuove token già scaduti dalla tabella
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Pulizia token scaduti dalla blacklist...");
        tokenRevocatoRepository.deleteExpiredTokens(Instant.now());
    }
}
