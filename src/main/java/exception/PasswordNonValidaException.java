package exception;



public class PasswordNonValidaException extends BusinessException {
    public PasswordNonValidaException(ErroreCodice codice) {
        super(codice);
    }
    public PasswordNonValidaException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
