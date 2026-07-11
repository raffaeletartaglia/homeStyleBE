package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = {"dettagliOrdine", "carrelloProdotti", "wishlist", "movimentiMagazzino"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "prodotto")
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name ="marca")
    private String marca;

    @Column(name = "nome_prodotto")
    private String nomeProdotto;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "colore")
    private String colore;

    @Column(name = "modello")
    private String modello;

    @Column(name = "prezzo", precision = 10, scale = 2)
    private BigDecimal prezzo;

    @Column(name ="descrizione")
    private String descrizione;

    @Column(name = "include_montaggio")
    private Boolean includeMontaggio;

    // gestione magazzino (solo admin)
    @Column(name = "soglia_riordino")
    private Integer sogliaRiordino;

    @Column(name = "quantita_riordino_standard")
    private Integer quantitaRiordinoStandard;

    @Column(name = "data_prossima_disponibilita")
    private LocalDate dataProssimaDisponibilita;

    @Column(name = "giacenza")
    private Integer giacenza;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "immagine", columnDefinition = "BYTEA")
    private byte[] immagine;

    // RELAZIONI
    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DettaglioOrdine> dettagliOrdine = new ArrayList<>();

    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarrelloProdotto> carrelloProdotti = new ArrayList<>();

    @ManyToMany(mappedBy = "prodotti")
    private List<Wishlist> wishlist = new ArrayList<>();

    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentoMagazzino> movimentiMagazzino = new ArrayList<>();

}//Prodotto
