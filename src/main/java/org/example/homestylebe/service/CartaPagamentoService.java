package org.example.homestylebe.service;

import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

import org.example.homestylebe.exception.ErroreCodice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.example.homestylebe.repository.CartaPagamentoRepository;
import org.example.homestylebe.entity.CartaPagamento;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.repository.UtenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.homestylebe.utils.ControlliUtils;
import org.example.homestylebe.exception.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartaPagamentoService {

    private final CartaPagamentoRepository cartaPagamentoRepo;
    private final UtenteRepository utenteRepo;
    private final PasswordEncoder passwordEncoder; // iniettato da SecurityConfig

    public CartaPagamento addCartaPagamento(CartaPagamento cartaPagamento){

        log.info("Creazione nuova carta di pagamento per utenteId={}", cartaPagamento.getUtente().getId());

        ControlliUtils.controlloIdValido(cartaPagamento.getUtente().getId(), "Utente");
        log.info("Id utente valido: {}", cartaPagamento.getUtente().getId());

        Utente utente = utenteRepo.findById(cartaPagamento.getUtente().getId())
                .orElseThrow(() -> new EntitaNonTrovataException(ErroreCodice.UTENTE_NON_TROVATO));
        cartaPagamento.setUtente(utente);
        log.info("Utente trovato e associato alla carta: {}", utente.getId());

        if (!controllaNumeroCarta(cartaPagamento.getNumeroCarta())) {
            log.error("Numero carta non valido: {}", cartaPagamento.getNumeroCarta());
            throw new ValoreNonValidoException(
                    "Numero della carta non valido",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA
            );
        }
        log.info("Numero carta valido");

        if (!controllaIntestatario(cartaPagamento.getIntestatario())) {
            log.error("Intestatario non valido: {}", cartaPagamento.getIntestatario());
            throw new ValoreNonValidoException(
                    "Intestatario non valido",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA
            );
        }
        log.info("Intestatario carta valido");

        if (!controllaScadenza(cartaPagamento.getScadenza())) {
            log.error("Scadenza carta non valida: {}", cartaPagamento.getScadenza());
            throw new ValoreNonValidoException(
                    "Valore della scadenza non valido",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA
            );
        }
        log.info("Scadenza carta valida: {}", cartaPagamento.getScadenza());

        if (!controlloCvvCarta(cartaPagamento.getCvv())) {
            log.error("CVV non valido");
            throw new ValoreNonValidoException(
                    "Valore del cvv non valido",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA
            );
        }
        log.info("CVV valido");

        if (!controllaTipoCarta(cartaPagamento.getNumeroCarta())) {
            log.error("Tipo carta non valido per numero: {}", cartaPagamento.getNumeroCarta());
            throw new ValoreNonValidoException(
                    "Tipo carta non valido",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA
            );
        }
        log.info("Tipo carta valido");

        // Logica per gestire isDefault
        if (cartaPagamento.isDefault()) {
            List<CartaPagamento> altreCarte = cartaPagamentoRepo.findByUtenteId(utente.getId());
            for (CartaPagamento carta : altreCarte) {
                if (carta.isDefault()) {
                    carta.setDefault(false);
                    cartaPagamentoRepo.save(carta);
                }
            }
        }

        // Criptazione CVV prima del salvataggio
        String cvvCriptato = passwordEncoder.encode(cartaPagamento.getCvv());
        cartaPagamento.setCvv(cvvCriptato);
        log.info("CVV criptato e pronto per il salvataggio");

        // Salvataggio carta nel DB
        CartaPagamento cartaSalvata = cartaPagamentoRepo.save(cartaPagamento);
        log.info("Carta salvata correttamente con id: {}", cartaSalvata.getId());

        return cartaSalvata;
    }//addCartaPagamento

    public CartaPagamento updateCartaPagamento(UUID cartaId, boolean impostaPredefinita) {
        log.info("Imposta carta {} come predefinita={}", cartaId, impostaPredefinita);
        ControlliUtils.controlloIdValido(cartaId, "Carta Pagamento");

        CartaPagamento cartaEsistente = getCartaById(cartaId);

        if (impostaPredefinita && !cartaEsistente.isDefault()) {
            List<CartaPagamento> altreCarte = cartaPagamentoRepo.findByUtenteId(cartaEsistente.getUtente().getId());
            for (CartaPagamento carta : altreCarte) {
                if (carta.isDefault() && !carta.getId().equals(cartaId)) {
                    carta.setDefault(false);
                    cartaPagamentoRepo.save(carta);
                }
            }
        }

        cartaEsistente.setDefault(impostaPredefinita);
        return cartaPagamentoRepo.save(cartaEsistente);
    }

    public CartaPagamento updateCartaPagamento(UUID cartaId, CartaPagamento cartaAggiornata) {
        log.info("Aggiornamento carta di pagamento per cartaId={}", cartaId);
        ControlliUtils.controlloIdValido(cartaId, "Carta Pagamento");

        CartaPagamento cartaEsistente = getCartaById(cartaId);

        // Se l'utente vuole impostare questa carta come predefinita
        if (cartaAggiornata.isDefault() && !cartaEsistente.isDefault()) {
            List<CartaPagamento> altreCarte = cartaPagamentoRepo.findByUtenteId(cartaEsistente.getUtente().getId());
            for (CartaPagamento carta : altreCarte) {
                if (carta.isDefault() && !carta.getId().equals(cartaId)) {
                    carta.setDefault(false);
                    cartaPagamentoRepo.save(carta);
                }
            }
        }

        // Aggiorniamo solo isDefault per ora, o altri campi se necessario.
        // In questo contesto (come per gli indirizzi), spesso l'update serve per l'isDefault.
        cartaEsistente.setDefault(cartaAggiornata.isDefault());
        
        return cartaPagamentoRepo.save(cartaEsistente);
    }

    public List<CartaPagamento> getCarteByUtente(UUID utenteId){

        log.info("Trovo tutte le carte intestate all' utenteId={}", utenteId);

        ControlliUtils.controlloIdValido(utenteId, "Utente");
        log.info("Id utente valido: {}", utenteId);

        List<CartaPagamento> cartePagamenti = cartaPagamentoRepo.findByUtenteId(utenteId);
        // Non lanciamo eccezione se vuota, ma ritorniamo lista vuota
        if (cartePagamenti.isEmpty()) {
            log.info("Nessuna carta pagamento trovata per l'utenteId={}", utenteId);
        }
        return cartePagamenti;
    }//getCarteByUtente

    public CartaPagamento getCartaById(UUID cartaId) {
        log.info("Trovo una carta pagamento specifica per mezzo l'uso di cartaId={}", cartaId);

        ControlliUtils.controlloIdValido(cartaId, "Carta Pagamento");
        log.info("Id carta pagamento valido: {}", cartaId);

        CartaPagamento cartaPagamento = cartaPagamentoRepo.findById(cartaId).orElseThrow(
                () -> {
                    log.error("Carta non trovata per l'id: {}", cartaId);
                    return new EntitaNonTrovataException(ErroreCodice.CARTA_PAGAMENTO_NON_TROVATA);
                }
        );
        return cartaPagamento;
    }//getCartaById

    public void deleteCartaPagamento(UUID cartaId) {
        log.info("Trovo la carta pagamento da eliminare tramite l'uso di cartaId={}", cartaId);

        ControlliUtils.controlloIdValido(cartaId, "Carta Pagamento");
        log.info("Id carta pagamento valido: {}", cartaId);

        CartaPagamento cartaPagamento = cartaPagamentoRepo.findById(cartaId).orElseThrow(
                () -> {
                    log.error("Carta non trovata per l'id: {}", cartaId);
                    return new EntitaNonTrovataException(ErroreCodice.CARTA_PAGAMENTO_NON_TROVATA);
                }
        );
        cartaPagamentoRepo.deleteById(cartaId);
        log.info("Carta Pagamento eliminata con successo id={}", cartaId);
    }//deleteCartaPagamento



    private boolean controllaNumeroCarta(String numeroCarta) {
        log.debug("Controllo numero carta: {}", numeroCarta);
        return ControlliUtils.numeroCartaPagamento(numeroCarta);
    }//controllaNumeroCarta

    private boolean controllaIntestatario(String intestatario) {
        log.debug("Controllo intestatario: {}", intestatario);
        return ControlliUtils.controllaIntestatarioCarta(intestatario);
    }//controllaIntestatario

    private boolean controllaScadenza(LocalDate scadenza) {
        log.debug("Controllo scadenza carta: {}", scadenza);
        return ControlliUtils.controlloScadenzaCarta(scadenza);
    }//controllaScadenza

    private boolean controlloCvvCarta(String cvv) {
        log.debug("Controllo CVV: {}", cvv);
        return ControlliUtils.controlloCVV(cvv);
    }//controlloCvvCarta

    private boolean controllaTipoCarta(String numeroCarta) {
        log.debug("Controllo tipo carta per numero: {}", numeroCarta);
        return ControlliUtils.controllaTipoCarta(numeroCarta);
    }//controllaTipoCarta

}//CartaPagamentoService
