package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "modalita_pagamento")
public class ModalitaPagamento {
	
	public enum Tipo{
		FISICO,
		ONLINE
	}//Tipo
	
	@Id
	@GeneratedValue(strategy =  GenerationType.UUID)
	@Column(name = "id")
	private UUID id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo", nullable = false)
	private Tipo tipo;
	
	@Column(name = "descrizione")
	private String descrizione;


	// ModalitaPagamento->Pagamento (1:n)
    @OneToMany(mappedBy = "modalitaPagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamenti = new ArrayList<>();
	
}//ModalitaPagamento
