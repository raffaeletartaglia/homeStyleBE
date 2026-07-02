package entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = {"prodotti", "stanze"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "categoria")
public class Categoria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "nome_categoria", nullable = false, unique=true)
	private String nomeCategoria;
	
	@Column(name = "descrizione")
	private String descrizione;
	
	//RELAZIONI
	
	//categoria->Prodotto (1:n)
	@OneToMany(mappedBy="categoria",cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Prodotto> prodotti = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(
		name = "categoria_stanza",
		joinColumns = @JoinColumn(name = "categoria_id"),
		inverseJoinColumns = @JoinColumn(name = "stanza_id")
	)
	private List<Stanza> stanze = new ArrayList<>();

}//Categoria
