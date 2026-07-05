package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "recensione")
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    
    @ManyToOne
    @JoinColumn(name = "dettaglio_ordine_id")
    private DettaglioOrdine dettaglioOrdine;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;

    
    @Column(name = "valutazione_prodotto")
    private Integer valutazioneProdotto;

    @Column(name = "valutazione_consegna")
    private Integer valutazioneConsegna;

    @Column(name = "valutazione_montaggio")
    private Integer valutazioneMontaggio;

    @Column(name ="commento")
    private String commento;

    @Column(name = "data_recensione")
    private LocalDateTime dataRecensione;

    @PrePersist
    protected void onCreate() {
        this.dataRecensione = LocalDateTime.now();
    }
    
}//Recensione
