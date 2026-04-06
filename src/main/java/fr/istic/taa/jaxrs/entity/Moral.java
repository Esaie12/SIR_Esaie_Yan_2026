package fr.istic.taa.jaxrs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MORAL")
public class Moral extends Users {

	private String companyName;

    protected Moral() {
        super();
    }

    public Moral(String email, String password, String firstname, 
                 String lastname, boolean isActive, 
                 LocalDateTime createdAt, String companyName) {
        super(email, password, firstname, lastname, isActive, createdAt);
        this.companyName = companyName;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { 
        this.companyName = companyName; 
    }
    
}
