package org.example.homestylebe.repository;

import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p FROM Prodotto p LEFT JOIN p.categoria c LEFT JOIN c.stanze s " +
           "WHERE LOWER(p.nomeProdotto) LIKE :query " +
           "OR LOWER(p.marca) LIKE :query " +
           "OR LOWER(c.nomeCategoria) LIKE :query " +
           "OR LOWER(CAST(s.tipologia AS String)) LIKE :query")
    List<Prodotto> ricercaCompletaSuggerimenti(@Param("query") String query);

    // Filtra prodotti per categoriaId (paginato)
    @Query("SELECT p FROM Prodotto p WHERE p.categoria.id = :categoriaId")
    Page<Prodotto> findByCategoriaId(@Param("categoriaId") UUID categoriaId, Pageable pageable);

    // Filtra prodotti per stanzaId (paginato) - naviga la relazione categoria -> stanze
    @Query("SELECT DISTINCT p FROM Prodotto p JOIN p.categoria c JOIN c.stanze s WHERE s.id = :stanzaId")
    Page<Prodotto> findByStanzaId(@Param("stanzaId") UUID stanzaId, Pageable pageable);

    // Filtra prodotti per stanzaId + categoriaId (paginato)
    @Query("SELECT DISTINCT p FROM Prodotto p JOIN p.categoria c JOIN c.stanze s WHERE s.id = :stanzaId AND c.id = :categoriaId")
    Page<Prodotto> findByStanzaIdAndCategoriaId(@Param("stanzaId") UUID stanzaId, @Param("categoriaId") UUID categoriaId, Pageable pageable);

    // Ricerca full-text paginata
    @Query("SELECT DISTINCT p FROM Prodotto p LEFT JOIN p.categoria c LEFT JOIN c.stanze s " +
           "WHERE LOWER(p.nomeProdotto) LIKE :query " +
           "OR LOWER(p.marca) LIKE :query " +
           "OR LOWER(c.nomeCategoria) LIKE :query " +
           "OR LOWER(CAST(s.tipologia AS String)) LIKE :query")
    Page<Prodotto> ricercaCompletaPaginata(@Param("query") String query, Pageable pageable);

}//ProdottoRepository
