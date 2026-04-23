package fr.istic.taa.jaxrs.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("USER")
public class Users extends Account {

	public Users() {
		super();
	}
	
	public Users(String email, String password, String firstname, String lastname, boolean isActive,  LocalDateTime createdAt) {
		super(email, password, firstname, lastname);
		this.isActive = false;
		this.createdAt = createdAt;
	}
	
	@Column(name = "is_active")
	private boolean isActive = false;
	
	@Column(name = "created_at")
    private LocalDateTime createdAt;

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Client> clients = new ArrayList<>();
	
	public List<Client> getClients() {
	    return clients;
	}
	
	public void addClient(Client client) {
	    clients.add(client);
	}

	public void removeClient(Client client) {
	    clients.remove(client);
	}
	
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Message> messages = new ArrayList<>();

	public List<Message> getMessages() {
	    return messages;
	}

	public void setMessages(List<Message> messages) {
	    this.messages = messages;
	}

	public void addMessage(Message message) {
	    messages.add(message);
	    message.setSender(this);
	}

	public void removeMessage(Message message) {
	    messages.remove(message);
	    message.setUser(null);
	}
	
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Groupe> groupes = new ArrayList<>();

	public List<Groupe> getGroupes() {
	    return groupes;
	}

	public void addGroupe(Groupe groupe) {
	    groupes.add(groupe);
	    groupe.setUser(this);
	}

	public void removeGroupe(Groupe groupe) {
	    groupes.remove(groupe);
	    groupe.setUser(null);
	}
}
