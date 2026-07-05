package org.example.homestylebe.config;

import org.example.homestylebe.component.JwtBlacklistFilter;
import org.example.homestylebe.exception.SecurityExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig (SecurityFilterChain): configura Spring Security:
 *
 * dice che l’app è stateless e usa JWT di un provider esterno
 * (oauth2ResourceServer().jwt()),
 *
 * decide quali endpoint sono pubblici e quali richiedono autenticazione/ruoli,
 *
 * abilita/disabilita CSRF e aggancia CORS dentro la chain di sicurezza.
 * In pratica è il “firewall” interno dell’app che controlla autenticazione e
 * autorizzazione su tutte le richieste.
 */
@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        KeycloakRoleConverter keycloakRoleConverter,
                        JwtBlacklistFilter jwtBlacklistFilter,
                        SecurityExceptionHandler securityExceptionHandler,
                        @org.springframework.beans.factory.annotation.Value("${app.security.public-paths}") String[] publicPaths)
                        throws Exception {

                log.info("Configurazione SecurityFilterChain in corso...");

                JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
                jwtConverter.setJwtGrantedAuthoritiesConverter(keycloakRoleConverter);

                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtBlacklistFilter, BearerTokenAuthenticationFilter.class)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(publicPaths)
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(securityExceptionHandler)
                                                .accessDeniedHandler(securityExceptionHandler))
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

                log.info("SecurityFilterChain configurata correttamente");
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(); // puoi passare anche il "strength" es. new
                                                    // BCryptPasswordEncoder(12)
        }

}
