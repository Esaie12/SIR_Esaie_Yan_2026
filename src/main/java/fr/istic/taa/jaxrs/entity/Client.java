package fr.istic.taa.jaxrs.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientGroupe> clientGroupes = new ArrayList<>();

    // Constructeur vide obligatoire pour JPA
    public Client() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<ClientGroupe> getClientGroupes() {
        return clientGroupes;
    }
    public void setClientGroupes(List<ClientGroupe> clientGroupes) {
        this.clientGroupes = clientGroupes;
    }
}
