package org.example.homestylebe.exception;


public class BusinessException extends RuntimeException {

    private final ErroreCodice erroreCodice;

    public BusinessException(ErroreCodice erroreCodice) {
        super(erroreCodice.getMessaggioDiDefault());
        this.erroreCodice = erroreCodice;
    }

    public BusinessException(ErroreCodice erroreCodice, String messaggioCustom) {
        super(messaggioCustom);
        this.erroreCodice = erroreCodice;
    }

    public ErroreCodice getErroreCodice() {
        return erroreCodice;
    }
}
