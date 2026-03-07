package fr.istic.taa.jaxrs.dto;

import java.time.LocalDateTime;

public class ClientGroupeDTO {

    private Long clientId;
    private Long groupeId;
    private LocalDateTime dateAdd;

    // Optionnel : infos résumées des deux côtés pour éviter des appels supplémentaires
    private String clientName;
    private String groupeLibelle;

    public ClientGroupeDTO() {}

    public ClientGroupeDTO(Long clientId, Long groupeId, LocalDateTime dateAdd,
                           String clientName, String groupeLibelle) {
        this.clientId = clientId;
        this.groupeId = groupeId;
        this.dateAdd = dateAdd;
        this.clientName = clientName;
        this.groupeLibelle = groupeLibelle;
    }

    // Getters & Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getGroupeId() { return groupeId; }
    public void setGroupeId(Long groupeId) { this.groupeId = groupeId; }

    public LocalDateTime getDateAdd() { return dateAdd; }
    public void setDateAdd(LocalDateTime dateAdd) { this.dateAdd = dateAdd; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getGroupeLibelle() { return groupeLibelle; }
    public void setGroupeLibelle(String groupeLibelle) { this.groupeLibelle = groupeLibelle; }
}