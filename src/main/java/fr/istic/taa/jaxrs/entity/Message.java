package fr.istic.taa.jaxrs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(
                name  = "Message.findByUser",
                query = "SELECT m FROM Message m WHERE m.user.id = :userId ORDER BY m.dateSend DESC"
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
    private String content;

    @Column(name = "date_send")
    private LocalDateTime dateSend;

    // Destinataire : soit un user direct (groupe = null),
    //                soit un groupe entier (user = null)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "groupe_id", nullable = true)
    private Groupe groupe;

    public Message() {}

    // Constructeur envoi à un User
    public Message(String title, String content, LocalDateTime dateSend, Users user) {
        this.title    = title;
        this.content  = content;
        this.dateSend = dateSend;
        this.user     = user;
        this.groupe   = null;
    }

    // Constructeur envoi à un Groupe
    public Message(String title, String content, LocalDateTime dateSend, Groupe groupe) {
        this.title    = title;
        this.content  = content;
        this.dateSend = dateSend;
        this.groupe   = groupe;
        this.user     = null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getDateSend() { return dateSend; }
    public void setDateSend(LocalDateTime dateSend) { this.dateSend = dateSend; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public Groupe getGroupe() { return groupe; }
    public void setGroupe(Groupe groupe) { this.groupe = groupe; }
}