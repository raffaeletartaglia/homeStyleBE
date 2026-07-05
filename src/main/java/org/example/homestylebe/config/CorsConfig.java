package org.example.homestylebe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CorsConfig: definisce le regole CORS,
 * cioè da quali domini esterni (es. il tuo frontend React/Vue su un’altra porta
 * o dominio)
 * il browser è autorizzato a chiamare le API, con quali metodi HTTP e header.
 * Senza questa config il browser bloccherebbe molte richieste cross-origin
 * anche se il backend funziona.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // metti qui gli origin del tuo frontend
        config.setAllowedOrigins(List.of(
                "http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Applica CORS a tutte le API
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
