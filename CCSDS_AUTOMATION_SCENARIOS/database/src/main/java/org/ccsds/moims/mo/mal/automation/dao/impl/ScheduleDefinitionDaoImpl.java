package org.ccsds.moims.mo.mal.automation.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.automation.dao.ScheduleDefinitionDao;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleDefinitionDaoImpl implements ScheduleDefinitionDao {
	
	@PersistenceUnit(unitName="planning-persistence")
    private EntityManagerFactory factory;

	public void insertUpdate(ScheduleDefinition def) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		if (def.getId() != null && entityManager.find(ScheduleDefinition.class, def.getId()) != null) {
			entityManager.merge(def);
		} else {
			entityManager.persist(def);
		}
		entityManager.getTransaction().commit();
	}

	public void remove(long id) {
		EntityManager entityManager = factory.createEntityManager();
		ScheduleDefinition def = entityManager.find(ScheduleDefinition.class, id);
		if (def != null) {
			entityManager.getTransaction().begin();
			entityManager.remove(def);
			entityManager.getTransaction().commit();
		}
	}

	public List<ScheduleDefinition> getList() {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("ScheduleDefinition.findAll");
		return query.getResultList();
	}

	public ScheduleDefinition get(long id) {
		EntityManager entityManager = factory.createEntityManager();
		return entityManager.find(ScheduleDefinition.class, id);
	}

}
