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
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
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
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
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
	
//	private BaseComServer comArcProv;
	private ComArchiveProviderFactory caProvFct;
//	private static boolean inited = false;
	
//	@Autowired
	private PlanningRequestProviderFactory provFct;
	
//	@Autowired
	private PlanningRequestConsumerFactory consFct;
	
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
		
		provFct = new PlanningRequestProviderFactory();
		provFct.setPropertyFile(props);
		provFct.start();
		
//		IdentifierList comDomain = new IdentifierList();
//		comDomain.add(new Identifier("test"));
//		Identifier comNet = new Identifier("junit");
//		comArcProv = new BaseComServer(comDomain, comNet) {
//		};
//		String comName = "comArc";
//		String protocol = "rmi";
//		comArcProv.init(comName, protocol);
//		comArcProv.start();
		caProvFct = new ComArchiveProviderFactory();
		caProvFct.setPropertyFile(props);
		caProvFct.setSharedBrokerUri(provFct.getBrokerUri()); // use broker from PR provider
		caProvFct.start();

		consFct = new PlanningRequestConsumerFactory();
		consFct.setPropertyFile(props);
		consFct.setProviderUri(provFct.getProviderUri());
		consFct.setBrokerUri(provFct.getBrokerUri());
		consFct.start();

		caConsFct = new ComArchiveConsumerFactory();
		caConsFct.setPropertyFile(props);
		caConsFct.setProviderUri(caProvFct.getProviderUri());
//		caConsFct.setBrokerUri(caProvFct.getBrokerUri());
		caConsFct.setBrokerUri(provFct.getBrokerUri()); // use broker from PR provider
		caConsFct.start();
		
//		IdentifierList domain = new IdentifierList();
//		domain.add(new Identifier("some"));
//		Identifier networkZone = new Identifier("test");
//		SessionType sessionType = SessionType.LIVE;
//		Identifier sessionName = new Identifier("desd");
		
//		taskPublisher = prov.createMonitorTasksPublisher(domain, networkZone, sessionType, sessionName,
//				QoSLevel.ASSURED, System.getProperties(), priority);
//		
//		EntityKeyList keys = new EntityKeyList();
//		keys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
//		
//		taskPublisher.register(keys, new MALPublishInteractionListener() {
//		});
//		
//		publisher = prov.createMonitorPlanningRequestsPublisher(domain, networkZone, sessionType, sessionName,
//				QoSLevel.ASSURED, System.getProperties(), priority);
//		
//		publisher.register(keys, new MALPublishInteractionListener() {
//			
//			public void publishRegisterErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties)
//					throws MALException {
//				System.out.println("reg.err");
//			}
//			
//			public void publishRegisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
//				System.out.println("pub.reg.ack");
//			}
//			
//			public void publishErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {
//				System.out.println("pub.err");
//			}
//			
//			public void publishDeregisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
//				System.out.println("pub.dereg");
//			}
//		});
		System.out.println("setup end");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("teardown start");
		if (consFct != null) {
			consFct.stop();
		}
		consFct = null;
		
//		if (comArcProv != null) {
//			comArcProv.stop();
//		}
//		comArcProv = null;
		if (caProvFct != null) {
			caProvFct.stop();
		}
		caProvFct = null;
		
		if (provFct != null) {
			provFct.stop();
		}
		provFct = null;
		System.out.println("teardown end");
	}

	@Test
	public void testGetConsumer() {
		assertNotNull(consFct.getConsumer());
	}

	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		System.out.println("testSubmitPR start");
		
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier("id")); // mandatory - encoding exception if missing/null
		
		PlanningRequestDefinitionDetailsList prDefList = new PlanningRequestDefinitionDetailsList();
		prDefList.add(prDef);
		
		LongList prDefIdList = consFct.getConsumer().addDefinition(prDefList);
		Long prDefId = prDefIdList.get(0);
		
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setName(prDef.getName());
		
		Boolean returnObjIds = new Boolean(true);
		ObjectType objType = new ObjectType(PlanningHelper.PLANNING_AREA_NUMBER,
				PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NUMBER, PlanningHelper.PLANNING_AREA_VERSION,
				new UShort(0));
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("test"));
		
//		Identifier network = new Identifier("junit");
//		FineTime timestamp = new FineTime(System.currentTimeMillis());
//		ArchiveDetails arcDetails = new ArchiveDetails();
//		arcDetails.setInstId(new Long(1L)); // mandatory
		
		ArchiveDetailsList objDetails = new ArchiveDetailsList();
		objDetails.add(null);
		
		PlanningRequestInstanceDetailsList objBodies = new PlanningRequestInstanceDetailsList();
		objBodies.add(prInst);
		
		LongList prInstIds = caConsFct.getConsumer().store(returnObjIds, objType, domain, objDetails, objBodies);
		Long prInstId = prInstIds.get(0);
		
		consFct.getConsumer().submitPlanningRequest(prDefId, prInstId, prInst);
		
		System.out.println("testSubmitPR end");
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
		PlanningRequestStatusDetailsList prStats = consFct.getConsumer().getPlanningRequestStatus(prInstIds);
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
		consFct.getConsumer().monitorPlanningRequestsRegister(sub, new PlanningRequestAdapter() {
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
		consFct.getConsumer().monitorPlanningRequestsDeregister(subIdList);
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
		return consFct.getConsumer().listDefinition(ids);
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
		LongList prDefIds = consFct.getConsumer().addDefinition(prDefs);
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
		consFct.getConsumer().updateDefinition(e.getKey(), e.getValue());
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
		consFct.getConsumer().removeDefinition(e.getKey());
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
		TaskStatusDetailsList taskStats = consFct.getConsumer().getTaskStatus(taskInstIds);
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
		consFct.getConsumer().setTaskStatus(taskInstIds, taskStats);
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
		consFct.getConsumer().monitorTasksRegister(sub, new PlanningRequestAdapter() {
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
		consFct.getConsumer().monitorTasksDeregister(subIdList);
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
		return consFct.getConsumer().listTaskDefinition(idList);
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
		LongList taskDefIdList = consFct.getConsumer().addTaskDefinition(taskDefList);
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
//	public void testAddTaskDefinitionWithMonitor() throws MALException, MALInteractionException {
//		System.out.println("testAddTaskDefWithMonitor start");
//		Identifier subId = new Identifier("testSubId");
//		EntityRequestList entReqList = new EntityRequestList();
//		EntityKeyList entKeyList = new EntityKeyList();
//		entKeyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
//		entReqList.add(new EntityRequest(null, true, true, true, false, entKeyList));
//		Subscription sub = new Subscription(subId, entReqList);
//		
//		final Object[] notified = { null, null, null, null, null };
//		
//		consFct.getConsumer().monitorTasksRegister(sub, new PlanningRequestAdapter() {
//			@Override
//			public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
//			{
//				System.out.println("task mon req ack: " + msgHeader + " ; " + qosProperties);
//			}
//			@Override
//			public void monitorTasksRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
//					Map qosProperties)
//			{
//				System.out.println("task mon req err: " + msgHeader + " ; " + error + " ; " + qosProperties);
//			}
//			@Override
//			public void monitorTasksDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
//			{
//				System.out.println("task mon de-reg ack: " + msgHeader + " ; " + qosProperties);
//			}
//			@Override
//			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
//					UpdateHeaderList updHdrList, ObjectIdList objIdList,
//					TaskStatusDetailsList taskStatList, Map qosProps)
//			{
//				System.out.println("task mon notify: " + msgHeader + " ; " + subId + " ; " + updHdrList
//						+ " ; " + objIdList + " ; " + taskStatList + " ; " + qosProps);
//				notified[0] = msgHeader;
//				notified[1] = subId;
//				notified[2] = updHdrList;
//				notified[3] = objIdList;
//				notified[4] = taskStatList;
//			}
//			@Override
//			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
//					Map qosProperties)
//			{
//				System.out.println("task mon notify err: " + msgHeader + " ; " + error + " ; " + qosProperties);
//			}
//			@Override
//			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body,
//					Map qosProperties) throws MALException
//			{
//				System.out.println("task mon notify from other: " + msgHeader + " ; " + body + " ; " + qosProperties);
//			}
//		});
//		testAddTaskDefinition();
//		
//		try { // notify is delayed - may need to sleep even more
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//		}
//		// subscription id matches
//		assertNotNull(notified[1]);
//		assertEquals(subId.getValue(), ((Identifier)notified[1]).getValue());
//		// updates list has at least one element
//		assertNotNull(notified[2]);
//		assertEquals(1, ((UpdateHeaderList)notified[2]).size());
//		assertNotNull(((UpdateHeaderList)notified[2]).get(0));
//		assertEquals(UpdateType.CREATION, ((UpdateHeaderList)notified[2]).get(0).getUpdateType());
//		// id list has at least one element
//		assertNotNull(notified[3]);
//		assertEquals(1, ((ObjectIdList)notified[3]).size());
//		assertNotNull(((ObjectIdList)notified[3]).get(0));
//		
//		IdentifierList subIdList = new IdentifierList();
//		subIdList.add(subId);
//		consFct.getConsumer().monitorTasksDeregister(subIdList);
//		System.out.println("testAddTaskDefWithMonitor end");
//	}

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
		consFct.getConsumer().updateTaskDefinition(e.getKey(), e.getValue());
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
		consFct.getConsumer().removeTaskDefinition(e.getKey());
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
