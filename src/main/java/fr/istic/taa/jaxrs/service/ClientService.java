package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.ClientGroupeDTO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.ClientGroupe;
import fr.istic.taa.jaxrs.entity.ClientGroupeId;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientService {

    private final ClientDAO  clientDAO  = new ClientDAO();
    private final GroupeDAO  groupeDAO  = new GroupeDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    // Convertit un Client en DTO
    public ClientDTO toDTO(Client client) {
        if (client == null) return null;
        return new ClientDTO(
                client.getId(), client.getName(), client.getEmail(),
                client.getPhone(), client.getLocalisation(),
                client.getCountry(), client.getSexe(),
                client.getUser().getId() != null ? client.getUser().getId() : null
        );
    }

    // Convertit un DTO en entité Client (sans assigner le user)
    public Client toEntity(ClientDTO dto) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setLocalisation(dto.getLocalisation());
        client.setCountry(dto.getCountry());
        client.setSexe(dto.getSexe());
        return client;
    }

    // Récupère un client par son ID
    public ClientDTO findUser(Long id) {
        return toDTO(clientDAO.findOne(id));
    }

    // Récupère tous les clients
    public List<ClientDTO> findAllUsers() {
        List<Client> clients = clientDAO.findAll();
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // Récupère les clients appartenant à un utilisateur donné
    public List<ClientDTO> getClientsByUser(Long userId) {
        List<Client> clients = clientDAO.findByUserId(userId);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // Crée un client et l'associe à un utilisateur existant
    public ClientDTO createUser(ClientDTO dto) {
        Client client = toEntity(dto);
        if (dto.getUserId() != null) {
            Users user = accountDAO.findUserById(dto.getUserId());
            if (user == null) throw new RuntimeException("Utilisateur introuvable");
            client.setUser(user);
        } else {
            throw new RuntimeException("userId est requis pour créer un client");
        }
        clientDAO.save(client);
        return toDTO(client);
    }

    // Met à jour les infos d'un client existant
    public ClientDTO updateUser(Long id, ClientDTO dto) {
        Client existing = clientDAO.findOne(id);
        if (existing == null) return null;
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setLocalisation(dto.getLocalisation());
        existing.setCountry(dto.getCountry());
        existing.setSexe(dto.getSexe());
        return toDTO(clientDAO.update(existing));
    }

    // Supprime un client et ses messages associés
    // Les messages liés au client doivent être supprimés avant le client
    // sinon la contrainte FK en base provoque un 500
    public void deleteUser(Long id) {
        messageDAO.deleteByClient(id); // supprime d'abord les messages du client
        clientDAO.deleteById(id);
    }

    // Récupère tous les clients d'un groupe donné
    public List<ClientDTO> findByGroupe(Long groupeId) {
        List<Client> clients = clientDAO.findByGroupe(groupeId);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // Recherche un client par email exact
    public ClientDTO findByEmail(String email) {
        return toDTO(clientDAO.findByEmail(email));
    }

    // Recherche les clients d'un pays donné
    public List<ClientDTO> findByCountry(String country) {
        List<Client> clients = clientDAO.findByCountry(country);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // Recherche multi-critères : pays et/ou sexe (les null sont ignorés)
    public List<ClientDTO> findByCriteria(String country, String sexe) {
        List<Client> clients = clientDAO.findByCriteria(country, sexe);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // Ajoute un client à un groupe
    public ClientGroupeDTO addClientToGroupe(Long clientId, Long groupeId) {
        EntityManager em = clientDAO.getEntityManager();
        em.clear();

        Client client = em.find(Client.class, clientId);
        Groupe groupe = em.find(Groupe.class, groupeId);
        if (client == null || groupe == null) return null;

        ClientGroupeId cgId = new ClientGroupeId(clientId, groupeId);

        // Si l'association existe déjà, on retourne sans toucher la BDD
        ClientGroupe existing = em.find(ClientGroupe.class, cgId);
        if (existing != null) {
            return new ClientGroupeDTO(clientId, groupeId, existing.getDateAdd(),
                    client.getName(), groupe.getLibelle());
        }

        ClientGroupe cg = new ClientGroupe();
        cg.setId(cgId);
        cg.setClient(client);
        cg.setGroupe(groupe);
        cg.setDateAdd(LocalDateTime.now());

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(cg);
        tx.commit();

        return new ClientGroupeDTO(clientId, groupeId, cg.getDateAdd(),
                client.getName(), groupe.getLibelle());
    }

    // Retire un client d'un groupe
    public void removeClientFromGroupe(Long clientId, Long groupeId) {
        EntityManager em = clientDAO.getEntityManager();
        em.clear();

        ClientGroupeId cgId = new ClientGroupeId(clientId, groupeId);
        ClientGroupe cg = em.find(ClientGroupe.class, cgId);

        if (cg == null) {
            throw new RuntimeException("Ce client n'appartient pas au groupe " + groupeId);
        }

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(cg);
        tx.commit();
    }

    // Retourne les clients d'un user qui ne sont PAS dans un groupe
    public List<ClientDTO> findClientsNotInGroupe(Long groupeId, Long userId) {
        Groupe groupe = groupeDAO.findOne(groupeId);
        if (groupe == null) throw new RuntimeException("Groupe introuvable");

        EntityManager em = clientDAO.getEntityManager();
        em.clear();

        List<Client> tousLesClients = clientDAO.findByUserId(userId);
        List<ClientDTO> result = new ArrayList<>();

        for (Client client : tousLesClients) {
            ClientGroupeId cgId = new ClientGroupeId(client.getId(), groupeId);
            ClientGroupe cg = em.find(ClientGroupe.class, cgId);
            if (cg == null) result.add(toDTO(client));
        }
        return result;
    }

    // Retourne tous les groupes auxquels appartient un client
    public List<ClientGroupeDTO> getGroupesOfClient(Long clientId) {
        EntityManager em = clientDAO.getEntityManager();
        em.clear();

        Client client = em.find(Client.class, clientId);
        if (client == null) return new ArrayList<>();

        List<ClientGroupeDTO> result = new ArrayList<>();
        for (ClientGroupe cg : client.getClientGroupes()) {
            result.add(new ClientGroupeDTO(
                    cg.getClient().getId(), cg.getGroupe().getId(),
                    cg.getDateAdd(), cg.getClient().getName(),
                    cg.getGroupe().getLibelle()));
        }
        return result;
    }
}