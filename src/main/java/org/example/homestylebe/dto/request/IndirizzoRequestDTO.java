package org.example.homestylebe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.UUID;
import org.example.homestylebe.entity.Indirizzo;

import jakarta.validation.constraints.NotBlank;

@Data
public class IndirizzoRequestDTO {

    @jakarta.validation.constraints.NotNull(message = "L'ID dell'utente non può essere nullo")
    private UUID utenteId;

    @NotBlank(message = "La nazione non può essere nulla")
    private String nazione;

    @NotBlank(message = "La via non può essere nulla")
    private String via;

    @NotBlank(message = "Il numero civico non può essere nullo")
    private String numeroCivico;

    @NotBlank(message = "La città non può essere nulla")
    private String citta;

    @NotBlank(message = "La provincia non può essere nulla")
    private String provincia;

    @NotBlank(message = "Il CAP non può essere nullo")
    private String cap;

    @NotBlank(message = "Il tipo di indirizzo non può essere nullo")
    private Indirizzo.Tipo tipo;

    @JsonProperty("isDefault")
    private boolean isDefault;

}// IndirizzoRequestDTO
