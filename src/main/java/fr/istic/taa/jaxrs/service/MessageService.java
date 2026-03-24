package fr.istic.taa.jaxrs.service;

import java.util.List;
import java.util.stream.Collectors;

import fr.istic.taa.jaxrs.dao.generic.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.generic.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Message;
import fr.istic.taa.jaxrs.entity.Users;

public class MessageService {

	private final MessageDAO messageDAO = new MessageDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public MessageDTO createMessage(MessageDTO dto) {
        Users user = accountDAO.findUserById(dto.getUserId());
        if (user == null) throw new RuntimeException("Utilisateur introuvable");

        Message message = new Message(dto.getTitle(), dto.getContent(), dto.getDateSend(), user);
        Message saved = messageDAO.update(message);
        return toDTO(saved);
    }

    public List<MessageDTO> getMessagesByUser(Long userId) {
        return messageDAO.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setDateSend(message.getDateSend());
        dto.setUserId(message.getUser().getId());
        return dto;
    }
    
    public void deleteMessage(Long id) {
        messageDAO.deleteById(id);
    }
}
