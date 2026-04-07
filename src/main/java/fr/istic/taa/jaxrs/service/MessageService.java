package fr.istic.taa.jaxrs.service;

import java.util.ArrayList;
import java.util.List;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Message;
import fr.istic.taa.jaxrs.entity.Users;

public class MessageService {

    private final MessageDAO messageDAO = new MessageDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    // ─── Mapping entité → DTO ───────────────────────────────────────────────

    private MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setDateSend(message.getDateSend());
        dto.setUserId(message.getUser().getId());
        return dto;
    }

    // ─── CRUD ───────────────────────────────────────────────────────────────

    public MessageDTO createMessage(MessageDTO dto) {
        Users user = accountDAO.findUserById(dto.getUserId());
        if (user == null) throw new RuntimeException("Utilisateur introuvable");

        Message message = new Message(dto.getTitle(), dto.getContent(), dto.getDateSend(), user);
        // save() → persist() : création propre sans ID
        messageDAO.save(message);
        return toDTO(message);
    }

    public List<MessageDTO> getMessagesByUser(Long userId) {
        List<Message> messages = messageDAO.findByUserId(userId);
        List<MessageDTO> dtos = new ArrayList<>();
        for (Message message : messages) {
            dtos.add(toDTO(message));
        }
        return dtos;
    }

    public void deleteMessage(Long id) {
        messageDAO.deleteById(id);
    }
}