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
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
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
	public void testMonitorPlanningRequestsRegister() throws MALException, MALInteractionException {
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
	public void testMonitorPlanningRequestsDeregister() throws MALException, MALInteractionException {
		enter("testMonitorPrDeReg");
		
		String subId = "subId2";
		Object[] details = asyncRegisterPrMonitor(subId);
		MALMessage malMsg = (MALMessage)details[0];
		PrMonitor prMon = (PrMonitor)details[1];
		
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
	public void testMonitorTasksRegister() throws MALException, MALInteractionException {
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
	public void testMonitorTasksDeregister() throws MALException, MALInteractionException {
		enter("testMonitorTasksDeregister");
		
		String subId = "subId2";
		Object[] details = asyncRegisterTaskMonitor(subId);
		MALMessage malMsg = (MALMessage)details[0];
		TaskMonitor taskMon = (TaskMonitor)details[1];
		
		MALMessage malMsg2 = asyncDeRegisterTaskMonitor(subId, taskMon);
		
		sleep(1000); // give broker a sec to fire callback
		
		assertTrue(taskMon.deRegistered);
		
		malMsg2.free();
		malMsg.free();
		
		leave("testMonitorTasksDeregister");
	}
	
	protected MALMessage asyncSubmitPr(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst,
			LongList taskDefIds, LongList taskInstIds, final PlanningRequestResponseInstanceDetailsList[] resp)
					throws MALException, MALInteractionException {
		
		MALMessage malMsg = prCons.asyncSubmitPlanningRequest(prDefId, prInstId, prInst, taskDefIds, taskInstIds,
				new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void submitPlanningRequestResponseReceived(MALMessageHeader msgHeader,
					PlanningRequestResponseInstanceDetailsList resp2, Map qosProperties) {
				LOG.log(Level.INFO, "submit pr resp={0}", resp2);
				resp[0] = resp2;
			}
			
			@SuppressWarnings("rawtypes")
			public void submitPlanningRequestErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "submit pr error={0}", error);
			}
		});
		return malMsg;
	}
	
	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequest");
		
		PlanningRequestDefinitionDetails prDef = createPrDef("async pr def");
		Long prDefId = submitPrDef(prDef);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, null);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		final PlanningRequestResponseInstanceDetailsList[] response = { null };
		
		MALMessage malMsg = asyncSubmitPr(prDefId, prInstId, prInst, null, null, response);
		
		sleep(1000);
		
		assertNotNull(response[0]);
		assertEquals(1, response[0].size());
		assertNotNull(response[0].get(0));
		
		LongList prIds = new LongList();
		prIds.add(prInstId);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNotNull(prStats.get(0));
		
		malMsg.free();
		
		leave("testSubmitPlanningRequest");
	}

	@Test
	public void testSubmitPlanningRequestWithTask() throws MALException, MALInteractionException {
		enter("testSubmitPlanningRequestWithTask");
		
		TaskDefinitionDetails taskDef = createTaskDef("async task def", "async pr def");
		Long taskDefId = submitTaskDef(taskDef);
		
		TaskInstanceDetails taskInst = createTaskInst(taskDef);
		Long taskInstId = generateId();
		
		storeTaskInst(taskDefId, taskInstId, taskInst);
		
		PlanningRequestDefinitionDetails prDef = createPrDef("async pr def");
		Long prDefId = submitPrDef(prDef);
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, taskInsts);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		LongList taskDefIds = new LongList();
		taskDefIds.add(taskDefId);
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(taskInstId);
		
		final PlanningRequestResponseInstanceDetailsList[] response = { null };
		
		MALMessage malMsg = asyncSubmitPr(prDefId, prInstId, prInst, taskDefIds, taskInstIds, response);
		
		sleep(1000);
		
		assertNotNull(response[0]);
		assertEquals(1, response[0].size());
		assertNotNull(response[0].get(0));
		
		LongList prIds = new LongList();
		prIds.add(prInstId);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNotNull(prStats.get(0));
		
		LongList taskIds = new LongList();
		taskIds.add(taskInstId);
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskIds);
		
		assertNotNull(taskStats);
		assertEquals(1, taskStats.size());
		assertNotNull(taskStats.get(0));
		
		malMsg.free();
		
		leave("testSubmitPlanningRequestWithTask");
	}

	@Test
	public void testUpdatePlanningRequest() throws MALException, MALInteractionException {
		enter("testUpdatePlanningRequest");
		
		Object[] details = createAndSubmitPlanningRequestWithTask();
		Long prDefId = (Long)details[0];
		Long prInstId = (Long)details[1];
		PlanningRequestInstanceDetails prInst = (PlanningRequestInstanceDetails)details[2];
		LongList taskDefIds = (LongList)details[3];
		LongList taskInstIds = (LongList)details[4];
		
		prInst.setDescription("async updated");
		
		final boolean[] updated = { false };
		
		MALMessage malMsg = prCons.asyncUpdatePlanningRequest(prDefId, prInstId, prInst, taskDefIds, taskInstIds, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updatePlanningRequestAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "update pr ack");
				updated[0] = true;
			}
			
			@SuppressWarnings("rawtypes")
			public void updatePlanningRequestErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "update pr error={0}", error);
			}
		});
		
		sleep(1000);
		
		assertTrue(updated[0]);
		
		LongList prIds = new LongList();
		prIds.add(prInstId);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNotNull(prStats.get(0));
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskInstIds);
		
		assertNotNull(taskStats);
		assertEquals(1, taskStats.size());
		assertNotNull(taskStats.get(0));
		
		malMsg.free();
		
		leave("testUpdatePlanningRequest");
	}

	@Test
	public void testRemovePlanningRequest() throws MALException, MALInteractionException {
		enter("testRemovePlanningRequest");
		
		Object[] details = createAndSubmitPlanningRequestWithTask();
		Long prInstId = (Long)details[1];
		LongList taskInstIds = (LongList)details[4];
		
		final boolean[] removed = { false };
		
		MALMessage malMsg = prCons.asyncRemovePlanningRequest(prInstId, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void removePlanningRequestAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "remove pr ack");
				removed[0] = true;
			}

			@SuppressWarnings("rawtypes")
			public void removePlanningRequestErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "remove pr error={0}", error);
			}
		});
		
		sleep(1000);
		
		assertTrue(removed[0]);
		
		LongList prIds = new LongList();
		prIds.add(prInstId);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNull(prStats.get(0));
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskInstIds);
		
		assertNotNull(taskStats);
		assertEquals(1, taskStats.size());
		assertNull(taskStats.get(0));
		
		malMsg.free();
		
		leave("testRemovePlanningRequest");
	}

	@Test
	public void testGetPlanningRequestStatus() throws MALException, MALInteractionException {
		enter("testGetPlanningRequestStatus");
		
		LongList prInstIds = new LongList();
		prInstIds.add(1L);
		
		final PlanningRequestStatusDetailsList[] stats = { null };
		
		MALMessage malMsg = prCons.asyncGetPlanningRequestStatus(prInstIds, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void getPlanningRequestStatusResponseReceived(MALMessageHeader msgHeader,
					PlanningRequestStatusDetailsList prStats, Map qosProps) {
				LOG.log(Level.INFO, "get pr status resp={0}", prStats);
				stats[0] = prStats;
			}
			
			@SuppressWarnings("rawtypes")
			public void getPlanningRequestStatusErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "get pr status error={0}", error);
			}
		});
		
		sleep(1000);
		// should get null
		assertNotNull(stats[0]);
		assertEquals(1, stats[0].size());
		assertNull(stats[0].get(0));
		
		malMsg.free();
		
		leave("testGetPlanningRequestStatus");
	}
	
	@Test
	public void testGetTaskStatus() throws MALException, MALInteractionException {
		enter("testGetTaskStatus");
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		
		final TaskStatusDetailsList[] stats = { null };
		
		MALMessage malMsg = prCons.asyncGetTaskStatus(taskInstIds, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void getTaskStatusResponseReceived(MALMessageHeader msgHeader, TaskStatusDetailsList taskStats,
					Map qosProps) {
				LOG.log(Level.INFO, "get task status resp={0}", taskStats);
				stats[0] = taskStats;
			}
			
			@SuppressWarnings("rawtypes")
			public void getTaskStatusErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				LOG.log(Level.INFO, "get task status error={0}", error);
			}
		});
		
		sleep(1000);
		
		assertNotNull(stats[0]);
		assertEquals(1, stats[0].size());
		assertNull(stats[0].get(0));
		
		malMsg.free();
		
		leave("testGetTaskStatus");
	}
	
	@Test
	public void testListPrDefinition() throws MALException, MALInteractionException {
		enter("testListPrDefs");
		
		IdentifierList names = new IdentifierList();
		names.add(new Identifier("*"));
		
		final LongList[] ids =  { null };
		
		MALMessage malMsg = prCons.asyncListDefinition(DefinitionType.PLANNING_REQUEST_DEF, names, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void listDefinitionResponseReceived(MALMessageHeader msgHeader, LongList prDefIds,
					Map qosProps) {
				LOG.log(Level.INFO, "list pr defs resp={0}", prDefIds);
				ids[0] = prDefIds;
			}
			
			@SuppressWarnings("rawtypes")
			public void listDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "list pr defs error={0}", error);
			}
		});
		
		sleep(1000);
		
		assertNotNull(ids[0]);
		assertEquals(0, ids[0].size());
		
		malMsg.free();
		
		leave("testListPrDefs");
	}
	
	@Test
	public void testAddPrDefinition() throws MALException, MALInteractionException {
		enter("testAddPrDef");
		
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(createPrDef("async pr def"));
		
		final LongList[] ids = { null };
		
		MALMessage malMsg = prCons.asyncAddDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void addDefinitionResponseReceived(MALMessageHeader msgHeader, LongList prDefIds,
					Map qosProps) {
				LOG.log(Level.INFO, "add pr defs resp={0}", prDefIds);
				ids[0] = prDefIds;
			}
			
			@SuppressWarnings("rawtypes")
			public void addDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "add pr defs err={0}", error);
			}
		});
		
		sleep(1000);
		
		assertNotNull(ids[0]);
		assertEquals(1, ids[0].size());
		assertNotNull(ids[0].get(0));
		// added pr id is listed
		LongList ids2 = listPrDefs("*");
		
		assertTrue(ids2.contains(ids[0].get(0)));
		
		malMsg.free();
		
		leave("testAddPrDef");
	}
	
	@Test
	public void testUpdatePrDefinition() throws MALException, MALInteractionException {
		enter("testUpdatePrDef");
		
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		
		e.getValue().get(0).setDescription("updated desc");
		
		final boolean[] updated = { false };
		
		MALMessage malMsg = prCons.asyncUpdateDefinition(DefinitionType.PLANNING_REQUEST_DEF, e.getKey(), e.getValue(), new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updateDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "update pr def ack");
				updated[0] = true;
			}
			
			@SuppressWarnings("rawtypes")
			public void updateDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "update pr def error={0}", error);
			}
		});
		
		sleep(1000);
		
		assertTrue(updated[0]);
		
		// updated pr id is still listed, but cant verify description
		LongList ids = listPrDefs("*");
		
		assertTrue(ids.contains(e.getKey().get(0)));
		
		malMsg.free();
		
		leave("testUpdatePrDef");
	}
	
	@Test
	public void testRemovePrDefinition() throws MALException, MALInteractionException {
		enter("testRemovePrDef");
		
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		
		final boolean[] removed = { false };
		
		MALMessage malMsg = prCons.asyncRemoveDefinition(DefinitionType.PLANNING_REQUEST_DEF, e.getKey(), new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			public void removeDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
				LOG.log(Level.INFO, "remove pr def ack");
				removed[0] = true;
			}
			
			@SuppressWarnings("rawtypes")
			public void removeDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "remove pr def error={0}", error);
			}
		});
		
		sleep(1000);
		
		assertTrue(removed[0]);
		
		// removed pr id is not listed anymore
		LongList ids = listPrDefs("*");
		
		assertFalse(ids.contains(e.getKey().get(0)));
		
		malMsg.free();
		
		leave("testRemovePrDef");
	}
	
	@Test
	public void testListTaskDefinition() throws MALException, MALInteractionException {
		enter("testListTaskDefs");
		
		IdentifierList taskNames = new IdentifierList();
		taskNames.add(new Identifier("*"));
		
		final LongList[] taskDefIds = { null };
		LOG.log(Level.INFO, "sending list task defs request");
		MALMessage malMsg = prCons.asyncListDefinition(DefinitionType.TASK_DEF, taskNames, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void listDefinitionResponseReceived(MALMessageHeader msgHeader, LongList taskDefIds1,
					Map qosProperties) {
				LOG.log(Level.INFO, "list task defs resp={0}", taskDefIds1);
				taskDefIds[0] = taskDefIds1;
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
		
		TaskDefinitionDetails taskDef = createTaskDef("async task def", "async pr def");
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		
		final LongList[] taskDefIds = { null };
		LOG.log(Level.INFO, "sending add task def request");
		MALMessage malMsg = prCons.asyncAddDefinition(DefinitionType.TASK_DEF, taskDefs, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void addDefinitionResponseReceived(MALMessageHeader msgHeader, LongList taskDefIds1,
					Map qosProperties) {
				LOG.log(Level.INFO, "add task def ack={0}", taskDefIds1);
				taskDefIds[0] = taskDefIds1;
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
		
		LongList taskDefIds2 = listTaskDefs("*");
		
		assertNotNull(taskDefIds2);
		assertEquals(1, taskDefIds2.size());
		assertNotNull(taskDefIds2.get(0));
		// id from add() matches id from list()
		assertTrue(taskDefIds2.contains(taskDefIds[0].get(0)));
		
		malMsg.free();
		
		leave("testAddTaskDef");
	}
	
	@Test
	public void testUpdateTaskDefinition() throws MALException, MALInteractionException {
		enter("testUpdateTaskDef");
		
		Object[] details = addTaskDef();
		LongList taskDefIds = (LongList)details[0];
		TaskDefinitionDetailsList taskDefs = (TaskDefinitionDetailsList)details[1];
		
		taskDefs.get(0).setDescription("whoa");
		
		final boolean[] updated = { false };
		LOG.log(Level.INFO, "sending update task def request");
		MALMessage malMsg = prCons.asyncUpdateDefinition(DefinitionType.TASK_DEF, taskDefIds, taskDefs, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updateDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
				LOG.log(Level.INFO, "update task def ack");
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
		
		LongList taskDefIds2 = listTaskDefs("*");
		
		assertNotNull(taskDefIds2);
		// list() returns id, but unable to verify description
		// update()d id is still list()ed
		assertTrue(taskDefIds2.contains(taskDefIds.get(0)));
		
		malMsg.free();
		
		leave("testUpdateTaskDef");
	}
	
	@Test
	public void testRemoveTaskDefinition() throws MALException, MALInteractionException {
		enter("testRemoveTaskDef");
		
		Object[] details = addTaskDef();
		LongList taskDefIds = (LongList)details[0];
		
		final Boolean[] removed = { false };
		LOG.log(Level.INFO, "sending remove task def request");
		MALMessage malMsg = prCons.asyncRemoveDefinition(DefinitionType.TASK_DEF, taskDefIds, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void removeDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "remove task def ack");
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
		
		LongList taskDefIds2 = listTaskDefs("*");
		// remove()d id is not list()ed anymore
		assertFalse(taskDefIds2.contains(taskDefIds.get(0)));
		
		malMsg.free();
		
		leave("testRemoveTaskDef");
	}
}
