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
@Entity
@ToString(exclude = "carrello")
@EqualsAndHashCode(of = "id")
@Table(name = "carrello_prodotto", uniqueConstraints = @UniqueConstraint(columnNames = {"carrello_id", "prodotto_id"}))
public class CarrelloProdotto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "carrello_id", nullable = false)
    private Carrello carrello;

    @ManyToOne
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;
    
    @Column(name ="quantita", nullable = false)
    private Integer quantita;

    @Column(name = "data_aggiunta", updatable = false)
    private LocalDateTime dataAggiunta;

    @PrePersist
    protected void onCreate() {
        this.dataAggiunta = LocalDateTime.now();
    }
    
}//CarrelloProdotto
