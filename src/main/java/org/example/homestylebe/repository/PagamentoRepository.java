package org.example.homestylebe.repository;

import org.example.homestylebe.entity.Pagamento;
import org.example.homestylebe.entity.Ordine;
import org.example.homestylebe.entity.ModalitaPagamento;
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
