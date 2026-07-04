package org.example.homestylebe.exception;



public class TokenNonValidoException extends BusinessException {
    public TokenNonValidoException(String message) {
        super(ErroreCodice.TOKEN_JWT_NON_VALIDO);
    }
}
