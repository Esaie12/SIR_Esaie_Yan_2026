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
import java.util.stream.Collectors;

public class ClientService {

    private final ClientDAO  clientDAO  = new ClientDAO();
    private final GroupeDAO  groupeDAO  = new GroupeDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public ClientDTO toDTO(Client client) {
        if (client == null) return null;
        return new ClientDTO(
                client.getId(), client.getName(), client.getEmail(),
                client.getPhone(), client.getLocalisation(),
                client.getCountry(), client.getSexe(),
                client.getUser() != null ? client.getUser().getId() : null
        );
    }

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

    public ClientDTO findUser(Long id) {
        return toDTO(clientDAO.findOne(id));
    }

    public List<ClientDTO> findAllUsers() {
        List<Client> clients = clientDAO.findAll();
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) result.add(toDTO(client));
        return result;
    }

    public List<ClientDTO> getClientsByUser(Long userId) {
        return clientDAO.findByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

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

    public void deleteUser(Long id) {
        clientDAO.deleteById(id);
    }

    public List<ClientDTO> findByGroupe(Long groupeId) {
        return clientDAO.findByGroupe(groupeId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public ClientDTO findByEmail(String email) {
        return toDTO(clientDAO.findByEmail(email));
    }

    public List<ClientDTO> findByCountry(String country) {
        return clientDAO.findByCountry(country).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<ClientDTO> findByCriteria(String country, String sexe) {
        return clientDAO.findByCriteria(country, sexe).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

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

    // ─── retirer un client d'un groupe ────────────────────────────

    public void removeClientFromGroupe(Long clientId, Long groupeId) {
        Client client = clientDAO.findOne(clientId);
        if (client == null) throw new RuntimeException("Client introuvable");

        Groupe groupe = groupeDAO.findOne(groupeId);
        if (groupe == null) throw new RuntimeException("Groupe introuvable");

        ClientGroupe toRemove = client.getClientGroupes().stream()
                .filter(cg -> cg.getGroupe().getId().equals(groupeId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Ce client n'appartient pas au groupe " + groupeId));

        client.getClientGroupes().remove(toRemove);
        clientDAO.update(client);
    }

    // ─── clients d'un user n'étant pas dans un groupe ─────────────

    public List<ClientDTO> findClientsNotInGroupe(Long groupeId, Long userId) {
        Groupe groupe = groupeDAO.findOne(groupeId);
        if (groupe == null) throw new RuntimeException("Groupe introuvable");

        return clientDAO.findByUserId(userId).stream()
                .filter(client -> client.getClientGroupes().stream()
                        .noneMatch(cg -> cg.getGroupe().getId().equals(groupeId)))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ClientGroupeDTO> getGroupesOfClient(Long clientId) {
        Client client = clientDAO.findOne(clientId);
        if (client == null) return new ArrayList<>();

        return client.getClientGroupes().stream()
                .map(cg -> new ClientGroupeDTO(
                        cg.getClient().getId(), cg.getGroupe().getId(),
                        cg.getDateAdd(), cg.getClient().getName(),
                        cg.getGroupe().getLibelle()))
                .collect(Collectors.toList());
    }
}