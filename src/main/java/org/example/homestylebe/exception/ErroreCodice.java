package org.example.homestylebe.exception;

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
    RESO_NON_TROVATO("RESO_404", "Reso non trovato", HttpStatus.NOT_FOUND),
    PRODOTTO_NON_TROVATO("PROD_404", "Prodotto non trovato", HttpStatus.NOT_FOUND),
    ORDINE_NON_TROVATO("ORDINE_404", "Ordine non trovato", HttpStatus.NOT_FOUND),
    MODALITA_PAGAMENTO_NON_TROVATA("MODPAG_404", "Modalità pagamento non trovata", HttpStatus.NOT_FOUND),
    CARTA_PAGAMENTO_NON_TROVATA("CARTAPAG_404", "Carta pagamento non trovata", HttpStatus.NOT_FOUND),
    PAGAMENTO_IMPORTO_NON_VALIDO("PAG_001", "Importo pagamento non valido", HttpStatus.BAD_REQUEST),
    PAGAMENTO_NON_TROVATO("PAG_404", "Pagamento non trovato", HttpStatus.NOT_FOUND),
    INDIRIZZO_NON_TROVATO("IND_404", "Indirizzo non trovato", HttpStatus.NOT_FOUND),
    WISHLIST_ITEM_NON_TROVATO("WISH_404", "Articolo wishlist non trovato", HttpStatus.NOT_FOUND),
    DETTAGLIO_ORDINE_NON_TROVATO("DETT_404", "Dettaglio ordine non trovato", HttpStatus.NOT_FOUND),
    RECENSIONE_NON_TROVATA("REC_404", "Recensione non trovata", HttpStatus.NOT_FOUND),
    PAGAMENTO_GIA_EFFETTUATO("PAG_002", "Pagamento già effettuato", HttpStatus.BAD_REQUEST),
    STANZA_DUPLICATA("STANZA_001", "Stanza duplicata", HttpStatus.CONFLICT),
    CARRELLO_NON_TROVATO("CARR_404", "Carrello non trovato", HttpStatus.NOT_FOUND),
    CARRELLO_VUOTO("CARR_002", "Carrello vuoto", HttpStatus.BAD_REQUEST),
    MOVIMENTO_MAGAZZINO_NON_TROVATO("MOV_404", "Movimento magazzino non trovato", HttpStatus.NOT_FOUND),
    SPEDIZIONE_NON_TROVATA("SPED_404", "Spedizione non trovata", HttpStatus.NOT_FOUND),
    CARRELLO_PRODOTTO_NON_TROVATO("CARRPROD_404", "Prodotto nel carrello non trovato", HttpStatus.NOT_FOUND),

    STANZA_NON_TROVATA("STANZA_404", "Stanza non trovata", HttpStatus.NOT_FOUND),
    CARRELLO_PRODOTTO_QUANTITA_NON_VALIDA("CARRPROD_001", "Quantità non valida per prodotto nel carrello", HttpStatus.BAD_REQUEST),
    CARRELLO_GIA_ESISTENTE_PER_UTENTE("CARR_001", "Carrello già esistente per questo utente", HttpStatus.CONFLICT),
    CATEGORIA_DESCRIZIONE_NON_VALIDA("CAT_001", "Descrizione categoria non valida", HttpStatus.BAD_REQUEST),
    CATEGORIA_DUPLICATA("CAT_002", "Categoria duplicata", HttpStatus.CONFLICT),
    MOVIMENTO_MAGAZZINO_NON_VALIDO("MOV_001", "Movimento magazzino non valido", HttpStatus.BAD_REQUEST),
    MOVIMENTO_MAGAZZINO_NOTE_NON_VALIDE("MOV_002", "Note movimento magazzino non valide", HttpStatus.BAD_REQUEST),
    MOVIMENTO_MAGAZZINO_DATA_NON_VALIDA("MOV_003", "Data movimento magazzino non valida", HttpStatus.BAD_REQUEST),
    MOVIMENTO_MAGAZZINO_TIPO_NON_VALIDO("MOV_004", "Tipo movimento magazzino non valido", HttpStatus.BAD_REQUEST),
    MOVIMENTO_MAGAZZINO_QUANTITA_NON_VALIDA("MOV_005", "Quantità movimento magazzino non valida", HttpStatus.BAD_REQUEST),
    MOVIMENTO_MAGAZZINO_TIPO_NON_GESTITO("MOV_006", "Tipo movimento magazzino non gestito", HttpStatus.BAD_REQUEST),
    
    // Parametri
    UTENTE_NON_TROVATO("UTENTE_001", "Utente non trovato", HttpStatus.NOT_FOUND),
    UTENTE_EMAIL_GIA_REGISTRATA("UTENTE_002", "Email già registrata", HttpStatus.CONFLICT),
    UTENTE_EMAIL_NON_VALIDA("UTENTE_003", "Email non valida", HttpStatus.BAD_REQUEST),
    UTENTE_PASSWORD_NON_VALIDA("UTENTE_004", "Password non valida", HttpStatus.BAD_REQUEST),
    UTENTE_NUMERO_GIA_REGISTRATO("UTENTE_005", "Numero di telefono già registrato", HttpStatus.CONFLICT),
    UTENTE_NUMERO_TELEFONO_NON_VALIDO("UTENTE_006", "Numero di telefono non valido", HttpStatus.BAD_REQUEST),

    ORDINE_STATO_NON_VALIDO("ORDINE_003", "Stato ordine non valido", HttpStatus.BAD_REQUEST),
    ERRORE_VALIDAZIONE("VAL_001", "Errore di validazione", HttpStatus.BAD_REQUEST),
    CARTA_PAGAMENTO_NON_VALIDA("CARTA_001", "Carta di pagamento non valida", HttpStatus.BAD_REQUEST),
    INDIRIZZO_NON_VALIDO("IND_001", "Indirizzo non valido", HttpStatus.BAD_REQUEST),
    RECENSIONE_GIA_ESISTENTE("REC_001", "Recensione già esistente", HttpStatus.CONFLICT),
    RECENSIONE_VALUTAZIONE_NON_VALIDA("REC_002", "Valutazione recensione non valida", HttpStatus.BAD_REQUEST);

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
