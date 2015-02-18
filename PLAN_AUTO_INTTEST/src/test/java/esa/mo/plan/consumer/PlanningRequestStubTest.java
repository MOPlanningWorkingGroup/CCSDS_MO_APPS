package esa.mo.plan.consumer;

import static org.junit.Assert.*;

import org.ccsds.moims.mo.com.structures.ObjectIdList;
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
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import esa.mo.plan.provider.PlanningRequestProviderFactory;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath*:**/testIntContext.xml")
public class PlanningRequestStubTest {

//	@Autowired
	private PlanningRequestProviderFactory provFct;
	
//	@Autowired
	private PlanningRequestConsumerFactory consFct;
	
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
		
		consFct = new PlanningRequestConsumerFactory();
		consFct.setPropertyFile(props);
		consFct.setProviderUri(provFct.getProviderUri());
		consFct.setBrokerUri(provFct.getBrokerUri());
		consFct.start();

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
		Long prInstId = 1L; // TODO store to COM and get id back
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
	public void testGetPlanningRequestStatus() {
		fail("Not yet implemented"); // TODO
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

	@Test
	public void testMonitorPlanningRequestsRegister() throws MALException, MALInteractionException {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncMonitorPlanningRequestsRegister() {
//		fail("Not yet implemented");

	@Test
	public void testMonitorPlanningRequestsDeregister() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncMonitorPlanningRequestsDeregister() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testListDefinition() {
		fail("Not yet implemented"); // TODO
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

	@Test
	public void testAddDefinition() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncAddDefinition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContinueAddDefinition() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testUpdateDefinition() {
		fail("Not yet implemented"); // TODO
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
	public void testRemoveDefinition() {
		fail("Not yet implemented"); // TODO
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
	public void testGetTaskStatus() {
		fail("Not yet implemented"); // TODO
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
	public void testSetTaskStatus() {
		fail("Not yet implemented"); // TODO
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

	@Test
	public void testMonitorTasksRegister() throws MALException, MALInteractionException {
		System.out.println("testMonitorTasksRegister start");
		Identifier subId = new Identifier("desd");
		EntityRequestList entityList = new EntityRequestList();
		EntityKeyList keys = new EntityKeyList();
		keys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityList.add(new EntityRequest(null, true, true, true, false, keys));
		Subscription sub = new Subscription(subId, entityList);
		final boolean[] registered = { false };
		consFct.getConsumer().monitorTasksRegister(sub, new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				System.out.println("mon task reg ack");
				registered[0] = true;
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
		});
//		assertTrue(registered[0]);
		assertTrue(true);
//		IdentifierList subIdList = new IdentifierList();
//		subIdList.add(subId);
//		consFct.getConsumer().monitorTasksDeregister(subIdList);
		System.out.println("testMonitorTasksRegister end");
	}

//	@Test
//	public void testAsyncMonitorTasksRegister() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testMonitorTasksDeregister() throws MALException, MALInteractionException {
		System.out.println("testMonitorTasksDeregister start");
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier("id"));
		consFct.getConsumer().monitorTasksDeregister(subIdList);
		assertTrue(true);
		System.out.println("testMonitorTasksDeregister end");
	}

//	@Test
//	public void testAsyncMonitorTasksDeregister() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testListTaskDefinition() {
		fail("Not yet implemented");
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

	@Test
	public void testAddTaskDefinition() throws MALException, MALInteractionException {
		System.out.println("testAddTaskDef start");
		TaskDefinitionDetailsList taskDefList = new TaskDefinitionDetailsList();
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier("new task def")); // mandatory
		taskDef.setPrDefName(new Identifier("new pr def")); // mandatory
		taskDefList.add(taskDef);
		LongList taskDefIdList = consFct.getConsumer().addTaskDefinition(taskDefList);
		assertNotNull(taskDefIdList);
		assertEquals(1, taskDefIdList.size());
		assertNotNull(taskDefIdList.get(0));
		System.out.println("testAddTaskDef end");
	}
	
	@Test
	public void testAddTaskDefinitionWithMonitor() throws MALException, MALInteractionException {
		System.out.println("testAddTaskDefWithMonitor start");
		Identifier subId = new Identifier("testSubId");
		EntityRequestList entReqList = new EntityRequestList();
		EntityKeyList entKeyList = new EntityKeyList();
		entKeyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entReqList.add(new EntityRequest(null, true, true, true, false, entKeyList));
		Subscription sub = new Subscription(subId, entReqList);
		
		final Object[] notified = { null, null, null, null, null };
		
		consFct.getConsumer().monitorTasksRegister(sub, new PlanningRequestAdapter() {
			@Override
			public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				System.out.println("task mon req ack: " + msgHeader + " ; " + qosProperties);
			}
			@Override
			public void monitorTasksRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				System.out.println("task mon req err: " + msgHeader + " ; " + error + " ; " + qosProperties);
			}
			@Override
			public void monitorTasksDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
			{
				System.out.println("task mon de-reg ack: " + msgHeader + " ; " + qosProperties);
			}
			@Override
			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
					UpdateHeaderList updHdrList, ObjectIdList objIdList,
					TaskStatusDetailsList taskStatList, Map qosProps)
			{
				System.out.println("task mon notify: " + msgHeader + " ; " + subId + " ; " + updHdrList
						+ " ; " + objIdList + " ; " + taskStatList + " ; " + qosProps);
				notified[0] = msgHeader;
				notified[1] = subId;
				notified[2] = updHdrList;
				notified[3] = objIdList;
				notified[4] = taskStatList;
			}
			@Override
			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties)
			{
				System.out.println("task mon notify err: " + msgHeader + " ; " + error + " ; " + qosProperties);
			}
			@Override
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body,
					Map qosProperties) throws MALException
			{
				System.out.println("task mon notify from other: " + msgHeader + " ; " + body + " ; " + qosProperties);
			}
		});
		testAddTaskDefinition();
		
		try { // notify is delayed - may need to sleep even more
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		// subscription id matches
		assertNotNull(notified[1]);
		assertEquals(subId.getValue(), ((Identifier)notified[1]).getValue());
		// updates list has at least one element
		assertNotNull(notified[2]);
		assertEquals(1, ((UpdateHeaderList)notified[2]).size());
		assertNotNull(((UpdateHeaderList)notified[2]).get(0));
		assertEquals(UpdateType.CREATION, ((UpdateHeaderList)notified[2]).get(0).getUpdateType());
		// id list has at least one element
		assertNotNull(notified[3]);
		assertEquals(1, ((ObjectIdList)notified[3]).size());
		assertNotNull(((ObjectIdList)notified[3]).get(0));
		
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(subId);
		consFct.getConsumer().monitorTasksDeregister(subIdList);
		System.out.println("testAddTaskDefWithMonitor end");
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
	public void testUpdateTaskDefinition() {
		fail("Not yet implemented"); // TODO
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
	public void testRemoveTaskDefinition() {
		fail("Not yet implemented"); // TODO
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
