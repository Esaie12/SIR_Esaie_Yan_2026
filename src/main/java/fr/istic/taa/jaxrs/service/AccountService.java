package fr.istic.taa.jaxrs.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dto.AccountDTO;
import fr.istic.taa.jaxrs.entity.Account;
import fr.istic.taa.jaxrs.entity.Admin;
import fr.istic.taa.jaxrs.entity.Moral;
import fr.istic.taa.jaxrs.entity.Physique;
import fr.istic.taa.jaxrs.entity.Users;

public class AccountService {

    private final AccountDAO accountDAO = new AccountDAO();

    // ─── Mapping entité → DTO ───────────────────────────────────────────────

    public AccountDTO toDTO(Account account) {
        if (account == null) return null;
        AccountDTO dto = new AccountDTO();
        dto.setEmail(account.getEmail());
        dto.setFirstname(account.getFirstname());
        dto.setLastname(account.getLastname());
        dto.setType(account.getClass().getSimpleName().toUpperCase());
        dto.setId(account.getId());
        return dto;
    }

    // ─── Mapping DTO → entité ───────────────────────────────────────────────

    public Account toEntity(AccountDTO dto) {
        switch (dto.getType().toUpperCase()) {
            case "ADMIN":
                return new Admin(dto.getEmail(), dto.getPassword(),
                        dto.getFirstname(), dto.getLastname(), dto.getPseudo());
            case "MORAL":
                return new Moral(dto.getEmail(), dto.getPassword(),
                        dto.getFirstname(), dto.getLastname(),
                        false, LocalDateTime.now(), dto.getCompanyName());
            case "PHYSIQUE":
                return new Physique(dto.getEmail(), dto.getPassword(),
                        dto.getFirstname(), dto.getLastname(),
                        false, LocalDateTime.now(), dto.getSexe(), dto.getBirthday());
            case "USER":
            default:
                return new Users(dto.getEmail(), dto.getPassword(),
                        dto.getFirstname(), dto.getLastname(),
                        false, LocalDateTime.now());
        }
    }

    // ─── CRUD ───────────────────────────────────────────────────────────────

    public AccountDTO findAccount(Long id) {
        return toDTO(accountDAO.findOne(id));
    }

    public List<AccountDTO> findAllAccounts() {
        List<Account> accounts = accountDAO.findAll();
        List<AccountDTO> result = new ArrayList<>();
        for (Account account : accounts) {
            result.add(toDTO(account));
        }
        return result;
    }

    public AccountDTO createAccount(AccountDTO dto) {
        if (accountDAO.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        Account account = toEntity(dto);
        accountDAO.save(account);
        return toDTO(account);
    }

    public AccountDTO updateAccount(Long id, AccountDTO dto) {
        Account existing = accountDAO.findOne(id);
        if (existing == null) return null;
        existing.setEmail(dto.getEmail());
        existing.setFirstname(dto.getFirstname());
        existing.setLastname(dto.getLastname());
        if (dto.getPassword() != null) {
            existing.setPassword(dto.getPassword());
        }
        return toDTO(accountDAO.update(existing));
    }

    public void deleteAccount(Long id) {
        accountDAO.deleteById(id);
    }

    // ─── Requêtes métier ────────────────────────────────────────────────────

    public AccountDTO findByEmail(String email) {
        return toDTO(accountDAO.findByEmail(email));
    }

    public AccountDTO login(String email, String password) {
        Account account = accountDAO.findByEmail(email);
        if (account == null) return null;
        if (!account.getPassword().equals(password)) return null;
        accountDAO.update(account);
        return toDTO(account);
    }

    public List<AccountDTO> findActiveUsers() {
        List<Users> users = accountDAO.findActiveUsers();
        List<AccountDTO> result = new ArrayList<>();
        for (Users user : users) {
            result.add(toDTO(user));
        }
        return result;
    }

    public List<AccountDTO> findAdmins() {
        List<Admin> admins = accountDAO.findAllAdmins();
        List<AccountDTO> result = new ArrayList<>();
        for (Admin admin : admins) {
            result.add(toDTO(admin));
        }
        return result;
    }

    public List<AccountDTO> findMoralAccounts() {
        List<Moral> morals = accountDAO.findAllMorals();
        List<AccountDTO> result = new ArrayList<>();
        for (Moral moral : morals) {
            result.add(toDTO(moral));
        }
        return result;
    }

    public List<AccountDTO> findPhysiqueAccounts() {
        List<Physique> physiques = accountDAO.findAllPhysiques();
        List<AccountDTO> result = new ArrayList<>();
        for (Physique physique : physiques) {
            result.add(toDTO(physique));
        }
        return result;
    }
}