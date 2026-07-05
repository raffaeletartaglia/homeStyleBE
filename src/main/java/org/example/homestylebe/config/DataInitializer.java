package org.example.homestylebe.config;

import org.example.homestylebe.entity.AreaCasa;
import org.example.homestylebe.entity.Categoria;
import org.example.homestylebe.entity.Prodotto;
import org.example.homestylebe.entity.Stanza;
import org.example.homestylebe.repository.CategoriaRepository;
import org.example.homestylebe.repository.ProdottoRepository;
import org.example.homestylebe.repository.StanzaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final StanzaRepository stanzaRepository;
    private final ProdottoRepository prodottoRepository;

    public DataInitializer(CategoriaRepository categoriaRepository, StanzaRepository stanzaRepository, ProdottoRepository prodottoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.stanzaRepository = stanzaRepository;
        this.prodottoRepository = prodottoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (categoriaRepository.count() >= 10) {
            System.out.println("Categorie già presenti. Inizializzazione ignorata.");
            return;
        }

        System.out.println("Inizializzazione categorie, stanze e associazione ai prodotti in corso...");

        // Definizione delle 10 categorie e relative stanze
        Object[][] dati = {
                {"Arredamento Cucina", AreaCasa.CUCINA},
                {"Mobili Soggiorno", AreaCasa.SOGGIORNO},
                {"Accessori Salotto", AreaCasa.SALOTTO},
                {"Arredo Notte", AreaCasa.CAMERA_DA_LETTO},
                {"Arredo Bambini", AreaCasa.CAMERETTA},
                {"Arredo Bagno", AreaCasa.BAGNO},
                {"Mobili Ingresso", AreaCasa.INGRESSO},
                {"Arredo Ufficio", AreaCasa.STUDIO},
                {"Arredo Esterno", AreaCasa.GIARDINO_ESTERNO},
                {"Mobili Terrazzo", AreaCasa.TERRAZZO_BALCONE}
        };

        List<Categoria> categorieCreate = new ArrayList<>();

        for (Object[] dato : dati) {
            String nomeCat = (String) dato[0];
            AreaCasa area = (AreaCasa) dato[1];

            // Trova o crea la stanza
            Stanza stanza = stanzaRepository.findByTipologia(area).orElseGet(() -> {
                Stanza s = new Stanza();
                s.setTipologia(area);
                return stanzaRepository.save(s);
            });

            // Trova o crea la categoria
            Categoria categoria = categoriaRepository.findByNomeCategoria(nomeCat);
            if (categoria == null) {
                categoria = new Categoria();
                categoria.setNomeCategoria(nomeCat);
                categoria.setDescrizione("Categoria per " + nomeCat);
            }
            
            // Associa la stanza alla categoria
            if (!categoria.getStanze().contains(stanza)) {
                categoria.getStanze().add(stanza);
            }
            
            categoria = categoriaRepository.save(categoria);
            categorieCreate.add(categoria);
        }

        // Associa i prodotti esistenti alle nuove categorie
        List<Prodotto> prodotti = prodottoRepository.findAll();
        Random random = new Random();
        int prodottiAggiornati = 0;

        for (Prodotto p : prodotti) {
            if (p.getCategoria() == null) {
                // Assegna una categoria casuale tra le 10 create
                Categoria catRandom = categorieCreate.get(random.nextInt(categorieCreate.size()));
                p.setCategoria(catRandom);
                prodottoRepository.save(p);
                prodottiAggiornati++;
            }
        }

        System.out.println("Inizializzazione completata. Prodotti aggiornati: " + prodottiAggiornati);
    }
}
