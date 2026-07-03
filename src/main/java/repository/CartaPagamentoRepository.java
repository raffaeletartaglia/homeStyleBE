package repository;

import entity.CartaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartaPagamentoRepository extends JpaRepository<CartaPagamento, UUID> {

    // Trova tutte le carte di un utente
	List<CartaPagamento> findByUtenteId(UUID utenteId);

    // Trova una carta specifica di un utente (utile per controlli di unicità)
	Optional<CartaPagamento> findByUtenteIdAndNumeroCarta(UUID utenteId, String numeroCarta);

}//CartaPagamentoRepository
