package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.generic.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.generic.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class GroupeServiceTest {

    private GroupeService groupeService;
    private GroupeDAO groupeDAO;
    private Long groupeId;

    @Before
    public void setUp() {
        groupeService = new GroupeService();
        groupeDAO = new GroupeDAO();
    }

    @After
    public void tearDown() {
        if (groupeId != null) {
            Groupe g = groupeDAO.findOne(groupeId);
            if (g != null) groupeDAO.delete(g);
            groupeId = null;
        }
    }

    private GroupeDTO buildDTO(String libelle, String color) {
        GroupeDTO dto = new GroupeDTO();
        dto.setLibelle(libelle);
        dto.setColor(color);
        return dto;
    }

    // ─── createGroupe ────────────────────────────────────────────────────────

    @Test
    public void testCreateGroupe() {
        GroupeDTO dto = buildDTO("Gold", "#FFD700");
        GroupeDTO created = groupeService.createGroupe(dto);

        assertNotNull(created);
        assertNotNull("L'ID doit être généré", created.getId());
        assertEquals("Gold", created.getLibelle());
        assertEquals("#FFD700", created.getColor());
        assertNotNull("dateCreate doit être générée automatiquement", created.getDateCreate());
        groupeId = created.getId();
    }

    // ─── findGroupe ──────────────────────────────────────────────────────────

    @Test
    public void testFindGroupe() {
        GroupeDTO created = groupeService.createGroupe(buildDTO("Silver", "#C0C0C0"));
        groupeId = created.getId();

        GroupeDTO found = groupeService.findGroupe(groupeId);
        assertNotNull(found);
        assertEquals("Silver", found.getLibelle());
    }

    @Test
    public void testFindGroupe_notFound() {
        assertNull(groupeService.findGroupe(999999L));
    }

    // ─── updateGroupe ────────────────────────────────────────────────────────

    @Test
    public void testUpdateGroupe() {
        GroupeDTO created = groupeService.createGroupe(buildDTO("Old", "#000000"));
        groupeId = created.getId();

        GroupeDTO updateDto = buildDTO("New Libelle", "#FFFFFF");
        GroupeDTO updated = groupeService.updateGroupe(groupeId, updateDto);

        assertNotNull(updated);
        assertEquals("New Libelle", updated.getLibelle());
        assertEquals("#FFFFFF", updated.getColor());
    }

    @Test
    public void testUpdateGroupe_notFound() {
        assertNull(groupeService.updateGroupe(999999L, buildDTO("X", "#000")));
    }

    // ─── deleteGroupe ────────────────────────────────────────────────────────

    @Test
    public void testDeleteGroupe() {
        GroupeDTO created = groupeService.createGroupe(buildDTO("To Delete", "#111"));
        Long id = created.getId();

        groupeService.deleteGroupe(id);
        assertNull(groupeService.findGroupe(id));
    }

    // ─── findAllGroupes ──────────────────────────────────────────────────────

    @Test
    public void testFindAllGroupes() {
        GroupeDTO created = groupeService.createGroupe(buildDTO("List Test", "#222"));
        groupeId = created.getId();

        List<GroupeDTO> all = groupeService.findAllGroupes();
        assertFalse(all.isEmpty());
    }
}