package fr.istic.taa.jaxrs.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("PHYSIQUE")
public class Physique extends Users {
	
    @Column(name = "sexe")
    private String sexe;

    @Column(name = "birthday")
    private LocalDate birthday;

    protected Physique() {
        super();
    }
    
    public Physique(String email, String password, String firstname, String lastname, boolean isActive, LocalDateTime createdAt, String sexe, LocalDate birthday) {
	
		super(email, password, firstname, lastname, isActive, createdAt);
		this.sexe = sexe;
		this.birthday = birthday;
	}
	
	public String getSexe() {
		return sexe;
	}
	
	public void setSexe(String sexe) {
		this.sexe = sexe;
	}
	
	public LocalDate getBirthday() {
		return birthday;
	}
	
	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
}
