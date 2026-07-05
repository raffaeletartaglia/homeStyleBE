package org.example.homestylebe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdottoSuggerimentoDTO {
    private UUID id;
    private String nomeProdotto;
}
