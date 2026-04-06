package fr.istic.taa.jaxrs.dto;

public class ClientDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String localisation;
    private String country;
    private String sexe;
    private Long userId;
    
    public ClientDTO() {}

    public ClientDTO(Long id, String name, String email, String phone,
                     String localisation, String country, String sexe, Long userId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.localisation = localisation;
        this.country = country;
        this.sexe = sexe;
        this.userId = userId;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}