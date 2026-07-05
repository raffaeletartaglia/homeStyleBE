package org.example.homestylebe.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwtToken) {
        log.info("Conversione ruoli dal JWT per l'utente: {}", jwtToken.getSubject());

        List<GrantedAuthority> autorizzazioni = new ArrayList<>();

        Map<String, Object> accessiDelRealm = jwtToken.getClaimAsMap("realm_access");
        if (accessiDelRealm != null && accessiDelRealm.containsKey("roles")) {
            List<String> ruoli = (List<String>) accessiDelRealm.get("roles");
            log.info("Ruoli trovati nel realm_access: {}", ruoli);
            ruoli.stream()
                    .map(ruolo -> new SimpleGrantedAuthority("ROLE_" + ruolo.toUpperCase()))
                    .forEach(autorizzazioni::add);
        } else {
            log.warn("Nessun ruolo trovato nel claim 'realm_access' del JWT");
        }

        log.info("Autorizzazioni finali assegnate: {}", autorizzazioni);
        return autorizzazioni;
    }


}
