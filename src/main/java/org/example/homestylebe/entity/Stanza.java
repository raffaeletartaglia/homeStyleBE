package org.example.homestylebe.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = "categorie")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "stanza")
public class Stanza {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipologia", unique = true, nullable = false)
    private AreaCasa tipologia;

    @ManyToMany(mappedBy = "stanze")
    private List<Categoria> categorie = new ArrayList<>();

}
