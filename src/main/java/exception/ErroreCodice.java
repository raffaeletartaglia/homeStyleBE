package exception;

import org.springframework.http.HttpStatus;

public enum ErroreCodice {
    // Prodotto / Stock
    PRODOTTO_NON_DISPONIBILE("PROD_001", "Prodotto non disponibile", HttpStatus.BAD_REQUEST),
    PRODOTTO_STOCK_INSUFFICIENTE("PROD_002", "Stock insufficiente", HttpStatus.BAD_REQUEST),
    
    // Ordine / Reso / Carrello / Wishlist
    ORDINE_GIA_ANNULLATO("ORDINE_001", "Ordine già annullato", HttpStatus.BAD_REQUEST),
    ORDINE_GIA_CONSEGNATO("ORDINE_002", "Ordine già consegnato", HttpStatus.BAD_REQUEST),
    RESO_GIA_ESISTENTE("RESO_001", "Reso già esistente", HttpStatus.CONFLICT),
    CARRELLO_NON_ATTIVO("CARR_001", "Carrello non attivo", HttpStatus.BAD_REQUEST),
    WISHLIST_ITEM_GIA_ESISTENTE("WISH_001", "Articolo già in wishlist", HttpStatus.CONFLICT),
    OPERAZIONE_NON_CONSENTITA("OP_001", "Operazione non consentita", HttpStatus.FORBIDDEN),
    
    // Generiche
    ERRORE_INTERNO("ERR_500", "Errore interno del server", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_JWT_NON_VALIDO("AUTH_001", "Token JWT non valido", HttpStatus.UNAUTHORIZED),
    CATEGORIA_NON_TROVATA("CAT_001", "Categoria non trovata", HttpStatus.NOT_FOUND),
    
    // Altre non trovate
    ENTITA_NON_TROVATA("ERR_404", "Entità non trovata", HttpStatus.NOT_FOUND),
    
    // Parametri
    UTENTE_NON_TROVATO("UTENTE_001", "Utente non trovato", HttpStatus.NOT_FOUND),
    UTENTE_EMAIL_GIA_REGISTRATA("UTENTE_002", "Email già registrata", HttpStatus.CONFLICT),
    UTENTE_EMAIL_NON_VALIDA("UTENTE_003", "Email non valida", HttpStatus.BAD_REQUEST),
    UTENTE_PASSWORD_NON_VALIDA("UTENTE_004", "Password non valida", HttpStatus.BAD_REQUEST),
    UTENTE_NUMERO_GIA_REGISTRATO("UTENTE_005", "Numero di telefono già registrato", HttpStatus.CONFLICT),
    PRENOTAZIONE_STATO_NON_VALIDO("PREN_001", "Stato prenotazione non valido", HttpStatus.BAD_REQUEST),
    RECENSIONE_GIA_ESISTENTE("REC_001", "Recensione già esistente", HttpStatus.CONFLICT);

    private final String codice;
    private final String messaggioDiDefault;
    private final HttpStatus httpStatus;

    ErroreCodice(String codice, String messaggioDiDefault, HttpStatus httpStatus) {
        this.codice = codice;
        this.messaggioDiDefault = messaggioDiDefault;
        this.httpStatus = httpStatus;
    }

    public String getCodice() { return codice; }
    public String getMessaggioDiDefault() { return messaggioDiDefault; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}
