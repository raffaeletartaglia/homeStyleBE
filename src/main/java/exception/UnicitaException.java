package exception;



public class UnicitaException extends BusinessException {
    public UnicitaException(ErroreCodice codice) {
        super(codice);
    }
    public UnicitaException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
