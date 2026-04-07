package fr.istic.taa.jaxrs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class ClientGroupe implements Serializable {

    @EmbeddedId
    private ClientGroupeId id = new ClientGroupeId();

    @ManyToOne
    @MapsId("clientId")
    private Client client;

    @ManyToOne
    @MapsId("groupeId")
    private Groupe groupe;

    private LocalDateTime dateAdd;

    public ClientGroupe() {}

    public ClientGroupe(Client client, Groupe groupe) {
        this.client = client;
        this.groupe = groupe;
        this.dateAdd = LocalDateTime.now();
        this.id = new ClientGroupeId(client.getId(), groupe.getId());
    }

    public ClientGroupeId getId() { return id; }
    public void setId(ClientGroupeId id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Groupe getGroupe() { return groupe; }
    public void setGroupe(Groupe groupe) { this.groupe = groupe; }

    public LocalDateTime getDateAdd() { return dateAdd; }
    public void setDateAdd(LocalDateTime dateAdd) { this.dateAdd = dateAdd; }
}