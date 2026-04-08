package fr.istic.taa.jaxrs.dao;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.entity.Groupe;
import fr.istic.taa.jaxrs.entity.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class GroupeDAOTest {

    private GroupeDAO groupeDAO;
    private AccountDAO accountDAO;
    private Long groupeId;
    private Users testUser;

    @Before
    public void setUp() {
        groupeDAO = new GroupeDAO();
        accountDAO = new AccountDAO();

        testUser = new Users("groupeuser@test.com", "pass", "Test", "User", false, LocalDateTime.now());
        accountDAO.save(testUser);
    }

    @After
    public void tearDown() {
        if (groupeId != null) {
            Groupe g = groupeDAO.findOne(groupeId);
            if (g != null) groupeDAO.delete(g);
            groupeId = null;
        }
        if (testUser != null) {
            Users u = accountDAO.findUserById(testUser.getId());
            if (u != null) accountDAO.delete(u);
            testUser = null;
        }
    }

    private Groupe createGroupe(String libelle, String color) {
        Groupe g = new Groupe(libelle);
        g.setColor(color);
        // On assigne l'utilisateur pour satisfaire la contrainte not-null
        g.setUser(testUser);
        groupeDAO.save(g);
        groupeId = g.getId();
        return g;
    }

    @Test
    public void testSaveAndFind() {
        Groupe g = createGroupe("VIP", "#FFD700");

        assertNotNull("L'ID doit être généré", g.getId());
        Groupe found = groupeDAO.findOne(g.getId());
        assertNotNull(found);
        assertEquals("VIP", found.getLibelle());
        assertEquals("#FFD700", found.getColor());
        assertNotNull("dateCreate doit être renseignée", found.getDateCreate());
    }

    @Test
    public void testFindByLibelle_exact() {
        createGroupe("Premium", "#0000FF");

        List<Groupe> result = groupeDAO.findByLibelle("Premium");
        assertFalse(result.isEmpty());
        assertEquals("Premium", result.get(0).getLibelle());
    }

    @Test
    public void testFindByLibelle_partiel() {
        createGroupe("Super VIP", "#FF0000");

        List<Groupe> result = groupeDAO.findByLibelle("VIP");
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByLibelle_caseInsensitive() {
        createGroupe("Bronze", "#CD7F32");

        List<Groupe> result = groupeDAO.findByLibelle("bronze");
        assertFalse("La recherche doit être insensible à la casse", result.isEmpty());
    }

    @Test
    public void testFindByLibelle_notFound() {
        List<Groupe> result = groupeDAO.findByLibelle("ZZZNOMATCH");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindAllSorted() {
        createGroupe("Argent", "#C0C0C0");

        List<Groupe> result = groupeDAO.findAllSorted();
        assertFalse(result.isEmpty());
        for (int i = 0; i < result.size() - 1; i++) {
            assertFalse(
                    result.get(i).getDateCreate()
                            .isBefore(result.get(i + 1).getDateCreate())
            );
        }
    }

    @Test
    public void testUpdate() {
        Groupe g = createGroupe("Old Libelle", "#111111");

        g.setLibelle("New Libelle");
        g.setColor("#222222");
        groupeDAO.update(g);

        Groupe found = groupeDAO.findOne(g.getId());
        assertEquals("New Libelle", found.getLibelle());
        assertEquals("#222222", found.getColor());
    }

    @Test
    public void testDeleteById() {
        Groupe g = createGroupe("To Delete", "#000000");
        Long id = g.getId();
        groupeId = null;

        groupeDAO.deleteById(id);
        assertNull(groupeDAO.findOne(id));
    }

    @Test
    public void testFindAll() {
        createGroupe("Groupe A", "#AAAAAA");

        List<Groupe> all = groupeDAO.findAll();
        assertTrue(all.size() >= 1);
    }
}