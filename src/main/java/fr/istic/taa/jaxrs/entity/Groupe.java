package fr.istic.taa.jaxrs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name  = "Groupe.findByLibelle",
                query = "SELECT g FROM Groupe g WHERE LOWER(g.libelle) LIKE LOWER(:libelle)"
        ),
        @NamedQuery(
                name  = "Groupe.findAll",
                query = "SELECT g FROM Groupe g ORDER BY g.dateCreate DESC"
        )
})
public class Groupe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;
    private LocalDateTime dateCreate;
    private String color;

    @OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientGroupe> clientGroupes = new ArrayList<>();

    public Groupe() {}

    public Groupe(String libelle) {
        this.libelle = libelle;
        this.dateCreate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public LocalDateTime getDateCreate() { return dateCreate; }
    public void setDateCreate(LocalDateTime dateCreate) { this.dateCreate = dateCreate; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public List<ClientGroupe> getClientGroupes() { return clientGroupes; }
    public void setClientGroupes(List<ClientGroupe> clientGroupes) { this.clientGroupes = clientGroupes; }
}