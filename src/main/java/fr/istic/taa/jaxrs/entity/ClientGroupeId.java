package fr.istic.taa.jaxrs.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClientGroupeId implements Serializable {

    private Long clientId;
    private Long groupeId;

    public ClientGroupeId() {}

    public ClientGroupeId(Long clientId, Long groupeId) {
        this.clientId = clientId;
        this.groupeId = groupeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientGroupeId)) return false;
        ClientGroupeId that = (ClientGroupeId) o;
        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(groupeId, that.groupeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, groupeId);
    }
}