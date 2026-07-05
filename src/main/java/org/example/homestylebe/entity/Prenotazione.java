package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "prenotazione")
public class Prenotazione {

    public enum Stato {
        ATTIVA,
        ESEGUITA,
        ANNULLATA
    }//Stato

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    
    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    
    @Column(name ="quantita", nullable = false)
    private Integer quantita;

    @Column(name = "data_prenotazione", updatable = false)
    private LocalDateTime dataPrenotazione;
    
    @PrePersist
    protected void onCreate() {
        this.dataPrenotazione = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private Stato stato;

    @Column(name = "data_prevista_disponibilita")
    private LocalDate dataPrevistaDisponibilita;

    @Version
    @Column(name = "version")
    private Long version;

}//Prenotazione
