package fr.istic.taa.jaxrs.dao.generic.classic;

import fr.istic.taa.jaxrs.dao.generic.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Groupe;

public class GroupeDAO extends AbstractJpaDao<Long, Groupe> {

    public GroupeDAO() {
        setClazz(Groupe.class);
    }
}