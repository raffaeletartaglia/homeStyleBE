package org.example.homestylebe.exception;



public class EmailEsistenteException extends BusinessException {
    public EmailEsistenteException(ErroreCodice codice) {
        super(codice);
    }
    public EmailEsistenteException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
