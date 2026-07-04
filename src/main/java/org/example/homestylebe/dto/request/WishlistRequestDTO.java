package org.example.homestylebe.dto.request;

import lombok.Data;
import java.util.UUID;


@Data
public class WishlistRequestDTO {
	
	public enum PrioritaWishlist {
	    BASSA,
	    MEDIA,
	    ALTA
	}//PrioritaWishlist

    private UUID utenteId;

    private UUID prodottoId;

    private PrioritaWishlist priorita; // BASSA, MEDIA, ALTA

}// WishlistRequestDTO
