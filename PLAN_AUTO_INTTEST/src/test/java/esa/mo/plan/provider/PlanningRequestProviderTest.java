package esa.mo.plan.provider;

import static org.junit.Assert.*;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorTasksPublisher;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import esa.mo.plan.provider.PlanningRequestProvider;

public class PlanningRequestProviderTest {

	private PlanningRequestProvider prov = null;

	@Before
	public void setUp() throws Exception {
		prov = new PlanningRequestProvider();
	}

	@After
	public void tearDown() throws Exception {
		prov = null;
	}

	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		PlanningRequestDefinitionDetailsList prDefList = new PlanningRequestDefinitionDetailsList();
		prDefList.add(prDef);
		LongList prIdList = prov.addDefinition(prDefList, null);
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prov.submitPlanningRequest(prIdList.get(0), null, prInst, null);
	}

	@Test
	public void testUpdatePlanningRequest() throws MALException, MALInteractionException {
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		Long prInstId = 1L;
		prov.updatePlanningRequest(prInstId, prInst, null);
	}

	@Test
	public void testRemovePlanningRequest() throws MALException, MALInteractionException {
		Long prInstId = 1L;
		prov.removePlanningRequest(prInstId, null);
	}

	@Test
	public void testGetPlanningRequestStatus() throws MALException, MALInteractionException {
		LongList prIdList = new LongList();
		prIdList.add(1L);
		PlanningRequestStatusDetailsList statsList = prov.getPlanningRequestStatus(prIdList, null);
		assertNotNull(statsList);
	}

	@Test
	public void testListDefinition() throws MALException, MALInteractionException {
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier("*"));
		LongList prDefIdsList = prov.listDefinition(idList, null);
		assertNotNull(prDefIdsList);
		assertEquals(0, prDefIdsList.size());
	}

	@Test
	public void testAddDefinition() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		PlanningRequestDefinitionDetailsList prDefsList = new PlanningRequestDefinitionDetailsList();
		prDefsList.add(prDef);
		LongList prIdList = prov.addDefinition(prDefsList, null);
		assertNotNull(prIdList);
		assertEquals(1, prIdList.size());
		assertNotNull(prIdList.get(0));
	}

	@Test
	public void testUpdateDefinition() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		PlanningRequestDefinitionDetailsList prDefsList = new PlanningRequestDefinitionDetailsList();
		prDefsList.add(prDef);
		LongList prIdList = prov.addDefinition(prDefsList, null);
		// assuming add was successful
		prDef.setDescription("changed desc");
		prov.updateDefinition(prIdList, prDefsList, null);
		
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier("*"));
		LongList prIdList2 = prov.listDefinition(idList, null);
		assertNotNull(idList);
		assertEquals(1, prIdList2.size());
	}

	@Test
	public void testRemoveDefinition() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		PlanningRequestDefinitionDetailsList prDefsList = new PlanningRequestDefinitionDetailsList();
		prDefsList.add(prDef);
		LongList prInstIdList = prov.addDefinition(prDefsList, null);
		
		prov.removeDefinition(prInstIdList, null);
		
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier("*"));
		LongList idList2 = prov.listDefinition(idList, null);
		assertNotNull(idList2);
		assertEquals(0, idList2.size());
	}

	@Test
	public void testGetTaskStatus() throws MALException, MALInteractionException {
		LongList taskIdList = new LongList();
		taskIdList.add(1L);
		TaskStatusDetailsList taskStatsList = prov.getTaskStatus(taskIdList, null);
		assertNotNull(taskStatsList);
		assertEquals(1, taskStatsList.size());
	}

	@Test
	public void testSetTaskStatus() throws MALException, MALInteractionException {
		LongList taskIdsList = new LongList();
		taskIdsList.add(1L);
		TaskStatusDetails stat = new TaskStatusDetails();
		TaskStatusDetailsList taskStatsList = new TaskStatusDetailsList();
		taskStatsList.add(stat);
		prov.setTaskStatus(taskIdsList, taskStatsList, null);
	}

	@Test
	public void testListTaskDefinition() throws MALException, MALInteractionException {
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier("*"));
		LongList taskDefIdsList = prov.listTaskDefinition(idList, null);
		assertNotNull(taskDefIdsList);
		assertEquals(0, taskDefIdsList.size());
	}

	@Test
	public void testAddTaskDefinition() throws MALException, MALInteractionException {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		TaskDefinitionDetailsList taskDefsList = new TaskDefinitionDetailsList();
		taskDefsList.add(taskDef);
		LongList taskDefIdsList = prov.addTaskDefinition(taskDefsList, null);
		assertNotNull(taskDefIdsList);
		assertEquals(1, taskDefIdsList.size());
		assertNotNull(taskDefIdsList.get(0));
	}

	private MonitorTasksPublisher createTaskPublisher() throws MALException {
		IdentifierList domain = new IdentifierList();
		Identifier network = new Identifier();
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier();
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		return prov.createMonitorTasksPublisher(domain, network, sessionType, sessionName, qos, System.getProperties(), priority);
	}
	
	// MAL needs to be initialized and set up properly for monitoring to work
	@Test
	public void testAddTaskDefinitionWithMonitor() throws MALException, MALInteractionException {
		MonitorTasksPublisher pub = createTaskPublisher();
		EntityKeyList keyList = new EntityKeyList();
		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		pub.register(keyList, prov/*new MALPublishInteractionListener() {
			
			@Override
			public void publishRegisterErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties)
					throws MALException {
				System.out.println("pub reg err");
			}
			
			@Override
			public void publishRegisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
				System.out.println("pub reg ack");
			}
			
			@Override
			public void publishErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {
				System.out.println("pub err");
			}
			
			@Override
			public void publishDeregisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
				System.out.println("pub de-reg ack");
			}
		}*/);
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		TaskDefinitionDetailsList taskDefsList = new TaskDefinitionDetailsList();
		taskDefsList.add(taskDef);
		LongList taskDefIdsList = prov.addTaskDefinition(taskDefsList, null);
		assertNotNull(taskDefIdsList);
		assertEquals(1, taskDefIdsList.size());
		assertNotNull(taskDefIdsList.get(0));
	}

	@Test
	public void testUpdateTaskDefinition() throws MALException, MALInteractionException {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		TaskDefinitionDetailsList taskDefsList = new TaskDefinitionDetailsList();
		taskDefsList.add(taskDef);
		LongList taskDefIdsList = prov.addTaskDefinition(taskDefsList, null);
		// assuming add was successful
		taskDef.setDescription("changed desc");
		prov.updateTaskDefinition(taskDefIdsList, taskDefsList, null);
		
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier("*"));
		LongList taskDefIdsList2 = prov.listTaskDefinition(idList, null);
		assertNotNull(taskDefIdsList2);
		assertEquals(1, taskDefIdsList2.size());
		assertNotNull(taskDefIdsList2.get(0));
	}

	@Test
	public void testRemoveTaskDefinition() throws MALException, MALInteractionException {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		TaskDefinitionDetailsList taskDefsList = new TaskDefinitionDetailsList();
		taskDefsList.add(taskDef);
		LongList taskDefIdsList = prov.addTaskDefinition(taskDefsList, null);
		
		prov.removeTaskDefinition(taskDefIdsList, null);
		
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier("*"));
		LongList taskDefIdsList2 = prov.listTaskDefinition(idList, null);
		assertNotNull(taskDefIdsList2);
		assertEquals(0, taskDefIdsList2.size());
	}

}
