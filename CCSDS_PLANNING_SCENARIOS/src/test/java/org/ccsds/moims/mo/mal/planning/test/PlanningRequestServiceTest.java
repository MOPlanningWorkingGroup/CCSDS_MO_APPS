package org.ccsds.moims.mo.mal.planning.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumer;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumerAdapter;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequestservice.consumer.PlanningRequestServiceAdapter;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequestGroup;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.StateEnum;
import org.ccsds.moims.mo.mal.planning.provider.PlanningRequestServiceProvider;
import org.ccsds.moims.mo.mal.planning.service.PlanningRequestServiceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit integration tests with consumer and provider.
 * @author krikse
 *
 */
public class PlanningRequestServiceTest {

	public static final Logger LOGGER = Logger
			.getLogger(PlanningRequestServiceTest.class.getName());
	private static PlanningRequestServiceAdapter adapter = null;
	private static PlanningRequestServiceProvider provider = null;
	private static PlanningRequestServiceConsumer consumer = null;
	private static Long PLANNING_REQUEST_ID = 12L;

	@BeforeClass
	public static void testSetup() throws IOException, MALInteractionException, MALException {
		adapter = new PlanningRequestServiceConsumerAdapter();
		// set up provider
		provider = new PlanningRequestServiceProvider(new PlanningRequestServiceImpl());
		provider.setPropertyFile("/demoProvider.properties");
		provider.start();

		// set up consumer
		consumer = new PlanningRequestServiceConsumer();
		consumer.setPropertyFile("/demoConsumer.properties");
		consumer.setBroker(provider.getBrokerUri());
		consumer.setUri(provider.getUri());
		consumer.start();
		subscribeConsumer();
	}

	@AfterClass
	public static void testCleanup() throws MALException,
			MALInteractionException {
		unsubscribeConsumer();
		if (consumer != null) {
			consumer.stop();
		}
		if (provider != null) {
			provider.stop();
		}
	}

	@Test
	public void submitPlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		pr.setId(PLANNING_REQUEST_ID);
		PlanningRequestGroup group = new PlanningRequestGroup();
		group.setId(1L);
		consumer.getPlanningRequestService().submitPlanningRequest(group, pr);
		assertTrue(true);
	}

	@Test
	public void getPlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		pr.setId(PLANNING_REQUEST_ID);
		PlanningRequestGroup group = new PlanningRequestGroup();
		group.setId(1L);
		consumer.getPlanningRequestService().submitPlanningRequest(group, pr);
		pr = consumer.getPlanningRequestService().getPlanningRequest(
				PLANNING_REQUEST_ID);
		assertTrue(pr != null && pr.getStatus() == StateEnum.SUBMITTED);
	}

	@Test
	public void updatePlanningRequest() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		pr.setId(PLANNING_REQUEST_ID);
		pr.setStatus(StateEnum.ACCEPTED);
		consumer.getPlanningRequestService().updatePlanningRequest(
				PLANNING_REQUEST_ID, pr);
		pr = consumer.getPlanningRequestService().getPlanningRequest(
				PLANNING_REQUEST_ID);
		assertTrue(pr != null && pr.getStatus() == StateEnum.ACCEPTED);
	}

	@Test
	public void removePlanningRequest() throws MALInteractionException,
			MALException {
		consumer.getPlanningRequestService().removePlanningRequest(
				PLANNING_REQUEST_ID);
		PlanningRequest pr = consumer.getPlanningRequestService()
				.getPlanningRequest(PLANNING_REQUEST_ID);
		assertTrue(pr == null);
	}

	@Test
	public void getPlanningRequestStatus() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		pr.setId(PLANNING_REQUEST_ID);
		PlanningRequestGroup group = new PlanningRequestGroup();
		group.setId(1L);
		consumer.getPlanningRequestService().submitPlanningRequest(group, pr);
		StateEnum status = consumer.getPlanningRequestService()
				.getPlanningRequestStatus(PLANNING_REQUEST_ID);
		assertTrue(status == StateEnum.SUBMITTED);
	}

	@Test
	public void getPlanningRequestList() throws MALInteractionException,
			MALException {
		PlanningRequest pr = new PlanningRequest();
		pr.setId(100L);
		PlanningRequestGroup group = new PlanningRequestGroup();
		group.setId(1L);
		consumer.getPlanningRequestService().submitPlanningRequest(group, pr);
		PlanningRequestList list = consumer.getPlanningRequestService()
				.getPlanningRequestList(new PlanningRequestFilter());
		assertTrue(list.size() == 2);
	}

	@Test
	public void subscribingTest() throws MALInteractionException, MALException, InterruptedException, IOException {
		PlanningRequest pr = new PlanningRequest();
		pr.setId(PLANNING_REQUEST_ID);
		PlanningRequestGroup group = new PlanningRequestGroup();
		group.setId(1L);
		TestAdapter testAdapter = new TestAdapter();
		consumer.getPlanningRequestService().asyncSubmitPlanningRequest(group, pr, testAdapter);
		Thread.sleep(300);
		pr.setStatus(StateEnum.ACCEPTED);
		consumer.getPlanningRequestService().asyncUpdatePlanningRequest(PLANNING_REQUEST_ID, pr, testAdapter);
		Thread.sleep(300);
		consumer.getPlanningRequestService().asyncRemovePlanningRequest(PLANNING_REQUEST_ID, testAdapter);
		assertTrue(testAdapter.counter > 0);
	}
	
	private class TestAdapter extends PlanningRequestServiceAdapter {
		
		public int counter = 0;

		@Override
		public void submitPlanningRequestAckReceived(
				MALMessageHeader msgHeader, Map qosProperties) {
			counter++;
			super.submitPlanningRequestAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void updatePlanningRequestAckReceived(
				MALMessageHeader msgHeader, Map qosProperties) {
			counter++;
			super.updatePlanningRequestAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void removePlanningRequestAckReceived(
				MALMessageHeader msgHeader, Map qosProperties) {
			counter++;
			super.removePlanningRequestAckReceived(msgHeader, qosProperties);
		}
		
	}
	
	private static void subscribeConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier("SUB");
		// set up the wildcard subscription
		final EntityKey entitykey = new EntityKey(new Identifier("*"), 0L, 0L,
				0L);

		final EntityKeyList entityKeys = new EntityKeyList();
		entityKeys.add(entitykey);

		final EntityRequest entity = new EntityRequest(null, false, false,
				false, false, entityKeys);

		final EntityRequestList entities = new EntityRequestList();
		entities.add(entity);

		Subscription subRequestWildcard = new Subscription(subscriptionId,
				entities);
		consumer.getPlanningRequestService().subscribeRegister(
				subRequestWildcard, adapter);
	}
	
	private static void unsubscribeConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier("SUB");
		final IdentifierList subLst = new IdentifierList();
		subLst.add(subscriptionId);
		consumer.getPlanningRequestService().subscribeDeregister(subLst);
	}

}
