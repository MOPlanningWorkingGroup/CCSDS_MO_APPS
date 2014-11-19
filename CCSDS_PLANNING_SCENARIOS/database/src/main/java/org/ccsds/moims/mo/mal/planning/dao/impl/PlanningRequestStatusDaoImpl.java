package org.ccsds.moims.mo.mal.planning.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.ccsds.moims.mo.mal.planning.dao.PlanningRequestStatusDao;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestStatus;
import org.springframework.stereotype.Repository;

@Repository
public class PlanningRequestStatusDaoImpl implements PlanningRequestStatusDao {
	
	@PersistenceUnit(unitName="planning-persistence")
    private EntityManagerFactory factory;

	public void insertUpdate(PlanningRequestStatus planningRequestStatus) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		if (planningRequestStatus.getId() != null && entityManager.find(PlanningRequest.class, planningRequestStatus.getId()) != null) {
			entityManager.merge(planningRequestStatus);
		} else {
			entityManager.persist(planningRequestStatus);
		}
		entityManager.getTransaction().commit();
	}
	
	public List<PlanningRequestStatus> getList(PlanningRequest planningRequest) {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("PlanningRequestStatus.findAll");
		query.setParameter("planningRequest", planningRequest);
		return query.getResultList();
	}

}
