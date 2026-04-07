package fr.istic.taa.jaxrs.dao.generic.classic;

import fr.istic.taa.jaxrs.dao.generic.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Groupe;

import java.util.List;

public class GroupeDAO extends AbstractJpaDao<Long, Groupe> {

    public GroupeDAO() {
        setClazz(Groupe.class);
    }

    public List<Groupe> findByLibelle(String libelle) {
        return entityManager
                .createNamedQuery("Groupe.findByLibelle", Groupe.class)
                .setParameter("libelle", "%" + libelle + "%")
                .getResultList();
    }

    public List<Groupe> findAllSorted() {
        return entityManager
                .createNamedQuery("Groupe.findAll", Groupe.class)
                .getResultList();
    }
}