package exception;

public class DataNonFuturaException extends BusinessException {
    public DataNonFuturaException(ErroreCodice codice) {
        super(codice);
    }
    public DataNonFuturaException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
