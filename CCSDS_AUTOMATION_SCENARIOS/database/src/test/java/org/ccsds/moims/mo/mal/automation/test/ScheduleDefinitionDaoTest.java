package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.automation.dao.impl.ScheduleDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleArgumentDefinition;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaContext.xml")
public class ScheduleDefinitionDaoTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ScheduleDefinitionDaoTest.class);
	
	@Autowired
	private ScheduleDefinitionDaoImpl scheduleDefinitionDaoImpl;
	
	@Test
	public void testScheduleDefinitionDao() {
		ScheduleDefinition def = new ScheduleDefinition();
		def.setName("schedule def");
		def.setDescription("schedule description");
		def.setArguments(new ArrayList<ScheduleArgumentDefinition>());
		ScheduleArgumentDefinition arg1 = new ScheduleArgumentDefinition();
		arg1.setName("name");
		arg1.setScheduleDefinition(def);
		arg1.setValueType((short) 4); 
		def.getArguments().add(arg1);
		scheduleDefinitionDaoImpl.insertUpdate(def);
		def = scheduleDefinitionDaoImpl.get(def.getId());
		assertTrue(def != null);
		scheduleDefinitionDaoImpl.remove(def.getId());
		List<ScheduleDefinition> list = scheduleDefinitionDaoImpl.getList();
		assertTrue(list.size() == 0);
	}

}
