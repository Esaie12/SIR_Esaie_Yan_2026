package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.generic.EntityManagerHelper;
import fr.istic.taa.jaxrs.dao.generic.classic.ClientDAO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Groupe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ClientService  {

    private ClientDAO clientDAO = new  ClientDAO();

        public Client findUser(Long id) {
            return clientDAO.findOne(id);
        }

        public List<Client> findAllUsers() {
            return clientDAO.findAll();
        }

        public Client createUser(Client client) {
            // logique métier éventuelle
            return clientDAO.update(client);
        }

        public void deleteUser(Long id) {
            clientDAO.deleteById(id);
        }

}
