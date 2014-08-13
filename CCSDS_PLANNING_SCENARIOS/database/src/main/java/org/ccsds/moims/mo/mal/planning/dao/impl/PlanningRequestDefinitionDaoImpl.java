package org.ccsds.moims.mo.mal.planning.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.planning.dao.PlanningRequestDefinitionDao;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition;
import org.springframework.stereotype.Repository;

@Repository
public class PlanningRequestDefinitionDaoImpl implements PlanningRequestDefinitionDao {
	
	@PersistenceUnit(unitName="planning-persistence")
    private EntityManagerFactory factory;

	public void insertUpdate(PlanningRequestDefinition definition) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		if (definition.getId() != null && entityManager.find(PlanningRequestDefinition.class, definition.getId()) != null) {
			entityManager.merge(definition);
		} else {
			entityManager.persist(definition);
		}
		entityManager.getTransaction().commit();
	}

	public void remove(long id) {
		EntityManager entityManager = factory.createEntityManager();
		PlanningRequestDefinition definition = entityManager.find(PlanningRequestDefinition.class, id);
		if (definition != null) {
			entityManager.getTransaction().begin();
			entityManager.remove(definition);
			entityManager.getTransaction().commit();
		}
	}

	public List<PlanningRequestDefinition> getList() {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("PlanningRequestDefinition.findAll");
		return query.getResultList();
	}

	public PlanningRequestDefinition get(long id) {
		EntityManager entityManager = factory.createEntityManager();
		return entityManager.find(PlanningRequestDefinition.class, id);
	}

}
