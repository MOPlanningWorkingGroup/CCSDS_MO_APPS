package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.automation.dao.impl.ScheduleDaoImpl;
import org.ccsds.moims.mo.mal.automation.datamodel.Schedule;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleArgumentValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaAutomationContext.xml")
public class ScheduleDaoTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ScheduleDaoTest.class);
	
	@Autowired
	private ScheduleDaoImpl scheduleDaoImpl;
	
	@Test
	public void testScheduleDao() {
		Schedule schedule = new Schedule();
		schedule.setName("schedule");
		schedule.setDescription("schedule description");
		schedule.setArgumentValues(new ArrayList<ScheduleArgumentValue>());
		ScheduleArgumentValue arg1 = new ScheduleArgumentValue();
		arg1.setSchedule(schedule);
		arg1.setValue("schedule value");
		schedule.getArgumentValues().add(arg1);
		scheduleDaoImpl.insertUpdate(schedule);
		schedule = scheduleDaoImpl.get(schedule.getId());
		assertTrue(schedule != null);
		scheduleDaoImpl.remove(schedule.getId());
		List<Schedule> list = scheduleDaoImpl.getList();
		assertTrue(list.size() == 0);
	}

}
