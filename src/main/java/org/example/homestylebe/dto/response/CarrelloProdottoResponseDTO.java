package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CarrelloProdottoResponseDTO {

    private UUID id;

    private Integer quantita;

    private LocalDateTime dataAggiunta;

    private ProdottoResponseDTO prodotto;

}//CarrelloProdottoResponseDTO
