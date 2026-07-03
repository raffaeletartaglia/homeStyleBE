package repository;

import entity.Prodotto;
import entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, UUID> {

    // Filtrare prodotti per categoria
    List<Prodotto> findByCategoria(Categoria categoria);

    // Filtrare prodotti per marca
    List<Prodotto> findByMarca(String marca);

    // Filtrare prodotti con prezzo minore di un valore
    List<Prodotto> findByPrezzoLessThan(BigDecimal prezzo);

    // Filtrare prodotti disponibili per montaggio incluso
    List<Prodotto> findByIncludeMontaggioTrue();

    // Filtrare prodotti con quantità inferiore alla soglia di riordino (magazzino)
    List<Prodotto> findByQuantitaRiordinoStandardLessThan(Integer soglia);
    
}//ProdottoRepository
