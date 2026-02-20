package fr.istic.taa.jaxrs.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin  extends Account{
	private String pseudo;

	
	public Admin() {
		super();
	}

	public Admin(String email, String password, String firstname, String lastname, String pseudo) {
		super(email, password, firstname, lastname);
		this.pseudo = pseudo;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	
}
