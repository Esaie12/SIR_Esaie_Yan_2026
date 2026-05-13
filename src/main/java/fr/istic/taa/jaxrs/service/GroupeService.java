package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroupeService {

    private final GroupeDAO  groupeDAO  = new GroupeDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    // Convertit un Groupe en DTO
    // memberCount est compté en base pour éviter les problèmes de lazy loading
    public GroupeDTO toDTO(Groupe groupe) {
        if (groupe == null) return null;
        int memberCount = (int) groupeDAO.countMembersByGroupeId(groupe.getId());
        return new GroupeDTO(
                groupe.getId(),
                groupe.getLibelle(),
                groupe.getDateCreate(),
                groupe.getColor(),
                groupe.getUser() != null ? groupe.getUser().getId() : null,
                memberCount
        );
    }

    // Convertit un DTO en entité Groupe
    public Groupe toEntity(GroupeDTO dto) {
        Groupe groupe = new Groupe();
        groupe.setLibelle(dto.getLibelle());
        groupe.setColor(dto.getColor());
        groupe.setDateCreate(LocalDateTime.now());
        return groupe;
    }

    // Récupère un groupe par son ID
    public GroupeDTO findGroupe(Long id) {
        Groupe groupe = groupeDAO.findOne(id);
        if (groupe != null) {
            groupeDAO.getEntityManager().refresh(groupe);
        }
        return toDTO(groupe);
    }

    // Récupère tous les groupes
    public List<GroupeDTO> findAllGroupes() {
        List<Groupe> groupes = groupeDAO.findAll();
        List<GroupeDTO> dtos = new ArrayList<>();
        for (Groupe groupe : groupes) dtos.add(toDTO(groupe));
        return dtos;
    }

    // Récupère les groupes d'un utilisateur donné
    public List<GroupeDTO> getGroupesByUser(Long userId) {
        List<Groupe> groupes = groupeDAO.findByUserId(userId);
        List<GroupeDTO> dtos = new ArrayList<>();
        for (Groupe groupe : groupes) dtos.add(toDTO(groupe));
        return dtos;
    }

    // Crée un groupe et l'associe à un utilisateur
    public GroupeDTO createGroupe(GroupeDTO dto) {
        Groupe groupe = toEntity(dto);
        if (dto.getUserId() != null) {
            Users user = accountDAO.findUserById(dto.getUserId());
            if (user == null) throw new RuntimeException("Utilisateur introuvable");
            groupe.setUser(user);
        } else {
            throw new RuntimeException("userId est requis pour créer un groupe");
        }
        groupeDAO.save(groupe);
        return toDTO(groupe);
    }

    // Met à jour le libellé et la couleur d'un groupe
    public GroupeDTO updateGroupe(Long id, GroupeDTO dto) {
        Groupe existing = groupeDAO.findOne(id);
        if (existing == null) return null;
        existing.setLibelle(dto.getLibelle());
        existing.setColor(dto.getColor());
        Groupe updated = groupeDAO.update(existing);
        groupeDAO.getEntityManager().refresh(updated);
        return toDTO(updated);
    }

    // Supprime un groupe et ses messages associés
    // Les messages liés au groupe doivent être supprimés avant le groupe
    // sinon la contrainte FK en base provoque un 500
    public void deleteGroupe(Long id) {
        messageDAO.deleteByGroupe(id); // supprime d'abord les messages du groupe
        groupeDAO.deleteById(id);
    }
}