package org.example.homestylebe.exception;



public class NumeroEsistenteException extends BusinessException {
    public NumeroEsistenteException(ErroreCodice codice) {
        super(codice);
    }
    public NumeroEsistenteException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
