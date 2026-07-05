package org.example.homestylebe.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.example.homestylebe.dto.request.StanzaRequestDTO;
import org.example.homestylebe.dto.response.StanzaResponseDTO;
import org.example.homestylebe.mapper.StanzaMapper;
import org.example.homestylebe.service.StanzaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stanza")
@RequiredArgsConstructor
public class StanzaController {

    private final StanzaService stanzaService;

    private final StanzaMapper stanzaMapper;

    @GetMapping("/{id}")
    public StanzaResponseDTO prendiUnaStanza(@PathVariable("id") UUID id) {
        return stanzaMapper.toDTO(stanzaService.prendiStanza(id));
    }

    @GetMapping()
    public Page<StanzaResponseDTO> prendiTutteLeStanze(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return stanzaService.prendiTutteLeStanze(pageable).map(stanzaMapper::toDTO);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public StanzaResponseDTO creaUnaStanza(@RequestBody StanzaRequestDTO stanza) {
        return stanzaMapper.toDTO(stanzaService.creaUnaStanza(stanzaMapper.toEntity(stanza)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StanzaResponseDTO aggiornaStanza(@PathVariable("id") UUID id, @RequestBody StanzaRequestDTO stanza) {
        return stanzaMapper.toDTO(stanzaService.aggiornaStanza(id, stanzaMapper.toEntity(stanza)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminaStanza(@PathVariable("id") UUID id) {
        stanzaService.eliminaStanza(id);
    }

}
