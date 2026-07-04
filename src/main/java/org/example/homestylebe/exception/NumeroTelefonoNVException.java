package org.example.homestylebe.exception;




public class NumeroTelefonoNVException extends BusinessException {
    public NumeroTelefonoNVException(ErroreCodice codice) {
        super(codice);
    }
    public NumeroTelefonoNVException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
    public NumeroTelefonoNVException(String messaggio, ErroreCodice codice) {
        super(codice, messaggio);
    }
}
