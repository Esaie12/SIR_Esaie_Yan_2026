package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.generic.EntityManagerHelper;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Groupe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ClientService {

    public void createClientInGroupe(String clientName, Long groupeId) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Groupe g = em.find(Groupe.class, groupeId);
        Client c = new Client();
        c.setName(clientName);
        c.setGroupe(g);

        em.persist(c);
        tx.commit();
    }

    public List<Client> getAllClients() {
        return EntityManagerHelper.getEntityManager().createQuery("select c from Client c", Client.class).getResultList();
    }
}
