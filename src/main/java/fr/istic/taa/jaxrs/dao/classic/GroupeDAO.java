package fr.istic.taa.jaxrs.dao.classic;

import fr.istic.taa.jaxrs.dao.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Groupe;

import java.util.List;

public class GroupeDAO extends AbstractJpaDao<Long, Groupe> {

    public GroupeDAO() {
        setClazz(Groupe.class);
    }

    public List<Groupe> findByLibelle(String libelle) {
        return entityManager
                .createNamedQuery("Groupe.findByLibelle", Groupe.class)
                .setParameter("libelle", "%" + libelle + "%")
                .getResultList();
    }

    public List<Groupe> findAllSorted() {
        return entityManager
                .createNamedQuery("Groupe.findAll", Groupe.class)
                .getResultList();
    }

    public List<Groupe> findByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT g FROM Groupe g WHERE g.user.id = :userId",
                        Groupe.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // ─── Compte le nombre de groupes créés par un utilisateur ───────────────
    public long countByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT COUNT(g) FROM Groupe g WHERE g.user.id = :userId",
                        Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    // ─── Compte le nombre de membres (clients) dans un groupe précis ─────────
    // On interroge directement la table ClientGroupe pour avoir le vrai chiffre
    public long countMembersByGroupeId(Long groupeId) {
        return entityManager.createQuery(
                        "SELECT COUNT(cg) FROM ClientGroupe cg WHERE cg.groupe.id = :groupeId",
                        Long.class)
                .setParameter("groupeId", groupeId)
                .getSingleResult();
    }
}