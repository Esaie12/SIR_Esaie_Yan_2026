package fr.istic.taa.jaxrs.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
//@Table(name = "messages")
public class Message  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @Column(name = "date_send")
    private LocalDateTime dateSend;

    // Relation ManyToOne vers Users (Moral ou Physique)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // ─── Constructeurs ────────────────────────────────────────────────
    public Message() {}

    public Message(String title, String content, LocalDateTime dateSend, Users user) {
        this.title = title;
        this.content = content;
        this.dateSend = dateSend;
        this.user = user;
    }

    // ─── Getters & Setters ───────────────────────────────────────────
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateSend() {
        return dateSend;
    }

    public void setDateSend(LocalDateTime dateSend) {
        this.dateSend = dateSend;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}