package org.example.homestylebe.exception;



public class UnicitaException extends BusinessException {
    public UnicitaException(ErroreCodice codice) {
        super(codice);
    }
    public UnicitaException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
    public UnicitaException(String messaggio, ErroreCodice codice) {
        super(codice, messaggio);
    }
}
