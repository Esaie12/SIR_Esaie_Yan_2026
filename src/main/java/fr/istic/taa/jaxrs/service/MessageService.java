package fr.istic.taa.jaxrs.service;

import java.util.ArrayList;
import java.util.List;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Message;
import fr.istic.taa.jaxrs.entity.Users;

public class MessageService {

    private final MessageDAO messageDAO = new MessageDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final GroupeDAO  groupeDAO  = new GroupeDAO();
    private final ClientDAO  clientDAO  = new ClientDAO();

    // ─── Convertit un Message en DTO ────────────────────────────────────────
    // userId = destinataire client, senderId = expéditeur, groupeId = groupe destinataire
    // L'un des deux (userId ou groupeId) sera toujours null
    private MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setDateSend(message.getDateSend());
        dto.setUserId(message.getClient()   != null ? message.getClient().getId()   : null);
        dto.setSenderId(message.getSender() != null ? message.getSender().getId()   : null);
        dto.setGroupeId(message.getGroupe() != null ? message.getGroupe().getId()   : null);
        return dto;
    }

    // ─── Crée un message vers un client OU vers un groupe ───────────────────
    // userId et groupeId ne peuvent pas être renseignés en même temps
    public MessageDTO createMessage(MessageDTO dto) {

        boolean hasUser   = dto.getUserId()   != null;
        boolean hasGroupe = dto.getGroupeId() != null;

        if (!hasUser && !hasGroupe)
            throw new RuntimeException("userId ou groupeId est obligatoire");
        if (hasUser && hasGroupe)
            throw new RuntimeException("userId et groupeId ne peuvent pas être renseignés en même temps");
        if (dto.getSenderId() == null)
            throw new RuntimeException("senderId est obligatoire pour envoyer un message");

        // Vérifie que l'expéditeur existe
        Users sender = accountDAO.findUserById(dto.getSenderId());
        if (sender == null)
            throw new RuntimeException("Expéditeur (sender) introuvable");

        Message message;

        if (hasUser) {
            // Message destiné à un client précis
            Client client = clientDAO.findClientById(dto.getUserId());
            if (client == null) throw new RuntimeException("Client destinataire introuvable");
            message = new Message(dto.getTitle(), dto.getContent(), dto.getDateSend(), client, sender);
        } else {
            // Message destiné à tous les membres d'un groupe
            Groupe groupe = groupeDAO.findOne(dto.getGroupeId());
            if (groupe == null) throw new RuntimeException("Groupe introuvable");
            message = new Message(dto.getTitle(), dto.getContent(), dto.getDateSend(), groupe, sender);
        }

        messageDAO.save(message);
        return toDTO(message);
    }

    // ─── Récupère les messages reçus par un client (trié par date desc) ──────
    public List<MessageDTO> getMessagesByUser(Long userId) {
        List<Message> messages = messageDAO.findByUserId(userId);
        List<MessageDTO> dtos = new ArrayList<>();
        for (Message message : messages) dtos.add(toDTO(message));
        return dtos;
    }

    // ─── Récupère les messages envoyés à un groupe (trié par date desc) ──────
    public List<MessageDTO> getMessagesByGroupe(Long groupeId) {
        List<Message> messages = messageDAO.findByGroupeId(groupeId);
        List<MessageDTO> dtos = new ArrayList<>();
        for (Message message : messages) dtos.add(toDTO(message));
        return dtos;
    }

    // ─── Supprime un message par son ID ─────────────────────────────────────
    public void deleteMessage(Long id) {
        messageDAO.deleteById(id);
    }

    // ─── Récupère tous les messages envoyés par un utilisateur ──────────────
    // senderId = l'utilisateur connecté qui veut voir ses messages envoyés
    public List<MessageDTO> getMesMessages(Long senderId) {
        List<Message> messages = messageDAO.findBySender(senderId);
        List<MessageDTO> dtos = new ArrayList<>();
        for (Message message : messages) dtos.add(toDTO(message));
        return dtos;
    }
}