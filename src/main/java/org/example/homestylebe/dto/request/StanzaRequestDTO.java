package org.example.homestylebe.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
@Data
public class StanzaRequestDTO {

    @NotBlank(message = "La tipologia è obbligatoria")
    private String tipologia;

}
