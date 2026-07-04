package org.example.homestylebe.exception;



public class CarrelloNonAttivoException extends BusinessException {

    public CarrelloNonAttivoException() {
        super(ErroreCodice.CARRELLO_NON_ATTIVO);
    }
}
