package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
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
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import esa.mo.inttest.ca.consumer.ComArchiveConsumerFactory;
import esa.mo.inttest.ca.provider.ComArchiveProviderFactory;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

/**
 * Planning request stub test.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath*:**/testIntContext.xml")
public class PlanningRequestStubTest {
	
	private static final Logger LOG = Logger.getLogger(PlanningRequestStubTest.class.getName());
	
	private ComArchiveProviderFactory caProvFct;
	
//	@Autowired
	private PlanningRequestProviderFactory prProvFct;
	
//	@Autowired
	private PlanningRequestConsumerFactory prConsFct;
	private PlanningRequestStub prCons;
	
	private ComArchiveConsumerFactory caConsFct;
	
	private void enter(String msg) {
		LOG.entering(getClass().getName(), msg);
	}
	
	private void leave(String msg) {
		LOG.exiting(getClass().getName(), msg);
	}
	
	@Before
	public void setUp() throws Exception {
		enter("setUp");
		
		String props = "testInt.properties";
		
		prProvFct = new PlanningRequestProviderFactory();
		prProvFct.setPropertyFile(props);
		prProvFct.start();
		
		URI sharedBrokerUri = prProvFct.getBrokerUri();
		
		caProvFct = new ComArchiveProviderFactory();
		caProvFct.setPropertyFile(props);
		caProvFct.setSharedBrokerUri(sharedBrokerUri);
		caProvFct.start();
		
		caConsFct = new ComArchiveConsumerFactory();
		caConsFct.setPropertyFile(props);
		caConsFct.setProviderUri(caProvFct.getProviderUri());
		caConsFct.setBrokerUri(sharedBrokerUri);
		caConsFct.start();
		
		prConsFct = new PlanningRequestConsumerFactory();
		prConsFct.setPropertyFile(props);
		prConsFct.setProviderUri(prProvFct.getProviderUri());
		prConsFct.setBrokerUri(sharedBrokerUri);
		prCons = prConsFct.start();
		
		leave("setUp");
	}

	@After
	public void tearDown() throws Exception {
		enter("tearDown");
		if (prConsFct != null) {
			prConsFct.stop(prCons);
		}
		prCons = null;
		prConsFct = null;
		
		if (caConsFct != null) {
			caConsFct.stop();
		}
		caConsFct = null;
		
		if (caProvFct != null) {
			caProvFct.stop();
		}
		caProvFct = null;
		
		if (prProvFct != null) {
			prProvFct.stop();
		}
		prProvFct = null;
		leave("tearDown");
	}

	private PlanningRequestDefinitionDetails createPrDef(String id) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier(id)); // mandatory - encoding exception if missing/null
		return prDef;
	}
	
	private Long submitPrDef(PlanningRequestDefinitionDetails prDef) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		LongList prDefIdList = prCons.addDefinition(prDefs);
		Long prDefId = prDefIdList.get(0);
		return prDefId;
	}
	
	private PlanningRequestInstanceDetails createPrInst(PlanningRequestDefinitionDetails prDef,
			TaskInstanceDetailsList taskInsts) {
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setName(prDef.getName()); // mandatory
		prInst.setTasks(taskInsts);
		return prInst;
	}
	
	private long lastId = 0;
	
	private Long generateId() {
		return ++lastId;
	}
	
	private void storePrInst(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst) throws MALException,
			MALInteractionException {
		
		ObjectType objType = new ObjectType(PlanningHelper.PLANNING_AREA_NUMBER,
				PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NUMBER, PlanningHelper.PLANNING_AREA_VERSION,
				new UShort(1));
		
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("test"));
		
		ObjectDetails objDetail = new ObjectDetails(prDefId, null);
		
		ArchiveDetails arcDetail = new ArchiveDetails();
		arcDetail.setInstId(prInstId); // mandatory - i guess com archive does not generate id-s - we are
		arcDetail.setDetails(objDetail); // mandatory
		
		ArchiveDetailsList arcDetails = new ArchiveDetailsList();
		arcDetails.add(arcDetail);
		
		PlanningRequestInstanceDetailsList elements = new PlanningRequestInstanceDetailsList();
		elements.add(prInst);
		
		caConsFct.getConsumer().store(false, objType, domain, arcDetails, elements);
	}
	
	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		enter("testSubmitPR");
		
		PlanningRequestDefinitionDetails prDef = createPrDef("id1");
		
		Long prDefId = submitPrDef(prDef);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, null);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		prCons.submitPlanningRequest(prDefId, prInstId, prInst);
		
		leave("testSubmitPR");
	}

	@Test
	public void testSubmitPlanningRequestWithMonitoring() throws MALException, MALInteractionException {
		enter("testSubmitPrWithMonitor");
		
		final PlanningRequestStatusDetailsList[] prStatDets = { null };
		
		String subId = "subId1";
		EntityRequestList entityReqs = new EntityRequestList();
		EntityKeyList entityKeys = new EntityKeyList();
		entityKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityReqs.add(new EntityRequest(null, true, true, true, false, entityKeys));
		Subscription sub = new Subscription(new Identifier(subId), entityReqs);
		prCons.monitorPlanningRequestsRegister(sub, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier id,
					UpdateHeaderList updHdrs, ObjectIdList objIds,
					PlanningRequestStatusDetailsList prStats, Map qosProps) {
				LOG.log(Level.INFO, "pr notify: " + msgHeader + " ; " + id + " ; " + updHdrs + " ; " + objIds + " ; "
					+ prStats + " ; " + qosProps);
				prStatDets[0] = prStats;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "pr notify err: " + msgHeader + " ; " + error + " ; " + qosProps);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
					throws MALException {
				LOG.log(Level.INFO, "pr other notify: " + msgHeader + " ; " + body);
			}
		});
		
		PlanningRequestDefinitionDetails prDef = createPrDef("id2");
		
		Long prDefId = submitPrDef(prDef);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, null);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		prCons.submitPlanningRequest(prDefId, prInstId, prInst);
		
		sleep(1000); // give broker a sec to respond
		
		assertNotNull(prStatDets[0]);
		assertEquals(1, prStatDets[0].size());
		assertNotNull(prStatDets[0].get(0));
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		prCons.monitorPlanningRequestsDeregister(subIds);
		
		leave("testSubmitPrWithMonitor");
	}

	private TaskDefinitionDetails createTaskDef(String id, String prDefName) {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier(id)); // mandatory
		taskDef.setPrDefName(new Identifier(prDefName)); // mandatory
		return taskDef;
	}
	
	private Long submitTaskDef(TaskDefinitionDetails taskDef) throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		LongList taskDefIds = prCons.addTaskDefinition(taskDefs);
		Long taskDefId = taskDefIds.get(0);
		return taskDefId;
	}
	
	private TaskInstanceDetails createTaskInst(TaskDefinitionDetails taskDef) {
		TaskInstanceDetails taskInst = new TaskInstanceDetails();
		taskInst.setName(taskDef.getName()); // mandatory
		taskInst.setPrName(taskDef.getPrDefName()); // mandatory
		return taskInst;
	}
	
	private void storeTaskInst(Long taskDefId, Long taskInstId, TaskInstanceDetails taskInst) throws MALException,
			MALInteractionException {
		
		ObjectType objType = new ObjectType(TaskInstanceDetails.AREA_SHORT_FORM, TaskInstanceDetails.SERVICE_SHORT_FORM,
				TaskInstanceDetails.AREA_VERSION, new UShort(1));
		
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		
		ObjectDetails objDetail = new ObjectDetails();
		objDetail.setRelated(taskDefId);
		
		ArchiveDetails arcDetail = new ArchiveDetails();
		arcDetail.setInstId(taskInstId);
		arcDetail.setDetails(objDetail);
		
		ArchiveDetailsList arcDetails = new ArchiveDetailsList();
		arcDetails.add(arcDetail);
		
		TaskInstanceDetailsList elements = new TaskInstanceDetailsList();
		elements.add(taskInst);
		
		caConsFct.getConsumer().store(false, objType, domain, arcDetails, elements);
	}
	
	@Test
	public void testSubmitPlanningRequestWithTask() throws MALException, MALInteractionException {
		enter("testSubmitPrWithTask");
		
		String prDefName = "id1";
		TaskDefinitionDetails taskDef = createTaskDef("id2", prDefName);
		
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = createPrDef(prDefName);
		
		Long prDefId = submitPrDef(prDef);
		
		TaskInstanceDetails taskInst = createTaskInst(taskDef);
		Long taskInstId = generateId();
		
		storeTaskInst(taskDefId, taskInstId, taskInst);
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, taskInsts);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		prCons.submitPlanningRequest(prDefId, prInstId, prInst);
		
		leave("testSubmitPrWithTask");
	}

	@Test
	public void testSubmitPlanningRequestWithTaskAndMonitoring() throws MALException, MALInteractionException {
		enter("testSubmitPrWithTaskAndMonitoring");
		
		final PlanningRequestStatusDetailsList[] prStatDets = { null };
		final TaskStatusDetailsList[] taskStatDets = { null };
		
		String prSubId = "prSubId";
		Subscription prSub = new Subscription();
		prSub.setSubscriptionId(new Identifier(prSubId));
		EntityRequestList entityReqs = new EntityRequestList();
		EntityKeyList entityKeys = new EntityKeyList();
		entityKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityReqs.add(new EntityRequest(null, true, true, true, false, entityKeys));
		prSub.setEntities(entityReqs);
		prCons.monitorPlanningRequestsRegister(prSub, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier id,
					UpdateHeaderList updHdrs, ObjectIdList objIds,
					PlanningRequestStatusDetailsList prStats, Map qosProps) {
				LOG.log(Level.INFO, "pr notify: " + msgHeader + " ; " + id + " ; " + updHdrs + " ; " + objIds + " ; "
					+ prStats + " ; " + qosProps);
				prStatDets[0] = prStats;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "pr notify err: " + msgHeader + " ; " + error + " ; " + qosProps);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
					throws MALException {
				LOG.log(Level.INFO, "pr other notify: " + msgHeader + " ; " + body + " ; " + qosProps);
			}
		});
		
		String taskSubId = "taskSubId";
		Subscription taskSub = new Subscription();
		taskSub.setSubscriptionId(new Identifier(taskSubId));
		taskSub.setEntities(entityReqs);
		prCons.monitorTasksRegister(taskSub, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier id, UpdateHeaderList updHdrs,
					ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
				LOG.log(Level.INFO, "task notify: " + msgHeader + " ; " + id + " ; " + updHdrs + " ; " + objIds + " ; "
					+ taskStats + " ; " + qosProps);
				taskStatDets[0] = taskStats;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "task notify err: " + msgHeader + " ; " + error + " ; " + qosProps);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
					throws MALException {
				LOG.log(Level.INFO, "task other notify: " + msgHeader + " ; " + body + " ; " + qosProps);
			}
		});
		
		String prDefName = "id1";
		TaskDefinitionDetails taskDef = createTaskDef("id2", prDefName);
		
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = createPrDef(prDefName);
		
		Long prDefId = submitPrDef(prDef);
		
		TaskInstanceDetails taskInst = createTaskInst(taskDef);
		Long taskInstId = generateId();
		
		storeTaskInst(taskDefId, taskInstId, taskInst);
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, taskInsts);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		prCons.submitPlanningRequest(prDefId, prInstId, prInst);
		
		sleep(2000); // give broker a sec to respond
		
		assertNotNull(prStatDets[0]);
		assertEquals(1, prStatDets[0].size());
		assertNotNull(prStatDets[0].get(0));
		
		assertNotNull(taskStatDets[0]);
		assertEquals(1, taskStatDets[0].size());
		assertNotNull(taskStatDets[0].get(0));
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(taskSubId));
		prCons.monitorTasksDeregister(subIds);
		
		IdentifierList subIds2 = new IdentifierList();
		subIds2.add(new Identifier(prSubId));
		prCons.monitorPlanningRequestsDeregister(subIds2);
		
		leave("testSubmitPrWithTaskAndMonitoring");
	}

//	@Test
//	public void testAsyncSubmitPlanningRequest() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueSubmitPlanningRequest() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testGetPlanningRequestStatus() throws MALException, MALInteractionException {
		enter("testGetPrStatus");
		LongList prInstIds = new LongList();
		prInstIds.add(1L);
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prInstIds);
		assertNotNull(prStats);
		leave("testGetPrStatus end");
	}

//	@Test
//	public void testAsyncGetPlanningRequestStatus() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueGetPlanningRequestStatus() {
//		fail("Not yet implemented");
//	}

	private void registerPrMonitor(String subId, final Boolean[] regs) throws MALException, MALInteractionException {
		Identifier id = new Identifier(subId);
		EntityRequestList entityList = new EntityRequestList();
		EntityKeyList keys = new EntityKeyList();
		keys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityList.add(new EntityRequest(null, true, true, true, false, keys));
		Subscription sub = new Subscription(id, entityList);
		prCons.monitorPlanningRequestsRegister(sub, new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				LOG.log(Level.INFO, "pr monitor registration ack");
				regs[0] = true;
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				LOG.log(Level.INFO, "pr monitor registration err");
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				LOG.log(Level.INFO, "pr monitor de-registration ack");
				regs[1] = true;
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
					UpdateHeaderList _UpdateHeaderList1, ObjectIdList _ObjectIdList2,
					TaskStatusDetailsList _TaskStatusDetailsList3, Map qosProperties)
			{
				LOG.log(Level.INFO, "pr monitor notify");
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				LOG.log(Level.INFO, "pr monitor notify err");
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body,
					Map qosProperties) throws org.ccsds.moims.mo.mal.MALException
			{
				LOG.log(Level.INFO, "pr monitor other notify");
			}
		});
	}

	@Ignore("register ack response never arrives")
	@Test
	public void testMonitorPlanningRequestsRegister() throws MALException, MALInteractionException {
		enter("testMonitorPrReg");
		String subId = "subId";
		final Boolean[] regs = { false, false };
		registerPrMonitor(subId, regs);
		sleep(1000); // give broker a second to fire callback
		assertTrue(regs[0]);
		leave("testMonitorPrReg");
	}

//	@Test
//	public void testAsyncMonitorPlanningRequestsRegister() {
//		fail("Not yet implemented");
//	}

	@Ignore("de-register ack response never arrives")
	@Test
	public void testMonitorPlanningRequestsDeregister() throws MALException, MALInteractionException {
		enter("testMonitorPrDeReg");
		String subId = "subId2";
		final Boolean[] regs = { false, false };
		registerPrMonitor(subId, regs);
		sleep(1000); // wait a sec before de-registering
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier(subId));
		prCons.monitorPlanningRequestsDeregister(subIdList);
		sleep(1000); // give broker a sec to fire callback
		assertTrue(regs[1]);
		leave("testMonitorPrDeReg");
	}

//	@Test
//	public void testAsyncMonitorPlanningRequestsDeregister() {
//		fail("Not yet implemented");
//	}

	private LongList listPrDefs(String id) throws MALException, MALInteractionException {
		IdentifierList ids = new IdentifierList();
		ids.add(new Identifier(id));
		return prCons.listDefinition(ids);
	}
	
	@Test
	public void testListDefinition() throws MALException, MALInteractionException {
		enter("testListPrDefs");
		LongList ids = listPrDefs("*");
		assertNotNull(ids);
		assertEquals(0, ids.size());
		leave("testListPrDefs");
	}

//	@Test
//	public void testAsyncListDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueListDefinition() {
//		fail("Not yet implemented");
//	}

	private Map.Entry<LongList, PlanningRequestDefinitionDetailsList> addPrDef() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier("new pr def"));
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		LongList prDefIds = prCons.addDefinition(prDefs);
		return new AbstractMap.SimpleEntry<LongList, PlanningRequestDefinitionDetailsList>(prDefIds, prDefs);
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

//	@Test
//	public void testAsyncAddDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueAddDefinition() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testUpdateDefinition() throws MALException, MALInteractionException {
		enter("testUpdatePrDef");
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		e.getValue().get(0).setDescription("updated desc");
		prCons.updateDefinition(e.getKey(), e.getValue());
		// updated pr id is still listed, but verify description
		LongList ids = listPrDefs("*");
		assertTrue(ids.contains(e.getKey().get(0)));
		leave("testUpdatePrDef");
	}

//	@Test
//	public void testAsyncUpdateDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueUpdateDefinition() {
//		fail("Not yet implemented");

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

//	@Test
//	public void testAsyncRemoveDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueRemoveDefinition() {
//		fail("Not yet implemented");

	@Test
	public void testGetTaskStatus() throws MALException, MALInteractionException {
		enter("testGetTaskStatus");
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		TaskStatusDetailsList taskStats = prCons.getTaskStatus(taskInstIds);
		assertNotNull(taskStats);
		leave("testGetTaskStatus");
	}

//	@Test
//	public void testAsyncGetTaskStatus() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueGetTaskStatus() {
//		fail("Not yet implemented");

	@Test
	public void testSetTaskStatus() throws MALException, MALInteractionException {
		enter("testSetTaskStatus");
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
		TaskStatusDetails taskStat = new TaskStatusDetails();
		taskStat.setTaskInstName(new Identifier("id")); // mandatory
		taskStats.add(taskStat);
		prCons.setTaskStatus(taskInstIds, taskStats);
		leave("testSetTaskStatus");
	}

//	@Test
//	public void testAsyncSetTaskStatus() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueSetTaskStatus() {
//		fail("Not yet implemented");
//	}

	private void registerTaskMonitor(String subId, final Boolean[] regs) throws MALException, MALInteractionException {
		Identifier id = new Identifier(subId);
		EntityRequestList entityList = new EntityRequestList();
		EntityKeyList keys = new EntityKeyList();
		keys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityList.add(new EntityRequest(null, true, true, true, false, keys));
		Subscription sub = new Subscription(id, entityList);
		prCons.monitorTasksRegister(sub, new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				LOG.log(Level.INFO, "register task monitor ack");
				regs[0] = true;
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				LOG.log(Level.INFO, "register task monitor err");
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				LOG.log(Level.INFO, "de-register task monitor ack");
				regs[1] = true;
			}
			@SuppressWarnings("rawtypes")
			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
					UpdateHeaderList _UpdateHeaderList1, ObjectIdList _ObjectIdList2,
					TaskStatusDetailsList _TaskStatusDetailsList3, Map qosProperties)
			{
				LOG.log(Level.INFO, "task monitor notify");
			}
			@SuppressWarnings("rawtypes")
			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				LOG.log(Level.INFO, "task monitor notify err");
			}
			@SuppressWarnings("rawtypes")
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body,
					Map qosProperties) throws org.ccsds.moims.mo.mal.MALException
			{
				LOG.log(Level.INFO, "taks other notify");
			}
		});
	}
	
	private void sleep(long ms) {
		try { // give broker a second to fire callback
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	
	@Ignore("register ack response never arrives")
	@Test
	public void testMonitorTasksRegister() throws MALException, MALInteractionException {
		enter("testMonitorTasksRegister");
		String subId = "subId";
		final Boolean[] regs = { false, false };
		registerTaskMonitor(subId, regs);
		sleep(1000); // give broker a second to fire callback
		assertTrue(regs[0]);
		leave("testMonitorTasksRegister");
	}

//	@Test
//	public void testAsyncMonitorTasksRegister() {
//		fail("Not yet implemented");
//	}
	
	@Ignore("de-register ack response never arrives")
	@Test
	public void testMonitorTasksDeregister() throws MALException, MALInteractionException {
		enter("testMonitorTasksDeregister");
		String subId = "subId2";
		final Boolean[] regs = { false, false };
		registerTaskMonitor(subId, regs);
		sleep(1000); // wait a sec before de-registering
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier(subId));
		prCons.monitorTasksDeregister(subIdList);
		sleep(1000); // give broker a sec to fire callback
		assertTrue(regs[1]);
		leave("testMonitorTasksDeregister");
	}

//	@Test
//	public void testAsyncMonitorTasksDeregister() {
//		fail("Not yet implemented");
//	}

	private LongList listTaskDefs(String f) throws MALException, MALInteractionException {
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier(f));
		return prCons.listTaskDefinition(idList);
	}
	
	@Test
	public void testListTaskDefinition() throws MALException, MALInteractionException {
		enter("testListTaskDefs");
		LongList taskDefIdList = listTaskDefs("*");
		assertNotNull(taskDefIdList);
		assertEquals(0, taskDefIdList.size());
		leave("testListTaskDefs");
	}

//	@Test
//	public void testAsyncListTaskDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueListTaskDefinition() {
//		fail("Not yet implemented");
//	}

	private Map.Entry<LongList, TaskDefinitionDetailsList> addTaskDef() throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefList = new TaskDefinitionDetailsList();
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier("new task def")); // mandatory
		taskDef.setPrDefName(new Identifier("new pr def")); // mandatory
		taskDefList.add(taskDef);
		LongList taskDefIdList = prCons.addTaskDefinition(taskDefList);
		return new AbstractMap.SimpleEntry<LongList, TaskDefinitionDetailsList>(taskDefIdList, taskDefList);
	}
	
	@Test
	public void testAddTaskDefinition() throws MALException, MALInteractionException {
		enter("testAddTaskDef");
		Map.Entry<LongList, TaskDefinitionDetailsList> e = addTaskDef();
		assertNotNull(e.getKey());
		assertEquals(1, e.getKey().size());
		assertNotNull(e.getKey().get(0));
		
		LongList taskDefIdList = listTaskDefs("*");
		assertNotNull(taskDefIdList);
		assertEquals(1, taskDefIdList.size());
		assertNotNull(taskDefIdList.get(0));
		// id from add() matches id from list()
		assertEquals(e.getKey().get(0), taskDefIdList.get(0));
		leave("testAddTaskDef");
	}
	
//	@Test
//	public void testAsyncAddTaskDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueAddTaskDefinition() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testUpdateTaskDefinition() throws MALException, MALInteractionException {
		enter("testUpdateTaskDef");
		Map.Entry<LongList, TaskDefinitionDetailsList> e = addTaskDef();
		e.getValue().get(0).setDescription("whoa");
		prCons.updateTaskDefinition(e.getKey(), e.getValue());
		// list() returns id - unable to verify description
		LongList taskDefIdList = listTaskDefs("*");
		// added id is still listed
		assertTrue(taskDefIdList.contains(e.getKey().get(0)));
		leave("testUpdateTaskDef");
	}

//	@Test
//	public void testAsyncUpdateTaskDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueUpdateTaskDefinition() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testRemoveTaskDefinition() throws MALException, MALInteractionException {
		enter("testRemoveTaskDef");
		Map.Entry<LongList, TaskDefinitionDetailsList> e = addTaskDef();
		prCons.removeTaskDefinition(e.getKey());
		LongList taskDefIdList = listTaskDefs("*");
		// added id is not listed anymore
		assertFalse(taskDefIdList.contains(e.getKey().get(0)));
		leave("testRemoveTaskDef");
	}

//	@Test
//	public void testAsyncRemoveTaskDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueRemoveTaskDefinition() {
//		fail("Not yet implemented");
//	}

}