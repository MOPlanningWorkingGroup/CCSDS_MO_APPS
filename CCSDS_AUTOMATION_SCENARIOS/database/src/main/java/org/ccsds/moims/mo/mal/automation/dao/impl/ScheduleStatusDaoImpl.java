package org.ccsds.moims.mo.mal.automation.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.ccsds.moims.mo.mal.automation.dao.ScheduleStatusDao;
import org.ccsds.moims.mo.mal.automation.datamodel.Schedule;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleState;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatus;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatusMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleStatusDaoImpl implements ScheduleStatusDao {
	
	@PersistenceUnit(unitName="automation-persistence")
    private EntityManagerFactory factory;
	
	@Autowired
	private ScheduleDaoImpl scheduleDaoImpl;

	public void insert(Schedule schedule, ScheduleState state) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		ScheduleStatus scheduleStatus = new ScheduleStatus();
		scheduleStatus.setSchedule(schedule);
		scheduleStatus.setState(state);
		scheduleStatus.setCreationDate(new Date());
		scheduleStatus.setMessages(new ArrayList<ScheduleStatusMessage>());
		ScheduleStatusMessage msg1 = new ScheduleStatusMessage();
		msg1.setScheduleStatus(scheduleStatus);
		msg1.setMessage("this is status message");
		scheduleStatus.getMessages().add(msg1);
		entityManager.persist(scheduleStatus);
		entityManager.getTransaction().commit();
	}

	public List<ScheduleStatus> getList(Schedule schedule) {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("ScheduleStatus.findScheduleStatuses");
		query.setParameter("schedule", schedule);
		return query.getResultList();
	}

	public void start(Long scheduleId) {
		Schedule schedule = scheduleDaoImpl.get(scheduleId);
		if (schedule != null) {
			insert(schedule, ScheduleState.RUNNING);
		}
	}

	public void pause(Long scheduleId) {
		Schedule schedule = scheduleDaoImpl.get(scheduleId);
		if (schedule != null) {
			insert(schedule, ScheduleState.PAUSED);
		}
	}

	public void resume(Long scheduleId) {
		Schedule schedule = scheduleDaoImpl.get(scheduleId);
		if (schedule != null) {
			insert(schedule, ScheduleState.RUNNING);
		}
	}

	public void terminate(Long scheduleId) {
		Schedule schedule = scheduleDaoImpl.get(scheduleId);
		if (schedule != null) {
			insert(schedule, ScheduleState.ABORTED);
		}
	}

	public ScheduleStatus getCurrentStatus(Schedule schedule) {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("ScheduleStatus.findScheduleStatuses");
		query.setParameter("schedule", schedule);
		List<ScheduleStatus> list = query.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(list.size() - 1);
		} else {
			return null;
		}
	}

	

}
