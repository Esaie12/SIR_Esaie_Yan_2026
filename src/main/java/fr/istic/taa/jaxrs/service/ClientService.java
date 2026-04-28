package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.ClientGroupeDTO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.ClientGroupe;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;

import java.util.ArrayList;
import java.util.List;

public class ClientService {

    private final ClientDAO  clientDAO  = new ClientDAO();
    private final GroupeDAO  groupeDAO  = new GroupeDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    // ─── Convertit un Client en DTO pour l'envoyer au front ─────────────────
    public ClientDTO toDTO(Client client) {
        if (client == null) return null;
        return new ClientDTO(
                client.getId(), client.getName(), client.getEmail(),
                client.getPhone(), client.getLocalisation(),
                client.getCountry(), client.getSexe(),
                client.getUser().getId() != null ? client.getUser().getId() : null
        );
    }

    // ─── Convertit un DTO en entité Client (sans assigner le user) ──────────
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

    // ─── Récupère un client par son ID ──────────────────────────────────────
    public ClientDTO findUser(Long id) {
        return toDTO(clientDAO.findOne(id));
    }

    // ─── Récupère tous les clients ───────────────────────────────────────────
    public List<ClientDTO> findAllUsers() {
        List<Client> clients = clientDAO.findAll();
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // ─── Récupère les clients appartenant à un utilisateur donné ────────────
    public List<ClientDTO> getClientsByUser(Long userId) {
        List<Client> clients = clientDAO.findByUserId(userId);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // ─── Crée un client et l'associe à un utilisateur existant ──────────────
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

    // ─── Met à jour les infos d'un client existant ──────────────────────────
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

    // ─── Supprime un client par son ID ──────────────────────────────────────
    public void deleteUser(Long id) {
        clientDAO.deleteById(id);
    }

    // ─── Récupère tous les clients d'un groupe donné ─────────────────────────
    public List<ClientDTO> findByGroupe(Long groupeId) {
        List<Client> clients = clientDAO.findByGroupe(groupeId);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // ─── Recherche un client par email exact ─────────────────────────────────
    public ClientDTO findByEmail(String email) {
        return toDTO(clientDAO.findByEmail(email));
    }

    // ─── Recherche les clients d'un pays donné ───────────────────────────────
    public List<ClientDTO> findByCountry(String country) {
        List<Client> clients = clientDAO.findByCountry(country);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // ─── Recherche multi-critères : pays et/ou sexe (les null sont ignorés) ──
    public List<ClientDTO> findByCriteria(String country, String sexe) {
        List<Client> clients = clientDAO.findByCriteria(country, sexe);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    // ─── Ajoute un client à un groupe (crée l'association ClientGroupe) ──────
    public ClientGroupeDTO addClientToGroupe(Long clientId, Long groupeId) {
        Client client = clientDAO.findOne(clientId);
        Groupe groupe = groupeDAO.findOne(groupeId);
        if (client == null || groupe == null) return null;

        ClientGroupe cg = new ClientGroupe(client, groupe);
        client.getClientGroupes().add(cg);
        clientDAO.update(client);

        return new ClientGroupeDTO(clientId, groupeId, cg.getDateAdd(),
                client.getName(), groupe.getLibelle());
    }

    // ─── Retire un client d'un groupe (supprime l'association) ──────────────
    public void removeClientFromGroupe(Long clientId, Long groupeId) {
        Client client = clientDAO.findOne(clientId);
        if (client == null) throw new RuntimeException("Client introuvable");

        Groupe groupe = groupeDAO.findOne(groupeId);
        if (groupe == null) throw new RuntimeException("Groupe introuvable");

        // Cherche l'association dans la liste du client
        ClientGroupe toRemove = null;
        for (ClientGroupe cg : client.getClientGroupes()) {
            if (cg.getGroupe().getId().equals(groupeId)) {
                toRemove = cg;
                break;
            }
        }
        if (toRemove == null) {
            throw new RuntimeException("Ce client n'appartient pas au groupe " + groupeId);
        }

        client.getClientGroupes().remove(toRemove);
        clientDAO.update(client);
    }

    // ─── Retourne les clients d'un user qui ne sont PAS dans un groupe ───────
    public List<ClientDTO> findClientsNotInGroupe(Long groupeId, Long userId) {
        Groupe groupe = groupeDAO.findOne(groupeId);
        if (groupe == null) throw new RuntimeException("Groupe introuvable");

        List<Client> tousLesClients = clientDAO.findByUserId(userId);
        List<ClientDTO> result = new ArrayList<>();

        for (Client client : tousLesClients) {
            boolean dansLeGroupe = false;
            // Vérifie si ce client appartient déjà au groupe
            for (ClientGroupe cg : client.getClientGroupes()) {
                if (cg.getGroupe().getId().equals(groupeId)) {
                    dansLeGroupe = true;
                    break;
                }
            }
            if (!dansLeGroupe) result.add(toDTO(client));
        }
        return result;
    }

    // ─── Retourne tous les groupes auxquels appartient un client ────────────
    public List<ClientGroupeDTO> getGroupesOfClient(Long clientId) {
        Client client = clientDAO.findOne(clientId);
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