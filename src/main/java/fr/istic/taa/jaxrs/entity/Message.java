package fr.istic.taa.jaxrs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(
                name  = "Message.findByUser",
                query = "SELECT m FROM Message m WHERE m.client.id = :userId ORDER BY m.dateSend DESC"
        ),
        @NamedQuery(
                name  = "Message.findByGroupe",
                query = "SELECT m FROM Message m WHERE m.groupe.id = :groupeId ORDER BY m.dateSend DESC"
        ),
        @NamedQuery(
                name  = "Message.findByTitle",
                query = "SELECT m FROM Message m WHERE LOWER(m.title) LIKE LOWER(:keyword)"
        )
})
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // Pas de columnDefinition=TEXT pour rester compatible HSQLDB et PostgreSQL
    // La colonne sera VARCHAR(255) par défaut, suffisant pour les messages courts
    // Sur PostgreSQL, vous pouvez exécuter : ALTER TABLE message ALTER COLUMN content TYPE TEXT;
    @Column(length = 5000)
    private String content;

    @Column(name = "date_send")
    private LocalDateTime dateSend;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "groupe_id", nullable = true)
    private Groupe groupe;

    public Message() {}

    public Message(String title, String content, LocalDateTime dateSend, Client c, Users sender) {
        this.title    = title;
        this.content  = content;
        this.dateSend = dateSend;
        this.client   = c;
        this.sender   = sender;
        this.groupe   = null;
    }

    public Message(String title, String content, LocalDateTime dateSend, Groupe groupe, Users sender) {
        this.title    = title;
        this.content  = content;
        this.dateSend = dateSend;
        this.groupe   = groupe;
        this.client   = null;
        this.sender   = sender;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getDateSend() { return dateSend; }
    public void setDateSend(LocalDateTime dateSend) { this.dateSend = dateSend; }

    public Client getClient() { return client; }
    public void setUser(Client c) { this.client = c; }

    public Groupe getGroupe() { return groupe; }
    public void setGroupe(Groupe groupe) { this.groupe = groupe; }

    public Users getSender() { return sender; }
    public void setSender(Users sender) { this.sender = sender; }
}