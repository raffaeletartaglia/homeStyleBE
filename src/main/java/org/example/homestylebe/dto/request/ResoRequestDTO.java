package org.example.homestylebe.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ResoRequestDTO {

    private UUID dettaglioOrdineId;

    private LocalDate dataResoPrevista;

    private LocalTime oraRitiroReso;

    private String motivo;

    private UUID indirizzoId;

}//ResoRequestDTO 
