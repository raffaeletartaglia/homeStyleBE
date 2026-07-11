package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import org.example.homestylebe.dto.response.ProdottoResponseDTO;

@Data
public class WishlistResponseDTO {

    private UUID id;

    private UUID utenteId;

    private java.util.List<ProdottoResponseDTO> prodotti;

    private LocalDateTime dataCreazione;

}//WishlistResponseDTO
