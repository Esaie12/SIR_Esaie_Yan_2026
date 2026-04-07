package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dto.AccountDTO;
import fr.istic.taa.jaxrs.entity.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class AccountServiceTest {

    private AccountService accountService;
    private AccountDAO accountDAO;

    @Before
    public void setUp() {
        accountService = new AccountService();
        accountDAO = new AccountDAO();
    }

    @After
    public void tearDown() {
        List<Account> all = accountDAO.findAll();
        for (Account a : all) {
            if (a.getEmail().endsWith("@svctest.com")) {
                accountDAO.delete(a);
            }
        }
    }

    // ─── createAccount ───────────────────────────────────────────────────────

    @Test
    public void testCreateAdmin() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("admin@svctest.com");
        dto.setPassword("pass");
        dto.setFirstname("Alice");
        dto.setLastname("Admin");
        dto.setType("ADMIN");
        dto.setPseudo("superadmin");

        AccountDTO created = accountService.createAccount(dto);

        assertNotNull(created);
        assertEquals("admin@svctest.com", created.getEmail());
        // toDTO() retourne maintenant "ADMIN" (majuscules) grâce à toUpperCase()
        assertEquals("ADMIN", created.getType());
    }

    @Test
    public void testCreateMoral() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("moral@svctest.com");
        dto.setPassword("pass");
        dto.setFirstname("Corp");
        dto.setLastname("SARL");
        dto.setType("MORAL");
        dto.setCompanyName("MaCorp SARL");

        AccountDTO created = accountService.createAccount(dto);
        assertNotNull(created);
        // toDTO() retourne maintenant "MORAL" (majuscules)
        assertEquals("MORAL", created.getType());
    }

    @Test
    public void testCreatePhysique() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("physique@svctest.com");
        dto.setPassword("pass");
        dto.setFirstname("Clara");
        dto.setLastname("Dupont");
        dto.setType("PHYSIQUE");
        dto.setSexe("F");
        dto.setBirthday(LocalDate.of(1995, 5, 20));

        AccountDTO created = accountService.createAccount(dto);
        assertNotNull(created);
        // toDTO() retourne maintenant "PHYSIQUE" (majuscules)
        assertEquals("PHYSIQUE", created.getType());
    }

    @Test(expected = RuntimeException.class)
    public void testCreateAccount_emailDuplique() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("dup@svctest.com");
        dto.setPassword("pass");
        dto.setFirstname("A");
        dto.setLastname("B");
        dto.setType("ADMIN");
        dto.setPseudo("ps");

        accountService.createAccount(dto);
        // Deuxième création avec le même email → doit lever RuntimeException
        accountService.createAccount(dto);
    }

    // ─── findAccount ─────────────────────────────────────────────────────────

    @Test
    public void testFindAccount_notFound() {
        AccountDTO result = accountService.findAccount(999999L);
        assertNull(result);
    }

    // ─── updateAccount ───────────────────────────────────────────────────────

    @Test
    public void testUpdateAccount() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("update@svctest.com");
        dto.setPassword("pass");
        dto.setFirstname("Old");
        dto.setLastname("Name");
        dto.setType("ADMIN");
        dto.setPseudo("ps");
        accountService.createAccount(dto);

        Account saved = accountDAO.findByEmail("update@svctest.com");
        assertNotNull(saved);

        AccountDTO updateDto = new AccountDTO();
        updateDto.setEmail("update@svctest.com");
        updateDto.setFirstname("New");
        updateDto.setLastname("Updated");
        updateDto.setType("ADMIN");

        AccountDTO updated = accountService.updateAccount(saved.getId(), updateDto);
        assertNotNull(updated);
        assertEquals("New", updated.getFirstname());
        assertEquals("Updated", updated.getLastname());
    }

    @Test
    public void testUpdateAccount_notFound() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("ghost@svctest.com");
        dto.setFirstname("Ghost");
        dto.setLastname("User");

        AccountDTO result = accountService.updateAccount(999999L, dto);
        assertNull(result);
    }

    // ─── login ───────────────────────────────────────────────────────────────

    @Test
    public void testLogin_success() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("login@svctest.com");
        dto.setPassword("secret");
        dto.setFirstname("Log");
        dto.setLastname("In");
        dto.setType("ADMIN");
        dto.setPseudo("ps");
        accountService.createAccount(dto);

        AccountDTO result = accountService.login("login@svctest.com", "secret");
        assertNotNull(result);
        assertEquals("login@svctest.com", result.getEmail());
    }

    @Test
    public void testLogin_wrongPassword() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("wrongpass@svctest.com");
        dto.setPassword("correct");
        dto.setFirstname("W");
        dto.setLastname("P");
        dto.setType("ADMIN");
        dto.setPseudo("ps");
        accountService.createAccount(dto);

        AccountDTO result = accountService.login("wrongpass@svctest.com", "wrong");
        assertNull("Login avec mauvais mot de passe doit retourner null", result);
    }

    @Test
    public void testLogin_unknownEmail() {
        AccountDTO result = accountService.login("unknown@svctest.com", "pass");
        assertNull(result);
    }

    // ─── findAllAccounts ─────────────────────────────────────────────────────

    @Test
    public void testFindAllAccounts() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("list@svctest.com");
        dto.setPassword("p");
        dto.setFirstname("L");
        dto.setLastname("T");
        dto.setType("ADMIN");
        dto.setPseudo("ps");
        accountService.createAccount(dto);

        List<AccountDTO> all = accountService.findAllAccounts();
        assertFalse(all.isEmpty());
    }

    // ─── deleteAccount ───────────────────────────────────────────────────────

    @Test
    public void testDeleteAccount() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("del@svctest.com");
        dto.setPassword("p");
        dto.setFirstname("D");
        dto.setLastname("E");
        dto.setType("ADMIN");
        dto.setPseudo("ps");
        accountService.createAccount(dto);

        Account saved = accountDAO.findByEmail("del@svctest.com");
        assertNotNull(saved);

        accountService.deleteAccount(saved.getId());
        assertNull(accountService.findAccount(saved.getId()));
    }
}