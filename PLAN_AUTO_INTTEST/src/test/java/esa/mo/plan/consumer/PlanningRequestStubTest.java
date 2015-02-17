package esa.mo.plan.consumer;

import static org.junit.Assert.*;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
//			
//			public void publishRegisterErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties)
//					throws MALException {
//				System.out.println("task.pub.reg.err");
//			}
//			
//			public void publishRegisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
//				System.out.println("task.pub.reg.ack");
//			}
//			
//			public void publishErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {
//				System.out.println("task.pub.err");
//			}
//			
//			public void publishDeregisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
//				System.out.println("task.dereg");
//			}
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

//	@Test
//	public void testPlanningRequestStub() {
//		prov = new PlanningRequestStub(cons);
//		assertNotNull(prov);
//	}

//	@Test
//	public void testGetConsumer() {
//		assertNotNull(prov.getConsumer());
//	}

	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		System.out.println("testSubmitPR start");
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier("id")); // mandatory - encoding exception if null
		PlanningRequestDefinitionDetailsList prDefList = new PlanningRequestDefinitionDetailsList();
		prDefList.add(prDef);
		LongList prDefIdList = consFct.getConsumer().addDefinition(prDefList);
		Long defInstId = prDefIdList.get(0);
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setName(prDef.getName());
		Long prInstId = 1L; // TODO store to COM and get id back
		consFct.getConsumer().submitPlanningRequest(defInstId, prInstId, prInst);
		System.out.println("testSubmitPR end");
	}

//	@Test
//	public void testAsyncSubmitPlanningRequest() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueSubmitPlanningRequest() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testGetPlanningRequestStatus() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncGetPlanningRequestStatus() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueGetPlanningRequestStatus() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testMonitorPlanningRequestsRegister() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncMonitorPlanningRequestsRegister() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testMonitorPlanningRequestsDeregister() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncMonitorPlanningRequestsDeregister() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testListDefinition() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncListDefinition() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueListDefinition() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testAddDefinition() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncAddDefinition() {
//		fail("Not yet implemented"); // TODO
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
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueUpdateDefinition() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testRemoveDefinition() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncRemoveDefinition() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueRemoveDefinition() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testGetTaskStatus() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncGetTaskStatus() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueGetTaskStatus() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testSetTaskStatus() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncSetTaskStatus() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueSetTaskStatus() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testMonitorTasksRegister() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncMonitorTasksRegister() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testMonitorTasksDeregister() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncMonitorTasksDeregister() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testListTaskDefinition() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncListTaskDefinition() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueListTaskDefinition() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testAddTaskDefinition() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncAddTaskDefinition() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueAddTaskDefinition() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testUpdateTaskDefinition() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncUpdateTaskDefinition() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueUpdateTaskDefinition() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testRemoveTaskDefinition() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testAsyncRemoveTaskDefinition() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testContinueRemoveTaskDefinition() {
//		fail("Not yet implemented"); // TODO
//	}

}
