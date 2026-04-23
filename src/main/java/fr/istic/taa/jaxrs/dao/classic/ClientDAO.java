package fr.istic.taa.jaxrs.dao.classic;

import fr.istic.taa.jaxrs.dao.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Client;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
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

    public List<Client> findByGroupe(Long groupeId) {
        return entityManager.createQuery(
                        "SELECT c FROM Client c JOIN c.clientGroupes cg WHERE cg.groupe.id = :groupeId",
                        Client.class)
                .setParameter("groupeId", groupeId)
                .getResultList();
    }

    public Client findByEmail(String email) {
        List<Client> results = entityManager
                .createNamedQuery("Client.findByEmail", Client.class)
                .setParameter("email", email)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Client> findByCountry(String country) {
        return entityManager
                .createNamedQuery("Client.findByCountry", Client.class)
                .setParameter("country", country)
                .getResultList();
    }

    public List<Client> findAllSorted() {
        return entityManager
                .createNamedQuery("Client.findAll", Client.class)
                .getResultList();
    }

    public List<Client> findByCriteria(String country, String sexe) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);

        List<Predicate> predicates = new ArrayList<>();
        if (country != null && !country.isBlank())
            predicates.add(cb.equal(root.get("country"), country));
        if (sexe != null && !sexe.isBlank())
            predicates.add(cb.equal(root.get("sexe"), sexe));

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("name")));
        return entityManager.createQuery(cq).getResultList();
    }

    public long countByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM Client c WHERE c.user.id = :userId",
                        Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}