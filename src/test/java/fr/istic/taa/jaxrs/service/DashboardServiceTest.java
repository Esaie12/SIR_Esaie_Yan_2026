package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.DashboardDTO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Message;
import fr.istic.taa.jaxrs.entity.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DashboardServiceTest {

    private DashboardService dashboardService;
    private AccountDAO       accountDAO;
    private GroupeDAO        groupeDAO;
    private MessageDAO       messageDAO;
    private ClientDAO        clientDAO;

    private Users       testUser;
    private Users       otherUser;
    private List<Long>  groupeIds  = new ArrayList<>();
    private List<Long>  messageIds = new ArrayList<>();
    private List<Long>  clientIds  = new ArrayList<>();

    @Before
    public void setUp() {
        dashboardService = new DashboardService();
        accountDAO       = new AccountDAO();
        groupeDAO        = new GroupeDAO();
        messageDAO       = new MessageDAO();
        clientDAO        = new ClientDAO();

        testUser  = new Users("dashboard@test.com", "pass", "Dash", "User",  false, LocalDateTime.now());
        otherUser = new Users("other@test.com",      "pass", "Other","User", false, LocalDateTime.now());
        accountDAO.save(testUser);
        accountDAO.save(otherUser);
    }

    @After
    public void tearDown() {
        for (Long mid : messageIds) {
            Message m = messageDAO.findOne(mid);
            if (m != null) messageDAO.delete(m);
        }
        for (Long cid : clientIds) {
            Client c = clientDAO.findOne(cid);
            if (c != null) clientDAO.delete(c);
        }
        for (Long gid : groupeIds) {
            Groupe g = groupeDAO.findOne(gid);
            if (g != null) groupeDAO.delete(g);
        }
        accountDAO.delete(testUser);
        accountDAO.delete(otherUser);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void createGroupe(String libelle) {
        Groupe g = new Groupe(libelle);
        g.setColor("#000");
        g.setUser(testUser);
        groupeDAO.save(g);
        groupeIds.add(g.getId());
    }

    private void createMessage(String title) {
        // Message de testUser → otherUser
        Message m = new Message(title, "contenu", LocalDateTime.now(), otherUser, testUser);
        messageDAO.save(m);
        messageIds.add(m.getId());
    }

    private void createClient(String name) {
        Client c = new Client();
        c.setName(name);
        c.setEmail(name.replace(" ", "") + "@test.com");
        c.setPhone("0600000000");
        c.setLocalisation("Paris");
        c.setCountry("France");
        c.setSexe("M");
        c.setUser(testUser);
        clientDAO.save(c);
        clientIds.add(c.getId());
    }

    // ─── Tests stats ─────────────────────────────────────────────────────────

    @Test
    public void testGetStats_vide() {
        DashboardDTO stats = dashboardService.getStats(testUser.getId());

        assertNotNull(stats);
        assertEquals(testUser.getId(), stats.getUserId());
        assertEquals(0L, stats.getNombreGroupes());
        assertEquals(0L, stats.getNombreMessagesEnvoyes());
        assertEquals(0L, stats.getNombreClients());
    }

    @Test
    public void testGetStats_avecGroupes() {
        createGroupe("VIP");
        createGroupe("Standard");
        createGroupe("Premium");

        DashboardDTO stats = dashboardService.getStats(testUser.getId());
        assertEquals(3L, stats.getNombreGroupes());
    }

    @Test
    public void testGetStats_avecMessages() {
        createMessage("Message 1");
        createMessage("Message 2");

        DashboardDTO stats = dashboardService.getStats(testUser.getId());
        assertEquals(2L, stats.getNombreMessagesEnvoyes());
    }

    @Test
    public void testGetStats_avecClients() {
        createClient("Client Alpha");
        createClient("Client Beta");
        createClient("Client Gamma");
        createClient("Client Delta");

        DashboardDTO stats = dashboardService.getStats(testUser.getId());
        assertEquals(4L, stats.getNombreClients());
    }

    @Test
    public void testGetStats_complet() {
        createGroupe("G1");
        createGroupe("G2");
        createMessage("M1");
        createClient("C1");
        createClient("C2");
        createClient("C3");

        DashboardDTO stats = dashboardService.getStats(testUser.getId());

        assertEquals(testUser.getId(), stats.getUserId());
        assertEquals(2L, stats.getNombreGroupes());
        assertEquals(1L, stats.getNombreMessagesEnvoyes());
        assertEquals(3L, stats.getNombreClients());
    }

    @Test
    public void testGetStats_isolationEntreUsers() {
        // Créer des données pour testUser
        createGroupe("Groupe testUser");
        createClient("Client testUser");

        // Vérifier que les stats de otherUser ne comptent pas les données de testUser
        DashboardDTO statsOther = dashboardService.getStats(otherUser.getId());
        assertEquals(0L, statsOther.getNombreGroupes());
        assertEquals(0L, statsOther.getNombreClients());
    }

    @Test(expected = RuntimeException.class)
    public void testGetStats_userInexistant() {
        dashboardService.getStats(999999L);
    }
}