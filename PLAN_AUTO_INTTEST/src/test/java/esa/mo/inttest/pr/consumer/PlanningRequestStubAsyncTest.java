package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.junit.Test;

public class PlanningRequestStubAsyncTest extends PlanningRequestStubTestBase {

	private static final Logger LOG = Logger.getLogger(PlanningRequestStubAsyncTest.class.getName());
	
	private void enter(String msg) {
		LOG.entering(getClass().getName(), msg);
	}
	
	private void leave(String msg) {
		LOG.exiting(getClass().getName(), msg);
	}

	protected Object[] asyncRegisterPrMonitor(String subId) throws MALException, MALInteractionException {
		Identifier id = new Identifier(subId);
		EntityRequestList entityList = new EntityRequestList();
		EntityKeyList keys = new EntityKeyList();
		keys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityList.add(new EntityRequest(null, true, true, true, false, keys));
		Subscription sub = new Subscription(id, entityList);
		PrMonitor prMon = new PrMonitor();
		MALMessage malMsg = prCons.asyncMonitorPlanningRequestsRegister(sub, prMon);
		return new Object[] { malMsg, prMon };
	}
	
	@Test
	public void testAsyncMonitorPlanningRequestsRegister() throws MALException, MALInteractionException {
		enter("testMonitorPrReg");
		
		String subId = "subId";
		Object[] details = asyncRegisterPrMonitor(subId);
		MALMessage malMsg = (MALMessage)details[0];
		PrMonitor prMon = (PrMonitor)details[1];
		
		sleep(1000); // give broker a second to fire callback
		
		assertTrue(prMon.registered);
		
		malMsg.free();
		
		leave("testMonitorPrReg");
	}
	
	protected MALMessage asyncDeRegisterPrMonitor(String subId, PrMonitor prMon) throws MALException, MALInteractionException {
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier(subId));
		return prCons.asyncMonitorPlanningRequestsDeregister(subIdList, prMon);
	}
	
	@Test
	public void testAsyncMonitorPlanningRequestsDeregister() throws MALException, MALInteractionException {
		enter("testMonitorPrDeReg");
		
		String subId = "subId2";
		Object[] details = asyncRegisterPrMonitor(subId);
		MALMessage malMsg = (MALMessage)details[0];
		PrMonitor prMon = (PrMonitor)details[1];
		
		sleep(1000); // wait a sec before de-registering
		
		MALMessage malMsg2 = asyncDeRegisterPrMonitor(subId, prMon);
		
		sleep(1000); // give broker a sec to fire callback
		
		assertTrue(prMon.deRegistered);
		
		malMsg2.free();
		malMsg.free();
		
		leave("testMonitorPrDeReg");
	}
	
	protected Object[] asyncRegisterTaskMonitor(String subId) throws MALException, MALInteractionException {
		Identifier id = new Identifier(subId);
		EntityRequestList entityList = new EntityRequestList();
		EntityKeyList keys = new EntityKeyList();
		keys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityList.add(new EntityRequest(null, true, true, true, false, keys));
		Subscription sub = new Subscription(id, entityList);
		TaskMonitor taskMon = new TaskMonitor();
		MALMessage malMsg = prCons.asyncMonitorTasksRegister(sub, taskMon);
		return new Object[] { malMsg, taskMon };
	}
	
	@Test
	public void testAsyncMonitorTasksRegister() throws MALException, MALInteractionException {
		enter("testMonitorTasksRegister");
		
		String subId = "subId";
		Object[] details = asyncRegisterTaskMonitor(subId);
		MALMessage malMsg = (MALMessage)details[0];
		TaskMonitor taskMon = (TaskMonitor)details[1];
		
		sleep(1000); // give broker a second to fire callback
		
		assertTrue(taskMon.registered);
		
		malMsg.free();
		
		leave("testMonitorTasksRegister");
	}
	
	protected MALMessage asyncDeRegisterTaskMonitor(String subId, TaskMonitor taskMon) throws MALException, MALInteractionException {
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier(subId));
		return prCons.asyncMonitorTasksDeregister(subIdList, taskMon);
	}
	
	@Test
	public void testAsyncMonitorTasksDeregister() throws MALException, MALInteractionException {
		enter("testMonitorTasksDeregister");
		
		String subId = "subId2";
		Object[] details = asyncRegisterTaskMonitor(subId);
		MALMessage malMsg = (MALMessage)details[0];
		TaskMonitor taskMon = (TaskMonitor)details[1];
		
		sleep(1000); // wait a sec before de-registering
		
		MALMessage malMsg2 = asyncDeRegisterTaskMonitor(subId, taskMon);
		
		sleep(1000); // give broker a sec to fire callback
		
		assertTrue(taskMon.deRegistered);
		
		malMsg2.free();
		malMsg.free();
		
		leave("testMonitorTasksDeregister");
	}
	
//	@Test
//	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
//		enter("testSubmitPlanningRequest");
//		
//		Object[] details = createAndSubmitPlanningRequest();
//		Long prInstId = (Long)details[1];
//		
//		LongList prIds = new LongList();
//		prIds.add(prInstId);
//		
//		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
//		
//		assertNotNull(prStats);
//		assertEquals(1, prStats.size());
//		assertNotNull(prStats.get(0));
//		
//		leave("testSubmitPlanningRequest");
//	}

//	@Test
//	public void testSubmitPlanningRequestWithTask() throws MALException, MALInteractionException {
//		enter("testSubmitPlanningRequestWithTask");
//		
//		Long[] ids = createAndSubmitPlanningRequestWithTask();
//		Long prInstId = ids[0];
//		Long taskId = ids[1];
//		
//		LongList prIds = new LongList();
//		prIds.add(prInstId);
//		
//		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
//		
//		assertNotNull(prStats);
//		assertEquals(1, prStats.size());
//		assertNotNull(prStats.get(0));
//		
//		LongList taskIds = new LongList();
//		taskIds.add(taskId);
//		
//		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskIds);
//		
//		assertNotNull(taskStats);
//		assertEquals(1, taskStats.size());
//		assertNotNull(taskStats.get(0));
//		
//		leave("testSubmitPlanningRequestWithTask");
//	}

//	@Test
//	public void testUpdatePlanningRequest() throws MALException, MALInteractionException {
//		enter("testUpdatePlanningRequest");
//		
//		Object[] details = createAndSubmitPlanningRequest();
//		
//		updatePlanningRequestWithTask(details);
//		
//		leave("testUpdatePlanningRequest");
//	}

//	@Test
//	public void testRemovePlanningRequest() throws MALException, MALInteractionException {
//		enter("testRemovePlanningRequest");
//		
//		Long[] ids = createAndSubmitPlanningRequestWithTask();
//		Long prInstId = ids[0];
//		
//		removePlanningRequest(prInstId);
//		
//		leave("testRemovePlanningRequest");
//	}

//	@Test
//	public void testGetPlanningRequestStatus() throws MALException, MALInteractionException {
//		enter("testGetPlanningRequestStatus");
//		
//		// ask for random pr status
//		PlanningRequestStatusDetailsList prStats = getPlanningRequestStatus(1L);
//		// should get nothing
//		assertNotNull(prStats);
//		assertEquals(0, prStats.size());
//		
//		leave("testGetPlanningRequestStatus");
//	}
	
//	@Test
//	public void testGetTaskStatus() throws MALException, MALInteractionException {
//		enter("testGetTaskStatus");
//		
//		LongList taskInstIds = new LongList();
//		taskInstIds.add(new Long(1L));
//		
//		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskInstIds);
//		
//		assertNotNull(taskStats);
//		assertEquals(0, taskStats.size());
//		
//		leave("testGetTaskStatus");
//	}
	
//	@Test
//	public void testSetTaskStatus() throws MALException, MALInteractionException {
//		enter("testSetTaskStatus");
//		
//		LongList taskInstIds = new LongList();
//		taskInstIds.add(new Long(1L));
//		
//		TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
//		TaskStatusDetails taskStat = new TaskStatusDetails();
//		taskStat.setTaskInstName(new Identifier("id")); // mandatory
//		taskStats.add(taskStat);
//		
//		prCons.setTaskStatus(taskInstIds, taskStats);
//		
//		leave("testSetTaskStatus");
//	}
	
//	@Test
//	public void testListDefinition() throws MALException, MALInteractionException {
//		enter("testListPrDefs");
//		LongList ids = listPrDefs("*");
//		
//		assertNotNull(ids);
//		assertEquals(0, ids.size());
//		leave("testListPrDefs");
//	}
	
//	@Test
//	public void testAddDefinition() throws MALException, MALInteractionException {
//		enter("testAddPrDef");
//		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
//		
//		assertNotNull(e.getKey());
//		assertEquals(1, e.getKey().size());
//		assertNotNull(e.getKey().get(0));
//		// added pr id is listed
//		LongList ids = listPrDefs("*");
//		
//		assertTrue(ids.contains(e.getKey().get(0)));
//		leave("testAddPrDef");
//	}
	
//	@Test
//	public void testUpdateDefinition() throws MALException, MALInteractionException {
//		enter("testUpdatePrDef");
//		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
//		
//		e.getValue().get(0).setDescription("updated desc");
//		
//		prCons.updateDefinition(e.getKey(), e.getValue());
//		// updated pr id is still listed, but cant verify description
//		LongList ids = listPrDefs("*");
//		
//		assertTrue(ids.contains(e.getKey().get(0)));
//		leave("testUpdatePrDef");
//	}
	
//	@Test
//	public void testRemoveDefinition() throws MALException, MALInteractionException {
//		enter("testRemovePrDef");
//		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
//		
//		prCons.removeDefinition(e.getKey());
//		// removed pr id is not listed anymore
//		LongList ids = listPrDefs("*");
//		
//		assertFalse(ids.contains(e.getKey().get(0)));
//		leave("testRemovePrDef");
//	}
	
	@Test
	public void testListTaskDefinition() throws MALException, MALInteractionException {
		enter("testListTaskDefs");
		
		IdentifierList taskNames = new IdentifierList();
		taskNames.add(new Identifier("*"));
		
		final LongList[] taskDefIds = { null };
		LOG.log(Level.INFO, "sending list task defs request");
		MALMessage malMsg = prCons.asyncListDefinition(DefinitionType.TASK_DEF, taskNames, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void listDefinitionResponseReceived(MALMessageHeader msgHeader, LongList taskDefInstIds,
					Map qosProperties) {
				LOG.log(Level.INFO, "list task defs resp={0}", taskDefInstIds);
				taskDefIds[0] = taskDefInstIds;
			}

			@SuppressWarnings("rawtypes")
			public void listDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "list task defs err={0}", error);
			}
		});
		
		sleep(1000);
		
		assertNotNull(taskDefIds[0]);
		assertEquals(0, taskDefIds[0].size());
		
		malMsg.free();
		
		leave("testListTaskDefs");
	}
	
	@Test
	public void testAddTaskDefinition() throws MALException, MALInteractionException {
		enter("testAddTaskDef");
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		TaskDefinitionDetails taskDef = createTaskDef("async task def", "async pr def");
		taskDefs.add(taskDef);
		
		final LongList[] taskDefIds = { null };
		LOG.log(Level.INFO, "sending add task def request");
		MALMessage malMsg = prCons.asyncAddDefinition(DefinitionType.TASK_DEF, taskDefs, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void addDefinitionResponseReceived(MALMessageHeader msgHeader, LongList taskDefInstIds,
					Map qosProperties) {
				LOG.log(Level.INFO, "add task def resp={0}", taskDefInstIds);
				taskDefIds[0] = taskDefInstIds;
			}

			@SuppressWarnings("rawtypes")
			public void addDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "add task def err={0}", error);
			}
		});
		
		sleep(1000);
		
		assertNotNull(taskDefIds[0]);
		assertEquals(1, taskDefIds[0].size());
		assertNotNull(taskDefIds[0].get(0));
		
		LongList taskDefIdList = listTaskDefs("*");
		
		assertNotNull(taskDefIdList);
		assertEquals(1, taskDefIdList.size());
		assertNotNull(taskDefIdList.get(0));
		// id from add() matches id from list()
		assertEquals(taskDefIds[0].get(0), taskDefIdList.get(0));
		
		malMsg.free();
		
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
		
		final Boolean[] updated = { false };
		LOG.log(Level.INFO, "sending update task def request");
		MALMessage malMsg = prCons.asyncUpdateDefinition(DefinitionType.TASK_DEF, taskDefIds, taskDefs, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updateDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
				LOG.log(Level.INFO, "update task def resp");
				updated[0] = true;
			}

			@SuppressWarnings("rawtypes")
			public void updateDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "update task def err={0}", error);
			}
		});
		
		sleep(1000);
		
		assertTrue(updated[0]);
		// list() returns id - unable to verify description
		LongList taskDefIdList = listTaskDefs("*");
		// added id is still listed
		assertTrue(taskDefIdList.contains(taskDefId));
		
		malMsg.free();
		
		leave("testUpdateTaskDef");
	}
	
	@Test
	public void testRemoveTaskDefinition() throws MALException, MALInteractionException {
		enter("testRemoveTaskDef");
		
		Object[] details = addTaskDef();
		Long taskDefId = (Long)details[0];
		
		LongList taskDefIds = new LongList();
		taskDefIds.add(taskDefId);
		
		final Boolean[] removed = { false };
		LOG.log(Level.INFO, "sending remove task def request");
		prCons.asyncRemoveDefinition(DefinitionType.TASK_DEF, taskDefIds, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void removeDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "remove task def resp");
				removed[0] = true;
			}

			@SuppressWarnings("rawtypes")
			public void removeDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "remove task def err={0}", error);
			}
		});
		
		sleep(1000);
		
		assertTrue(removed[0]);
		
		LongList taskDefIdList = listTaskDefs("*");
		// added id is not listed anymore
		assertFalse(taskDefIdList.contains(taskDefId));
		
		leave("testRemoveTaskDef");
	}
}
