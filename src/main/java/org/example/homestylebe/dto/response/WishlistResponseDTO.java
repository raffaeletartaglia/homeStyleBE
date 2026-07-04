package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import org.example.homestylebe.dto.response.ProdottoResponseDTO;

@Data
public class WishlistResponseDTO {

    private UUID id;

    private UUID utenteId;

    private ProdottoResponseDTO prodotto;

    private LocalDateTime dataAggiunta;

    private String priorita;

}//WishlistResponseDTO 
