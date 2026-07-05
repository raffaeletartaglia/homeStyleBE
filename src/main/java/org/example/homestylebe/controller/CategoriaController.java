package org.example.homestylebe.controller;

import org.example.homestylebe.dto.request.CategoriaRequestDTO;
import org.example.homestylebe.dto.response.CategoriaResponseDTO;
import org.example.homestylebe.mapper.CategoriaMapper;
import org.example.homestylebe.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Controller REST per la gestione delle categorie prodotto.
 * <p>
 * Espone endpoint per operazioni CRUD sulle categorie.
 * Le operazioni di lettura sono accessibili a USER e ADMIN,
 * mentre le operazioni di scrittura sono riservate esclusivamente ad ADMIN.
 * </p>
 *
 * @author HomeStyle Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/categorie")
@RequiredArgsConstructor
public class CategoriaController {

	private final CategoriaMapper categoriaMapper;
	private final CategoriaService categoriaService;

	/**
	 * Recupera una categoria tramite il suo identificativo univoco.
	 * <p>
	 * Accessibile da utenti con ruolo USER o ADMIN.
	 * </p>
	 *
	 * @param idCategoria l'UUID della categoria da recuperare
	 * @return {@link ResponseEntity} contenente il {@link CategoriaResponseDTO}
	 *         della categoria trovata
	 */
	@GetMapping("/{idCategoria}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<CategoriaResponseDTO> trovaCategoriaPerId(@PathVariable UUID idCategoria) {
		return ResponseEntity.ok(
				categoriaMapper.toDTO(
						categoriaService.prendiCategoriaPerId(idCategoria)));
	}

	/**
	 * Recupera l'elenco completo di tutte le categorie disponibili.
	 * <p>
	 * Accessibile da utenti con ruolo USER o ADMIN.
	 * </p>
	 *
	 * @return {@link ResponseEntity} contenente la lista di
	 *         {@link CategoriaResponseDTO}
	 */
	@GetMapping
	public ResponseEntity<Page<CategoriaResponseDTO>> getAllCategorie(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(
				categoriaService.prendiTutteLeCategorie(pageable).map(categoriaMapper::toDTO));
	}

	/**
	 * Crea e salva una nuova categoria nel sistema.
	 * <p>
	 * Accessibile esclusivamente da utenti con ruolo ADMIN.
	 * </p>
	 *
	 * @param requestDTO il {@link CategoriaRequestDTO} contenente i dati della
	 *                   nuova categoria
	 * @return {@link ResponseEntity} contenente il {@link CategoriaResponseDTO}
	 *         della categoria creata
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CategoriaResponseDTO> aggiungiCategoria(
			@RequestBody CategoriaRequestDTO requestDTO) {
		return ResponseEntity.ok(
				categoriaMapper.toDTO(categoriaService.creaUnaCategoria(categoriaMapper.toEntity(requestDTO))));
	}

	/**
	 * Aggiorna i dati di una categoria esistente identificata dal suo UUID.
	 * <p>
	 * Accessibile esclusivamente da utenti con ruolo ADMIN.
	 * </p>
	 *
	 * @param idCategoria l'UUID della categoria da modificare
	 * @param requestDTO  il {@link CategoriaRequestDTO} contenente i nuovi dati
	 *                    della categoria
	 * @return {@link ResponseEntity} contenente il {@link CategoriaResponseDTO}
	 *         aggiornato
	 */
	@PutMapping("/{idCategoria}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CategoriaResponseDTO> modificaCategoria(
			@PathVariable UUID idCategoria,
			@RequestBody CategoriaRequestDTO requestDTO) {
		return ResponseEntity.ok(
				categoriaMapper
						.toDTO(categoriaService.aggiornaCategoria(idCategoria, categoriaMapper.toEntity(requestDTO))));
	}

	/**
	 * Elimina definitivamente una categoria tramite il suo identificativo univoco.
	 * <p>
	 * Accessibile esclusivamente da utenti con ruolo ADMIN.
	 * </p>
	 *
	 * @param idCategoria l'UUID della categoria da eliminare
	 * @return {@link ResponseEntity} con status 204 No Content se l'operazione va a
	 *         buon fine
	 */
	@DeleteMapping("/{idCategoria}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminaCategoria(@PathVariable UUID idCategoria) {
		categoriaService.eliminaCategoria(idCategoria);
		return ResponseEntity.noContent().build();
	}

}
