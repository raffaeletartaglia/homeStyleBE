package repository;


import entity.Carrello;
import entity.CarrelloProdotto;
import entity.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrelloProdottoRepository extends JpaRepository<CarrelloProdotto, UUID> {

    // Trova tutti i prodotti in un carrello specifico
    List<CarrelloProdotto> findByCarrello(Carrello carrello);

    // Trova un prodotto specifico in un carrello
    Optional<CarrelloProdotto> findByCarrelloAndProdotto(Carrello carrello, Prodotto prodotto);

    // Trova tutte le occorrenze di un prodotto in tutti i carrelli (utile per statistiche)
    List<CarrelloProdotto> findByProdotto(Prodotto prodotto);

    List<CarrelloProdotto> findByCarrello_Id(UUID carrelloId);
}//CarrelloProdottoRepository
