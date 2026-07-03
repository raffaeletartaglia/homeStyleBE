package repository;

import com.homestyle.demo.entity.Wishlist;
import com.homestyle.demo.entity.Utente;
import com.homestyle.demo.entity.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {

    // Trova tutti i prodotti in wishlist di un utente
    List<Wishlist> findByUtente(Utente utente);

    // Trova un prodotto specifico nella wishlist di un utente
    Optional<Wishlist> findByUtenteAndProdotto(Utente utente, Prodotto prodotto);

    // Trova tutti i prodotti con una certa priorità per un utente
    List<Wishlist> findByUtenteAndPriorita(Utente utente, Wishlist.Priorita priorita);

    List<Wishlist> findByUtente_IdOrderByDataAggiuntaDesc(UUID utenteId);

    Optional<Wishlist> findByUtente_IdAndProdotto_Id(UUID utenteId, UUID prodottoId);

    void deleteByUtente_Id(UUID utenteId);
    
}//WishlistRepository
