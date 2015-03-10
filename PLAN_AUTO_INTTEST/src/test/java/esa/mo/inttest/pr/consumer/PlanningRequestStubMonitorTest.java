package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlanningRequestStubMonitorTest extends PlanningRequestStubBaseTest {

	private static final Logger LOG = Logger.getLogger(PlanningRequestStubMonitorTest.class.getName());
	
	private void enter(String msg) {
		LOG.entering(getClass().getName(), msg);
	}
	
	private void leave(String msg) {
		LOG.exiting(getClass().getName(), msg);
	}

	@Before
	public void setUp() throws Exception {
		enter("setUp");
		super.setUp();
		leave("setUp");
	}

	@After
	public void tearDown() throws Exception {
		enter("tearDown");
		super.tearDown();
		leave("tearDown");
	}

	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequest");
		
		String subId = "subId1";
		PrMonitor prMon = registerPrMonitor(subId);
		
		createAndSubmitPlanningRequest();
		
		sleep(3000); // give broker a sec to respond
		
		// verify that we got pr notification
		assertNotNull(prMon.prStats);
		assertEquals(1, prMon.prStats.size());
		assertNotNull(prMon.prStats.get(0));
		
		deRegisterPrMonitor(subId);
		
		leave("testSubmitPlanningRequest");
	}

	@Test
	public void testSubmitPlanningRequestWithTask() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequestWithTask");
		
		String prSubId = "subId2";
		PrMonitor prMon = registerPrMonitor(prSubId);
		
		String taskSubId = "subId3";
		TaskMonitor taskMon = registerTaskMonitor(taskSubId);
		
		createAndSubmitPlanningRequestWithTask();
		
		sleep(3000); // give broker a sec to respond
		
		// verify that we got pr notification
		assertNotNull(prMon.prStats);
		assertEquals(1, prMon.prStats.size());
		assertNotNull(prMon.prStats.get(0));
		
		// verify that we got task notification
		assertNotNull(taskMon.taskStats);
		assertEquals(1, taskMon.taskStats.size());
		assertNotNull(taskMon.taskStats.get(0));
		
		deRegisterPrMonitor(taskSubId);
		deRegisterPrMonitor(prSubId);
		
		leave("testSubmitPlanningRequestWithTask");
	}

	@Test
	public void testUpdatePlanningRequest() throws MALException, MALInteractionException {
		enter("testUpdatePlanningRequest");
		
		String prSubId = "subId4";
		PrMonitor prMon = registerPrMonitor(prSubId);
		
		String taskSubId = "subId5";
		TaskMonitor taskMon = registerTaskMonitor(taskSubId);
		
		Object[] details = createAndSubmitPlanningRequest();
		
		sleep(2000);
		
		// reset notify helpers
		prMon.prStats = null;
		taskMon.taskStats = null;
		
		updatePlanningRequestWithTask(details);
		
		sleep(2000);
		
		// verify that we got pr notification
		assertNotNull(prMon.prStats);
		assertEquals(1, prMon.prStats.size());
		assertNotNull(prMon.prStats.get(0));
		
		// verify that we got task notification
		assertNotNull(taskMon.taskStats);
		assertEquals(1, taskMon.taskStats.size());
		assertNotNull(taskMon.taskStats.get(0));
		
		deRegisterTaskMonitor(taskSubId);
		
		deRegisterPrMonitor(prSubId);
		
		leave("testUpdatePlanningRequest");
	}

	@Test
	public void testRemovePlanningRequest() throws MALException, MALInteractionException {
		enter("testRemovePlanningRequest");
		
		String prSubId = "subId6";
		PrMonitor prMon = registerPrMonitor(prSubId);
		
		String taskSubId = "subId7";
		TaskMonitor taskMon = registerTaskMonitor(taskSubId);
		
		Long[] ids = createAndSubmitPlanningRequestWithTask();
		Long prInstId = ids[0];
		
		sleep(2000);
		
		// reset notify helpers
		prMon.prStats = null;
		taskMon.taskStats = null;
		
		removePlanningRequest(prInstId);
		
		sleep(2000);
		
		// verify that we got pr notification
		assertNotNull(prMon.prStats);
		assertEquals(1, prMon.prStats.size());
		assertNotNull(prMon.prStats.get(0));
		
		// verify that we got task notification
		assertNotNull(taskMon.taskStats);
		assertEquals(1, taskMon.taskStats.size());
		assertNotNull(taskMon.taskStats.get(0));
		
		deRegisterTaskMonitor(taskSubId);
		
		deRegisterPrMonitor(prSubId);

		leave("testRemovePlanningRequest");
	}

	@Test
	public void testSetTaskStatus() throws MALException, MALInteractionException {
		String taskSubId = "subId8";
		TaskMonitor taskMon = registerTaskMonitor(taskSubId);
		
		Long[] ids = createAndSubmitPlanningRequestWithTask();
//		Long prInstId = ids[0];
		Long taskInstId = ids[1];
		
		sleep(2000);
		
		LongList taskIds = new LongList();
		taskIds.add(taskInstId);
		
		TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
		taskMon.taskStats.get(0).setScheduled(new StatusRecord(new Time(System.currentTimeMillis()), "scheduled"));
		taskStats.add(taskMon.taskStats.get(0));
		
		// reset notify helpers
		taskMon.taskStats = null;
		
		prCons.setTaskStatus(taskIds, taskStats);
		
		sleep(2000);
		
		// verify that we got task notification
		assertNotNull(taskMon.taskStats);
		assertEquals(1, taskMon.taskStats.size());
		assertNotNull(taskMon.taskStats.get(0));
		
		deRegisterTaskMonitor(taskSubId);
	}

}
