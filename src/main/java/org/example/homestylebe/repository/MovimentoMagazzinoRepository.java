package org.example.homestylebe.repository;

import org.example.homestylebe.entity.MovimentoMagazzino;
import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.entity.Ordine;
import org.example.homestylebe.entity.Reso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovimentoMagazzinoRepository extends JpaRepository<MovimentoMagazzino, UUID> {

    // Trova tutti i movimenti di un prodotto specifico
    List<MovimentoMagazzino> findByProdotto(Prodotto prodotto);

    // Trova tutti i movimenti di un ordine
    List<MovimentoMagazzino> findByOrdine(Ordine ordine);

    // Trova tutti i movimenti di un reso
    List<MovimentoMagazzino> findByReso(Reso reso);

    // Trova tutti i movimenti di un certo tipo
    List<MovimentoMagazzino> findByTipoMovimento(MovimentoMagazzino.TipoMovimento tipoMovimento);
    
}//MovimentoMagazzinoRepository
