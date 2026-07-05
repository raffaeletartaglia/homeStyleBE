package org.example.homestylebe.exception;

public class EntitaNonTrovataException extends BusinessException {
    public EntitaNonTrovataException(String messaggio) {
        super(ErroreCodice.ENTITA_NON_TROVATA, messaggio);
    }

    public EntitaNonTrovataException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }

    public EntitaNonTrovataException(ErroreCodice codice) {
        super(codice);
    }
}
