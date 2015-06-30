/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.Callable;
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
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.Test;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;

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
	public void testMonitorPlanningRequestsRegister() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testMonitorPrReg");
		
		String subId = "subId";
		Object[] details = asyncRegisterPrMonitor(subId);
		MALMessage malMsg = (MALMessage)details[0];
		final PrMonitor prMon = (PrMonitor)details[1];
		
		Util.waitFor(prMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return prMon.registered;
			}
		});
		
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
	public void testMonitorPlanningRequestsDeregister() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testMonitorPrDeReg");
		
		String subId = "subId2";
		Object[] details = asyncRegisterPrMonitor(subId);
		MALMessage malMsg = (MALMessage)details[0];
		final PrMonitor prMon = (PrMonitor)details[1];
		
		MALMessage malMsg2 = asyncDeRegisterPrMonitor(subId, prMon);
		
		Util.waitFor(prMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return prMon.deRegistered;
			}
		});
		
		assertTrue(prMon.deRegistered);
		
		malMsg2.free();
		malMsg.free();
		
		leave("testMonitorPrDeReg");
	}
	
	protected MALMessage asyncSubmitPr(PlanningRequestInstanceDetails prInst,
			final PlanningRequestStatusDetailsList[] resp) throws MALException, MALInteractionException {
		
		PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
		insts.add(prInst);
		
		return prCons.asyncSubmitPlanningRequest(insts, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void submitPlanningRequestResponseReceived(MALMessageHeader msgHeader,
					PlanningRequestStatusDetailsList resp2, Map qosProps) {
				LOG.log(Level.INFO, "submit pr resp={0}", resp2);
				resp[0] = resp2;
				synchronized (resp) {
					resp.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void submitPlanningRequestErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "submit pr error={0}", error);
			}
		});
	}
	
	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testSubmitPlanningRequest");
		
		PlanningRequestDefinitionDetails prDef = createPrDef("async pr def");
		prDef.setId(0L);
		Long prDefId = submitPrDef(prDef);
		prDef.setId(prDefId);
		
		PlanningRequestInstanceDetails prInst = createPrInst(generateId(), prDefId, null);
		
		storePrInst(prInst);
		
		final PlanningRequestStatusDetailsList[] response = { null };
		
		MALMessage malMsg = asyncSubmitPr(prInst, response);
		
		Util.waitFor(response, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != response[0];
			}
		});
		
		assertNotNull(response[0]);
		
		verifyPrStat(prInst);
		
		malMsg.free();
		
		leave("testSubmitPlanningRequest");
	}

	@Test
	public void testSubmitPlanningRequestWithTask() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testSubmitPlanningRequestWithTask");
		
		TaskDefinitionDetails taskDef = createTaskDef("async task def");
		taskDef.setId(0L);
		Long taskDefId = submitTaskDef(taskDef);
		taskDef.setId(taskDefId);
		
		TaskInstanceDetails taskInst = createTaskInst(generateId(), taskDefId);
		Long prId = generateId();
		taskInst.setPrInstId(prId);
		storeTaskInst(taskInst);
		
		PlanningRequestDefinitionDetails prDef = createPrDef("async pr def");
		prDef.setId(0L);
		Long prDefId = submitPrDef(prDef);
		prDef.setId(prDefId);
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prId, prDefId, taskInsts);
		
		storePrInst(prInst);
		
		final PlanningRequestStatusDetailsList[] response = { null };
		
		MALMessage malMsg = asyncSubmitPr(prInst, response);
		
		Util.waitFor(response, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != response[0];
			}
		});
		
		assertNotNull(response[0]);
		
		verifyPrStat(prInst);
		
		LongList taskIds = new LongList();
		taskIds.add(taskInst.getId());
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskIds);
		
		assertNotNull(taskStats);
		assertEquals(1, taskStats.size());
		assertNotNull(taskStats.get(0));
		
		malMsg.free();
		
		leave("testSubmitPlanningRequestWithTask");
	}

	@Test
	public void testUpdatePlanningRequest() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testUpdatePlanningRequest");
		
		PlanningRequestInstanceDetails prInst = createAndSubmitPlanningRequestWithTask();
		
		prInst.setComment("async updated");
		
		PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
		insts.add(prInst);
		
		final boolean[] updated = { false };
		
		MALMessage malMsg = prCons.asyncUpdatePlanningRequest(insts, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updatePlanningRequestResponseReceived(MALMessageHeader msgHeader,
					PlanningRequestStatusDetailsList prStats, Map qosProps) {
				LOG.log(Level.INFO, "update pr response={0}", Dumper.prStats(prStats));
				updated[0] = true;
				synchronized (updated) {
					updated.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void updatePlanningRequestErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "update pr error={0}", error);
			}
		});
		
		Util.waitFor(updated, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return updated[0];
			}
		});
		
		assertTrue(updated[0]);
		
		verifyPrStat(prInst);
		
		LongList taskIds = new LongList();
		taskIds.add(prInst.getTasks().get(0).getId());
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskIds);
		
		assertNotNull(taskStats);
		assertEquals(1, taskStats.size());
		assertNotNull(taskStats.get(0));
		
		malMsg.free();
		
		leave("testUpdatePlanningRequest");
	}

	@Test
	public void testRemovePlanningRequest() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testRemovePlanningRequest");
		
		PlanningRequestInstanceDetails prInst = createAndSubmitPlanningRequestWithTask();
		Long prId = prInst.getId();
		
		LongList ids = new LongList();
		ids.add(prId);
		
		final boolean[] removed = { false };
		
		MALMessage malMsg = prCons.asyncRemovePlanningRequest(ids, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void removePlanningRequestResponseReceived(MALMessageHeader msgHeader,
					PlanningRequestStatusDetailsList prStats, Map qosProps) {
				LOG.log(Level.INFO, "remove pr response={0}", Dumper.prStats(prStats));
				removed[0] = true;
				synchronized (removed) {
					removed.notifyAll();
				}
			}

			@SuppressWarnings("rawtypes")
			public void removePlanningRequestErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "remove pr error={0}", error);
			}
		});
		
		Util.waitFor(removed, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return removed[0];
			}
		});
		
		assertTrue(removed[0]);
		
		LongList prIds = new LongList();
		prIds.add(prInst.getId());
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNull(prStats.get(0));
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(prInst.getTasks().get(0).getId());
		
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskInstIds);
		
		assertNotNull(taskStats);
		assertEquals(1, taskStats.size());
		assertNull(taskStats.get(0));
		
		malMsg.free();
		
		leave("testRemovePlanningRequest");
	}

	@Test
	public void testGetPlanningRequestStatus() throws MALException, MALInteractionException, InterruptedException, Exception {
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
				synchronized (stats) {
					stats.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void getPlanningRequestStatusErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "get pr status error={0}", error);
			}
		});
		
		Util.waitFor(stats, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != stats[0];
			}
		});
		// should get null
		assertNotNull(stats[0]);
		assertEquals(1, stats[0].size());
		assertNull(stats[0].get(0));
		
		malMsg.free();
		
		leave("testGetPlanningRequestStatus");
	}
	
	@Test
	public void testGetTaskStatus() throws MALException, MALInteractionException, InterruptedException, Exception {
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
				synchronized (stats) {
					stats.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void getTaskStatusErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				LOG.log(Level.INFO, "get task status error={0}", error);
			}
		});
		
		Util.waitFor(stats, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != stats[0];
			}
		});
		
		assertNotNull(stats[0]);
		assertEquals(1, stats[0].size());
		assertNull(stats[0].get(0));
		
		malMsg.free();
		
		leave("testGetTaskStatus");
	}
	
	@Test
	public void testListPrDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
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
				synchronized (ids) {
					ids.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void listDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "list pr defs error={0}", error);
			}
		});
		
		Util.waitFor(ids, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != ids[0];
			}
		});
		
		assertNotNull(ids[0]);
		assertEquals(0, ids[0].size());
		
		malMsg.free();
		
		leave("testListPrDefs");
	}
	
	@Test
	public void testAddPrDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testAddPrDef");
		
		PlanningRequestDefinitionDetails prDef = createPrDef("async pr def");
		prDef.setId(0L);
		
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		
		final LongList[] ids = { null };
		
		MALMessage malMsg = prCons.asyncAddDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void addDefinitionResponseReceived(MALMessageHeader msgHeader, LongList prDefIds,
					Map qosProps) {
				LOG.log(Level.INFO, "add pr defs resp={0}", prDefIds);
				ids[0] = prDefIds;
				synchronized (ids) {
					ids.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void addDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "add pr defs err={0}", error);
			}
		});
		
		Util.waitFor(ids, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != ids[0];
			}
		});
		
		assertNotNull(ids[0]);
		assertEquals(1, ids[0].size());
		assertNotNull(ids[0].get(0));
		assertFalse(0L == ids[0].get(0));
		// added pr id is listed
		LongList ids2 = listPrDefs("*");
		
		assertTrue(ids2.contains(ids[0].get(0)));
		
		malMsg.free();
		
		leave("testAddPrDef");
	}
	
	@Test
	public void testUpdatePrDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testUpdatePrDef");
		
		PlanningRequestDefinitionDetails prDef = addPrDef();
		
		prDef.setDescription("updated desc");
		
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		
		final boolean[] updated = { false };
		
		MALMessage malMsg = prCons.asyncUpdateDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updateDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "update pr def ack");
				updated[0] = true;
				synchronized (updated) {
					updated.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void updateDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "update pr def error={0}", error);
			}
		});
		
		Util.waitFor(updated, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return updated[0];
			}
		});
		
		assertTrue(updated[0]);
		
		// updated pr id is still listed, but cant verify description
		LongList ids = listPrDefs("*");
		
		assertTrue(ids.contains(prDef.getId()));
		
		malMsg.free();
		
		leave("testUpdatePrDef");
	}
	
	@Test
	public void testRemovePrDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testRemovePrDef");
		
		PlanningRequestDefinitionDetails prDef = addPrDef();
		
		LongList prDefIds = new LongList();
		prDefIds.add(prDef.getId());
		
		final boolean[] removed = { false };
		
		MALMessage malMsg = prCons.asyncRemoveDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefIds, new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			public void removeDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
				LOG.log(Level.INFO, "remove pr def ack");
				removed[0] = true;
				synchronized (removed) {
					removed.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void removeDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "remove pr def error={0}", error);
			}
		});
		
		Util.waitFor(removed, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return removed[0];
			}
		});
		
		assertTrue(removed[0]);
		
		// removed pr id is not listed anymore
		LongList ids = listPrDefs("*");
		
		assertFalse(ids.contains(prDef.getId()));
		
		malMsg.free();
		
		leave("testRemovePrDef");
	}
	
	@Test
	public void testListTaskDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
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
				synchronized (taskDefIds) {
					taskDefIds.notifyAll();
				}
			}

			@SuppressWarnings("rawtypes")
			public void listDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "list task defs err={0}", error);
			}
		});
		
		Util.waitFor(taskDefIds, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != taskDefIds[0];
			}
		});
		
		assertNotNull(taskDefIds[0]);
		assertEquals(0, taskDefIds[0].size());
		
		malMsg.free();
		
		leave("testListTaskDefs");
	}
	
	@Test
	public void testAddTaskDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testAddTaskDef");
		
		TaskDefinitionDetails taskDef = createTaskDef("async task def");
		taskDef.setId(0L);
		
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
				synchronized (taskDefIds) {
					taskDefIds.notifyAll();
				}
			}

			@SuppressWarnings("rawtypes")
			public void addDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "add task def err={0}", error);
				assertTrue(false);
			}
		});
		
		Util.waitFor(taskDefIds, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != taskDefIds[0];
			}
		});
		
		assertNotNull(taskDefIds[0]);
		assertEquals(1, taskDefIds[0].size());
		assertNotNull(taskDefIds[0].get(0));
		assertFalse(0L == taskDefIds[0].get(0));
		
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
	public void testUpdateTaskDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testUpdateTaskDef");
		
		TaskDefinitionDetails taskDef = addTaskDef();
		
		taskDef.setDescription("whoa");
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		
		final boolean[] updated = { false };
		LOG.log(Level.INFO, "sending update task def request");
		MALMessage malMsg = prCons.asyncUpdateDefinition(DefinitionType.TASK_DEF, taskDefs, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updateDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
				LOG.log(Level.INFO, "update task def ack");
				updated[0] = true;
				synchronized (updated) {
					updated.notifyAll();
				}
			}

			@SuppressWarnings("rawtypes")
			public void updateDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				LOG.log(Level.INFO, "update task def err={0}", error);
			}
		});
		
		Util.waitFor(updated, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return updated[0];
			}
		});
		
		assertTrue(updated[0]);
		
		LongList taskDefIds2 = listTaskDefs("*");
		
		assertNotNull(taskDefIds2);
		// list() returns id, but unable to verify description
		// update()d id is still list()ed
		assertTrue(taskDefIds2.contains(taskDef.getId()));
		
		malMsg.free();
		
		leave("testUpdateTaskDef");
	}
	
	@Test
	public void testRemoveTaskDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testRemoveTaskDef");
		
		TaskDefinitionDetails taskDef = addTaskDef();
		
		LongList taskDefIds = new LongList();
		taskDefIds.add(taskDef.getId());
		
		final Boolean[] removed = { false };
		LOG.log(Level.INFO, "sending remove task def request");
		MALMessage malMsg = prCons.asyncRemoveDefinition(DefinitionType.TASK_DEF, taskDefIds, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void removeDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "remove task def ack");
				removed[0] = true;
				synchronized (removed) {
					removed.notifyAll();
				}
			}

			@SuppressWarnings("rawtypes")
			public void removeDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "remove task def err={0}", error);
			}
		});
		
		Util.waitFor(removed, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return removed[0];
			}
		});
		
		assertTrue(removed[0]);
		
		LongList taskDefIds2 = listTaskDefs("*");
		// remove()d id is not list()ed anymore
		assertFalse(taskDefIds2.contains(taskDefIds.get(0)));
		
		malMsg.free();
		
		leave("testRemoveTaskDef");
	}
}
