package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@ToString(exclude = "ordine")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "spedizione")
public class Spedizione {

    public enum StatoSpedizione {
        ANNULLATO,
        PREPARAZIONE,
        SPEDITO,
        IN_TRANSITO,
        CONSEGNATO
    }//StatoSpedizione

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name ="id")
    private UUID id;

    
    @ManyToOne
    @JoinColumn(name = "ordine_id", nullable = false)
    private Ordine ordine;

    @Column(name = "corriere")
    private String corriere;

    @Column(name = "codice_tracking")
    private String codiceTracking;

    @Column(name = "data_spedizione")
    private LocalDateTime dataSpedizione;

    @Column(name = "data_consegna_effettiva")
    private LocalDateTime dataConsegnaEffettiva;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_spedizione")
    private StatoSpedizione statoSpedizione;
    
}//Spedizione
