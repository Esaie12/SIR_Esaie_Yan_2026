package fr.istic.taa.jaxrs.dao;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Message;
import fr.istic.taa.jaxrs.entity.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class MessageDAOTest {

    private MessageDAO messageDAO;
    private AccountDAO accountDAO;
    private ClientDAO clientDAO;
    private Client testClient;   // destinataire (Client au lieu de Users)
    private Users testSender;    // expéditeur

    @Before
    public void setUp() {
        messageDAO = new MessageDAO();
        accountDAO = new AccountDAO();
        clientDAO = new ClientDAO();

        testSender = new Users("msgsender@test.com", "pass", "Sender", "User", false, LocalDateTime.now());
        accountDAO.save(testSender);

        testClient = new Client();
        testClient.setName("Marie Curie");
        testClient.setEmail("marie@lab.fr");
        testClient.setPhone("0600000001");
        testClient.setLocalisation("Paris");
        testClient.setCountry("France");
        testClient.setSexe("F");
        testClient.setUser(testSender);
        clientDAO.save(testClient);
    }

    @After
    public void tearDown() {
        for (Message m : messageDAO.findByUserId(testClient.getId()))
            messageDAO.delete(m);
        clientDAO.delete(testClient);
        accountDAO.delete(testSender);
    }

    // créer un message du user (CRM) vers le client
    private Message msg(String title, String content, LocalDateTime date) {
        return new Message(title, content, date, testClient, testSender);
    }

    @Test
    public void testSaveAndFind() {
        Message message = msg("Titre test", "Contenu test", LocalDateTime.now());
        messageDAO.save(message);

        assertNotNull("L'ID doit être généré", message.getId());
        Message found = messageDAO.findOne(message.getId());
        assertNotNull(found);
        assertEquals("Titre test",  found.getTitle());
        assertEquals("Contenu test", found.getContent());
        assertEquals(testClient.getId(), found.getClient().getId());
        assertEquals(testSender.getId(), found.getSender().getId());
    }

    @Test
    public void testFindByUserId() {
        messageDAO.save(msg("Msg 1", "Contenu 1", LocalDateTime.now()));
        messageDAO.save(msg("Msg 2", "Contenu 2", LocalDateTime.now().minusHours(1)));

        List<Message> result = messageDAO.findByUserId(testClient.getId());
        assertEquals("2 messages attendus", 2, result.size());

        assertTrue(
                result.get(0).getDateSend().isAfter(result.get(1).getDateSend())
                        || result.get(0).getDateSend().isEqual(result.get(1).getDateSend())
        );
    }

    @Test
    public void testFindByUserId_empty() {
        Client emptyClient = new Client();
        emptyClient.setName("Empty Client");
        emptyClient.setUser(testSender);
        clientDAO.save(emptyClient);

        List<Message> result = messageDAO.findByUserId(emptyClient.getId());
        assertTrue(result.isEmpty());

        clientDAO.delete(emptyClient);
    }

    @Test
    public void testFindByTitle_exact() {
        messageDAO.save(msg("Bonjour monde", "contenu", LocalDateTime.now()));
        List<Message> result = messageDAO.findByTitle("Bonjour monde");
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByTitle_partiel() {
        messageDAO.save(msg("Rapport mensuel", "contenu", LocalDateTime.now()));
        List<Message> result = messageDAO.findByTitle("mensuel");
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByTitle_caseInsensitive() {
        messageDAO.save(msg("Alerte Système", "contenu", LocalDateTime.now()));
        List<Message> result = messageDAO.findByTitle("alerte système");
        assertFalse("La recherche doit être insensible à la casse", result.isEmpty());
    }

    @Test
    public void testFindRecentMessages() {
        messageDAO.save(msg("Recent 1", "c1", LocalDateTime.now()));
        messageDAO.save(msg("Recent 2", "c2", LocalDateTime.now().minusMinutes(5)));
        messageDAO.save(msg("Recent 3", "c3", LocalDateTime.now().minusMinutes(10)));

        List<Message> result = messageDAO.findRecentMessages(2);
        assertEquals("Doit retourner exactement 2 messages", 2, result.size());
        assertTrue(
                result.get(0).getDateSend().isAfter(result.get(1).getDateSend())
                        || result.get(0).getDateSend().isEqual(result.get(1).getDateSend())
        );
    }

    @Test
    public void testCountSentByUserId() {
        messageDAO.save(msg("Sent 1", "c1", LocalDateTime.now()));
        messageDAO.save(msg("Sent 2", "c2", LocalDateTime.now()));

        long count = messageDAO.countSentByUserId(testSender.getId());
        assertTrue("L'expéditeur doit avoir au moins 2 messages envoyés", count >= 2);
    }

    @Test
    public void testUpdate() {
        Message message = msg("Original", "Contenu original", LocalDateTime.now());
        messageDAO.save(message);

        message.setTitle("Modifié");
        message.setContent("Contenu modifié");
        messageDAO.update(message);

        Message found = messageDAO.findOne(message.getId());
        assertEquals("Modifié",          found.getTitle());
        assertEquals("Contenu modifié",  found.getContent());
    }

    @Test
    public void testDeleteById() {
        Message message = msg("To Delete", "contenu", LocalDateTime.now());
        messageDAO.save(message);
        Long id = message.getId();

        messageDAO.deleteById(id);
        assertNull(messageDAO.findOne(id));
    }

    @Test
    public void testDeleteByUser() {

        messageDAO.save(msg("Del 1", "c", LocalDateTime.now()));
        messageDAO.save(msg("Del 2", "c", LocalDateTime.now()));

        // On appelle deleteByUser avec l'ID de l'EXPÉDITEUR (testSender)
        int deleted = messageDAO.deleteByUser(testSender.getId());
        assertEquals("2 messages doivent être supprimés", 2, deleted);

        List<Message> remaining = messageDAO.findByUserId(testClient.getId());
        assertTrue(remaining.isEmpty());
    }
}