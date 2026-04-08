package fr.istic.taa.jaxrs.dao;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.entity.Account;
import fr.istic.taa.jaxrs.entity.Admin;
import fr.istic.taa.jaxrs.entity.Moral;
import fr.istic.taa.jaxrs.entity.Physique;
import fr.istic.taa.jaxrs.entity.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class AccountDAOTest {

    private AccountDAO accountDAO;

    @Before
    public void setUp() {
        accountDAO = new AccountDAO();
    }

    @After
    public void tearDown() {
        List<Account> all = accountDAO.findAll();
        for (Account a : all) {
            if (a.getEmail().endsWith("@test.com")) {
                accountDAO.delete(a);
            }
        }
    }

    // ─── save / findOne ──────────────────────────────────────────────────────

    @Test
    public void testSaveAndFindAdmin() {
        Admin admin = new Admin("admin@test.com", "pass123", "Alice", "Admin", "superadmin");
        accountDAO.save(admin);

        assertNotNull("L'ID doit être généré après save()", admin.getId());

        Account found = accountDAO.findOne(admin.getId());
        assertNotNull(found);
        assertEquals("admin@test.com", found.getEmail());
        assertTrue(found instanceof Admin);
        assertEquals("superadmin", ((Admin) found).getPseudo());
    }

    @Test
    public void testSaveAndFindUsers() {
        Users user = new Users("user@test.com", "pass", "Bob", "User", false, LocalDateTime.now());
        accountDAO.save(user);

        assertNotNull(user.getId());
        Account found = accountDAO.findOne(user.getId());
        assertNotNull(found);
        assertTrue(found instanceof Users);
    }

    @Test
    public void testSaveAndFindMoral() {
        Moral moral = new Moral("moral@test.com", "pass", "Corp", "SARL", false,
                LocalDateTime.now(), "MaCorp SARL");
        accountDAO.save(moral);

        assertNotNull(moral.getId());
        Account found = accountDAO.findOne(moral.getId());
        assertNotNull(found);
        assertTrue(found instanceof Moral);
        assertEquals("MaCorp SARL", ((Moral) found).getCompanyName());
    }

    @Test
    public void testSaveAndFindPhysique() {
        Physique physique = new Physique("physique@test.com", "pass", "Clara", "Dupont",
                false, LocalDateTime.now(), "F", LocalDate.of(1995, 3, 14));
        accountDAO.save(physique);

        assertNotNull(physique.getId());
        Account found = accountDAO.findOne(physique.getId());
        assertNotNull(found);
        assertTrue(found instanceof Physique);
        assertEquals("F", ((Physique) found).getSexe());
    }

    // ─── findByEmail ─────────────────────────────────────────────────────────

    @Test
    public void testFindByEmail() {
        Admin admin = new Admin("findme@test.com", "pass", "Find", "Me", "findpseudo");
        accountDAO.save(admin);

        Account found = accountDAO.findByEmail("findme@test.com");
        assertNotNull(found);
        assertEquals("findme@test.com", found.getEmail());
    }

    @Test
    public void testFindByEmail_notFound() {
        Account found = accountDAO.findByEmail("inexistant@test.com");
        assertNull(found);
    }

    // ─── existsByEmail ───────────────────────────────────────────────────────

    @Test
    public void testExistsByEmail_true() {
        Admin admin = new Admin("exists@test.com", "pass", "Ex", "Ist", "pseudo");
        accountDAO.save(admin);

        assertTrue(accountDAO.existsByEmail("exists@test.com"));
    }

    @Test
    public void testExistsByEmail_false() {
        assertFalse(accountDAO.existsByEmail("nope@test.com"));
    }

    // ─── update ──────────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Admin admin = new Admin("update@test.com", "pass", "Old", "Name", "pseudo");
        accountDAO.save(admin);

        admin.setFirstname("New");
        admin.setLastname("Updated");
        accountDAO.update(admin);

        Account found = accountDAO.findOne(admin.getId());
        assertEquals("New", found.getFirstname());
        assertEquals("Updated", found.getLastname());
    }

    // ─── delete ──────────────────────────────────────────────────────────────

    @Test
    public void testDeleteById() {
        Admin admin = new Admin("delete@test.com", "pass", "Del", "Me", "pseudo");
        accountDAO.save(admin);
        Long id = admin.getId();

        accountDAO.deleteById(id);
        assertNull(accountDAO.findOne(id));
    }

    // ─── findAll ─────────────────────────────────────────────────────────────

    @Test
    public void testFindAll() {
        accountDAO.save(new Admin("all1@test.com", "p", "A", "B", "ps1"));
        accountDAO.save(new Admin("all2@test.com", "p", "C", "D", "ps2"));

        List<Account> all = accountDAO.findAll();
        assertTrue("Il doit y avoir au moins 2 comptes", all.size() >= 2);
    }

    // ─── findAllAdmins / findAllMorals / findAllPhysiques ────────────────────

    @Test
    public void testFindAllAdmins() {
        accountDAO.save(new Admin("admin2@test.com", "p", "A", "B", "ps"));
        List<Admin> admins = accountDAO.findAllAdmins();
        assertFalse(admins.isEmpty());
        for (Admin a : admins) {
            assertTrue(a instanceof Admin);
        }
    }

    @Test
    public void testFindAllMorals() {
        accountDAO.save(new Moral("moral2@test.com", "p", "A", "B", false, LocalDateTime.now(), "Corp2"));
        List<Moral> morals = accountDAO.findAllMorals();
        assertFalse(morals.isEmpty());
    }

    @Test
    public void testFindAllPhysiques() {
        accountDAO.save(new Physique("physique2@test.com", "p", "A", "B",
                false, LocalDateTime.now(), "M", LocalDate.of(1990, 1, 1)));
        List<Physique> physiques = accountDAO.findAllPhysiques();
        assertFalse(physiques.isEmpty());
    }

    // ─── findActiveUsers ─────────────────────────────────────────────────────

    @Test
    public void testFindActiveUsers() {
        // ✅ CORRIGÉ : le constructeur de Users force isActive=false
        // Il faut créer l'utilisateur puis appeler setActive(true) + update()
        Users active = new Users("active@test.com", "p", "Active", "User", false, LocalDateTime.now());
        accountDAO.save(active);

        // On active le compte après la création
        active.setActive(true);
        accountDAO.update(active);

        List<Users> actives = accountDAO.findActiveUsers();
        boolean found = false;
        for (Users u : actives) {
            if ("active@test.com".equals(u.getEmail())) {
                found = true;
                break;
            }
        }
        assertTrue("L'utilisateur actif doit être dans la liste", found);
    }
}