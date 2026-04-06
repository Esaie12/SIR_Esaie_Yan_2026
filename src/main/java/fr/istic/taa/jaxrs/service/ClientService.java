package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.generic.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.generic.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.generic.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.ClientGroupeDTO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.ClientGroupe;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;

import java.util.List;
import java.util.stream.Collectors;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAO();
    private final GroupeDAO groupeDAO = new GroupeDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    
    // ─── Mapping entité → DTO ───────────────────────────────────────────────

    public ClientDTO toDTO(Client client) {
        if (client == null) return null;
        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getLocalisation(),
                client.getCountry(),
                client.getSexe(),
                client.getUser() != null ? client.getUser().getId() : null
        );
    }

    public Client toEntity(ClientDTO dto) {
        Client client = new Client();
        client.setId(dto.getId());
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setLocalisation(dto.getLocalisation());
        client.setCountry(dto.getCountry());
        client.setSexe(dto.getSexe());
        return client;
    }

    // ─── CRUD ───────────────────────────────────────────────────────────────

    /** Récupère un client par son id. */
    public ClientDTO findUser(Long id) {
        return toDTO(clientDAO.findOne(id));
    }

    /** Récupère tous les clients. */
    public List<ClientDTO> findAllUsers() {
        return clientDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ClientDTO> getClientsByUser(Long userId) {
        return clientDAO.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Crée ou met à jour un client à partir d'un DTO. */
    public ClientDTO createUser(ClientDTO dto) {
    	
    	Users user = accountDAO.findUserById(dto.getUserId());
        if (user == null) throw new RuntimeException("Utilisateur introuvable");

        Client client = new Client();
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setLocalisation(dto.getLocalisation());
        client.setCountry(dto.getCountry());
        client.setSexe(dto.getSexe());

        // 🔥 relation
        client.setUser(user);

        Client saved = clientDAO.update(client);
        return toDTO(saved);
        
        /*
        Client client = toEntity(dto);
        Client saved = clientDAO.update(client);
        return toDTO(saved);
        */
    }

    /** Met à jour un client existant. */
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

    /** Supprime un client par son id. */
    public void deleteUser(Long id) {
        clientDAO.deleteById(id);
    }

    // ─── Requêtes métier ────────────────────────────────────────────────────

    /** Récupère tous les clients d'un groupe donné. */
    public List<ClientDTO> findByGroupe(Long groupeId) {
        return clientDAO.findByGroupe(groupeId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Récupère un client par son email. */
    public ClientDTO findByEmail(String email) {
        return toDTO(clientDAO.findByEmail(email));
    }

    /** Récupère les clients par pays. */
    public List<ClientDTO> findByCountry(String country) {
        return clientDAO.findByCountry(country).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Gestion de la relation Client ↔ Groupe ─────────────────────────────

    /** Ajoute un client dans un groupe. */
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

    /** Retourne les groupes d'un client sous forme de DTOs. */
    public List<ClientGroupeDTO> getGroupesOfClient(Long clientId) {
        Client client = clientDAO.findOne(clientId);
        if (client == null) return List.of();

        return client.getClientGroupes().stream()
                .map(cg -> new ClientGroupeDTO(
                        cg.getClient().getId(),
                        cg.getGroupe().getId(),
                        cg.getDateAdd(),
                        cg.getClient().getName(),
                        cg.getGroupe().getLibelle()))
                .collect(Collectors.toList());
    }
}