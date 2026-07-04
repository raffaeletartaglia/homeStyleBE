package org.example.homestylebe.exception;



// StockInsufficienteException
public class StockInsufficienteException extends BusinessException {
    public StockInsufficienteException() {
        super(ErroreCodice.PRODOTTO_STOCK_INSUFFICIENTE);
    }
}
