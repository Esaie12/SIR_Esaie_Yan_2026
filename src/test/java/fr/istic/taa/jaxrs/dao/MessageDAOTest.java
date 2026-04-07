package fr.istic.taa.jaxrs.dao;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
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
    private Users testUser;

    @Before
    public void setUp() {
        messageDAO = new MessageDAO();
        accountDAO = new AccountDAO();

        testUser = new Users("msguser@test.com", "pass", "Msg", "User", false, LocalDateTime.now());
        accountDAO.save(testUser);
    }

    @After
    public void tearDown() {
        List<Message> messages = messageDAO.findByUserId(testUser.getId());
        for (Message m : messages) {
            messageDAO.delete(m);
        }
        accountDAO.delete(testUser);
    }

    // ─── save / findOne ──────────────────────────────────────────────────────

    @Test
    public void testSaveAndFind() {
        Message msg = new Message("Titre test", "Contenu test", LocalDateTime.now(), testUser);
        messageDAO.save(msg);

        assertNotNull("L'ID doit être généré", msg.getId());
        Message found = messageDAO.findOne(msg.getId());
        assertNotNull(found);
        assertEquals("Titre test", found.getTitle());
        assertEquals("Contenu test", found.getContent());
        assertEquals(testUser.getId(), found.getUser().getId());
    }

    // ─── findByUserId (@NamedQuery) ──────────────────────────────────────────

    @Test
    public void testFindByUserId() {
        messageDAO.save(new Message("Msg 1", "Contenu 1", LocalDateTime.now(), testUser));
        messageDAO.save(new Message("Msg 2", "Contenu 2", LocalDateTime.now().minusHours(1), testUser));

        List<Message> result = messageDAO.findByUserId(testUser.getId());
        assertEquals("2 messages attendus", 2, result.size());

        // Vérifie le tri DESC par dateSend → le plus récent en premier
        assertTrue(
                result.get(0).getDateSend().isAfter(result.get(1).getDateSend())
                        || result.get(0).getDateSend().isEqual(result.get(1).getDateSend())
        );
    }

    @Test
    public void testFindByUserId_empty() {
        Users emptyUser = new Users("empty@test.com", "p", "Empty", "User", false, LocalDateTime.now());
        accountDAO.save(emptyUser);

        List<Message> result = messageDAO.findByUserId(emptyUser.getId());
        assertTrue(result.isEmpty());

        accountDAO.delete(emptyUser);
    }

    // ─── findByTitle (@NamedQuery) ───────────────────────────────────────────

    @Test
    public void testFindByTitle_exact() {
        messageDAO.save(new Message("Bonjour monde", "contenu", LocalDateTime.now(), testUser));

        List<Message> result = messageDAO.findByTitle("Bonjour monde");
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByTitle_partiel() {
        messageDAO.save(new Message("Rapport mensuel", "contenu", LocalDateTime.now(), testUser));

        List<Message> result = messageDAO.findByTitle("mensuel");
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByTitle_caseInsensitive() {
        messageDAO.save(new Message("Alerte Système", "contenu", LocalDateTime.now(), testUser));

        List<Message> result = messageDAO.findByTitle("alerte système");
        assertFalse("La recherche doit être insensible à la casse", result.isEmpty());
    }

    // ─── findRecentMessages (JPQL avec setMaxResults) ────────────────────────

    @Test
    public void testFindRecentMessages() {
        messageDAO.save(new Message("Recent 1", "c1", LocalDateTime.now(), testUser));
        messageDAO.save(new Message("Recent 2", "c2", LocalDateTime.now().minusMinutes(5), testUser));
        messageDAO.save(new Message("Recent 3", "c3", LocalDateTime.now().minusMinutes(10), testUser));

        List<Message> result = messageDAO.findRecentMessages(2);
        assertEquals("Doit retourner exactement 2 messages", 2, result.size());
        assertTrue(
                result.get(0).getDateSend().isAfter(result.get(1).getDateSend())
                        || result.get(0).getDateSend().isEqual(result.get(1).getDateSend())
        );
    }

    // ─── update ──────────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Message msg = new Message("Original", "Contenu original", LocalDateTime.now(), testUser);
        messageDAO.save(msg);

        msg.setTitle("Modifié");
        msg.setContent("Contenu modifié");
        messageDAO.update(msg);

        Message found = messageDAO.findOne(msg.getId());
        assertEquals("Modifié", found.getTitle());
        assertEquals("Contenu modifié", found.getContent());
    }

    // ─── deleteById ──────────────────────────────────────────────────────────

    @Test
    public void testDeleteById() {
        Message msg = new Message("To Delete", "contenu", LocalDateTime.now(), testUser);
        messageDAO.save(msg);
        Long id = msg.getId();

        messageDAO.deleteById(id);
        assertNull(messageDAO.findOne(id));
    }

    // ─── deleteByUser (JPQL bulk delete) ─────────────────────────────────────

    @Test
    public void testDeleteByUser() {
        // deleteByUser() gère sa propre transaction
        // car executeUpdate() requiert une transaction active (TransactionRequiredException)
        messageDAO.save(new Message("Del 1", "c", LocalDateTime.now(), testUser));
        messageDAO.save(new Message("Del 2", "c", LocalDateTime.now(), testUser));

        int deleted = messageDAO.deleteByUser(testUser.getId());
        assertEquals("2 messages doivent être supprimés", 2, deleted);

        List<Message> remaining = messageDAO.findByUserId(testUser.getId());
        assertTrue(remaining.isEmpty());
    }
}