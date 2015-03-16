package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
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
		
		sleep(1000); // give broker a second to fire callback
		
		assertTrue(true);
		
		leave("testMonitorPrReg");
	}
	
	@Test
	public void testMonitorPlanningRequestsDeregister() throws MALException, MALInteractionException {
		enter("testMonitorPrDeReg");
		
		String subId = "subId2";
		registerPrMonitor(subId);
		
		sleep(1000); // wait a sec before de-registering
		
		deRegisterPrMonitor(subId);
		
		sleep(1000); // give broker a sec to fire callback
		
		assertTrue(true);
		
		leave("testMonitorPrDeReg");
	}
	
	@Test
	public void testMonitorTasksRegister() throws MALException, MALInteractionException {
		enter("testMonitorTasksRegister");
		
		String subId = "subId";
		registerTaskMonitor(subId);
		
		sleep(1000); // give broker a second to fire callback
		
		assertTrue(true);
		
		leave("testMonitorTasksRegister");
	}
	
	@Test
	public void testMonitorTasksDeregister() throws MALException, MALInteractionException {
		enter("testMonitorTasksDeregister");
		
		String subId = "subId2";
		registerTaskMonitor(subId);
		
		sleep(1000); // wait a sec before de-registering
		
		deRegisterTaskMonitor(subId);
		
		sleep(1000); // give broker a sec to fire callback
		
		assertTrue(true);
		
		leave("testMonitorTasksDeregister");
	}
	
	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequest");
		
		Object[] details = createAndSubmitPlanningRequest();
		Long prInstId = (Long)details[2];
		
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
	public void testSetTaskStatus() throws MALException, MALInteractionException {
		enter("testSetTaskStatus");
		
		Long[] ids = createAndSubmitPlanningRequestWithTask();
		Long taskInstId = ids[1];
		
		LongList taskIds = new LongList();
		taskIds.add(new Long(taskInstId));
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskIds);
		// assuming there is one task status
		TaskStatusDetails taskStat = taskStats.get(0);
		StatusRecordList stats = taskStat.getStatus();
		if (null == stats) {
			stats = new StatusRecordList();
		}
		StatusRecord rec = null;
		for (StatusRecord sr: stats) {
			if (sr.getState() == InstanceState.INVALID) {
				rec = sr;
			}
		}
		if (null == rec) {
			rec = new StatusRecord(InstanceState.INVALID, new Time(System.currentTimeMillis()), "invalid");
			stats.add(rec);
		} else {
			rec.setDate(new Time(System.currentTimeMillis()));
			rec.setComment("invalid");
		}
		
		prCons.setTaskStatus(taskIds, taskStats);
		
		leave("testSetTaskStatus");
	}
	
	@Test
	public void testListDefinition() throws MALException, MALInteractionException {
		enter("testListPrDefs");
		
		LongList ids = listPrDefs("*");
		// non submitted yet
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
		
		prCons.updateDefinition(DefinitionType.PLANNING_REQUEST_DEF, e.getKey(), e.getValue());
		// updated pr id is still listed, but cant verify description
		LongList ids = listPrDefs("*");
		
		assertTrue(ids.contains(e.getKey().get(0)));
		
		leave("testUpdatePrDef");
	}
	
	@Test
	public void testRemoveDefinition() throws MALException, MALInteractionException {
		enter("testRemovePrDef");
		
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		
		prCons.removeDefinition(DefinitionType.PLANNING_REQUEST_DEF, e.getKey());
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
		LongList taskDefIds = (LongList)details[0];
		TaskDefinitionDetailsList taskDefs = (TaskDefinitionDetailsList)details[1];
		
		assertNotNull(taskDefIds);
		assertEquals(1, taskDefIds.size());
		assertNotNull(taskDefIds.get(0));
		
		assertNotNull(taskDefs);
		assertEquals(1, taskDefs.size());
		assertNotNull(taskDefs.get(0));
		
		LongList taskDefIds2 = listTaskDefs("*");
		
		assertNotNull(taskDefIds2);
		// id from add() is list()ed
		assertTrue(taskDefIds2.contains(taskDefIds.get(0)));
		
		leave("testAddTaskDef");
	}
	
	@Test
	public void testUpdateTaskDefinition() throws MALException, MALInteractionException {
		enter("testUpdateTaskDef");
		
		Object[] details = addTaskDef();
		LongList taskDefIds = (LongList)details[0];
		TaskDefinitionDetailsList taskDefs = (TaskDefinitionDetailsList)details[1];
		
		taskDefs.get(0).setDescription("whoa");
		
		prCons.updateDefinition(DefinitionType.TASK_DEF, taskDefIds, taskDefs);
		
		LongList taskDefIds2 = listTaskDefs("*");
		
		assertNotNull(taskDefIds2);
		// list() returns id, but unable to verify description
		// update()d id is still list()ed
		assertTrue(taskDefIds2.contains(taskDefIds.get(0)));
		
		leave("testUpdateTaskDef");
	}
	
	@Test
	public void testRemoveTaskDefinition() throws MALException, MALInteractionException {
		enter("testRemoveTaskDef");
		
		Object[] details = addTaskDef();
		LongList taskDefIds = (LongList)details[0];
		
		prCons.removeDefinition(DefinitionType.TASK_DEF, taskDefIds);
		
		LongList taskDefIds2 = listTaskDefs("*");
		
		assertNotNull(taskDefIds2);
		// remove()d id is not list()ed anymore
		assertFalse(taskDefIds2.contains(taskDefIds.get(0)));
		
		leave("testRemoveTaskDef");
	}
}
