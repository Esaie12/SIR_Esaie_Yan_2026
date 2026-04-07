package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.generic.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.generic.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Message;
import fr.istic.taa.jaxrs.entity.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class MessageServiceTest {

    private MessageService messageService;
    private AccountDAO accountDAO;
    private MessageDAO messageDAO;
    private Users testUser;

    @Before
    public void setUp() {
        messageService = new MessageService();
        accountDAO = new AccountDAO();
        messageDAO = new MessageDAO();

        testUser = new Users("msgsvc@test.com", "pass", "Msg", "Svc", false, LocalDateTime.now());
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

    private MessageDTO buildDTO(String title, String content) {
        MessageDTO dto = new MessageDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(testUser.getId());
        return dto;
    }

    // ─── createMessage ───────────────────────────────────────────────────────

    @Test
    public void testCreateMessage() {
        MessageDTO dto = buildDTO("Hello", "Contenu du message");
        MessageDTO created = messageService.createMessage(dto);

        assertNotNull(created);
        assertNotNull("L'ID doit être généré", created.getId());
        assertEquals("Hello", created.getTitle());
        assertEquals("Contenu du message", created.getContent());
        assertEquals(testUser.getId(), created.getUserId());
    }

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_userInexistant() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(999999L); // userId inexistant → RuntimeException

        messageService.createMessage(dto);
    }

    // ─── getMessagesByUser ───────────────────────────────────────────────────

    @Test
    public void testGetMessagesByUser() {
        messageService.createMessage(buildDTO("Msg 1", "c1"));
        messageService.createMessage(buildDTO("Msg 2", "c2"));

        List<MessageDTO> result = messageService.getMessagesByUser(testUser.getId());
        assertEquals(2, result.size());

        // Vérifie que tous les messages appartiennent au bon user
        for (MessageDTO m : result) {
            assertEquals(testUser.getId(), m.getUserId());
        }
    }

    @Test
    public void testGetMessagesByUser_empty() {
        Users emptyUser = new Users("empty2@test.com", "p", "E", "U", false, LocalDateTime.now());
        accountDAO.save(emptyUser);

        List<MessageDTO> result = messageService.getMessagesByUser(emptyUser.getId());
        assertTrue(result.isEmpty());

        accountDAO.delete(emptyUser);
    }

    // ─── deleteMessage ───────────────────────────────────────────────────────

    @Test
    public void testDeleteMessage() {
        MessageDTO created = messageService.createMessage(buildDTO("To Delete", "contenu"));
        Long id = created.getId();

        messageService.deleteMessage(id);
        assertNull(messageDAO.findOne(id));
    }
}