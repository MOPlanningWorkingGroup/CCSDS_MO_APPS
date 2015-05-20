package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.Test;

public class PlanningRequestStubSimpleTest extends PlanningRequestStubTestBase {

	private static final Logger LOG = Logger.getLogger(PlanningRequestStubSimpleTest.class.getName());
	
	private void enter(String msg) {
		LOG.entering(getClass().getName(), msg);
	}
	
	private void leave(String msg) {
		LOG.exiting(getClass().getName(), msg);
	}

	@Test
	public void testMonitorPlanningRequestsRegister() throws MALException, MALInteractionException {
		enter("testMonitorPrReg");
		
		String subId = "subId";
		registerPrMonitor(subId);
		
		assertTrue(true);
		
		leave("testMonitorPrReg");
	}
	
	@Test
	public void testMonitorPlanningRequestsDeregister() throws MALException, MALInteractionException {
		enter("testMonitorPrDeReg");
		
		String subId = "subId2";
		registerPrMonitor(subId);
		
		deRegisterPrMonitor(subId);
		
		assertTrue(true);
		
		leave("testMonitorPrDeReg");
	}
	
	@Test
	public void testMonitorTasksRegister() throws MALException, MALInteractionException {
		enter("testMonitorTasksRegister");
		
		String subId = "subId";
		registerTaskMonitor(subId);
		
		assertTrue(true);
		
		leave("testMonitorTasksRegister");
	}
	
	@Test
	public void testMonitorTasksDeregister() throws MALException, MALInteractionException {
		enter("testMonitorTasksDeregister");
		
		String subId = "subId2";
		registerTaskMonitor(subId);
		
		deRegisterTaskMonitor(subId);
		
		assertTrue(true);
		
		leave("testMonitorTasksDeregister");
	}
	
	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequest");
		
		PlanningRequestInstanceDetails prInst = createAndSubmitPlanningRequest();
		
		verifyPrStat(prInst.getId());
		
		leave("testSubmitPlanningRequest");
	}

	@Test
	public void testSubmitPlanningRequestWithTask() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequestWithTask");
		
		PlanningRequestInstanceDetails prInst = createAndSubmitPlanningRequestWithTask();
		
		verifyPrStat(prInst.getId());
		
		LongList ids = new LongList();
		ids.add(prInst.getTasks().get(0).getId());
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(ids);
		
		assertNotNull(taskStats);
		assertEquals(1, taskStats.size());
		assertNotNull(taskStats.get(0));
		assertEquals(prInst.getTasks().get(0).getId(), taskStats.get(0).getTaskInstId());
		
		leave("testSubmitPlanningRequestWithTask");
	}

	@Test
	public void testUpdatePlanningRequest() throws MALException, MALInteractionException {
		enter("testUpdatePlanningRequest");
		
		PlanningRequestInstanceDetails prInst = createAndSubmitPlanningRequest();
		
		updatePlanningRequestWithTask(prInst);
		
		verifyPrStat(prInst.getId());
		
		leave("testUpdatePlanningRequest");
	}

	@Test
	public void testRemovePlanningRequest() throws MALException, MALInteractionException {
		enter("testRemovePlanningRequest");
		
		PlanningRequestInstanceDetails prInst = createAndSubmitPlanningRequestWithTask();
		
		removePlanningRequest(prInst.getId());
		
		leave("testRemovePlanningRequest");
	}

	@Test
	public void testGetPlanningRequestStatus() throws MALException, MALInteractionException {
		enter("testGetPlanningRequestStatus");
		
		LongList prInstIds = new LongList();
		prInstIds.add(1L);
		prInstIds.add(2L);
		prInstIds.add(3L);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prInstIds);
		
		assertNotNull(prStats);
		// get back same amount of statuses
		assertEquals(prInstIds.size(), prStats.size());
		// .. of "null"s
		assertNull(prStats.get(0));
		assertNull(prStats.get(1));
		assertNull(prStats.get(2));
		
		leave("testGetPlanningRequestStatus");
	}
	
	@Test
	public void testGetTaskStatus() throws MALException, MALInteractionException {
		enter("testGetTaskStatus");
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		taskInstIds.add(new Long(2L));
		taskInstIds.add(new Long(3L));
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskInstIds);
		
		assertNotNull(taskStats);
		// get back same amount statuses
		assertEquals(taskInstIds.size(), taskStats.size());
		// .. of "null"s
		assertNull(taskStats.get(0));
		assertNull(taskStats.get(1));
		assertNull(taskStats.get(2));
		
		leave("testGetTaskStatus");
	}
	
	@Test
	public void testListPrDefinition() throws MALException, MALInteractionException {
		enter("testListPrDefs");
		
		LongList ids = listPrDefs("*");
		// none submitted yet
		assertNotNull(ids);
		assertEquals(0, ids.size());
		
		leave("testListPrDefs");
	}
	
	@Test
	public void testAddPrDefinition() throws MALException, MALInteractionException {
		enter("testAddPrDef");
		
		PlanningRequestDefinitionDetails def = addPrDef();
		
		assertNotNull(def);
		assertNotNull(def.getId());
		assertFalse(0L == def.getId());
		// added pr id is listed
		LongList ids = listPrDefs("*");
		
		assertNotNull(ids);
		assertTrue(ids.contains(def.getId()));
		
		leave("testAddPrDef");
	}
	
	@Test
	public void testUpdatePrDefinition() throws MALException, MALInteractionException {
		enter("testUpdatePrDef");
		
		PlanningRequestDefinitionDetails def = addPrDef();
		
		def.setDescription("updated desc");
		
		LongList ids = new LongList();
		ids.add(def.getId());
		
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		
		prCons.updateDefinition(DefinitionType.PLANNING_REQUEST_DEF, ids, defs);
		// updated pr id is still listed, but cant verify description
		LongList defIds = listPrDefs("*");
		
		assertNotNull(defIds);
		assertTrue(defIds.contains(def.getId()));
		
		leave("testUpdatePrDef");
	}
	
	@Test
	public void testRemovePrDefinition() throws MALException, MALInteractionException {
		enter("testRemovePrDef");
		
		PlanningRequestDefinitionDetails def = addPrDef();
		
		LongList ids = new LongList();
		ids.add(def.getId());
		
		prCons.removeDefinition(DefinitionType.PLANNING_REQUEST_DEF, ids);
		// removed pr id is not listed anymore
		LongList defIds = listPrDefs("*");
		
		assertNotNull(defIds);
		assertFalse(defIds.contains(def.getId()));
		
		leave("testRemovePrDef");
	}
	
	@Test
	public void testListTaskDefinition() throws MALException, MALInteractionException {
		enter("testListTaskDefs");
		
		LongList taskDefIdList = listTaskDefs("*");
		// none submitted
		assertNotNull(taskDefIdList);
		assertEquals(0, taskDefIdList.size());
		
		leave("testListTaskDefs");
	}
	
	@Test
	public void testAddTaskDefinition() throws MALException, MALInteractionException {
		enter("testAddTaskDef");
		
		TaskDefinitionDetails taskDef = addTaskDef();
		
		assertNotNull(taskDef);
		assertNotNull(taskDef.getId());
		assertFalse(0L == taskDef.getId());
		
		LongList taskDefIds = listTaskDefs("*");
		
		assertNotNull(taskDefIds);
		// id from add() is list()ed
		assertTrue(taskDefIds.contains(taskDef.getId()));
		
		leave("testAddTaskDef");
	}
	
	@Test
	public void testUpdateTaskDefinition() throws MALException, MALInteractionException {
		enter("testUpdateTaskDef");
		
		TaskDefinitionDetails def = addTaskDef();
		
		def.setDescription("whoa");
		
		LongList ids = new LongList();
		ids.add(def.getId());
		
		TaskDefinitionDetailsList defs = new TaskDefinitionDetailsList();
		defs.add(def);
		
		prCons.updateDefinition(DefinitionType.TASK_DEF, ids, defs);
		
		LongList defIds = listTaskDefs("*");
		
		assertNotNull(defIds);
		// list() returns id, but unable to verify description
		// update()d id is still list()ed
		assertTrue(defIds.contains(def.getId()));
		
		leave("testUpdateTaskDef");
	}
	
	@Test
	public void testRemoveTaskDefinition() throws MALException, MALInteractionException {
		enter("testRemoveTaskDef");
		
		TaskDefinitionDetails def = addTaskDef();
		
		LongList ids = new LongList();
		ids.add(def.getId());
		
		prCons.removeDefinition(DefinitionType.TASK_DEF, ids);
		
		LongList defIds = listTaskDefs("*");
		
		assertNotNull(defIds);
		// remove()d id is not list()ed anymore
		assertFalse(defIds.contains(def.getId()));
		
		leave("testRemoveTaskDef");
	}
}
