package org.ccsds.moims.mo.mal.planning.dao;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.planning.dao.impl.PlanningRequestDaoImpl;
import org.ccsds.moims.mo.mal.planning.dao.impl.PlanningRequestStatusDaoImpl;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestStatus;
import org.ccsds.moims.mo.mal.planning.datamodel.StatusEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaPlanningContext.xml")
public class PlanningRequestStatusDaoTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PlanningRequestStatusDaoTest.class);
	
	@Autowired
	private PlanningRequestStatusDaoImpl planningRequestStatusDaoImpl;
	
	@Autowired
	private PlanningRequestDaoImpl planningRequestDaoImpl;
	
	@Test
	public void testInsertUpdate() {
		PlanningRequest request = new PlanningRequest();
		request.setName("test");
		request.setDestination("destination");
		request.setSource("source");
		planningRequestDaoImpl.insertUpdate(request);
		
		PlanningRequestStatus requestStatus = new PlanningRequestStatus();
		requestStatus.setComment("test");
		requestStatus.setDate(new Date());
		requestStatus.setStatusEnum(StatusEnum.ExecuteCompleted);
		requestStatus.setPlanningRequest(request);
		planningRequestStatusDaoImpl.insertUpdate(requestStatus);
		assertTrue(true);
	}

}
