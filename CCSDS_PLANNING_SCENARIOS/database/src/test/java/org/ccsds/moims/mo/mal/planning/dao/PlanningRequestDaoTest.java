package org.ccsds.moims.mo.mal.planning.dao;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.planning.dao.impl.PlanningRequestDaoImpl;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestStatus;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaPlanningContext.xml")
public class PlanningRequestDaoTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TaskDefinitionDaoTest.class);
	
	@Autowired
	private PlanningRequestDaoImpl planningRequestDaoImpl;
	
	@Test
	public void testPlanningRequestDao() {
		PlanningRequest request = new PlanningRequest();
		request.setName("request");
		request.setCreator("test");
		request.setCreationDate(new Date());
		request.setStatus(PlanningRequestStatus.SUBMITTED);
		request.setValues(new ArrayList<PlanningRequestValue>());
		PlanningRequestValue value1 = new PlanningRequestValue();
		value1.setPlanningRequest(request);
		value1.setType((short) 1);
		value1.setUnit("kg");
		value1.setValue("100".getBytes());
		request.getValues().add(value1);
		PlanningRequestValue value2 = new PlanningRequestValue();
		value2.setPlanningRequest(request);
		value2.setType((short) 2);
		value2.setUnit("m");
		value2.setValue("300".getBytes());
		request.getValues().add(value2);
		planningRequestDaoImpl.insertUpdate(request);
		request.setStatus(PlanningRequestStatus.ACCEPTED);
		planningRequestDaoImpl.insertUpdate(request);
		List<PlanningRequest> list = planningRequestDaoImpl.getList();
		assertTrue(list.size() == 1);
		planningRequestDaoImpl.remove(request.getId());
		list = planningRequestDaoImpl.getList();
		assertTrue(list.size() == 0);
	}

}
