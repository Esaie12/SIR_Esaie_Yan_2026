package fr.istic.taa.jaxrs.dao;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.ClientGroupe;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class ClientDAOTest {

    private ClientDAO clientDAO;
    private GroupeDAO groupeDAO;
    private AccountDAO accountDAO;

    private Long clientId;
    private Long groupeId;
    private Users testUser;

    @Before
    public void setUp() {
        clientDAO = new ClientDAO();
        groupeDAO = new GroupeDAO();
        accountDAO = new AccountDAO();

        // Création de l'utilisateur obligatoire pour les clients et groupes
        testUser = new Users("clientuser@test.com", "pass", "Test", "User", false, LocalDateTime.now());
        accountDAO.save(testUser);
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
        if (testUser != null) {
            Users u = accountDAO.findUserById(testUser.getId());
            if (u != null) accountDAO.delete(u);
            testUser = null;
        }
    }

    private Client createClient(String name, String email, String country, String sexe) {
        Client c = new Client();
        c.setName(name);
        c.setEmail(email);
        c.setCountry(country);
        c.setSexe(sexe);
        c.setPhone("0600000000");
        c.setLocalisation("Paris");
        // On assigne l'utilisateur pour satisfaire la contrainte not-null
        c.setUser(testUser);

        clientDAO.save(c);
        clientId = c.getId();
        return c;
    }

    private Groupe createGroupe(String libelle) {
        Groupe g = new Groupe(libelle);
        g.setColor("#FF0000");
        g.setUser(testUser); // Pareil ici
        groupeDAO.save(g);
        groupeId = g.getId();
        return g;
    }

    @Test
    public void testSaveAndFind() {
        Client c = createClient("Jean Dupont", "jean@test.com", "France", "M");

        assertNotNull("L'ID doit être généré", c.getId());
        Client found = clientDAO.findOne(c.getId());
        assertNotNull(found);
        assertEquals("Jean Dupont", found.getName());
        assertEquals("France", found.getCountry());
    }

    @Test
    public void testFindByEmail() {
        createClient("Marie Martin", "marie@test.com", "France", "F");

        Client found = clientDAO.findByEmail("marie@test.com");
        assertNotNull(found);
        assertEquals("Marie Martin", found.getName());
    }

    @Test
    public void testFindByEmail_notFound() {
        assertNull(clientDAO.findByEmail("nobody@test.com"));
    }

    @Test
    public void testFindByCountry() {
        createClient("Pierre Paul", "pierre@test.com", "Espagne", "M");

        List<Client> result = clientDAO.findByCountry("Espagne");
        assertFalse(result.isEmpty());
        boolean found = false;
        for (Client c : result) {
            if ("Espagne".equals(c.getCountry())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindByCriteria_countryAndSexe() {
        createClient("Sophie Leblanc", "sophie@test.com", "France", "F");

        List<Client> result = clientDAO.findByCriteria("France", "F");
        assertFalse(result.isEmpty());
        for (Client c : result) {
            assertEquals("France", c.getCountry());
            assertEquals("F", c.getSexe());
        }
    }

    @Test
    public void testFindByCriteria_countryOnly() {
        createClient("Paul Noir", "paul@test.com", "Allemagne", "M");

        List<Client> result = clientDAO.findByCriteria("Allemagne", null);
        assertFalse(result.isEmpty());
        for (Client c : result) {
            assertEquals("Allemagne", c.getCountry());
        }
    }

    @Test
    public void testFindByCriteria_noFilter() {
        createClient("Test NoFilter", "nofilter@test.com", "Italie", "M");

        List<Client> result = clientDAO.findByCriteria(null, null);
        assertFalse("Sans filtre, tous les clients sont retournés", result.isEmpty());
    }

    @Test
    public void testFindByGroupe() {
        Client client = createClient("Client Groupe", "groupe@test.com", "France", "M");
        Groupe groupe = createGroupe("Test Groupe");

        ClientGroupe cg = new ClientGroupe(client, groupe);
        client.getClientGroupes().add(cg);
        clientDAO.update(client);

        List<Client> result = clientDAO.findByGroupe(groupe.getId());
        assertFalse(result.isEmpty());
        assertEquals(client.getId(), result.get(0).getId());
    }

    @Test
    public void testFindAllSorted() {
        createClient("Zorro Test", "zorro@test.com", "France", "M");

        List<Client> result = clientDAO.findAllSorted();
        assertFalse(result.isEmpty());
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(
                    result.get(i).getName().compareToIgnoreCase(result.get(i + 1).getName()) <= 0
            );
        }
    }

    @Test
    public void testUpdate() {
        Client client = createClient("Old Name", "oldname@test.com", "France", "M");

        client.setName("New Name");
        client.setCountry("Belgique");
        clientDAO.update(client);

        Client found = clientDAO.findOne(client.getId());
        assertEquals("New Name", found.getName());
        assertEquals("Belgique", found.getCountry());
    }

    @Test
    public void testDeleteById() {
        Client client = createClient("To Delete", "todelete@test.com", "France", "M");
        Long id = client.getId();
        clientId = null;

        clientDAO.deleteById(id);
        assertNull(clientDAO.findOne(id));
    }
}