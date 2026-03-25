package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.generic.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.generic.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;

import java.time.LocalDateTime;
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
                groupe.getUser().getId()
        );
    }

    public Groupe toEntity(GroupeDTO dto) {
        Groupe groupe = new Groupe();
        groupe.setId(dto.getId());
        groupe.setLibelle(dto.getLibelle());
        groupe.setColor(dto.getColor());
        // dateCreate jamais pris du DTO, toujours généré ici
        groupe.setDateCreate(LocalDateTime.now());
        return groupe;
    }

    public GroupeDTO findGroupe(Long id) {
        return toDTO(groupeDAO.findOne(id));
    }

    public List<GroupeDTO> findAllGroupes() {
        return groupeDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<GroupeDTO> getGroupesByUser(Long userId) {
        Users user = accountDAO.findUserById(userId);
        if (user == null) throw new RuntimeException("Utilisateur introuvable");

        return user.getGroupes().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GroupeDTO createGroupe(GroupeDTO dto) {
    	
    	Users user = accountDAO.findUserById(dto.getUserId());
        if (user == null) throw new RuntimeException("Utilisateur introuvable");

        /*
        Groupe groupe = toEntity(dto);
        Groupe saved = groupeDAO.update(groupe);*/
        
        Groupe groupe = new Groupe();
        groupe.setLibelle(dto.getLibelle());
        groupe.setUser(user); // relation bidirectionnelle possible : user.addGroupe(groupe)

        Groupe saved = groupeDAO.update(groupe);
        return toDTO(saved);
    }

    public GroupeDTO updateGroupe(Long id, GroupeDTO dto) {
        Groupe existing = groupeDAO.findOne(id);
        if (existing == null) return null;

        existing.setLibelle(dto.getLibelle());
        existing.setColor(dto.getColor());
        // dateCreate non modifiable après création

        return toDTO(groupeDAO.update(existing));
    }

    public void deleteGroupe(Long id) {
        groupeDAO.deleteById(id);
    }
}