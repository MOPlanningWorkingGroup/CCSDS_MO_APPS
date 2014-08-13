package org.ccsds.moims.mo.mal.planning.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.planning.dao.TaskDefinitionDao;
import org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition;
import org.springframework.stereotype.Repository;

@Repository
public class TaskDefinitionDaoImpl implements TaskDefinitionDao {
	
	@PersistenceUnit(unitName="planning-persistence")
    private EntityManagerFactory factory;

	public void remove(long id) {
		EntityManager entityManager = factory.createEntityManager();
		TaskDefinition def = entityManager.find(TaskDefinition.class, id);
		if (def != null) {
			entityManager.getTransaction().begin();
			entityManager.remove(def);
			entityManager.getTransaction().commit();
		}
	}

	public List<TaskDefinition> getList() {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("TaskDefinition.findAll");
		return query.getResultList();
	}

	public void insertUpdate(TaskDefinition def) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		if (def.getId() != null && entityManager.find(TaskDefinition.class, def.getId()) != null) {
			entityManager.merge(def);
		} else {
			entityManager.persist(def);
		}
		entityManager.getTransaction().commit();
	}

	public TaskDefinition get(long id) {
		EntityManager entityManager = factory.createEntityManager();
		return entityManager.find(TaskDefinition.class, id);
	}
	
	

}
