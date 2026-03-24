package fr.istic.taa.jaxrs.dao.generic.classic;

import fr.istic.taa.jaxrs.dao.generic.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Message;

import java.util.List;

public class MessageDAO extends AbstractJpaDao<Long, Message> {

    public MessageDAO() {
        setClazz(Message.class);
    }

    /**
     * Récupérer tous les messages d'un utilisateur (Moral ou Physique)
     */
    public List<Message> findByUser(Long userId) {
        return entityManager.createQuery(
                "SELECT m FROM Message m WHERE m.user.id = :userId ORDER BY m.dateSend DESC",
                Message.class)
            .setParameter("userId", userId)
            .getResultList();
    }

    /**
     * Récupérer les messages récents
     */
    public List<Message> findRecentMessages(int limit) {
        return entityManager.createQuery(
                "SELECT m FROM Message m ORDER BY m.dateSend DESC",
                Message.class)
            .setMaxResults(limit)
            .getResultList();
    }

    /**
     * Rechercher par titre (LIKE)
     */
    public List<Message> findByTitle(String keyword) {
        return entityManager.createQuery(
                "SELECT m FROM Message m WHERE LOWER(m.title) LIKE LOWER(:keyword)",
                Message.class)
            .setParameter("keyword", "%" + keyword + "%")
            .getResultList();
    }

    /**
     * Supprimer tous les messages d'un utilisateur
     */
    public int deleteByUser(Long userId) {
        return entityManager.createQuery(
                "DELETE FROM Message m WHERE m.user.id = :userId")
            .setParameter("userId", userId)
            .executeUpdate();
    }
    
    
    public List<Message> findByUserId(Long userId) {
        return entityManager.createQuery(
                "SELECT m FROM Message m WHERE m.user.id = :userId ORDER BY m.dateSend DESC",
                Message.class)
            .setParameter("userId", userId)
            .getResultList();
    }
}