package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroupeService {

    private final GroupeDAO  groupeDAO  = new GroupeDAO();
    private final AccountDAO accountDAO = new AccountDAO();

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
        groupe.setDateCreate(LocalDateTime.now()); // date générée automatiquement
        return groupe;
    }

    // Récupère un groupe par son ID
    public GroupeDTO findGroupe(Long id) {
        // Vide le cache L1 avant de lire pour garantir les données fraîches
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
    // refresh() vide le cache L1 de JPA après le merge pour garantir
    // que le prochain getById retourne les nouvelles valeurs depuis la BDD
    public GroupeDTO updateGroupe(Long id, GroupeDTO dto) {
        Groupe existing = groupeDAO.findOne(id);
        if (existing == null) return null;
        existing.setLibelle(dto.getLibelle());
        existing.setColor(dto.getColor());
        Groupe updated = groupeDAO.update(existing);
        groupeDAO.getEntityManager().refresh(updated); // vide le cache L1
        return toDTO(updated);
    }

    // Supprime un groupe par son ID
    public void deleteGroupe(Long id) {
        groupeDAO.deleteById(id);
    }
}