package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.ClientGroupeDTO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Groupe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ClientServiceTest {

    private ClientService clientService;
    private ClientDAO clientDAO;
    private GroupeDAO groupeDAO;

    private Long clientId;
    private Long groupeId;

    @Before
    public void setUp() {
        clientService = new ClientService();
        clientDAO = new ClientDAO();
        groupeDAO = new GroupeDAO();
    }

    @After
    public void tearDown() {
        if (clientId != null) {
            Client c = clientDAO.findOne(clientId);
            if (c != null) clientDAO.delete(c);
            clientId = null;
        }
        if (groupeId != null) {
            Groupe g = groupeDAO.findOne(groupeId);
            if (g != null) groupeDAO.delete(g);
            groupeId = null;
        }
    }

    private ClientDTO buildDTO(String name, String email, String country, String sexe) {
        ClientDTO dto = new ClientDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setCountry(country);
        dto.setSexe(sexe);
        dto.setPhone("0600000000");
        dto.setLocalisation("Paris");
        return dto;
    }

    // ─── createUser ──────────────────────────────────────────────────────────

    @Test
    public void testCreateUser() {
        ClientDTO dto = buildDTO("Jean Test", "jean@svctest.com", "France", "M");
        ClientDTO created = clientService.createUser(dto);

        assertNotNull(created);
        assertNotNull("L'ID doit être généré", created.getId());
        assertEquals("Jean Test", created.getName());
        clientId = created.getId();
    }

    // ─── findUser ────────────────────────────────────────────────────────────

    @Test
    public void testFindUser() {
        ClientDTO dto = buildDTO("Find Me", "findme@svctest.com", "France", "F");
        ClientDTO created = clientService.createUser(dto);
        clientId = created.getId();

        ClientDTO found = clientService.findUser(clientId);
        assertNotNull(found);
        assertEquals("Find Me", found.getName());
    }

    @Test
    public void testFindUser_notFound() {
        assertNull(clientService.findUser(999999L));
    }

    // ─── updateUser ──────────────────────────────────────────────────────────

    @Test
    public void testUpdateUser() {
        ClientDTO dto = buildDTO("Old Name", "old@svctest.com", "France", "M");
        ClientDTO created = clientService.createUser(dto);
        clientId = created.getId();

        ClientDTO updateDto = buildDTO("New Name", "old@svctest.com", "Belgique", "M");
        ClientDTO updated = clientService.updateUser(clientId, updateDto);

        assertNotNull(updated);
        assertEquals("New Name", updated.getName());
        assertEquals("Belgique", updated.getCountry());
    }

    @Test
    public void testUpdateUser_notFound() {
        ClientDTO dto = buildDTO("Ghost", "ghost@svctest.com", "France", "M");
        assertNull(clientService.updateUser(999999L, dto));
    }

    // ─── deleteUser ──────────────────────────────────────────────────────────

    @Test
    public void testDeleteUser() {
        ClientDTO dto = buildDTO("To Delete", "del@svctest.com", "France", "M");
        ClientDTO created = clientService.createUser(dto);
        Long id = created.getId();

        clientService.deleteUser(id);
        assertNull(clientService.findUser(id));
    }

    // ─── findByEmail ─────────────────────────────────────────────────────────

    @Test
    public void testFindByEmail() {
        ClientDTO dto = buildDTO("Email Test", "emailtest@svctest.com", "France", "F");
        ClientDTO created = clientService.createUser(dto);
        clientId = created.getId();

        ClientDTO found = clientService.findByEmail("emailtest@svctest.com");
        assertNotNull(found);
        assertEquals("Email Test", found.getName());
    }

    // ─── findByCountry ───────────────────────────────────────────────────────

    @Test
    public void testFindByCountry() {
        ClientDTO dto = buildDTO("Country Test", "country@svctest.com", "Portugal", "M");
        ClientDTO created = clientService.createUser(dto);
        clientId = created.getId();

        List<ClientDTO> result = clientService.findByCountry("Portugal");
        assertFalse(result.isEmpty());
        for (ClientDTO c : result) {
            assertEquals("Portugal", c.getCountry());
        }
    }

    // ─── findByCriteria (CriteriaQuery) ──────────────────────────────────────

    @Test
    public void testFindByCriteria() {
        ClientDTO dto = buildDTO("Criteria Test", "criteria@svctest.com", "Espagne", "F");
        ClientDTO created = clientService.createUser(dto);
        clientId = created.getId();

        List<ClientDTO> result = clientService.findByCriteria("Espagne", "F");
        assertFalse(result.isEmpty());
        for (ClientDTO c : result) {
            assertEquals("Espagne", c.getCountry());
            assertEquals("F", c.getSexe());
        }
    }

    // ─── addClientToGroupe / getGroupesOfClient ───────────────────────────────

    @Test
    public void testAddClientToGroupe() {
        ClientDTO dto = buildDTO("Groupe Client", "groupeclient@svctest.com", "France", "M");
        ClientDTO created = clientService.createUser(dto);
        clientId = created.getId();

        Groupe groupe = new Groupe("Test Groupe Service");
        groupe.setColor("#123456");
        groupeDAO.save(groupe);
        groupeId = groupe.getId();

        ClientGroupeDTO result = clientService.addClientToGroupe(clientId, groupeId);

        assertNotNull(result);
        assertEquals(clientId, result.getClientId());
        assertEquals(groupeId, result.getGroupeId());
        assertNotNull("dateAdd doit être renseignée", result.getDateAdd());
    }

    @Test
    public void testAddClientToGroupe_clientInexistant() {
        Groupe groupe = new Groupe("Ghost Groupe");
        groupe.setColor("#000000");
        groupeDAO.save(groupe);
        groupeId = groupe.getId();

        ClientGroupeDTO result = clientService.addClientToGroupe(999999L, groupeId);
        assertNull("Client inexistant doit retourner null", result);
    }

    @Test
    public void testGetGroupesOfClient() {
        ClientDTO dto = buildDTO("Multi Groupe", "multigroupe@svctest.com", "France", "M");
        ClientDTO created = clientService.createUser(dto);
        clientId = created.getId();

        Groupe groupe = new Groupe("Mon Groupe");
        groupe.setColor("#ABCDEF");
        groupeDAO.save(groupe);
        groupeId = groupe.getId();

        clientService.addClientToGroupe(clientId, groupeId);

        List<ClientGroupeDTO> groupes = clientService.getGroupesOfClient(clientId);
        assertFalse(groupes.isEmpty());
        assertEquals(groupeId, groupes.get(0).getGroupeId());
    }

    @Test
    public void testGetGroupesOfClient_inexistant() {
        List<ClientGroupeDTO> result = clientService.getGroupesOfClient(999999L);
        assertTrue(result.isEmpty());
    }
}