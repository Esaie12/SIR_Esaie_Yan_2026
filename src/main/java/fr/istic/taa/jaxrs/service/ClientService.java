package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.ClientGroupeDTO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.ClientGroupe;
import fr.istic.taa.jaxrs.entity.Groupe;

import java.util.ArrayList;
import java.util.List;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAO();
    private final GroupeDAO groupeDAO = new GroupeDAO();

    // ─── Mapping ─────────────────────────────────────────────────────────────

    public ClientDTO toDTO(Client client) {
        if (client == null) return null;
        return new ClientDTO(
                client.getId(), client.getName(), client.getEmail(),
                client.getPhone(), client.getLocalisation(),
                client.getCountry(), client.getSexe()
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

    // ─── CRUD ────────────────────────────────────────────────────────────────

    public ClientDTO findUser(Long id) {
        return toDTO(clientDAO.findOne(id));
    }

    public List<ClientDTO> findAllUsers() {
        List<Client> clients = clientDAO.findAll();
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) {
            result.add(toDTO(client));
        }
        return result;
    }

    public ClientDTO createUser(ClientDTO dto) {
        Client client = toEntity(dto);
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

    // ─── Requêtes métier ─────────────────────────────────────────────────────

    public List<ClientDTO> findByGroupe(Long groupeId) {
        List<Client> clients = clientDAO.findByGroupe(groupeId);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) {
            result.add(toDTO(client));
        }
        return result;
    }

    public ClientDTO findByEmail(String email) {
        return toDTO(clientDAO.findByEmail(email));
    }

    public List<ClientDTO> findByCountry(String country) {
        List<Client> clients = clientDAO.findByCountry(country);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) {
            result.add(toDTO(client));
        }
        return result;
    }

    /**
     * Recherche dynamique : country et/ou sexe (CriteriaQuery sous-jacent).
     * Les paramètres null sont ignorés.
     */
    public List<ClientDTO> findByCriteria(String country, String sexe) {
        List<Client> clients = clientDAO.findByCriteria(country, sexe);
        List<ClientDTO> result = new ArrayList<>();
        for (Client client : clients) {
            result.add(toDTO(client));
        }
        return result;
    }

    // ─── Relation Client ↔ Groupe ────────────────────────────────────────────

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

    public List<ClientGroupeDTO> getGroupesOfClient(Long clientId) {
        Client client = clientDAO.findOne(clientId);
        if (client == null) return new ArrayList<>();

        List<ClientGroupe> clientGroupes = client.getClientGroupes();
        List<ClientGroupeDTO> result = new ArrayList<>();
        for (ClientGroupe cg : clientGroupes) {
            result.add(new ClientGroupeDTO(
                    cg.getClient().getId(), cg.getGroupe().getId(),
                    cg.getDateAdd(), cg.getClient().getName(),
                    cg.getGroupe().getLibelle()
            ));
        }
        return result;
    }
}