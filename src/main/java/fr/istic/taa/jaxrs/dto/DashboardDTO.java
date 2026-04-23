package fr.istic.taa.jaxrs.dto;

public class DashboardDTO {

    private Long userId;
    private long nombreGroupes;
    private long nombreMessagesEnvoyes;
    private long nombreClients;

    public DashboardDTO() {}

    public DashboardDTO(Long userId, long nombreGroupes, long nombreMessagesEnvoyes, long nombreClients) {
        this.userId = userId;
        this.nombreGroupes = nombreGroupes;
        this.nombreMessagesEnvoyes = nombreMessagesEnvoyes;
        this.nombreClients = nombreClients;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public long getNombreGroupes() { return nombreGroupes; }
    public void setNombreGroupes(long nombreGroupes) { this.nombreGroupes = nombreGroupes; }

    public long getNombreMessagesEnvoyes() { return nombreMessagesEnvoyes; }
    public void setNombreMessagesEnvoyes(long nombreMessagesEnvoyes) { this.nombreMessagesEnvoyes = nombreMessagesEnvoyes; }

    public long getNombreClients() { return nombreClients; }
    public void setNombreClients(long nombreClients) { this.nombreClients = nombreClients; }

}
