package fr.istic.taa.jaxrs.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import fr.istic.taa.jaxrs.dao.generic.classic.AccountDAO;
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
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setFirstname(account.getFirstname());
        dto.setLastname(account.getLastname());
       // dto.setLastLogin(account.getLastLogin());

        // type
        dto.setType(account.getClass().getSimpleName());

        // champs spécifiques
       /* if (account instanceof Admin admin) {
            dto.setPseudo(admin.getPseudo());
        }

        if (account instanceof Users user) {
            dto.setActive(user.isActive());
            dto.setCreatedAt(user.getCreatedAt());
        }

        if (account instanceof Moral moral) {
            dto.setCompanyName(moral.getCompanyName());
        }

        if (account instanceof Physique physique) {
            dto.setSexe(physique.getSexe());
            dto.setBirthday(physique.getBirthday());
        }*/

        return dto;
    }

    // ─── Mapping DTO → entité ───────────────────────────────────────────────

    public Account toEntity(AccountDTO dto) {

        switch (dto.getType().toUpperCase()) {

            case "ADMIN":
                return new Admin(
                        dto.getEmail(),
                        dto.getPassword(),
                        dto.getFirstname(),
                        dto.getLastname(),
                        dto.getPseudo()
                );

            case "MORAL":
                return new Moral(
                        dto.getEmail(),
                        dto.getPassword(),
                        dto.getFirstname(),
                        dto.getLastname(),
                        false,
                        LocalDateTime.now(),
                        dto.getCompanyName()
                );

            case "PHYSIQUE":
                return new Physique(
                        dto.getEmail(),
                        dto.getPassword(),
                        dto.getFirstname(),
                        dto.getLastname(),
                        false,
                        LocalDateTime.now(),
                        dto.getSexe(),
                        dto.getBirthday()
                );

            case "USER":
            default:
                return new Users(
                        dto.getEmail(),
                        dto.getPassword(),
                        dto.getFirstname(),
                        dto.getLastname(),
                        false,
                        LocalDateTime.now()
                );
        }
    }

    // ─── CRUD ───────────────────────────────────────────────────────────────

    /** Récupère un compte par ID */
    public AccountDTO findAccount(Long id) {
        return toDTO(accountDAO.findOne(id));
    }

    /** Récupère tous les comptes */
    public List<AccountDTO> findAllAccounts() {
        return accountDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Crée un compte */
    public AccountDTO createAccount(AccountDTO dto) {

        // vérifier email unique
        if (accountDAO.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Account account = toEntity(dto);
        Account saved = accountDAO.update(account);

        return toDTO(saved);
    }

    /** Met à jour un compte */
    public AccountDTO updateAccount(Long id, AccountDTO dto) {

        Account existing = accountDAO.findOne(id);
        if (existing == null) return null;

        existing.setEmail(dto.getEmail());
        existing.setFirstname(dto.getFirstname());
        existing.setLastname(dto.getLastname());

        // ⚠️ optionnel : update password
        if (dto.getPassword() != null) {
            existing.setPassword(dto.getPassword());
        }

        return toDTO(accountDAO.update(existing));
    }

    /** Supprime un compte */
    public void deleteAccount(Long id) {
        accountDAO.deleteById(id);
    }

    // ─── Requêtes métier ────────────────────────────────────────────────────

    /** Trouver par email */
    public AccountDTO findByEmail(String email) {
        return toDTO(accountDAO.findByEmail(email));
    }

    /** Login simple */
    public AccountDTO login(String email, String password) {
        Account account = accountDAO.findByEmail(email);

        if (account == null) return null;

        if (!account.getPassword().equals(password)) return null;

        // mettre à jour lastLogin
       // account.setLastLogin(LocalDateTime.now());
        accountDAO.update(account);

        return toDTO(account);
    }

    /** Récupérer les comptes actifs */
    public List<AccountDTO> findActiveUsers() {
        return accountDAO.findActiveUsers().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Récupérer les admins */
    public List<AccountDTO> findAdmins() {
        return accountDAO.findAllAdmins().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<AccountDTO> findMoralAccounts() {
        return accountDAO.findAllMorals().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> findPhysiqueAccounts() {
        return accountDAO.findAllPhysiques().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
