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
    
}//WishlistRepository
