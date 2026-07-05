package org.example.homestylebe.utils;

import org.example.homestylebe.exception.ValoreNonValidoException;
import org.example.homestylebe.exception.ErroreCodice;

import java.time.LocalDate;
import java.util.UUID;

import org.example.homestylebe.entity.Indirizzo;
import org.example.homestylebe.entity.Ordine;
import org.example.homestylebe.exception.CampoNullException;
import org.example.homestylebe.exception.DataNonFuturaException;
import org.example.homestylebe.exception.EmailNonValidaException;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import org.example.homestylebe.exception.NumeroTelefonoNVException;
import org.example.homestylebe.exception.PasswordNonValidaException;
import org.example.homestylebe.exception.ValoreEnumNonValidoException;
import org.example.homestylebe.exception.ValoreFuoriRangeException;

import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ControlliUtils {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(it|com|org)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    private static final String NUMERO_REGEX = "^3\\d{9}$";
    private static final Pattern NUMERO_PATTERN = Pattern.compile(NUMERO_REGEX);

    // --- Stati Ordine ---

    public static void controlloStatoOrdineValido(Ordine.StatoOrdine stato) {
        if (stato == null) {
            throw new ValoreNonValidoException(
                    "Lo stato dell'ordine non può essere nullo",
                    ErroreCodice.ORDINE_STATO_NON_VALIDO);
        }
    }

    public static void controlloTransizioneStatoValida(Ordine.StatoOrdine statoAttuale,
            Ordine.StatoOrdine nuovoStato) {
        boolean transizioneValida = switch (statoAttuale) {
            case IN_ELABORAZIONE -> nuovoStato == Ordine.StatoOrdine.SPEDITO
                    || nuovoStato == Ordine.StatoOrdine.ANNULLATO;
            case SPEDITO -> nuovoStato == Ordine.StatoOrdine.CONSEGNATO
                    || nuovoStato == Ordine.StatoOrdine.ANNULLATO;
            case CONSEGNATO -> false; // stato finale
            case ANNULLATO -> false; // stato finale
        };

        if (!transizioneValida) {
            throw new ValoreNonValidoException(
                    "Transizione non valida: " + statoAttuale + " → " + nuovoStato,
                    ErroreCodice.ORDINE_STATO_NON_VALIDO);
        }
    }

    // --- Esistenza entità / campi ---

    public void controlloEsistenzaEntita(Object entity, String messaggio) {
        if (entity == null) {
            throw new EntitaNonTrovataException(messaggio);
        }
    }

    public static void controlloEsistenzaCampo(Object oggetto, String messaggio) {
        if (oggetto == null) {
            throw new CampoNullException("ID di " + messaggio + " non può essere null");
        }
    }

    public static void controlloIdValido(Object id, String nomeEntita) {
        if (id == null) {
            throw new ValoreNonValidoException(
                    "ID di " + nomeEntita + " non può essere null",
                    ErroreCodice.ERRORE_VALIDAZIONE);
        }
    }

    // --- Valori numerici / range ---

    public void controlloValPositivi(Number valore, String nome) {
        if (valore == null || valore.doubleValue() <= 0) {
            throw new ValoreNonValidoException(
                    nome + " non può essere minore o pari a 0",
                    ErroreCodice.ERRORE_VALIDAZIONE);
        }
    }

    public void controlloRange(int valore, int min, int max, String nome) {
        if (valore < min || valore > max) {
            throw new ValoreFuoriRangeException(
                    ErroreCodice.ERRORE_VALIDAZIONE,
                    nome + " deve essere tra " + min + " e " + max);
        }
    }

    // --- Enum generico ---

    public static <E extends Enum<E>> void controlloValoreEnum(Class<E> enumClass,
            String valore,
            String nomeCampo) {
        try {
            Enum.valueOf(enumClass, valore);
        } catch (IllegalArgumentException e) {
            throw new ValoreEnumNonValidoException(
                    ErroreCodice.ERRORE_VALIDAZIONE,
                    nomeCampo + " non valido: " + valore);
        }
    }

    // --- Date ---

    public void controlloDateFuture(LocalDate data, String nome) {
        if (data == null || data.isBefore(LocalDate.now())) {
            throw new DataNonFuturaException(ErroreCodice.ERRORE_VALIDAZIONE, nome + " deve essere futura ");
        }
    }

    // --- Email / password / telefono ---

    public static boolean emailValida(String email) {
        if (email == null || email.isEmpty()
                || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new EmailNonValidaException(
                    ErroreCodice.UTENTE_EMAIL_NON_VALIDA,
                    email + " non è un'email valida");
        }
        return true;
    }

    public static boolean passValida(String password) {
        if (password == null || password.isEmpty()
                || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new PasswordNonValidaException(
                    ErroreCodice.UTENTE_PASSWORD_NON_VALIDA,
                    "Password non valida: deve avere almeno 8 caratteri, una maiuscola, " +
                            "un numero e un carattere speciale");
        }
        return true;
    }

    public static boolean numeroTelefonoValido(String numero) {
        if (numero == null || numero.isEmpty()
                || !NUMERO_PATTERN.matcher(numero).matches()) {
            throw new NumeroTelefonoNVException(
                    "Numero di telefono non valido: deve iniziare con 3 e contenere 10 cifre",
                    ErroreCodice.UTENTE_NUMERO_TELEFONO_NON_VALIDO);
        }
        return true;
    }

    // --- Carta di pagamento ---

    public static boolean numeroCartaPagamento(String numeroCartaP) {

        if (numeroCartaP == null) {
            throw new ValoreNonValidoException(
                    "Il numero della carta non può essere null",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        numeroCartaP = numeroCartaP.trim();

        if (numeroCartaP.isEmpty()) {
            throw new ValoreNonValidoException(
                    "Il numero della carta non può essere vuoto",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        if (!numeroCartaP.matches("\\d+")) {
            throw new ValoreNonValidoException(
                    "Il numero della carta deve contenere solo numeri",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        if (numeroCartaP.length() < 13 || numeroCartaP.length() > 19) {
            throw new ValoreNonValidoException(
                    "Il numero della carta deve avere tra 13 e 19 cifre",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        String regexVisa = "^4[0-9]{12}(?:[0-9]{3})?(?:[0-9]{3})?$";
        String regexMastercard = "^5[1-5][0-9]{14}$";
        String regexMaestro = "^(5018|5020|5038|5893|6304|6759|6761|6763)[0-9]{8,15}$";

        if (numeroCartaP.matches(regexVisa)
                || numeroCartaP.matches(regexMastercard)
                || numeroCartaP.matches(regexMaestro)) {

            return true;
        }

        throw new ValoreNonValidoException(
                "Numero carta non valido o tipo carta non supportato",
                ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
    }

    public static boolean controllaIntestatarioCarta(String intestatario) {

        if (intestatario == null) {
            throw new ValoreNonValidoException(
                    "L'intestatario della carta non può essere null",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        intestatario = intestatario.trim();

        if (intestatario.isEmpty()) {
            throw new ValoreNonValidoException(
                    "L'intestatario della carta non può essere vuoto",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        if (!intestatario.matches("^[A-Za-z ]+$")) {
            throw new ValoreNonValidoException(
                    "L'intestatario della carta può contenere solo lettere",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        return true;
    }

    public static boolean controlloScadenzaCarta(LocalDate scadenza) {

        if (scadenza == null) {
            throw new ValoreNonValidoException(
                    "La scadenza della carta non può essere null",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        if (scadenza.isBefore(LocalDate.now())) {
            throw new ValoreNonValidoException(
                    "La carta è scaduta",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        return true;
    }

    public static boolean controlloCVV(String cvv) {

        if (cvv == null) {
            throw new ValoreNonValidoException(
                    "Il CVV non può essere null",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        cvv = cvv.trim();

        if (cvv.isEmpty()) {
            throw new ValoreNonValidoException(
                    "Il CVV non può essere vuoto",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        if (!cvv.matches("\\d{3}")) {
            throw new ValoreNonValidoException(
                    "Il CVV deve contenere esattamente 3 cifre numeriche",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }

        return true;
    }

    public static boolean controllaTipoCarta(String numeroCarta) {
        if (numeroCarta == null || numeroCarta.trim().isEmpty()) {
            throw new ValoreNonValidoException(
                    "Numero carta non può essere null o vuoto",
                    ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
        }
        numeroCarta = numeroCarta.trim();

        if (numeroCarta.matches("^4[0-9]{12}(?:[0-9]{3})?$"))
            return true; // VISA
        if (numeroCarta.matches("^5[1-5][0-9]{14}$"))
            return true; // MASTERCARD
        if (numeroCarta.matches("^(5018|5020|5038|5893|6304|6759|6761|6763)[0-9]{8,15}$"))
            return true; // MAESTRO

        throw new ValoreNonValidoException(
                "Tipo carta non riconosciuto",
                ErroreCodice.CARTA_PAGAMENTO_NON_VALIDA);
    }

    /**
     * Controllo per verificare se il tipo di indirizzo che ci viene passato è
     * corretto o no
     */
    public static void controlloTipoIndirizzo(String tipoIndirizzo) {
        if (tipoIndirizzo == null || tipoIndirizzo.isBlank()) {
            log.error("Il tipo indirizzo è null o vuoto");
            throw new ValoreNonValidoException(
                    "Il tipo indirizzo è null o vuoto",
                    ErroreCodice.INDIRIZZO_NON_VALIDO);
        }
        try {
            Indirizzo.Tipo.valueOf(tipoIndirizzo.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Il tipo indirizzo {}, non è valido", tipoIndirizzo);
            throw new ValoreNonValidoException(
                    "Il tipo indirizzo non è valido",
                    ErroreCodice.INDIRIZZO_NON_VALIDO);
        }
    }

}
