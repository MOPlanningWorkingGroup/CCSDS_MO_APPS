package org.ccsds.moims.mo.mal.automation.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.automation.dao.ScheduleDao;
import org.ccsds.moims.mo.mal.automation.datamodel.Schedule;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleDaoImpl implements ScheduleDao {
	
	@PersistenceUnit(unitName="automation-persistence")
    private EntityManagerFactory factory;

	public void insertUpdate(Schedule schedule) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		if (schedule.getId() != null && entityManager.find(Schedule.class, schedule.getId()) != null) {
			entityManager.merge(schedule);
		} else {
			entityManager.persist(schedule);
		}
		entityManager.getTransaction().commit();
	}

	public void remove(long id) {
		EntityManager entityManager = factory.createEntityManager();
		Schedule schedule = entityManager.find(Schedule.class, id);
		if (schedule != null) {
			entityManager.getTransaction().begin();
			entityManager.remove(schedule);
			entityManager.getTransaction().commit();
		}
	}

	public List<Schedule> getList() {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("Schedule.findAll");
		return query.getResultList();
	}

	public Schedule get(long id) {
		EntityManager entityManager = factory.createEntityManager();
		return entityManager.find(Schedule.class, id);
	}

}
