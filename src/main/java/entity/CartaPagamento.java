package entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "carta_pagamento", uniqueConstraints = {@UniqueConstraint(columnNames = {"utente_id", "numero_carta"})})
public class CartaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    // RELAZIONE con Utente
    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @Column(name = "numero_carta", nullable = false)
    private String numeroCarta;

    @Column(name = "intestatario", nullable = false)
    private String intestatario;

    @Column(name = "scadenza", nullable = false)
    private LocalDate scadenza;

    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Column(name = "tipo_carta")
    private String tipoCarta;
    
}//CartaPagamento
