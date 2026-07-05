package org.example.homestylebe.dto.request;

import lombok.Data;
import java.util.UUID;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class RecensioneRequestDTO {

    private UUID dettaglioOrdineId;

    @NotNull
    private UUID utenteId;

   
    private Integer valutazioneProdotto;

    
    private Integer valutazioneConsegna;

    
    private Integer valutazioneMontaggio;

    private String commento;

}//RecensioneRequestDTO
