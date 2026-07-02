package entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "movimento_magazzino")
public class MovimentoMagazzino {

    public enum TipoMovimento {
        PRODUZIONE,
        SCARICO_VENDITA,
        RESO_CLIENTE,
        ANNULLAMENTO_ORDINE,
        RETTIFICA_INVENTARIO
    }//TipoMovimento

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    
    @ManyToOne
    @JoinColumn(name = "prodotto_id")
    private Prodotto prodotto;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento")
    private TipoMovimento tipoMovimento;

    @Column(name = "quantita")
    private Integer quantita;

    @Column(name = "data_movimento")
    private LocalDateTime dataMovimento;
    
    @PrePersist
    protected void onCreate() {
        this.dataMovimento = LocalDateTime.now();
    }
    

    @ManyToOne
    @JoinColumn(name = "ordine_id")
    private Ordine ordine;

    @ManyToOne
    @JoinColumn(name = "reso_id")
    private Reso reso;


    @Column(name = "note")
    private String note;

    
    
}//MovimentoMagazzino
