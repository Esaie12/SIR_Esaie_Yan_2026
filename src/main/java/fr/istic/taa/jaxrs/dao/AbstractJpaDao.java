package fr.istic.taa.jaxrs.dao;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public abstract class AbstractJpaDao<K, T extends Serializable> implements IGenericDao<K, T> {

	private Class<T> clazz;

	protected EntityManager entityManager;

	public AbstractJpaDao() {
		this.entityManager = EntityManagerHelper.getEntityManager();
	}

	public void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public T findOne(K id) {
		return entityManager.find(clazz, id);
	}

	public List<T> findAll() {
		return entityManager.createQuery("select e from " + clazz.getName() + " as e", clazz).getResultList();
	}

	public void save(T entity) {
		EntityTransaction t = this.entityManager.getTransaction();
		// Ouvre une transaction seulement si aucune n'est en cours
		boolean ownTx = !t.isActive();
		if (ownTx) t.begin();

		entityManager.persist(entity);

		if (ownTx) t.commit();
	}

	public T update(final T entity) {
		EntityTransaction t = this.entityManager.getTransaction();
		// Ouvre une transaction seulement si aucune n'est en cours
		boolean ownTx = !t.isActive();
		if (ownTx) t.begin();

		T res = entityManager.merge(entity);

		if (ownTx) t.commit();
		return res;
	}

	public void delete(T entity) {
		EntityTransaction t = this.entityManager.getTransaction();
		boolean ownTx = !t.isActive();
		if (ownTx) t.begin();

		// Sécurité anti-crash : évite l'erreur "Removing a detached instance"
		entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));

		if (ownTx) t.commit();
	}

	public void deleteById(K entityId) {
		T entity = findOne(entityId);
		if (entity != null) {
			delete(entity);
		}
	}
}