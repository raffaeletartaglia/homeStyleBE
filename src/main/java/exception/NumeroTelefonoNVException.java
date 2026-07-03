package exception;




public class NumeroTelefonoNVException extends BusinessException {
    public NumeroTelefonoNVException(ErroreCodice codice) {
        super(codice);
    }
    public NumeroTelefonoNVException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
