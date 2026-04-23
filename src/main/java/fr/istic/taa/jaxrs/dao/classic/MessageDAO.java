package fr.istic.taa.jaxrs.dao.classic;

import fr.istic.taa.jaxrs.dao.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Message;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class MessageDAO extends AbstractJpaDao<Long, Message> {

    public MessageDAO() {
        setClazz(Message.class);
    }

    public List<Message> findByUserId(Long userId) {
        return entityManager
                .createNamedQuery("Message.findByUser", Message.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Message> findByGroupeId(Long groupeId) {
        return entityManager
                .createNamedQuery("Message.findByGroupe", Message.class)
                .setParameter("groupeId", groupeId)
                .getResultList();
    }

    public List<Message> findByTitle(String keyword) {
        return entityManager
                .createNamedQuery("Message.findByTitle", Message.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

    public List<Message> findRecentMessages(int limit) {
        return entityManager.createQuery(
                        "SELECT m FROM Message m ORDER BY m.dateSend DESC",
                        Message.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public int deleteByUser(Long userId) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        int deleted = entityManager.createQuery(
                        "DELETE FROM Message m WHERE m.user.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        tx.commit();
        return deleted;
    }

    public int deleteByGroupe(Long groupeId) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        int deleted = entityManager.createQuery(
                        "DELETE FROM Message m WHERE m.groupe.id = :groupeId")
                .setParameter("groupeId", groupeId)
                .executeUpdate();
        tx.commit();
        return deleted;
    }

    
    public List<Message> findBySender(Long senderId) {
        return entityManager.createQuery(
                "SELECT m FROM Message m WHERE m.sender.id = :senderId ORDER BY m.dateSend DESC",
                Message.class)
            .setParameter("senderId", senderId)
            .getResultList();
  
  }

    public long countSentByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT COUNT(m) FROM Message m WHERE m.sender.id = :userId",
                        Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}