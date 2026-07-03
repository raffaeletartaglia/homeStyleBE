package exception;


public class EntitaNonTrovataException extends BusinessException {
    public EntitaNonTrovataException(ErroreCodice codice) {
        super(codice);
    }
    public EntitaNonTrovataException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
