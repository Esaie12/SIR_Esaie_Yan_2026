package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Groupe;
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
    private AccountDAO     accountDAO;
    private GroupeDAO      groupeDAO;
    private MessageDAO     messageDAO;

    private Users  testUser;
    private Groupe testGroupe;

    @Before
    public void setUp() {
        messageService = new MessageService();
        accountDAO     = new AccountDAO();
        groupeDAO      = new GroupeDAO();
        messageDAO     = new MessageDAO();

        testUser = new Users("msgsvc@test.com", "pass", "Msg", "Svc", false, LocalDateTime.now());
        accountDAO.save(testUser);

        testGroupe = new Groupe("Groupe Test Message");
        testGroupe.setColor("#123456");
        groupeDAO.save(testGroupe);
    }

    @After
    public void tearDown() {
        List<Message> messagesUser = messageDAO.findByUserId(testUser.getId());
        for (Message m : messagesUser) messageDAO.delete(m);

        List<Message> messagesGroupe = messageDAO.findByGroupeId(testGroupe.getId());
        for (Message m : messagesGroupe) messageDAO.delete(m);

        accountDAO.delete(testUser);
        groupeDAO.delete(testGroupe);
    }

    private MessageDTO buildDTOForUser(String title, String content) {
        MessageDTO dto = new MessageDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(testUser.getId());
        dto.setGroupeId(null);
        return dto;
    }

    private MessageDTO buildDTOForGroupe(String title, String content) {
        MessageDTO dto = new MessageDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(null);
        dto.setGroupeId(testGroupe.getId());
        return dto;
    }

    // ─── Envoi à un User ─────────────────────────────────────────────────────

    @Test
    public void testCreateMessage_toUser() {
        MessageDTO created = messageService.createMessage(buildDTOForUser("Hello User", "Contenu"));

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Hello User", created.getTitle());
        assertEquals(testUser.getId(), created.getUserId());
        assertNull("groupeId doit être null pour un message à un user", created.getGroupeId());
    }

    @Test
    public void testGetMessagesByUser() {
        messageService.createMessage(buildDTOForUser("Msg 1", "c1"));
        messageService.createMessage(buildDTOForUser("Msg 2", "c2"));

        List<MessageDTO> result = messageService.getMessagesByUser(testUser.getId());
        assertEquals(2, result.size());
        for (MessageDTO m : result) {
            assertEquals(testUser.getId(), m.getUserId());
            assertNull(m.getGroupeId());
        }
    }

    // ─── Envoi à un Groupe ───────────────────────────────────────────────────

    @Test
    public void testCreateMessage_toGroupe() {
        MessageDTO created = messageService.createMessage(buildDTOForGroupe("Hello Groupe", "Contenu"));

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Hello Groupe", created.getTitle());
        assertEquals(testGroupe.getId(), created.getGroupeId());
        assertNull("userId doit être null pour un message à un groupe", created.getUserId());
    }

    @Test
    public void testGetMessagesByGroupe() {
        messageService.createMessage(buildDTOForGroupe("Msg G1", "c1"));
        messageService.createMessage(buildDTOForGroupe("Msg G2", "c2"));

        List<MessageDTO> result = messageService.getMessagesByGroupe(testGroupe.getId());
        assertEquals(2, result.size());
        for (MessageDTO m : result) {
            assertEquals(testGroupe.getId(), m.getGroupeId());
            assertNull(m.getUserId());
        }
    }

    // ─── Cas d'erreur ────────────────────────────────────────────────────────

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_sansDestinataire() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        // userId et groupeId tous les deux null → RuntimeException
        messageService.createMessage(dto);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_deuxDestinataires() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(testUser.getId());
        dto.setGroupeId(testGroupe.getId());
        // Les deux renseignés → RuntimeException
        messageService.createMessage(dto);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_userInexistant() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(999999L);
        messageService.createMessage(dto);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_groupeInexistant() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setGroupeId(999999L);
        messageService.createMessage(dto);
    }

    // ─── deleteMessage ───────────────────────────────────────────────────────

    @Test
    public void testDeleteMessage() {
        MessageDTO created = messageService.createMessage(buildDTOForUser("To Delete", "contenu"));
        Long id = created.getId();

        messageService.deleteMessage(id);
        assertNull(messageDAO.findOne(id));
    }

    // ─── User sans messages ──────────────────────────────────────────────────

    @Test
    public void testGetMessagesByUser_empty() {
        Users emptyUser = new Users("empty2@test.com", "p", "E", "U", false, LocalDateTime.now());
        accountDAO.save(emptyUser);

        List<MessageDTO> result = messageService.getMessagesByUser(emptyUser.getId());
        assertTrue(result.isEmpty());

        accountDAO.delete(emptyUser);
    }
}