package repository;

import entity.Prenotazione;
import entity.Utente;
import entity.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, UUID> {

    // Trova tutte le prenotazioni di un utente
    List<Prenotazione> findByUtente(Utente utente);

    // Trova tutte le prenotazioni di un prodotto
    List<Prenotazione> findByProdotto(Prodotto prodotto);

    // Trova tutte le prenotazioni di un certo stato
    List<Prenotazione> findByStato(Prenotazione.Stato stato);

    // Trova tutte le prenotazioni con disponibilità prevista entro una certa data
    List<Prenotazione> findByDataPrevistaDisponibilitaBefore(LocalDate data);
    
    List<Prenotazione> findByProdottoAndStatoOrderByDataPrenotazioneAsc(Prodotto prodotto,Prenotazione.Stato stato);
    
}//PrenotazioneRepository
