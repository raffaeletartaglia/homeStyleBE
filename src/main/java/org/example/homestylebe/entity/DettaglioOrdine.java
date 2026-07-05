package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = {"ordine", "recensioni", "reso"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "dettaglio_ordine",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ordine_id", "prodotto_id"})
)
public class DettaglioOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ordine_id", nullable = false)
    private Ordine ordine;

    @ManyToOne
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    
    @Column(name = "quantita", nullable = false)
    private Integer quantita;
    

    @Column(name = "prezzo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoUnitario;

    // RELAZIONI

    @OneToOne(mappedBy = "dettaglioOrdine", cascade = CascadeType.ALL, orphanRemoval = true)
    private Reso reso;

    @OneToMany(mappedBy = "dettaglioOrdine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recensione> recensioni = new ArrayList<>();

}//DettaglioOrdine
