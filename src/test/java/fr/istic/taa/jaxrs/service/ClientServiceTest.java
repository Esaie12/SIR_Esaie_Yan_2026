package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.ClientGroupeDTO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class ClientServiceTest {

    private ClientService clientService;
    private ClientDAO     clientDAO;
    private GroupeDAO     groupeDAO;
    private AccountDAO    accountDAO;

    private Long clientId;
    private Long groupeId;
    private Users testUser;
    private Long  testUserId;

    @Before
    public void setUp() {
        clientService = new ClientService();
        clientDAO     = new ClientDAO();
        groupeDAO     = new GroupeDAO();
        accountDAO    = new AccountDAO();

        testUser   = new Users("testuser@svctest.com", "password123", "Prenom", "Nom", true, LocalDateTime.now());
        accountDAO.save(testUser);
        testUserId = testUser.getId();
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
        if (testUserId != null) {
            Users u = (Users) accountDAO.findOne(testUserId);
            if (u != null) accountDAO.delete(u);
            testUserId = null;
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
        dto.setUserId(testUserId);
        return dto;
    }

    private Groupe createGroupe(String libelle) {
        Groupe g = new Groupe(libelle);
        g.setColor("#123456");
        g.setUser(testUser);
        groupeDAO.save(g);
        groupeId = g.getId();
        return g;
    }

    // ─── createUser ──────────────────────────────────────────────────────────

    @Test
    public void testCreateUser() {
        ClientDTO created = clientService.createUser(buildDTO("Jean Test", "jean@svctest.com", "France", "M"));

        assertNotNull(created);
        assertNotNull("L'ID doit être généré", created.getId());
        assertEquals("Jean Test", created.getName());
        clientId = created.getId();
    }

    // ─── findUser ────────────────────────────────────────────────────────────

    @Test
    public void testFindUser() {
        ClientDTO created = clientService.createUser(buildDTO("Find Me", "findme@svctest.com", "France", "F"));
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
        ClientDTO created = clientService.createUser(buildDTO("Old Name", "old@svctest.com", "France", "M"));
        clientId = created.getId();

        ClientDTO updated = clientService.updateUser(clientId, buildDTO("New Name", "old@svctest.com", "Belgique", "M"));
        assertNotNull(updated);
        assertEquals("New Name",  updated.getName());
        assertEquals("Belgique",  updated.getCountry());
    }

    @Test
    public void testUpdateUser_notFound() {
        assertNull(clientService.updateUser(999999L, buildDTO("Ghost", "ghost@svctest.com", "France", "M")));
    }

    // ─── deleteUser ──────────────────────────────────────────────────────────

    @Test
    public void testDeleteUser() {
        ClientDTO created = clientService.createUser(buildDTO("To Delete", "del@svctest.com", "France", "M"));
        Long id = created.getId();

        clientService.deleteUser(id);
        assertNull(clientService.findUser(id));
    }

    // ─── findByEmail ─────────────────────────────────────────────────────────

    @Test
    public void testFindByEmail() {
        ClientDTO created = clientService.createUser(buildDTO("Email Test", "emailtest@svctest.com", "France", "F"));
        clientId = created.getId();

        ClientDTO found = clientService.findByEmail("emailtest@svctest.com");
        assertNotNull(found);
        assertEquals("Email Test", found.getName());
    }

    // ─── findByCountry ───────────────────────────────────────────────────────

    @Test
    public void testFindByCountry() {
        ClientDTO created = clientService.createUser(buildDTO("Country Test", "country@svctest.com", "Portugal", "M"));
        clientId = created.getId();

        List<ClientDTO> result = clientService.findByCountry("Portugal");
        assertFalse(result.isEmpty());
        for (ClientDTO c : result) assertEquals("Portugal", c.getCountry());
    }

    // ─── findByCriteria (CriteriaQuery) ──────────────────────────────────────

    @Test
    public void testFindByCriteria() {
        ClientDTO created = clientService.createUser(buildDTO("Criteria Test", "criteria@svctest.com", "Espagne", "F"));
        clientId = created.getId();

        List<ClientDTO> result = clientService.findByCriteria("Espagne", "F");
        assertFalse(result.isEmpty());
        for (ClientDTO c : result) {
            assertEquals("Espagne", c.getCountry());
            assertEquals("F",       c.getSexe());
        }
    }

    // ─── addClientToGroupe / getGroupesOfClient ───────────────────────────────

    @Test
    public void testAddClientToGroupe() {
        ClientDTO created = clientService.createUser(buildDTO("Groupe Client", "groupeclient@svctest.com", "France", "M"));
        clientId = created.getId();

        createGroupe("Test Groupe Service");

        ClientGroupeDTO result = clientService.addClientToGroupe(clientId, groupeId);
        assertNotNull(result);
        assertEquals(clientId,  result.getClientId());
        assertEquals(groupeId,  result.getGroupeId());
        assertNotNull("dateAdd doit être renseignée", result.getDateAdd());
    }

    @Test
    public void testAddClientToGroupe_clientInexistant() {
        createGroupe("Ghost Groupe");
        assertNull("Client inexistant doit retourner null",
                clientService.addClientToGroupe(999999L, groupeId));
    }

    @Test
    public void testGetGroupesOfClient() {
        ClientDTO created = clientService.createUser(buildDTO("Multi Groupe", "multigroupe@svctest.com", "France", "M"));
        clientId = created.getId();
        createGroupe("Mon Groupe");

        clientService.addClientToGroupe(clientId, groupeId);

        List<ClientGroupeDTO> groupes = clientService.getGroupesOfClient(clientId);
        assertFalse(groupes.isEmpty());
        assertEquals(groupeId, groupes.get(0).getGroupeId());
    }

    @Test
    public void testGetGroupesOfClient_inexistant() {
        assertTrue(clientService.getGroupesOfClient(999999L).isEmpty());
    }

    // ─── NOUVEAU : removeClientFromGroupe ────────────────────────────────────

    @Test
    public void testRemoveClientFromGroupe() {
        ClientDTO created = clientService.createUser(buildDTO("Remove Test", "remove@svctest.com", "France", "M"));
        clientId = created.getId();
        createGroupe("Groupe Remove");

        // Ajouter puis retirer
        clientService.addClientToGroupe(clientId, groupeId);
        clientService.removeClientFromGroupe(clientId, groupeId);

        // Le client ne doit plus apparaître dans le groupe
        List<ClientGroupeDTO> groupes = clientService.getGroupesOfClient(clientId);
        boolean stillInGroupe = groupes.stream()
                .anyMatch(cg -> cg.getGroupeId().equals(groupeId));
        assertFalse("Le client ne doit plus appartenir au groupe", stillInGroupe);
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveClientFromGroupe_clientInexistant() {
        createGroupe("Groupe Test");
        clientService.removeClientFromGroupe(999999L, groupeId);
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveClientFromGroupe_groupeInexistant() {
        ClientDTO created = clientService.createUser(buildDTO("No Groupe", "nogroupe@svctest.com", "France", "M"));
        clientId = created.getId();
        clientService.removeClientFromGroupe(clientId, 999999L);
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveClientFromGroupe_associationInexistante() {
        ClientDTO created = clientService.createUser(buildDTO("Not In", "notin@svctest.com", "France", "M"));
        clientId = created.getId();
        createGroupe("Groupe Sans Client");

        // Le client n'est pas dans le groupe → RuntimeException
        clientService.removeClientFromGroupe(clientId, groupeId);
    }

    // ─── NOUVEAU : findClientsNotInGroupe ─────────────────────────────────────

    @Test
    public void testFindClientsNotInGroupe() {
        // Créer 2 clients
        ClientDTO c1 = clientService.createUser(buildDTO("In Groupe",    "ingroupe@svctest.com",    "France", "M"));
        ClientDTO c2 = clientService.createUser(buildDTO("Not In Groupe","notingroupe@svctest.com", "France", "F"));
        clientId = c1.getId(); // tearDown supprimera celui-là

        createGroupe("Groupe Partiel");

        // Ajouter seulement c1 au groupe
        clientService.addClientToGroupe(c1.getId(), groupeId);

        // c2 ne doit pas être dans le groupe
        List<ClientDTO> notIn = clientService.findClientsNotInGroupe(groupeId, testUserId);
        boolean c1Present = notIn.stream().anyMatch(c -> c.getId().equals(c1.getId()));
        boolean c2Present = notIn.stream().anyMatch(c -> c.getId().equals(c2.getId()));

        assertFalse("c1 (dans le groupe) ne doit pas apparaître", c1Present);
        assertTrue("c2 (hors groupe) doit apparaître",             c2Present);

        // Nettoyage manuel du 2e client
        clientService.deleteUser(c2.getId());
    }

    @Test(expected = RuntimeException.class)
    public void testFindClientsNotInGroupe_groupeInexistant() {
        clientService.findClientsNotInGroupe(999999L, testUserId);
    }
}