package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "wishlist")
public class Wishlist {



    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "utente_id", nullable = false, unique = true)
    private Utente utente;

    @ManyToMany
    @JoinTable(
        name = "wishlist_prodotti",
        joinColumns = @JoinColumn(name = "wishlist_id"),
        inverseJoinColumns = @JoinColumn(name = "prodotto_id")
    )
    private java.util.List<Prodotto> prodotti = new java.util.ArrayList<>();

    @Column(name = "data_creazione", updatable = false)
    private LocalDateTime dataCreazione;
    
    @PrePersist
    protected void onCreate() {
        this.dataCreazione = LocalDateTime.now();
    }
    
}//Wishlist
