package org.example.homestylebe.repository;

import org.example.homestylebe.entity.Wishlist;
import org.example.homestylebe.entity.Utente;
import org.example.homestylebe.entity.Prodotto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {

    // Trova l'unica wishlist di un utente
    Optional<Wishlist> findByUtente_Id(UUID utenteId);

    void deleteByUtente_Id(UUID utenteId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p FROM Wishlist w JOIN w.prodotti p LEFT JOIN p.categoria c " +
           "WHERE w.utente.id = :utenteId " +
           "AND (:query IS NULL OR :query = '' OR LOWER(p.nomeProdotto) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:categoriaId IS NULL OR c.id = :categoriaId)")
    List<Prodotto> ricercaProdottiInWishlist(@org.springframework.data.repository.query.Param("utenteId") UUID utenteId, 
                                             @org.springframework.data.repository.query.Param("query") String query, 
                                             @org.springframework.data.repository.query.Param("categoriaId") UUID categoriaId);
    
}//WishlistRepository
