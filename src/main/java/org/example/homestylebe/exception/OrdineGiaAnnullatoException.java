package org.example.homestylebe.exception;



public class OrdineGiaAnnullatoException extends BusinessException {
    public OrdineGiaAnnullatoException() {
        super(ErroreCodice.ORDINE_GIA_ANNULLATO);
    }
}
