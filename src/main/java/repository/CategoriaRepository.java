package repository;


import entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
	
	/**
	 * Il metodo serve per trovare le categorie in base al nome
	 * @param nomeCategoria nome della categortia
	 * @return lista delle categorie
	 */
    Categoria findByNomeCategoria(String nomeCategoria);

 

}//CategoriaRepository
