package repository;

import com.homestyle.demo.entity.Pagamento;
import com.homestyle.demo.entity.Ordine;
import com.homestyle.demo.entity.ModalitaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {

    // Trova il pagamento relativo a un ordine specifico
    Optional<Pagamento> findByOrdine(Ordine ordine);

    // Trova tutti i pagamenti di una certa modalità
    List<Pagamento> findByModalitaPagamento(ModalitaPagamento modalitaPagamento);

    // Trova tutti i pagamenti già effettuati
    List<Pagamento> findByPagamentoEffettuatoTrue();

}//PagamentoRepository
