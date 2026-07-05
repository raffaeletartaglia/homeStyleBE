package org.example.homestylebe.repository;

import org.example.homestylebe.entity.Reso;
import org.example.homestylebe.entity.DettaglioOrdine;
import org.example.homestylebe.entity.Indirizzo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResoRepository extends JpaRepository<Reso, UUID> {

    // Trova il reso relativo a un dettaglio ordine specifico
    Optional<Reso> findByDettaglioOrdine(DettaglioOrdine dettaglioOrdine);

    // Trova tutti i resi previsti in una certa data
    List<Reso> findByDataResoPrevista(LocalDate data);

    // Trova tutti i resi legati a un indirizzo specifico
    List<Reso> findByIndirizzoReso(Indirizzo indirizzoReso);

    Optional<Reso> findByDettaglioOrdine_Id(UUID dettaglioOrdineId);
}//ResoRepository
