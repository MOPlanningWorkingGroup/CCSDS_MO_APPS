package org.ccsds.moims.mo.mal.planning.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumer;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumerAdapter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestArgumentDefinitionList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestGroup;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatus;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinitionList;
import org.ccsds.moims.mo.mal.planning.provider.PlanningRequestServiceProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * JUnit integration tests with consumer and provider.
 * 
 * @author krikse
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/applicationContext.xml")
public class PlanningRequestServiceTest {

	public static final Logger LOGGER = Logger
			.getLogger(PlanningRequestServiceTest.class.getName());
	
	@Autowired
	private PlanningRequestServiceProvider planningRequestServiceProvider;
	
	@Autowired
	private PlanningRequestServiceConsumer planningRequestConsumer;
	
	@Autowired
	private PlanningRequestServiceConsumer planningRequestConsumer2;

	@Before
	public void testSetup() throws IOException, MALInteractionException,
			MALException {
		PlanningRequestServiceUtil.subscribeConsumer(planningRequestConsumer, "SUB", 0L);
		PlanningRequestServiceUtil.subscribeConsumer(planningRequestConsumer2, "SUBLISTENER", 0L);
	}

	@After
	public void testCleanup() throws MALException,
			MALInteractionException {
		PlanningRequestServiceUtil.unsubscribeConsumer(planningRequestConsumer, "SUB");
		PlanningRequestServiceUtil.unsubscribeConsumer(planningRequestConsumer2, "SUBLISTENER");
	}

	@Test
	public void submitPlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test6");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		planningRequestConsumer.getPlanningRequestService().submitPlanningRequest(group, defId, pr);
		assertTrue(true);
	}

	@Test
	public void getPlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test5");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		Long planningRequestId = planningRequestConsumer.getPlanningRequestService()
				.submitPlanningRequest(group, defId, pr);
		pr = planningRequestConsumer.getPlanningRequestService().getPlanningRequest(
				planningRequestId);
		assertTrue(pr != null && pr.getPlanningRequestStatus().getState() == StateEnum.SUBMITTED);
	}

	@Test
	public void updatePlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test4");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		Long planningRequestId = planningRequestConsumer.getPlanningRequestService().submitPlanningRequest(group, defId, pr);
		pr = new PlanningRequest();
		PlanningRequestStatus status = new PlanningRequestStatus();
		status.setState(StateEnum.SCHEDULED);
		pr.setPlanningRequestStatus(status);
		planningRequestConsumer.getPlanningRequestService().updatePlanningRequest(
				planningRequestId, pr);
		pr = planningRequestConsumer.getPlanningRequestService().getPlanningRequest(
				planningRequestId);
		assertTrue(pr != null && pr.getPlanningRequestStatus().getState() == StateEnum.SCHEDULED);
	}

	@Test
	public void removePlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test6");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		Long id = planningRequestConsumer.getPlanningRequestService().submitPlanningRequest(group, defId, pr);
		planningRequestConsumer.getPlanningRequestService().removePlanningRequest(
				id);
		pr = planningRequestConsumer.getPlanningRequestService()
				.getPlanningRequest(id);
		assertTrue(pr == null);
	}

	@Test
	public void getPlanningRequestStatus() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test4");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		Long planningRequestId = planningRequestConsumer.getPlanningRequestService().submitPlanningRequest(group, defId, pr);
		StateEnum status = planningRequestConsumer.getPlanningRequestService()
				.getPlanningRequestStatus(planningRequestId);
		assertTrue(status == StateEnum.SUBMITTED);
	}

	@Test
	public void getPlanningRequestList() throws MALInteractionException,
			MALException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test3");
		prDef.setDescription("description");
		Long defId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		planningRequestConsumer.getPlanningRequestService().submitPlanningRequest(group, defId, pr);
		PlanningRequestList list = planningRequestConsumer.getPlanningRequestService()
				.getPlanningRequestList(new PlanningRequestFilter());
		assertTrue(list.size() > 0);
	}

	@Test
	public void testAsynchronously() throws MALInteractionException, MALException,
			InterruptedException, IOException {
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("test2");
		prDef.setDescription("description");
		prDef.setAllowedTaskTypes(new TaskDefinitionList());
		prDef.setArguments(new PlanningRequestArgumentDefinitionList());
		Long defId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		PlanningRequestServiceTestAdapter testAdapter = new PlanningRequestServiceTestAdapter("SUBASYNC");
		planningRequestConsumer.getPlanningRequestService().asyncSubmitPlanningRequest(group, defId,
				pr, testAdapter);
		Thread.sleep(300);
		PlanningRequestStatus status = new PlanningRequestStatus();
		status.setState(StateEnum.ACCEPTED);
		pr.setPlanningRequestStatus(status);
		planningRequestConsumer.getPlanningRequestService().asyncUpdatePlanningRequest(
				testAdapter.getPlanningRequestId(), pr, testAdapter);
		Thread.sleep(300);
		planningRequestConsumer.getPlanningRequestService().asyncRemovePlanningRequest(
				testAdapter.getPlanningRequestId(), testAdapter);
		assertTrue(testAdapter.counter > 0);
	}

	@Test
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
			Long defId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
			PlanningRequest pr = new PlanningRequest();
			PlanningRequestGroup group = new PlanningRequestGroup();
			Long id = planningRequestConsumer.getPlanningRequestService().submitPlanningRequest(group, defId, pr);
			// set up consumer
			consumer2 = new PlanningRequestServiceConsumer("SUBTest1");
			consumer2.setPropertyFile("/demoConsumer.properties");
			consumer2.setBroker(planningRequestServiceProvider.getBrokerUri());
			consumer2.setUri(planningRequestServiceProvider.getUri());
			consumer2.start();
			listener1 = PlanningRequestServiceUtil.subscribeConsumer(consumer2, "SUBTest1", id);
	
			// set up consumer
			consumer3 = new PlanningRequestServiceConsumer("SUBTest2");
			consumer3.setPropertyFile("/demoConsumer.properties");
			consumer3.setBroker(planningRequestServiceProvider.getBrokerUri());
			consumer3.setUri(planningRequestServiceProvider.getUri());
			consumer3.start();
			listener2 = PlanningRequestServiceUtil.subscribeConsumer(consumer3, "SUBTest2", 12L);
			PlanningRequestStatus status = new PlanningRequestStatus();
			status.setState(StateEnum.ACCEPTED);
			pr.setPlanningRequestStatus(status);
			planningRequestConsumer.getPlanningRequestService().updatePlanningRequest(
					id, pr);
			Thread.sleep(400);
			planningRequestConsumer.getPlanningRequestService().removePlanningRequest(
					id);
			Thread.sleep(400);
		} finally {
			PlanningRequestServiceUtil.unsubscribeConsumer(consumer2, "SUBTest1");
			PlanningRequestServiceUtil.unsubscribeConsumer(consumer3, "SUBTest2");
		}
		assertTrue(listener1.getMonitorNotifyReceivedCounter() > 0);
		assertTrue(listener2.getMonitorNotifyReceivedCounter() == 0);
	}

}
