package esa.mo.plan.consumer;

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
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Map;

import esa.mo.plan.comarc.consumer.ComArchiveConsumerFactory;
import esa.mo.plan.comarc.provider.ComArchiveProviderFactory;
import esa.mo.plan.provider.PlanningRequestProviderFactory;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath*:**/testIntContext.xml")
public class PlanningRequestStubTest {
	
	private ComArchiveProviderFactory caProvFct;
	
//	@Autowired
	private PlanningRequestProviderFactory prProvFct;
	
//	@Autowired
	private PlanningRequestConsumerFactory prConsFct;
	
	private ComArchiveConsumerFactory caConsFct;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("setup start");
		
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
		prConsFct.start();
		
		System.out.println("setup end");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("teardown start");
		if (prConsFct != null) {
			prConsFct.stop();
		}
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
		System.out.println("teardown end");
	}

	@Test
	public void testGetConsumer() {
		assertNotNull(prConsFct.getConsumer());
	}

	private PlanningRequestDefinitionDetails createPrDef(String id) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier(id)); // mandatory - encoding exception if missing/null
		return prDef;
	}
	
	private Long submitPrDef(PlanningRequestDefinitionDetails prDef) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		LongList prDefIdList = prConsFct.getConsumer().addDefinition(prDefs);
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
		System.out.println("testSubmitPR start");
		
		PlanningRequestDefinitionDetails prDef = createPrDef("id1");
		
		Long prDefId = submitPrDef(prDef);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, null);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		prConsFct.getConsumer().submitPlanningRequest(prDefId, prInstId, prInst);
		
		System.out.println("testSubmitPR end");
	}

	@Test
	public void testSubmitPlanningRequestWithMonitoring() throws MALException, MALInteractionException {
		System.out.println("testSubmitPrWithMonitor start");
		
		final PlanningRequestStatusDetailsList[] prStatDets = { null };
		
		String subId = "subId1";
		EntityRequestList entityReqs = new EntityRequestList();
//		IdentifierList subDomain = new IdentifierList();
//		subDomain.add(new Identifier("desd")); // makes a difference
		EntityKeyList entityKeys = new EntityKeyList();
		entityKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityReqs.add(new EntityRequest(null/*subDomain*/, true, true, true, false, entityKeys));
		Subscription sub = new Subscription(new Identifier(subId), entityReqs);
		prConsFct.getConsumer().monitorPlanningRequestsRegister(sub, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier id,
					UpdateHeaderList updHdrs, ObjectIdList objIds,
					PlanningRequestStatusDetailsList prStats, Map qosProps) {
				System.out.println("pr notify: " + msgHeader + " ; " + id + " ; " + updHdrs + " ; " + objIds + " ; "
					+ prStats + " ; " + qosProps);
				prStatDets[0] = prStats;
			}

			@SuppressWarnings("rawtypes")
			public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				System.out.println("pr notify err: " + msgHeader + " ; " + error + " ; " + qosProps);
			}

			@SuppressWarnings("rawtypes")
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
					throws MALException {
				System.out.println("pr other notify: " + msgHeader + " ; " + body);
			}
		});
		
		PlanningRequestDefinitionDetails prDef = createPrDef("id2");
		
		Long prDefId = submitPrDef(prDef);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, null);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		prConsFct.getConsumer().submitPlanningRequest(prDefId, prInstId, prInst);
		
		sleep(1000); // give broker a sec to respond
		
		assertNotNull(prStatDets[0]);
		assertEquals(1, prStatDets[0].size());
		assertNotNull(prStatDets[0].get(0));
		
		System.out.println(prStatDets[0].get(0));
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		prConsFct.getConsumer().monitorPlanningRequestsDeregister(subIds);
		
		System.out.println("testSubmitPrWithMonitor end");
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
		LongList taskDefIds = prConsFct.getConsumer().addTaskDefinition(taskDefs);
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
		System.out.println("testSubmitPrWithTask start");
		
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
		
		prConsFct.getConsumer().submitPlanningRequest(prDefId, prInstId, prInst);
		
		System.out.println("testSubmitPrWithTask end");
	}

	@Test
	public void testSubmitPlanningRequestWithTaskAndMonitoring() throws MALException, MALInteractionException {
		System.out.println("testSubmitPrWithTaskAndMonitoring start");
		
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
		prConsFct.getConsumer().monitorPlanningRequestsRegister(prSub, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier id,
					UpdateHeaderList updHdrs, ObjectIdList objIds,
					PlanningRequestStatusDetailsList prStats, Map qosProps) {
				System.out.println("pr notify: " + msgHeader + " ; " + id + " ; " + updHdrs + " ; " + objIds + " ; "
					+ prStats + " ; " + qosProps);
				prStatDets[0] = prStats;
			}

			@SuppressWarnings("rawtypes")
			public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				System.out.println("pr notify err: " + msgHeader + " ; " + error + " ; " + qosProps);
			}

			@SuppressWarnings("rawtypes")
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
					throws MALException {
				System.out.println("pr other notify: " + msgHeader + " ; " + body + " ; " + qosProps);
			}
		});
		
		String taskSubId = "taskSubId";
		Subscription taskSub = new Subscription();
		taskSub.setSubscriptionId(new Identifier(taskSubId));
		taskSub.setEntities(entityReqs);
		prConsFct.getConsumer().monitorTasksRegister(taskSub, new PlanningRequestAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier id, UpdateHeaderList updHdrs,
					ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
				System.out.println("task notify: " + msgHeader + " ; " + id + " ; " + updHdrs + " ; " + objIds + " ; "
					+ taskStats + " ; " + qosProps);
				taskStatDets[0] = taskStats;
			}

			@SuppressWarnings("rawtypes")
			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				System.out.println("task notify err: " + msgHeader + " ; " + error + " ; " + qosProps);
			}

			@SuppressWarnings("rawtypes")
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
					throws MALException {
				System.out.println("task other notify: " + msgHeader + " ; " + body + " ; " + qosProps);
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
		
		prConsFct.getConsumer().submitPlanningRequest(prDefId, prInstId, prInst);
		
		sleep(2000); // give broker a sec to respond
		
		assertNotNull(prStatDets[0]);
		assertEquals(1, prStatDets[0].size());
		assertNotNull(prStatDets[0].get(0));
		
		assertNotNull(taskStatDets[0]);
		assertEquals(1, taskStatDets[0].size());
		assertNotNull(taskStatDets[0].get(0));
		
		System.out.println(prStatDets[0].get(0) + " ; " + taskStatDets[0].get(0));
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(taskSubId));
		prConsFct.getConsumer().monitorTasksDeregister(subIds);
		
		IdentifierList subIds2 = new IdentifierList();
		subIds2.add(new Identifier(prSubId));
		prConsFct.getConsumer().monitorPlanningRequestsDeregister(subIds2);
		
		System.out.println("testSubmitPrWithTaskAndMonitoring end");
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
		System.out.println("testGetPrStatus start");
		LongList prInstIds = new LongList();
		prInstIds.add(1L);
		PlanningRequestStatusDetailsList prStats = prConsFct.getConsumer().getPlanningRequestStatus(prInstIds);
		assertNotNull(prStats);
		System.out.println("testGetPrStatus end");
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
		prConsFct.getConsumer().monitorPlanningRequestsRegister(sub, new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				System.out.println("mon pr reg ack");
				regs[0] = true;
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				System.out.println("mon pr reg err");
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				System.out.println("mon pr de-reg ack");
				regs[1] = true;
			}
			@SuppressWarnings("rawtypes")
			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
					UpdateHeaderList _UpdateHeaderList1, ObjectIdList _ObjectIdList2,
					TaskStatusDetailsList _TaskStatusDetailsList3, Map qosProperties)
			{
				System.out.println("mon pr notify");
			}
			@SuppressWarnings("rawtypes")
			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				System.out.println("mon pr notify err");
			}
			@SuppressWarnings("rawtypes")
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body,
					Map qosProperties) throws org.ccsds.moims.mo.mal.MALException
			{
				System.out.println("mon pr notify other");
			}
		});
	}

	@Ignore("register ack response never arrives")
	@Test
	public void testMonitorPlanningRequestsRegister() throws MALException, MALInteractionException {
		System.out.println("testMonitorPrReg start");
		String subId = "subId";
		final Boolean[] regs = { false, false };
		registerPrMonitor(subId, regs);
		sleep(1000); // give broker a second to fire callback
		assertTrue(regs[0]);
		System.out.println("testMonitorPrReg end");
	}

//	@Test
//	public void testAsyncMonitorPlanningRequestsRegister() {
//		fail("Not yet implemented");

	@Ignore("de-register ack response never arrives")
	@Test
	public void testMonitorPlanningRequestsDeregister() throws MALException, MALInteractionException {
		System.out.println("testMonitorPRDereg start");
		String subId = "subId2";
		final Boolean[] regs = { false, false };
		registerPrMonitor(subId, regs);
		sleep(1000); // wait a sec before de-registering
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier(subId));
		prConsFct.getConsumer().monitorPlanningRequestsDeregister(subIdList);
		sleep(1000); // give broker a sec to fire callback
		assertTrue(regs[1]);
		System.out.println("testMonitorPrDereg end");
	}

//	@Test
//	public void testAsyncMonitorPlanningRequestsDeregister() {
//		fail("Not yet implemented");
//	}

	private LongList listPrDefs(String id) throws MALException, MALInteractionException {
		IdentifierList ids = new IdentifierList();
		ids.add(new Identifier(id));
		return prConsFct.getConsumer().listDefinition(ids);
	}
	
	@Test
	public void testListDefinition() throws MALException, MALInteractionException {
		System.out.println("testListPrDefs start");
		LongList ids = listPrDefs("*");
		assertNotNull(ids);
		assertEquals(0, ids.size());
		System.out.println("testListPrDefs end");
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
		LongList prDefIds = prConsFct.getConsumer().addDefinition(prDefs);
		return new AbstractMap.SimpleEntry<LongList, PlanningRequestDefinitionDetailsList>(prDefIds, prDefs);
	}
	
	@Test
	public void testAddDefinition() throws MALException, MALInteractionException {
		System.out.println("testAddPrDef start");
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		assertNotNull(e.getKey());
		assertEquals(1, e.getKey().size());
		assertNotNull(e.getKey().get(0));
		// added pr id is listed
		LongList ids = listPrDefs("*");
		assertTrue(ids.contains(e.getKey().get(0)));
		System.out.println("testAddPrDef end");
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
		System.out.println("testUpdatePrDef start");
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		e.getValue().get(0).setDescription("updated desc");
		prConsFct.getConsumer().updateDefinition(e.getKey(), e.getValue());
		// updated pr id is still listed, but verify description
		LongList ids = listPrDefs("*");
		assertTrue(ids.contains(e.getKey().get(0)));
		System.out.println("testUpdatePrDef end");
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
		System.out.println("testRemovePrDef start");
		Map.Entry<LongList, PlanningRequestDefinitionDetailsList> e = addPrDef();
		prConsFct.getConsumer().removeDefinition(e.getKey());
		// removed pr id is not listed anymore
		LongList ids = listPrDefs("*");
		assertFalse(ids.contains(e.getKey().get(0)));
		System.out.println("testRemovePrDef start");
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
		System.out.println("testGetTaskStatus start");
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		TaskStatusDetailsList taskStats = prConsFct.getConsumer().getTaskStatus(taskInstIds);
		assertNotNull(taskStats);
		System.out.println("testGetTaskStatus end");
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
		System.out.println("testSetTaskStatus start");
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
		TaskStatusDetails taskStat = new TaskStatusDetails();
		taskStat.setTaskInstName(new Identifier("id")); // mandatory
		taskStats.add(taskStat);
		prConsFct.getConsumer().setTaskStatus(taskInstIds, taskStats);
		System.out.println("testSetTaskStatus end");
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
		prConsFct.getConsumer().monitorTasksRegister(sub, new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				System.out.println("mon task reg ack");
				regs[0] = true;
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				System.out.println("mon task reg err");
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				System.out.println("mon task de-reg ack");
				regs[1] = true;
			}
			@SuppressWarnings("rawtypes")
			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
					UpdateHeaderList _UpdateHeaderList1, ObjectIdList _ObjectIdList2,
					TaskStatusDetailsList _TaskStatusDetailsList3, Map qosProperties)
			{
				System.out.println("mon task notify");
			}
			@SuppressWarnings("rawtypes")
			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				System.out.println("mon task notify err");
			}
			@SuppressWarnings("rawtypes")
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body,
					Map qosProperties) throws org.ccsds.moims.mo.mal.MALException
			{
				System.out.println("mon pr notify other");
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
		System.out.println("testMonitorTasksRegister start");
		String subId = "subId";
		final Boolean[] regs = { false, false };
		registerTaskMonitor(subId, regs);
		sleep(1000); // give broker a second to fire callback
		assertTrue(regs[0]);
		System.out.println("testMonitorTasksRegister end");
	}

//	@Test
//	public void testAsyncMonitorTasksRegister() {
//		fail("Not yet implemented");
//	}
	
	@Ignore("de-register ack response never arrives")
	@Test
	public void testMonitorTasksDeregister() throws MALException, MALInteractionException {
		System.out.println("testMonitorTasksDeregister start");
		String subId = "subId2";
		final Boolean[] regs = { false, false };
		registerTaskMonitor(subId, regs);
		sleep(1000); // wait a sec before de-registering
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier(subId));
		prConsFct.getConsumer().monitorTasksDeregister(subIdList);
		sleep(1000); // give broker a sec to fire callback
		assertTrue(regs[1]);
		System.out.println("testMonitorTasksDeregister end");
	}

//	@Test
//	public void testAsyncMonitorTasksDeregister() {
//		fail("Not yet implemented");
//	}

	private LongList listTaskDefs(String f) throws MALException, MALInteractionException {
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier(f));
		return prConsFct.getConsumer().listTaskDefinition(idList);
	}
	
	@Test
	public void testListTaskDefinition() throws MALException, MALInteractionException {
		System.out.println("testListTaskDefs start");
		LongList taskDefIdList = listTaskDefs("*");
		assertNotNull(taskDefIdList);
		assertEquals(0, taskDefIdList.size());
		System.out.println("testListTaskDefs end");
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
		LongList taskDefIdList = prConsFct.getConsumer().addTaskDefinition(taskDefList);
		return new AbstractMap.SimpleEntry<LongList, TaskDefinitionDetailsList>(taskDefIdList, taskDefList);
	}
	
	@Test
	public void testAddTaskDefinition() throws MALException, MALInteractionException {
		System.out.println("testAddTaskDef start");
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
		System.out.println("testAddTaskDef end");
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
		System.out.println("testUpdateTaskDef start");
		Map.Entry<LongList, TaskDefinitionDetailsList> e = addTaskDef();
		e.getValue().get(0).setDescription("whoa");
		prConsFct.getConsumer().updateTaskDefinition(e.getKey(), e.getValue());
		// list() returns id - unable to verify description
		LongList taskDefIdList = listTaskDefs("*");
		// added id is still listed
		assertTrue(taskDefIdList.contains(e.getKey().get(0)));
		System.out.println("testUpdateTaskDef end");
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
		System.out.println("testRemoveTaskDef start");
		Map.Entry<LongList, TaskDefinitionDetailsList> e = addTaskDef();
		prConsFct.getConsumer().removeTaskDefinition(e.getKey());
		LongList taskDefIdList = listTaskDefs("*");
		// added id is not listed anymore
		assertFalse(taskDefIdList.contains(e.getKey().get(0)));
		System.out.println("testRemoveTaskDef end");
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
