package org.example.homestylebe.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MovimentoMagazzinoResponseDTO {

    private UUID id;

    private UUID prodottoId;

    private String tipoMovimento; // PRODUZIONE, SCARICO_VENDITA, ecc.

    private Integer quantita;

    private LocalDateTime dataMovimento;

    private UUID ordineId; // opzionale, se collegato a un ordine

    private UUID resoId;   // opzionale, se collegato a un reso

    private String note;

}//MovimentoMagazzinoResponseDTO
