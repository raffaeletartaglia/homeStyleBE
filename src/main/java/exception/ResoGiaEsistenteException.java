package exception;



public class ResoGiaEsistenteException extends BusinessException {
    public ResoGiaEsistenteException() {
        super(ErroreCodice.RESO_GIA_ESISTENTE);
    }
}
