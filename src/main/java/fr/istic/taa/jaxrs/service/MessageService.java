package fr.istic.taa.jaxrs.service;

import java.util.ArrayList;
import java.util.List;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Message;
import fr.istic.taa.jaxrs.entity.Users;

public class MessageService {

    private final MessageDAO messageDAO = new MessageDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final GroupeDAO  groupeDAO  = new GroupeDAO();

    // ─── Mapping entité → DTO ───────────────────────────────────────────────

    private MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setDateSend(message.getDateSend());
        // L'un des deux sera null selon le type de destinataire
        dto.setUserId(message.getUser()   != null ? message.getUser().getId()   : null);
        dto.setSenderId(message.getSender() != null ? message.getSender().getId() : null);
        dto.setGroupeId(message.getGroupe() != null ? message.getGroupe().getId() : null);
        return dto;
    }

    // ─── CRUD ───────────────────────────────────────────────────────────────

    /**
     * Crée un message destiné à un User (groupeId null)
     * ou à un Groupe entier (userId null).
     * Les deux ne peuvent pas être null en même temps.
     */
    public MessageDTO createMessage(MessageDTO dto) {

        boolean hasUser   = dto.getUserId()   != null;
        boolean hasGroupe = dto.getGroupeId() != null;

        if (!hasUser && !hasGroupe) {
            throw new RuntimeException("userId ou groupeId est obligatoire");
        }
        if (hasUser && hasGroupe) {
            throw new RuntimeException("userId et groupeId ne peuvent pas être renseignés en même temps");
        }
        if (dto.getSenderId() == null) {
            throw new RuntimeException("senderId est obligatoire pour envoyer un message");
        }
        Users sender = accountDAO.findUserById(dto.getSenderId());
        if (sender == null) {
            throw new RuntimeException("Expéditeur (sender) introuvable");
        }

        Message message;

        if (hasUser) {
            Users user = accountDAO.findUserById(dto.getUserId());
            if (user == null) throw new RuntimeException("Utilisateur destinataire introuvable");
            message = new Message(dto.getTitle(), dto.getContent(), dto.getDateSend(), user, sender);
        } else {
            Groupe groupe = groupeDAO.findOne(dto.getGroupeId());
            if (groupe == null) throw new RuntimeException("Groupe introuvable");
            message = new Message(dto.getTitle(), dto.getContent(), dto.getDateSend(), groupe, sender);
        }

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

    public List<MessageDTO> getMessagesByGroupe(Long groupeId) {
        List<Message> messages = messageDAO.findByGroupeId(groupeId);
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