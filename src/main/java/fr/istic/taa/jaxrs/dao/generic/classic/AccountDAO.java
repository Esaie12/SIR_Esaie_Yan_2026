package fr.istic.taa.jaxrs.dao.generic.classic;

import java.util.List;

import fr.istic.taa.jaxrs.dao.generic.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Account;
import fr.istic.taa.jaxrs.entity.Admin;
import fr.istic.taa.jaxrs.entity.Moral;
import fr.istic.taa.jaxrs.entity.Physique;
import fr.istic.taa.jaxrs.entity.Users;

public class AccountDAO extends AbstractJpaDao<Long, Account> {

	public AccountDAO() {
        setClazz(Account.class);
    }
	
	
	/**
     * Recherche un compte par email
     */
    public Account findByEmail(String email) {
        List<Account> results = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.email = :email",
                Account.class)
            .setParameter("email", email)
            .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Vérifie si un email existe déjà
     */
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.email = :email",
                Long.class)
            .setParameter("email", email)
            .getSingleResult();

        return count > 0;
    }
    
    /**
     * Récupérer tous les Admins
     */
    public List<Admin> findAllAdmins() {
        return entityManager.createQuery(
                "SELECT a FROM Admin a",
                Admin.class)
            .getResultList();
    }

    /**
     * Récupérer tous les Users (inclut Moral et Physique)
     */
    public List<Users> findAllUsers() {
        return entityManager.createQuery(
                "SELECT u FROM Users u",
                Users.class)
            .getResultList();
    }
    
    public List<Moral> findAllMorals() {
        return entityManager.createQuery(
                "SELECT m FROM Moral m",
                Moral.class
        ).getResultList();
    }

    public List<Physique> findAllPhysiques() {
        return entityManager.createQuery(
                "SELECT p FROM Physique p",
                Physique.class
        ).getResultList();
    }
    
    /**
     * Récupérer les comptes actifs
     */
    public List<Users> findActiveUsers() {
        return entityManager.createQuery(
                "SELECT u FROM Users u WHERE u.isActive = true",
                Users.class)
            .getResultList();
    }

    /**
     * Récupérer les comptes créés après une date
     */
    public List<Users> findUsersCreatedAfter(java.time.LocalDateTime date) {
        return entityManager.createQuery(
                "SELECT u FROM Users u WHERE u.createdAt >= :date",
                Users.class)
            .setParameter("date", date)
            .getResultList();
    }
    
    
    public Users findUserById(Long id) {
        List<Users> result = entityManager.createQuery(
                "SELECT u FROM Users u WHERE u.id = :id",
                Users.class)
            .setParameter("id", id)
            .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}
