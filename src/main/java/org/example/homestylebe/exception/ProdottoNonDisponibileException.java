package org.example.homestylebe.exception;



public class ProdottoNonDisponibileException extends BusinessException {

    public ProdottoNonDisponibileException() {
        super(ErroreCodice.PRODOTTO_NON_DISPONIBILE);
    }
}
