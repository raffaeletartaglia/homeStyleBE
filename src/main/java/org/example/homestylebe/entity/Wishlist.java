package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "wishlist", uniqueConstraints = @UniqueConstraint(columnNames = {"utente_id","prodotto_id"}))
public class Wishlist {

    public enum Priorita {
        BASSA,
        MEDIA,
        ALTA
    }//Priorita

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

    
    @Column(name = "data_aggiunta", updatable = false)
    private LocalDateTime dataAggiunta;
    
    @PrePersist
    protected void onCreate() {
        this.dataAggiunta = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "priorita")
    private Priorita priorita;

    
    
}//Wishlist
