package org.example.homestylebe.exception;

public class ValoreEnumNonValidoException extends BusinessException {
    public ValoreEnumNonValidoException(ErroreCodice codice) {
        super(codice);
    }
    public ValoreEnumNonValidoException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
    public ValoreEnumNonValidoException(String messaggio) {
        super(ErroreCodice.ERRORE_VALIDAZIONE, messaggio);
    }
}
