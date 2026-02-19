package fr.istic.taa.jaxrs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

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
	
}
