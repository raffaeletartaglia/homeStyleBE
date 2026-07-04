package org.example.homestylebe.exception;



public class OperazioneNonConsentitaException extends BusinessException {
    public OperazioneNonConsentitaException(String messaggioCustom, ErroreCodice prenotazioneStatoNonValido) {
        super(ErroreCodice.OPERAZIONE_NON_CONSENTITA, messaggioCustom);
    }
}
