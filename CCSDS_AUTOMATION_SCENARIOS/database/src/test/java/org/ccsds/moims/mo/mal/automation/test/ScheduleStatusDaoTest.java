package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.ccsds.moims.mo.mal.automation.dao.impl.ScheduleDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ScheduleDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ScheduleStatusDaoImpl;
import org.ccsds.moims.mo.mal.automation.datamodel.Schedule;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaContext.xml")
public class ScheduleStatusDaoTest {

	@Autowired
	private ScheduleStatusDaoImpl scheduleStatusDaoImpl;

	@Autowired
	private ScheduleDaoImpl scheduleDaoImpl;

	@Autowired
	private ScheduleDefinitionDaoImpl scheduleDefinitionDaoImpl;

	@PersistenceUnit(unitName = "planning-persistence")
	private EntityManagerFactory factory;

	@Test
	public void testScheduleStatusDao() {
		Schedule schedule = new Schedule();
		ScheduleDefinition scheduleDefinition = new ScheduleDefinition();
		scheduleDefinition.setName("Definition");
		scheduleDefinitionDaoImpl.insertUpdate(scheduleDefinition);
		schedule.setScheduleDefinition(scheduleDefinition);
		scheduleDaoImpl.insertUpdate(schedule);
		scheduleStatusDaoImpl.start(schedule.getId());
		scheduleStatusDaoImpl.pause(schedule.getId());
		scheduleStatusDaoImpl.resume(schedule.getId());
		scheduleStatusDaoImpl.terminate(schedule.getId());
		List<ScheduleStatus> list = scheduleStatusDaoImpl.getList(schedule);
		assertTrue(list.size() == 4);
		cleanScheduleStatusTable(schedule);
		list = scheduleStatusDaoImpl.getList(schedule);
		scheduleDaoImpl.remove(schedule.getId());
		scheduleDefinitionDaoImpl.remove(scheduleDefinition.getId());
		assertTrue(list.size() == 0);
	}

	private void cleanScheduleStatusTable(Schedule schedule) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		List<ScheduleStatus> list = scheduleStatusDaoImpl.getList(schedule);
		for (ScheduleStatus s : list) {
			ScheduleStatus scheduleStatus = entityManager.find(
					ScheduleStatus.class, s.getId());
			entityManager.remove(scheduleStatus);
		}
		entityManager.getTransaction().commit();
	}

}
