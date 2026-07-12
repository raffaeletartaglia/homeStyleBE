package org.example.homestylebe.entity;

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
@ToString(exclude = {"ordini", "resi", "utente"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "indirizzo")
public class Indirizzo {
	
	public enum Tipo{
		RESIDENZA,
		SPEDIZIONE,
		FATTURAZIONE
	}//Tipo


	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "utente_id", nullable = false)
	private Utente utente;
	
	@Column(name = "nazione")
	private String nazione;
	
	@Column(name = "via")
	private String via;
	
	@Column(name = "numero_civico")
	private String numeroCivico;
	
	@Column(name = "citta")
	private String citta;
	
	@Column(name = "provincia")
	private String provincia;
	
	@Column(name = "cap")
	private String cap;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo", nullable = false)
	private Tipo tipo;

	@Column(name = "is_default")
	private boolean isDefault;

	@Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
	private boolean isDeleted = false;
	
	//RELAZIONE
	//indirizzo->Ordine(1:n)
	@OneToMany(mappedBy = "indirizzoSpedizione")
	private List<Ordine> ordini = new ArrayList<>();
	
	@OneToMany(mappedBy = "indirizzoReso")
	private List<Reso> resi = new ArrayList<>();

}//Indirizzo
