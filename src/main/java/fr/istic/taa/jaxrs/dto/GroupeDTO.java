package fr.istic.taa.jaxrs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class GroupeDTO {

    private Long id;
    private String libelle;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreate;

    private String color;

    public GroupeDTO() {}

    public GroupeDTO(Long id, String libelle, LocalDateTime dateCreate, String color) {
        this.id = id;
        this.libelle = libelle;
        this.dateCreate = dateCreate;
        this.color = color;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public LocalDateTime getDateCreate() { return dateCreate; }
    public void setDateCreate(LocalDateTime dateCreate) { this.dateCreate = dateCreate; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}