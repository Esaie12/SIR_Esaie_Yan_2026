package fr.istic.taa.jaxrs.dao.generic.classic;

import fr.istic.taa.jaxrs.dao.generic.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Groupe;

import java.util.List;

public class ClientDAO extends AbstractJpaDao<Long, Client> {

    public ClientDAO() {
        setClazz(Client.class);
    }
    
    public List<Client> findByUserId(Long userId) {
        return entityManager.createQuery(
                "SELECT c FROM Client c WHERE c.user.id = :userId",
                Client.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Recherche tous les clients appartenant à un groupe.
     */
    public List<Client> findByGroupe(Long groupeId) {
        return entityManager.createQuery(
                        "SELECT c FROM Client c JOIN c.clientGroupes cg WHERE cg.groupe.id = :groupeId",
                        Client.class)
                .setParameter("groupeId", groupeId)
                .getResultList();
    }

    /**
     * Recherche un client par son email.
     */
    public Client findByEmail(String email) {
        List<Client> results = entityManager.createQuery(
                        "SELECT c FROM Client c WHERE c.email = :email",
                        Client.class)
                .setParameter("email", email)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Recherche les clients par pays.
     */
    public List<Client> findByCountry(String country) {
        return entityManager.createQuery(
                        "SELECT c FROM Client c WHERE c.country = :country",
                        Client.class)
                .setParameter("country", country)
                .getResultList();
    }
}