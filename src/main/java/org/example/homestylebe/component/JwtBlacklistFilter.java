package org.example.homestylebe.component;

import org.example.homestylebe.service.TokenBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtBlacklistFilter extends OncePerRequestFilter {

    private final TokenBlackListService blacklistService;
    private final JwtDecoder jwtDecoder;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Nessun token — lascia passare, sarà rifiutato da Spring Security se l'endpoint è protetto
            filterChain.doFilter(request, response);
            return;
        }

        String rawToken = authHeader.substring(7);

        try {
            Jwt jwt = jwtDecoder.decode(rawToken);
            String jti = jwt.getId();

            if (jti != null && blacklistService.isRevoked(jti)) {
                log.warn("Tentativo di accesso con token revocato: jti={}, utente={}", jti, jwt.getSubject());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getWriter(), Map.of(
                        "error", "token_revoked",
                        "message", "Il token è stato invalidato. Effettua nuovamente il login."
                ));
                return;
            }
        } catch (JwtException e) {
            // Token malformato — Spring Security lo gestirà dopo
            log.debug("Token non decodificabile nel blacklist filter: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Salta il filtro sugli endpoint pubblici
        String path = request.getServletPath();
        return path.startsWith("/actuator/health")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }
}
