package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.generic.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.entity.Groupe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GroupeService {

    private final GroupeDAO groupeDAO = new GroupeDAO();

    public GroupeDTO toDTO(Groupe groupe) {
        if (groupe == null) return null;
        return new GroupeDTO(
                groupe.getId(),
                groupe.getLibelle(),
                groupe.getDateCreate(),
                groupe.getColor()
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

    public GroupeDTO createGroupe(GroupeDTO dto) {
        Groupe groupe = toEntity(dto);
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