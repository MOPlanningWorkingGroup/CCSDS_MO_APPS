package org.ccsds.moims.mo.mal.planning.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.planning.dao.PlanningRequestDao;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest;
import org.springframework.stereotype.Repository;

@Repository
public class PlanningRequestDaoImpl implements PlanningRequestDao {
	
	@PersistenceUnit(unitName="planning-persistence")
    private EntityManagerFactory factory;

	public void insertUpdate(PlanningRequest request) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		if (request.getId() != null && entityManager.find(PlanningRequest.class, request.getId()) != null) {
			entityManager.merge(request);
		} else {
			entityManager.persist(request);
		}
		entityManager.getTransaction().commit();
	}

	public void remove(long id) {
		EntityManager entityManager = factory.createEntityManager();
		PlanningRequest request = entityManager.find(PlanningRequest.class, id);
		if (request != null) {
			entityManager.getTransaction().begin();
			entityManager.remove(request);
			entityManager.getTransaction().commit();
		}
	}

	public List<PlanningRequest> getList() {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("PlanningRequest.findAll");
		return query.getResultList();
	}

	public PlanningRequest get(long id) {
		EntityManager entityManager = factory.createEntityManager();
		return entityManager.find(PlanningRequest.class, id);
	}

}
