package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PlanningRequestStubSimpleTest extends PlanningRequestStubBaseTest {

	private static final Logger LOG = Logger.getLogger(PlanningRequestStubSimpleTest.class.getName());
	
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
	
	@Ignore("register pr monitor ack response never arrives")
	@Test
	public void testMonitorPlanningRequestsRegister() throws MALException, MALInteractionException {
		enter("testMonitorPrReg");
		String subId = "subId";
		PrMonitor prReg = registerPrMonitor(subId);
		
		sleep(1000); // give broker a second to fire callback
		
		assertTrue(prReg.registered);
		leave("testMonitorPrReg");
	}
	
	@Ignore("de-register pr monitor ack response never arrives")
	@Test
	public void testMonitorPlanningRequestsDeregister() throws MALException, MALInteractionException {
		enter("testMonitorPrDeReg");
		String subId = "subId2";
		PrMonitor prMon = registerPrMonitor(subId);
		
		sleep(1000); // wait a sec before de-registering
		
		deRegisterPrMonitor(subId);
		
		sleep(1000); // give broker a sec to fire callback
		
		assertTrue(prMon.deRegistered);
		leave("testMonitorPrDeReg");
	}
	
	@Ignore("register task monitor ack response never arrives")
	@Test
	public void testMonitorTasksRegister() throws MALException, MALInteractionException {
		enter("testMonitorTasksRegister");
		String subId = "subId";
		TaskMonitor taskMon = registerTaskMonitor(subId);
		
		sleep(1000); // give broker a second to fire callback
		
		assertTrue(taskMon.registered);
		leave("testMonitorTasksRegister");
	}
	
	@Ignore("de-register task monitor ack response never arrives")
	@Test
	public void testMonitorTasksDeregister() throws MALException, MALInteractionException {
		enter("testMonitorTasksDeregister");
		String subId = "subId2";
		TaskMonitor taskMon = registerTaskMonitor(subId);
		
		sleep(1000); // wait a sec before de-registering
		
		deRegisterTaskMonitor(subId);
		
		sleep(1000); // give broker a sec to fire callback
		
		assertTrue(taskMon.deRegistered);
		leave("testMonitorTasksDeregister");
	}
	
	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequest");
		
		Object[] details = createAndSubmitPlanningRequest();
		Long prInstId = (Long)details[2];
//		PlanningRequestInstanceDetails prInst = (PlanningRequestInstanceDetails)details[3];
		
		LongList prIds = new LongList();
		prIds.add(prInstId);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNotNull(prStats.get(0));
		
		leave("testSubmitPlanningRequest");
	}

	@Test
	public void testSubmitPlanningRequestWithTask() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequestWithTask");
		
		Long[] ids = createAndSubmitPlanningRequestWithTask();
		Long prInstId = ids[0];
		Long taskId = ids[1];
		
		LongList prIds = new LongList();
		prIds.add(prInstId);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNotNull(prStats.get(0));
		
		LongList taskIds = new LongList();
		taskIds.add(taskId);
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskIds);
		
		assertNotNull(taskStats);
		assertEquals(1, taskStats.size());
		assertNotNull(taskStats.get(0));
		
		leave("testSubmitPlanningRequestWithTask");
	}

	@Test
	public void testUpdatePlanningRequest() throws MALException, MALInteractionException {
		enter("testUpdatePlanningRequest");
		
		Object[] details = createAndSubmitPlanningRequest();
		
		updatePlanningRequestWithTask(details);
		
		leave("testUpdatePlanningRequest");
	}

	@Test
	public void testRemovePlanningRequest() throws MALException, MALInteractionException {
		enter("testRemovePlanningRequest");
		
		Long[] ids = createAndSubmitPlanningRequestWithTask();
		Long prInstId = ids[0];
		
		removePlanningRequest(prInstId);
		
		leave("testRemovePlanningRequest");
	}

	@Test
	public void testGetPlanningRequestStatus() throws MALException, MALInteractionException {
		enter("testGetPlanningRequestStatus");
		
		// ask for random pr status
		PlanningRequestStatusDetailsList prStats = getPlanningRequestStatus(1L);
		// should get nothing
		assertNotNull(prStats);
		assertEquals(0, prStats.size());
		
		leave("testGetPlanningRequestStatus");
	}
	
	@Test
	public void testGetTaskStatus() throws MALException, MALInteractionException {
		enter("testGetTaskStatus");
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskInstIds);
		
		assertNotNull(taskStats);
		assertEquals(0, taskStats.size());
		
		leave("testGetTaskStatus");
	}
	
	@Test
	public void testSetTaskStatus() throws MALException, MALInteractionException {
		enter("testSetTaskStatus");
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		
		TaskStatusDetails taskStat = new TaskStatusDetails();
		taskStat.setTaskInstName(new Identifier("id")); // mandatory
		
		TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
		taskStats.add(taskStat);
		
		prCons.setTaskStatus(taskInstIds, taskStats);
		
		leave("testSetTaskStatus");
	}
	
	@Test
	public void testListDefinition() throws MALException, MALInteractionException {
		enter("testListPrDefs");
		LongList ids = listPrDefs("*");
		
		assertNotNull(ids);
		assertEquals(0, ids.size());
		leave("testListPrDefs");
	}
	
	@Test
	public void testAddDefinition() throws MALException, MALInteractionException {
		enter("testAddPrDef");
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		
		assertNotNull(e.getKey());
		assertEquals(1, e.getKey().size());
		assertNotNull(e.getKey().get(0));
		// added pr id is listed
		LongList ids = listPrDefs("*");
		
		assertTrue(ids.contains(e.getKey().get(0)));
		leave("testAddPrDef");
	}
	
	@Test
	public void testUpdateDefinition() throws MALException, MALInteractionException {
		enter("testUpdatePrDef");
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		
		e.getValue().get(0).setDescription("updated desc");
		
		prCons.updateDefinition(e.getKey(), e.getValue());
		// updated pr id is still listed, but cant verify description
		LongList ids = listPrDefs("*");
		
		assertTrue(ids.contains(e.getKey().get(0)));
		leave("testUpdatePrDef");
	}
	
	@Test
	public void testRemoveDefinition() throws MALException, MALInteractionException {
		enter("testRemovePrDef");
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		
		prCons.removeDefinition(e.getKey());
		// removed pr id is not listed anymore
		LongList ids = listPrDefs("*");
		
		assertFalse(ids.contains(e.getKey().get(0)));
		leave("testRemovePrDef");
	}
	
	@Test
	public void testListTaskDefinition() throws MALException, MALInteractionException {
		enter("testListTaskDefs");
		
		LongList taskDefIdList = listTaskDefs("*");
		
		assertNotNull(taskDefIdList);
		assertEquals(0, taskDefIdList.size());
		
		leave("testListTaskDefs");
	}
	
	@Test
	public void testAddTaskDefinition() throws MALException, MALInteractionException {
		enter("testAddTaskDef");
		
		Object[] details = addTaskDef();
		Long taskDefId = (Long)details[0];
		TaskDefinitionDetails taskDef = (TaskDefinitionDetails)details[1];
		
		assertNotNull(taskDefId);
		assertNotNull(taskDef);
		
		LongList taskDefIdList = listTaskDefs("*");
		
		assertNotNull(taskDefIdList);
		assertEquals(1, taskDefIdList.size());
		assertNotNull(taskDefIdList.get(0));
		// id from add() matches id from list()
		assertEquals(taskDefId, taskDefIdList.get(0));
		
		leave("testAddTaskDef");
	}
	
	@Test
	public void testUpdateTaskDefinition() throws MALException, MALInteractionException {
		enter("testUpdateTaskDef");
		
		Object[] details = addTaskDef();
		Long taskDefId = (Long)details[0];
		TaskDefinitionDetails taskDef = (TaskDefinitionDetails)details[1];
		
		taskDef.setDescription("whoa");
		
		LongList taskDefIds = new LongList();
		taskDefIds.add(taskDefId);
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		
		prCons.updateTaskDefinition(taskDefIds, taskDefs);
		
		// list() returns id - unable to verify description
		LongList taskDefIdList = listTaskDefs("*");
		// added id is still listed
		assertTrue(taskDefIdList.contains(taskDefId));
		
		leave("testUpdateTaskDef");
	}
	
	@Test
	public void testRemoveTaskDefinition() throws MALException, MALInteractionException {
		enter("testRemoveTaskDef");
		
		Object[] details = addTaskDef();
		Long taskDefId = (Long)details[0];
		
		LongList taskDefIds = new LongList();
		taskDefIds.add(taskDefId);
		
		prCons.removeTaskDefinition(taskDefIds);
		
		LongList taskDefIdList = listTaskDefs("*");
		// added id is not listed anymore
		assertFalse(taskDefIdList.contains(taskDefId));
		
		leave("testRemoveTaskDef");
	}
}
