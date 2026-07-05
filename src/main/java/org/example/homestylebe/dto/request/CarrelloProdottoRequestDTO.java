package org.example.homestylebe.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class CarrelloProdottoRequestDTO {

    private UUID carrelloId;

    private UUID prodottoId;

    private Integer quantita;

}// CarrelloProdottoRequestDTO
