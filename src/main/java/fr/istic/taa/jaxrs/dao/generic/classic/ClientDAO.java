package fr.istic.taa.jaxrs.dao.generic.classic;

import fr.istic.taa.jaxrs.dao.generic.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Client;

public class ClientDAO extends AbstractJpaDao<Long, Client> {
    public ClientDAO() {
        setClazz(Client.class);
    }


}
