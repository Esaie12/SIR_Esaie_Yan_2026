package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupeService {

    private final GroupeDAO groupeDAO = new GroupeDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public GroupeDTO toDTO(Groupe groupe) {
        if (groupe == null) return null;
        return new GroupeDTO(
                groupe.getId(),
                groupe.getLibelle(),
                groupe.getDateCreate(),
                groupe.getColor(),
                groupe.getUser() != null ? groupe.getUser().getId() : null
        );
    }

    public Groupe toEntity(GroupeDTO dto) {
        Groupe groupe = new Groupe();
        groupe.setLibelle(dto.getLibelle());
        groupe.setColor(dto.getColor());
        groupe.setDateCreate(LocalDateTime.now());
        return groupe;
    }

    public GroupeDTO findGroupe(Long id) {
        return toDTO(groupeDAO.findOne(id));
    }

    public List<GroupeDTO> findAllGroupes() {
        List<Groupe> groupes = groupeDAO.findAll();
        List<GroupeDTO> dtos = new ArrayList<>();
        for (Groupe groupe : groupes) {
            dtos.add(toDTO(groupe));
        }
        return dtos;
    }

    public List<GroupeDTO> getGroupesByUser(Long userId) {
        return groupeDAO.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GroupeDTO createGroupe(GroupeDTO dto) {
        Groupe groupe = toEntity(dto);

        // Récupération et assignation du User obligatoire
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

    public GroupeDTO updateGroupe(Long id, GroupeDTO dto) {
        Groupe existing = groupeDAO.findOne(id);
        if (existing == null) return null;
        existing.setLibelle(dto.getLibelle());
        existing.setColor(dto.getColor());
        return toDTO(groupeDAO.update(existing));
    }

    public void deleteGroupe(Long id) {
        groupeDAO.deleteById(id);
    }
}