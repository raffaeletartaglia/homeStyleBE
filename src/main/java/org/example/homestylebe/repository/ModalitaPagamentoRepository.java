package org.example.homestylebe.repository;

import org.example.homestylebe.entity.ModalitaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface ModalitaPagamentoRepository extends JpaRepository<ModalitaPagamento, UUID> {

    // opzionale, se vuoi filtrare per tipo
    List<ModalitaPagamento> findByTipo(ModalitaPagamento.Tipo tipo);

}//ModalitaPagamentoRepository
