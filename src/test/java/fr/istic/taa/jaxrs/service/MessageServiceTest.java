package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Client;
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
    private ClientDAO      clientDAO;

    private Client testClient;  // destinataire (corrigé)
    private Users  testSender;  // expéditeur (senderId obligatoire)
    private Groupe testGroupe;

    @Before
    public void setUp() {
        messageService = new MessageService();
        accountDAO     = new AccountDAO();
        groupeDAO      = new GroupeDAO();
        messageDAO     = new MessageDAO();
        clientDAO      = new ClientDAO();

        testSender = new Users("msgsender@test.com", "pass", "Sender", "Svc", false, LocalDateTime.now());
        accountDAO.save(testSender);

        testClient = new Client();
        testClient.setName("Marie Curie");
        testClient.setUser(testSender);
        clientDAO.save(testClient);

        testGroupe = new Groupe("Groupe Test Message");
        testGroupe.setColor("#123456");
        testGroupe.setUser(testSender);
        groupeDAO.save(testGroupe);
    }

    @After
    public void tearDown() {
        for (Message m : messageDAO.findByUserId(testClient.getId()))
            messageDAO.delete(m);
        for (Message m : messageDAO.findByGroupeId(testGroupe.getId()))
            messageDAO.delete(m);

        groupeDAO.delete(testGroupe);
        clientDAO.delete(testClient);
        accountDAO.delete(testSender);
    }

    private MessageDTO buildDTOForUser(String title, String content) {
        MessageDTO dto = new MessageDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(testClient.getId()); // ID du Client
        dto.setSenderId(testSender.getId());
        dto.setGroupeId(null);
        return dto;
    }

    private MessageDTO buildDTOForGroupe(String title, String content) {
        MessageDTO dto = new MessageDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(null);
        dto.setSenderId(testSender.getId());
        dto.setGroupeId(testGroupe.getId());
        return dto;
    }

    @Test
    public void testCreateMessage_toUser() {
        MessageDTO created = messageService.createMessage(buildDTOForUser("Hello User", "Contenu"));

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Hello User", created.getTitle());
        assertEquals(testClient.getId(), created.getUserId());
        assertEquals(testSender.getId(), created.getSenderId());
        assertNull("groupeId doit être null", created.getGroupeId());
    }

    @Test
    public void testGetMessagesByUser() {
        messageService.createMessage(buildDTOForUser("Msg 1", "c1"));
        messageService.createMessage(buildDTOForUser("Msg 2", "c2"));

        List<MessageDTO> result = messageService.getMessagesByUser(testClient.getId());
        assertEquals(2, result.size());
        for (MessageDTO m : result) {
            assertEquals(testClient.getId(), m.getUserId());
            assertNull(m.getGroupeId());
        }
    }

    @Test
    public void testCreateMessage_toGroupe() {
        MessageDTO created = messageService.createMessage(buildDTOForGroupe("Hello Groupe", "Contenu"));

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Hello Groupe", created.getTitle());
        assertEquals(testGroupe.getId(), created.getGroupeId());
        assertEquals(testSender.getId(), created.getSenderId());
        assertNull("userId doit être null", created.getUserId());
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

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_sansDestinataire() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setSenderId(testSender.getId());
        messageService.createMessage(dto);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_deuxDestinataires() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setSenderId(testSender.getId());
        dto.setUserId(testClient.getId());
        dto.setGroupeId(testGroupe.getId());
        messageService.createMessage(dto);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_sansSender() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(testClient.getId());
        messageService.createMessage(dto);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_userInexistant() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setSenderId(testSender.getId());
        dto.setUserId(999999L);
        messageService.createMessage(dto);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateMessage_groupeInexistant() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Test");
        dto.setContent("Contenu");
        dto.setDateSend(LocalDateTime.now());
        dto.setSenderId(testSender.getId());
        dto.setGroupeId(999999L);
        messageService.createMessage(dto);
    }

    @Test
    public void testDeleteMessage() {
        MessageDTO created = messageService.createMessage(buildDTOForUser("To Delete", "contenu"));
        Long id = created.getId();

        messageService.deleteMessage(id);
        assertNull(messageDAO.findOne(id));
    }

    @Test
    public void testGetMessagesByUser_empty() {
        Client emptyClient = new Client();
        emptyClient.setName("Empty");
        emptyClient.setUser(testSender);
        clientDAO.save(emptyClient);

        List<MessageDTO> result = messageService.getMessagesByUser(emptyClient.getId());
        assertTrue(result.isEmpty());

        clientDAO.delete(emptyClient);
    }
}