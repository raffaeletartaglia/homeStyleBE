package org.example.homestylebe.exception;



public class OrdineGiaConsegnatoException extends BusinessException {
    public OrdineGiaConsegnatoException() {
        super(ErroreCodice.ORDINE_GIA_CONSEGNATO);
    }
}
