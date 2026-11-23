package org.example.homestylebe;

import jakarta.persistence.EntityManager;
import org.example.homestylebe.entity.CartaPagamento;
import org.example.homestylebe.entity.Indirizzo;
import org.example.homestylebe.entity.Ordine;
import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.service.OrdineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HomeStyleBeApplicationTests {

    @Autowired
    private OrdineService ordini;

    @Autowired
    private EntityManager entityManager;

    @Test
    void contextLoads() {
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    @Transactional
    void checkoutSalvaLaModalitaDiPagamentoScelta(boolean pagamentoOnline) {
        Utente utente = new Utente();
        utente.setKeycloakId(UUID.randomUUID().toString());
        utente.setNome("Utente");
        utente.setCognome("Checkout");
        utente.setEmail(utente.getKeycloakId() + "@example.test");
        utente.setRuolo(Utente.Ruolo.USER);
        entityManager.persist(utente);

        Indirizzo indirizzo = new Indirizzo();
        indirizzo.setUtente(utente);
        indirizzo.setTipo(Indirizzo.Tipo.SPEDIZIONE);
        entityManager.persist(indirizzo);

        Prodotto prodotto = new Prodotto();
        prodotto.setNomeProdotto("Prodotto checkout");
        prodotto.setPrezzo(new BigDecimal("12.50"));
        prodotto.setGiacenza(3);
        entityManager.persist(prodotto);

        CartaPagamento carta = new CartaPagamento();
        carta.setUtente(utente);
        carta.setIntestatario("Utente Checkout");
        carta.setNumeroCarta("4111111111111111");
        carta.setScadenza(LocalDate.now().plusYears(1));
        carta.setCvv("123");
        entityManager.persist(carta);

        UUID ordineId = ordini.checkout(UUID.fromString(utente.getKeycloakId()), indirizzo.getId(),
                prodotto.getId(), 2, pagamentoOnline, pagamentoOnline ? carta.getId() : null).getId();
        entityManager.flush();
        entityManager.clear();

        Ordine ordine = entityManager.find(Ordine.class, ordineId);
        assertThat(ordine.getPagamento().getPagamentoOnline()).isEqualTo(pagamentoOnline);
        assertThat(ordine.getPagamento().getPagamentoEffettuato()).isEqualTo(pagamentoOnline);
        assertThat(ordine.getPagamento().getImporto()).isEqualByComparingTo("25.00");
        assertThat(entityManager.find(Prodotto.class, prodotto.getId()).getGiacenza()).isEqualTo(1);
        if (pagamentoOnline) {
            assertThat(ordine.getPagamento().getCartaPagamento().getId()).isEqualTo(carta.getId());
        } else {
            assertThat(ordine.getPagamento().getCartaPagamento()).isNull();
        }
    }
}
