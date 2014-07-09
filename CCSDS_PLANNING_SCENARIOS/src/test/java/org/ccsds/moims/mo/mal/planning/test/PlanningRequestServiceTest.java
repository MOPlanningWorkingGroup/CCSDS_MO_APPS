package org.ccsds.moims.mo.mal.planning.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumer;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumerAdapter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestGroup;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;
import org.ccsds.moims.mo.mal.planning.provider.PlanningRequestServiceProvider;
import org.ccsds.moims.mo.mal.planning.service.PlanningRequestServiceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit integration tests with consumer and provider.
 * 
 * @author krikse
 *
 */
public class PlanningRequestServiceTest {

	public static final Logger LOGGER = Logger
			.getLogger(PlanningRequestServiceTest.class.getName());
	private static PlanningRequestServiceProvider provider = null;
	private static PlanningRequestServiceConsumer consumer = null;
	private static PlanningRequestServiceConsumer consumerListener = null;
	private Long planningRequestId;

	@BeforeClass
	public static void testSetup() throws IOException, MALInteractionException,
			MALException {
		// set up provider
		provider = new PlanningRequestServiceProvider(
				new PlanningRequestServiceImpl());
		provider.setPropertyFile("/demoProvider.properties");
		provider.start();

		// set up consumer
		consumer = new PlanningRequestServiceConsumer("SUB");
		consumer.setPropertyFile("/demoConsumer.properties");
		consumer.setBroker(provider.getBrokerUri());
		consumer.setUri(provider.getUri());
		consumer.start();
		
		// set up consumer
		consumerListener = new PlanningRequestServiceConsumer("SUBLISTENER");
		consumerListener.setPropertyFile("/demoConsumer.properties");
		consumerListener.setBroker(provider.getBrokerUri());
		consumerListener.setUri(provider.getUri());
		consumerListener.start();

		PlanningRequestServiceUtil.subscribeConsumer(consumer, "SUB", 0L);
		PlanningRequestServiceUtil.subscribeConsumer(consumerListener, "SUBLISTENER", 0L);
	}

	@AfterClass
	public static void testCleanup() throws MALException,
			MALInteractionException {
		PlanningRequestServiceUtil.unsubscribeConsumer(consumer, "SUB");
		PlanningRequestServiceUtil.unsubscribeConsumer(consumerListener, "SUBLISTENER");
		if (consumer != null) {
			consumer.stop();
		}
		if (consumerListener != null) {
			consumerListener.stop();
		}
		if (provider != null) {
			provider.stop();
		}
	}

	@Test
	public void submitPlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		consumer.getPlanningRequestService().submitPlanningRequest(group, pr);
		assertTrue(true);
	}

	@Test
	public void getPlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		planningRequestId = consumer.getPlanningRequestService()
				.submitPlanningRequest(group, pr);
		pr = consumer.getPlanningRequestService().getPlanningRequest(
				planningRequestId);
		assertTrue(pr != null && pr.getStatus() == StateEnum.SUBMITTED);
	}

	@Test
	public void updatePlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		pr.setStatus(StateEnum.ACCEPTED);
		consumer.getPlanningRequestService().updatePlanningRequest(
				planningRequestId, pr);
		pr = consumer.getPlanningRequestService().getPlanningRequest(
				planningRequestId);
		assertTrue(pr != null && pr.getStatus() == StateEnum.ACCEPTED);
	}

	@Test
	public void removePlanningRequest() throws MALInteractionException,
			MALException {
		consumer.getPlanningRequestService().removePlanningRequest(
				planningRequestId);
		PlanningRequest pr = consumer.getPlanningRequestService()
				.getPlanningRequest(planningRequestId);
		assertTrue(pr == null);
	}

	@Test
	public void getPlanningRequestStatus() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		consumer.getPlanningRequestService().submitPlanningRequest(group, pr);
		StateEnum status = consumer.getPlanningRequestService()
				.getPlanningRequestStatus(planningRequestId);
		assertTrue(status == StateEnum.ACCEPTED);
	}

	@Test
	public void getPlanningRequestList() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		consumer.getPlanningRequestService().submitPlanningRequest(group, pr);
		PlanningRequestList list = consumer.getPlanningRequestService()
				.getPlanningRequestList(new PlanningRequestFilter());
		assertTrue(list.size() > 0);
	}

	@Test
	public void AsyncTest() throws MALInteractionException, MALException,
			InterruptedException, IOException {
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		PlanningRequestServiceTestAdapter testAdapter = new PlanningRequestServiceTestAdapter("SUBASYNC");
		consumer.getPlanningRequestService().asyncSubmitPlanningRequest(group,
				pr, testAdapter);
		Thread.sleep(300);
		pr.setStatus(StateEnum.ACCEPTED);
		consumer.getPlanningRequestService().asyncUpdatePlanningRequest(
				testAdapter.getPlanningRequestId(), pr, testAdapter);
		Thread.sleep(300);
		consumer.getPlanningRequestService().asyncRemovePlanningRequest(
				testAdapter.getPlanningRequestId(), testAdapter);
		assertTrue(testAdapter.counter > 0);
	}

	@Test
	public void testConsumers() throws IOException, MALInteractionException,
			MALException, InterruptedException {
		PlanningRequest pr = new PlanningRequest();
		PlanningRequestGroup group = new PlanningRequestGroup();
		Long id = consumer.getPlanningRequestService().submitPlanningRequest(group, pr);
		// set up consumer
		PlanningRequestServiceConsumer consumer2 = new PlanningRequestServiceConsumer("SUBTest1");
		consumer2.setPropertyFile("/demoConsumer.properties");
		consumer2.setBroker(provider.getBrokerUri());
		consumer2.setUri(provider.getUri());
		consumer2.start();
		PlanningRequestServiceConsumerAdapter listener1 = PlanningRequestServiceUtil.subscribeConsumer(consumer2, "SUBTest1", id);

		// set up consumer
		PlanningRequestServiceConsumer consumer3 = new PlanningRequestServiceConsumer("SUBTest2");
		consumer3.setPropertyFile("/demoConsumer.properties");
		consumer3.setBroker(provider.getBrokerUri());
		consumer3.setUri(provider.getUri());
		consumer3.start();
		PlanningRequestServiceConsumerAdapter listener2 = PlanningRequestServiceUtil.subscribeConsumer(consumer3, "SUBTest2", 12L);
		pr.setStatus(StateEnum.ACCEPTED);
		consumer.getPlanningRequestService().updatePlanningRequest(
				id, pr);
		Thread.sleep(400);
		consumer.getPlanningRequestService().removePlanningRequest(
				id);
		Thread.sleep(400);
		PlanningRequestServiceUtil.unsubscribeConsumer(consumer2, "SUBTest1");
		PlanningRequestServiceUtil.unsubscribeConsumer(consumer3, "SUBTest2");
		assertTrue(listener1.getMonitorNotifyReceivedCounter() == 2);
		assertTrue(listener2.getMonitorNotifyReceivedCounter() == 0);
	}

}
