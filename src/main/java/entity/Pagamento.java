package entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = {"ordine", "modalitaPagamento", "cartaPagamento"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;


    // Ordine -> Pagamento 1:1 
    @OneToOne
    @JoinColumn(name = "ordine_id", nullable = false, unique = true)
    private Ordine ordine;

    // Modalità di pagamento
    @ManyToOne
    @JoinColumn(name = "modalita_pagamento_id", nullable = false)
    private ModalitaPagamento modalitaPagamento;

    // Carta di pagamento (opzionale)
    @ManyToOne
    @JoinColumn(name = "carta_pagamento_id")
    private CartaPagamento cartaPagamento;


    @Column(name = "pagamento_effettuato")
    private Boolean pagamentoEffettuato;
    
    @Column(name = "numero_rate", nullable = false)
    private Integer numeroRate = 1;

    @Column(name = "rata_corrente", nullable = false)
    private Integer rataCorrente = 1;
    
    @Column(name = "importo", precision = 10, scale = 2)
    private BigDecimal importo;

    @Column(name = "importo_rata", precision = 10, scale = 2)
    private BigDecimal importoRata;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "fattura")
    private String fattura;

    @Column(name = "importo_rimborsato", precision = 10, scale = 2)
    private BigDecimal importoRimborsato;

    @Column(name = "data_rimborso")
    private LocalDateTime dataRimborso;
    
}//Pagamento
