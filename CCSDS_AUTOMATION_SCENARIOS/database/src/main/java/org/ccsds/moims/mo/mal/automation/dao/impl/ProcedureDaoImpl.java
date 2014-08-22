package org.ccsds.moims.mo.mal.automation.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.ccsds.moims.mo.mal.automation.dao.ProcedureDao;
import org.ccsds.moims.mo.mal.automation.datamodel.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public class ProcedureDaoImpl implements ProcedureDao {
	
	@PersistenceUnit(unitName="automation-persistence")
    private EntityManagerFactory factory;

	public void insertUpdate(Procedure procedure) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		if (procedure.getId() != null && entityManager.find(Procedure.class, procedure.getId()) != null) {
			entityManager.merge(procedure);
		} else {
			entityManager.persist(procedure);
		}
		entityManager.getTransaction().commit();
	}

	public void remove(long id) {
		EntityManager entityManager = factory.createEntityManager();
		Procedure procedure = entityManager.find(Procedure.class, id);
		if (procedure != null) {
			entityManager.getTransaction().begin();
			entityManager.remove(procedure);
			entityManager.getTransaction().commit();
		}
	}

	public List<Procedure> getList() {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("Procedure.findAll");
		return query.getResultList();
	}

	public Procedure get(long id) {
		EntityManager entityManager = factory.createEntityManager();
		return entityManager.find(Procedure.class, id);
	}

}
