package org.example.homestylebe.exception;

public class CampoNullException extends BusinessException {
    public CampoNullException(ErroreCodice codice) {
        super(codice);
    }
    public CampoNullException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
    public CampoNullException(String messaggio) {
        super(ErroreCodice.ERRORE_VALIDAZIONE, messaggio);
    }
}
