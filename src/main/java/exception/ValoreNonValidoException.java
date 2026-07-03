package exception;



// ValoreNonValidoException
public class ValoreNonValidoException extends BusinessException {
    public ValoreNonValidoException(String messaggioCustom, ErroreCodice codice) {
        super(codice, messaggioCustom);
    }
}
