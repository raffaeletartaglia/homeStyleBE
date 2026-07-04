package org.example.homestylebe.exception;

public class ProdottoNonEsauritoException extends RuntimeException {
    
    private final ErroreCodice codice;

    public ProdottoNonEsauritoException(String message, ErroreCodice codice) {
        super(message);
        this.codice = codice;
    }

    public ErroreCodice getCodice() {
        return codice;
    }
}
