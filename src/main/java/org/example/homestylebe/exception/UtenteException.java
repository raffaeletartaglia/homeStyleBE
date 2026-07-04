package org.example.homestylebe.exception;



public class UtenteException  extends BusinessException{
    public UtenteException(ErroreCodice erroreCodice) {
        super(erroreCodice);
    }

    public UtenteException(ErroreCodice erroreCodice, String messaggioCustom) {
        super(erroreCodice, messaggioCustom);
    }
}
