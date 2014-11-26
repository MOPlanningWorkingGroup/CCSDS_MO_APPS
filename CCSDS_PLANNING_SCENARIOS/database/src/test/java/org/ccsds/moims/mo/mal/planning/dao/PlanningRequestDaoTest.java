package org.ccsds.moims.mo.mal.planning.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.planning.dao.impl.PlanningRequestDaoImpl;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaPlanningContext.xml")
public class PlanningRequestDaoTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PlanningRequestDaoTest.class);
	
	@Autowired
	private PlanningRequestDaoImpl planningRequestDaoImpl;
	
	@Test
	public void testPlanningRequestDao() {
		PlanningRequest request = new PlanningRequest();
		request.setName("test");
		request.setDestination("destination");
		request.setSource("source");
		planningRequestDaoImpl.insertUpdate(request);
		List<PlanningRequest> list = planningRequestDaoImpl.getList();
		long size = list.size();
		assertTrue(size > 0);
		planningRequestDaoImpl.remove(request.getId());
		list = planningRequestDaoImpl.getList();
		assertTrue(list.size() < size);
	}

}
