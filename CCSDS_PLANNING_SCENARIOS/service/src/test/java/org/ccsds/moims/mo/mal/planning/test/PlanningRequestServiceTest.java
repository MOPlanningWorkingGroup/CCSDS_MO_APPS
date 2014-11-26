package org.ccsds.moims.mo.mal.planning.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatus;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;
import org.ccsds.moims.mo.planning.planningrequest.structures.StatusTag;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionList;
import org.ccsds.moims.mo.planningcom.structures.ArgumentDefinitionList;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumer;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumerAdapter;
import org.ccsds.moims.mo.mal.planning.provider.PlanningRequestServiceProvider;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnit integration tests with consumer and provider.
 * 
 * @author krikse
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/applicationPlanningContext.xml")
public class PlanningRequestServiceTest {

	public static final Logger LOGGER = Logger
			.getLogger(PlanningRequestServiceTest.class.getName());

	private Long prDefId;

	@Autowired
	private PlanningRequestServiceProvider planningRequestServiceProvider;

	@Autowired
	private PlanningRequestServiceConsumer planningRequestConsumer;

	@Autowired
	private PlanningRequestServiceConsumer planningRequestConsumer2;
	
	private long getPrDefId() throws MALInteractionException, MALException {
		if (prDefId == null) {
			PlanningRequestDefinition prDef = new PlanningRequestDefinition();
			prDef.setName("test5");
			prDef.setDescription("description");
			prDefId = planningRequestConsumer.getPlanningRequestService()
					.addDefinition(prDef);
		}
		return prDefId;
	}

	@Before
	public void testSetup() throws IOException, MALInteractionException,
			MALException {
		PlanningRequestServiceUtil.subscribeConsumer(planningRequestConsumer,
				"SUB", 0L);
		PlanningRequestServiceUtil.subscribeConsumer(planningRequestConsumer2,
				"SUBLISTENER", 0L);
	}

	@After
	public void testCleanup() throws MALException, MALInteractionException {
		PlanningRequestServiceUtil.unsubscribeConsumer(planningRequestConsumer,
				"SUB");
		PlanningRequestServiceUtil.unsubscribeConsumer(
				planningRequestConsumer2, "SUBLISTENER");
	}

	@Test
	public void submitPlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test6");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService()
				.addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		planningRequestConsumer.getPlanningRequestService().add(defId, pr);
		assertTrue(true);
	}

	@Test
	public void getPlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test5");
		prDef.setDescription("description");
		PlanningRequest pr = new PlanningRequest();
		Long planningRequestId = planningRequestConsumer
				.getPlanningRequestService().add(getPrDefId(), pr);
		pr = planningRequestConsumer.getPlanningRequestService().get(
				planningRequestId);
		assertTrue(pr != null);
	}

	@Test
	public void updatePlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test4");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService()
				.addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		Long planningRequestId = planningRequestConsumer
				.getPlanningRequestService().add(defId, pr);
		pr = new PlanningRequest();
		planningRequestConsumer.getPlanningRequestService().update(
				planningRequestId, pr);
		pr = planningRequestConsumer.getPlanningRequestService().get(
				planningRequestId);
		assertTrue(pr != null);
	}

	@Test
	public void removePlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test6");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService()
				.addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		Long id = planningRequestConsumer.getPlanningRequestService().add(
				defId, pr);
		planningRequestConsumer.getPlanningRequestService().remove(id);
		pr = planningRequestConsumer.getPlanningRequestService().get(id);
		assertTrue(pr == null);
	}

	@Test
	public void getPlanningRequestList() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test3");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService()
				.addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		planningRequestConsumer.getPlanningRequestService().add(defId, pr);
		PlanningRequestList list = planningRequestConsumer
				.getPlanningRequestService().list(new PlanningRequestFilter());
		assertTrue(list.size() > 0);
	}

	@Test
	public void testAsynchronously() throws MALInteractionException,
			MALException, InterruptedException, IOException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test2");
		prDef.setDescription("description");
		prDef.setValidateOnSubmit(false);
		prDef.setArguments(new ArgumentDefinitionList());
		Long defId = planningRequestConsumer.getPlanningRequestService()
				.addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestServiceTestAdapter testAdapter = new PlanningRequestServiceTestAdapter(
				"SUBASYNC");
		planningRequestConsumer.getPlanningRequestService().asyncAdd(defId, pr,
				testAdapter);
		Thread.sleep(300);

		planningRequestConsumer.getPlanningRequestService().asyncUpdate(
				testAdapter.getPlanningRequestId(), pr, testAdapter);
		Thread.sleep(300);
		planningRequestConsumer.getPlanningRequestService().asyncRemove(
				testAdapter.getPlanningRequestId(), testAdapter);
		assertTrue(testAdapter.counter > 0);
	}

	// TODO @Test
	public void testConsumers() throws IOException, MALInteractionException,
			MALException, InterruptedException {
		PlanningRequestServiceConsumerAdapter listener1 = null;
		PlanningRequestServiceConsumerAdapter listener2 = null;
		PlanningRequestServiceConsumer consumer2 = null;
		PlanningRequestServiceConsumer consumer3 = null;
		try {
			PlanningRequestDefinition prDef = new PlanningRequestDefinition();
			prDef.setName("test");
			prDef.setDescription("description");
			Long defId = planningRequestConsumer.getPlanningRequestService()
					.addDefinition(prDef);
			PlanningRequest pr = new PlanningRequest();
			Long id = planningRequestConsumer.getPlanningRequestService().add(
					defId, pr);
			// set up consumer
			consumer2 = new PlanningRequestServiceConsumer("SUBTest1");
			consumer2.setPropertyFile("/demoConsumer.properties");
			consumer2.setBroker(planningRequestServiceProvider.getBrokerUri());
			consumer2.setUri(planningRequestServiceProvider.getUri());
			consumer2.start();
			listener1 = PlanningRequestServiceUtil.subscribeConsumer(consumer2,
					"SUBTest1", id);

			// set up consumer
			consumer3 = new PlanningRequestServiceConsumer("SUBTest2");
			consumer3.setPropertyFile("/demoConsumer.properties");
			consumer3.setBroker(planningRequestServiceProvider.getBrokerUri());
			consumer3.setUri(planningRequestServiceProvider.getUri());
			consumer3.start();
			listener2 = PlanningRequestServiceUtil.subscribeConsumer(consumer3,
					"SUBTest2", 12L);
			planningRequestConsumer.getPlanningRequestService().update(id, pr);
			Thread.sleep(400);
			planningRequestConsumer.getPlanningRequestService().remove(id);
			Thread.sleep(400);
		} finally {
			PlanningRequestServiceUtil.unsubscribeConsumer(consumer2,
					"SUBTest1");
			PlanningRequestServiceUtil.unsubscribeConsumer(consumer3,
					"SUBTest2");
		}
		assertTrue(listener1.getMonitorNotifyReceivedCounter() > 0);
		assertTrue(listener2.getMonitorNotifyReceivedCounter() == 0);
	}

	@Test
	public void testListTaskDefinition() throws MALInteractionException,
			MALException {
		TaskDefinition def = new TaskDefinition();
		def.setName("test");
		def.setDescription("description");
		def.setPlanningRequestDefinitionId(getPrDefId());
		ArgumentDefinitionList arguments = new ArgumentDefinitionList();
		def.setArguments(arguments);
		planningRequestConsumer.getPlanningRequestService().addTaskDefinition(
				def);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		identifierList.add(new Identifier("test2"));
		LongList list = planningRequestConsumer.getPlanningRequestService()
				.listTaskDefinition(identifierList);
		assertTrue(list.size() > 0);
	}

	@Test
	public void testAddTaskDefinition() throws MALInteractionException,
			MALException {
		TaskDefinition def = new TaskDefinition();
		def.setName("test2");
		def.setDescription("description2");
		def.setPlanningRequestDefinitionId(getPrDefId());
		planningRequestConsumer.getPlanningRequestService().addTaskDefinition(
				def);
		ArgumentDefinitionList arguments = new ArgumentDefinitionList();
		def.setArguments(arguments);
		Long id = planningRequestConsumer.getPlanningRequestService()
				.addTaskDefinition(def);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		identifierList.add(new Identifier("test2"));
		LongList list = planningRequestConsumer.getPlanningRequestService()
				.listTaskDefinition(identifierList);
		assertTrue(id > 0);
	}

	@Test
	public void testUpdateTaskDefinition() throws MALInteractionException,
			MALException {
		TaskDefinition def = new TaskDefinition();
		def.setName("test");
		def.setDescription("description");
		def.setPlanningRequestDefinitionId(getPrDefId());
		ArgumentDefinitionList arguments = new ArgumentDefinitionList();
		def.setArguments(arguments);
		Long id = planningRequestConsumer.getPlanningRequestService()
				.addTaskDefinition(def);
		def = new TaskDefinition();
		def.setName("test2update");
		def.setPlanningRequestDefinitionId(getPrDefId());
		def.setDescription("description2update");
		planningRequestConsumer.getPlanningRequestService()
				.updateTaskDefinition(id, def);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test2update"));
		LongList list = planningRequestConsumer.getPlanningRequestService()
				.listTaskDefinition(identifierList);
		assertTrue(list.size() > 0);
	}

	@Test
	public void testRemoveTaskDefinition() throws MALInteractionException,
			MALException {
		TaskDefinition def = new TaskDefinition();
		def.setName("test");
		def.setDescription("description");
		def.setPlanningRequestDefinitionId(getPrDefId());
		ArgumentDefinitionList arguments = new ArgumentDefinitionList();
		def.setArguments(arguments);
		Long id = planningRequestConsumer.getPlanningRequestService()
				.addTaskDefinition(def);
		planningRequestConsumer.getPlanningRequestService()
				.removeTaskDefinition(id);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test2update"));
		LongList list = planningRequestConsumer.getPlanningRequestService()
				.listTaskDefinition(identifierList);
		boolean exists = false;
		Iterator<Long> it = list.iterator();
		while (it.hasNext()) {
			Long _id = it.next();
			if (_id.equals(id)) {
				exists = true;
			}
		}
		assertTrue(!exists);
	}

}
