package exception;



public class WishlistItemGiaEsistenteException extends BusinessException {

    public WishlistItemGiaEsistenteException() {
        super(ErroreCodice.WISHLIST_ITEM_GIA_ESISTENTE);
    }
}
