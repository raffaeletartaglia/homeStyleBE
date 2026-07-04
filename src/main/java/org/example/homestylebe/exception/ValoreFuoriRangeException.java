package org.example.homestylebe.exception;

public class ValoreFuoriRangeException extends BusinessException {
    public ValoreFuoriRangeException(ErroreCodice codice) {
        super(codice);
    }
    public ValoreFuoriRangeException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
