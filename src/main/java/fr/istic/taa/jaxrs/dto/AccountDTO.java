package fr.istic.taa.jaxrs.dto;

import java.time.LocalDate;

public class AccountDTO {

	public String email;
    public String password;
    public String firstname;
    public String lastname;

    public String type; // ADMIN, USER, MORAL, PHYSIQUE

    // spécifique
    public String pseudo;
    public String companyName;
    public String sexe;
    public long id;
    
    public AccountDTO() {}
    
    public AccountDTO(String email, String password, String firstname, String lastname, String type, String pseudo,
			String companyName, String sexe, LocalDate birthday) {
		//super();
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.type = type;
		this.pseudo = pseudo;
		this.companyName = companyName;
		this.sexe = sexe;
		this.birthday = birthday;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public LocalDate birthday;
    
    
    
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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