package org.ccsds.moims.mo.mal.automation.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.automation.dao.ProcedureDefinitionDao;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition;
import org.springframework.stereotype.Repository;

@Repository
public class ProcedureDefinitionDaoImpl implements ProcedureDefinitionDao {
	
	@PersistenceUnit(unitName="automation-persistence")
    private EntityManagerFactory factory;

	public void insertUpdate(ProcedureDefinition definition) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		if (definition.getId() != null && entityManager.find(ProcedureDefinition.class, definition.getId()) != null) {
			entityManager.merge(definition);
		} else {
			entityManager.persist(definition);
		}
		entityManager.getTransaction().commit();
	}

	public void remove(long id) {
		EntityManager entityManager = factory.createEntityManager();
		ProcedureDefinition definition = entityManager.find(ProcedureDefinition.class, id);
		if (definition != null) {
			entityManager.getTransaction().begin();
			entityManager.remove(definition);
			entityManager.getTransaction().commit();
		}
	}

	public List<ProcedureDefinition> getList() {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("ProcedureDefinition.findAll");
		return query.getResultList();
	}

	public ProcedureDefinition get(long id) {
		EntityManager entityManager = factory.createEntityManager();
		return entityManager.find(ProcedureDefinition.class, id);
	}

}
