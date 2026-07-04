package org.example.homestylebe.exception;



public class EmailNonValidaException extends BusinessException {
    public EmailNonValidaException(ErroreCodice codice) {
        super(codice);
    }
    public EmailNonValidaException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
