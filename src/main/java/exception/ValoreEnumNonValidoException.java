package exception;

public class ValoreEnumNonValidoException extends BusinessException {
    public ValoreEnumNonValidoException(ErroreCodice codice) {
        super(codice);
    }
    public ValoreEnumNonValidoException(ErroreCodice codice, String messaggio) {
        super(codice, messaggio);
    }
}
