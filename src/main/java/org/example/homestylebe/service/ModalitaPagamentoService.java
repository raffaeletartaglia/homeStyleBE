package org.example.homestylebe.service;

import java.util.List;
import java.util.UUID;

import org.example.homestylebe.entity.ModalitaPagamento;
import org.example.homestylebe.exception.EntitaNonTrovataException;
import org.example.homestylebe.repository.ModalitaPagamentoRepository;
import org.example.homestylebe.utils.ControlliUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModalitaPagamentoService {
	
	private final ModalitaPagamentoRepository modPagamentoRepo;
	
	
	public List<ModalitaPagamento> getAllModalitaPagamento() {
	    log.info("Recupero tutte le modalità di pagamento");
	    
	    List<ModalitaPagamento> lista = modPagamentoRepo.findAll();
	    
	    log.info("Trovate {} modalità di pagamento", lista.size());
	    
	    return lista;
	}//getAllModalitaPagamento
	
	
	
	public ModalitaPagamento getModalitaPagamentoById(UUID id) {
		log.info("Recupero modalità pagamento id={}", id);
		
		ControlliUtils.controlloIdValido(id, "Modalita Pagamento");
		
		return modPagamentoRepo.findById(id)
                .orElseThrow(() -> {
                	log.error("Modalità pagamento non trovata id={}", id);
                	return new EntitaNonTrovataException("Modalita Pagamento non trovata");
                });
		
	}//getModalitaPagamentoById
	
	
	
	public ModalitaPagamento creaModalitaPagamento(ModalitaPagamento modalitaPagamento) {
		log.info("Creazione nuova modalità di pagamento");

		// Controllo enum
		try {
			controllaTipo(modalitaPagamento.getTipo().name());
		} catch (Exception e) {
			log.error("Tipo modalità pagamento non valido: {}", modalitaPagamento.getTipo());
			throw e;
		}

	    // Controllo descrizione
	    if (!controllaDescrizione(modalitaPagamento.getDescrizione())) {
	    	log.error("Descrizione non valida");
	        throw new IllegalArgumentException("Descrizione non valida");
	    }

	    ModalitaPagamento salvata = modPagamentoRepo.save(modalitaPagamento);
	    
	    log.info("Modalità pagamento creata con id={}", salvata.getId());
	    
	    return salvata;
		
	}//creaModalitaPagamento
	
	
	public ModalitaPagamento modificaModalitaPagamento(UUID id, ModalitaPagamento modalitaPagamento) {
		
		log.info("Modifica modalità pagamento id={}", id);
		
		ControlliUtils.controlloIdValido(id, "Modalita Pagamento");
		 
		ModalitaPagamento vecchioModPag = modPagamentoRepo.findById(id)
                .orElseThrow(() -> {
                	log.error("Modalità pagamento non trovata id={}", id);
                	return new EntitaNonTrovataException("Modalita Pagamento non trovata");
                });
		 
		// Controllo enum
		try {
			controllaTipo(modalitaPagamento.getTipo().name());
		} catch (Exception e) {
			log.error("Tipo modalità pagamento non valido: {}", modalitaPagamento.getTipo());
			throw e;
		}

		// Controllo descrizione
		if (!controllaDescrizione(modalitaPagamento.getDescrizione())) {
			log.error("Descrizione non valida");
		    throw new IllegalArgumentException("Descrizione non valida");
		}

		log.debug("Aggiornamento modalità pagamento id={}", id);
		
		vecchioModPag.setDescrizione(modalitaPagamento.getDescrizione());
		vecchioModPag.setTipo(modalitaPagamento.getTipo());
		    
		ModalitaPagamento aggiornata = modPagamentoRepo.save(vecchioModPag);
		    
		log.info("Modalità pagamento modificata con successo id={}", aggiornata.getId());
		    
		return aggiornata;
		    
	}//modificaModalitaPagamento
	
	
	
	public void deleteModalitaPagamento(UUID id) {
		
		log.info("Eliminazione modalità pagamento id={}", id);
		
		ControlliUtils.controlloIdValido(id, "Modalita Pagamento");
		 
		ModalitaPagamento modPag = modPagamentoRepo.findById(id)
                .orElseThrow(() -> {
                	log.error("Modalità pagamento non trovata id={}", id);
                	return new EntitaNonTrovataException("Modalita Pagamento non trovata");
                });
		 
		modPagamentoRepo.deleteById(id);
		
		log.info("Modalità pagamento eliminata id={}", id);
		
	}//deleteModalitaPagamento
	
	
	
	private boolean controllaDescrizione(String descrizione) {
		log.debug("Controllo descrizione modalità pagamento");
		return descrizione != null && !descrizione.isEmpty() && descrizione.length() < 50;
		
	}//controllaDescrizione
	
	
	private void controllaTipo(String tipo) {
		log.debug("Controllo tipo modalità pagamento: {}", tipo);
		ControlliUtils.controlloValoreEnum(
				ModalitaPagamento.Tipo.class,
				tipo,
				"Tipo"
		);
	}//controllaTipo
	
}//ModalitaPagamentoService
