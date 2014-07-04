package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.scheduleexecution.consumer.ScheduleExecutionAdapter;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.Schedule;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleFilter;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleState;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleStatus;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.automation.consumer.ScheduleExecutionServiceConsumer;
import org.ccsds.moims.mo.mal.automation.consumer.ScheduleExecutionServiceConsumerAdapter;
import org.ccsds.moims.mo.mal.automation.provider.ScheduleExecutionServiceProvider;
import org.ccsds.moims.mo.mal.automation.service.ScheduleExecutionServiceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit integration tests with consumer and provider.
 * @author krikse
 *
 */
public class ScheduleExecutionServiceTest {
	
	public static final Logger LOGGER = Logger
			.getLogger(ScheduleExecutionServiceTest.class.getName());
	private static ScheduleExecutionAdapter adapter = null;
	private static ScheduleExecutionServiceProvider provider = null;
	private static ScheduleExecutionServiceConsumer consumer = null;
	private Long schId = 0L;
	
	@BeforeClass
	public static void testSetup() throws IOException, MALInteractionException, MALException {
		adapter = new ScheduleExecutionServiceConsumerAdapter();
		// set up provider
		provider = new ScheduleExecutionServiceProvider(new ScheduleExecutionServiceImpl());
		provider.setPropertyFile("/demoProvider.properties");
		provider.start();

		// set up consumer
		consumer = new ScheduleExecutionServiceConsumer();
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
		consumer.getScheduleExecutionService().subscribeRegister(
				subRequestWildcard, adapter);
		consumer.getScheduleExecutionService().monitorExecutionRegister(
				subRequestWildcard, adapter);
	}
	
	private static void unsubscribeConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier("SUB");
		final IdentifierList subLst = new IdentifierList();
		subLst.add(subscriptionId);
		consumer.getScheduleExecutionService().subscribeDeregister(subLst);
		consumer.getScheduleExecutionService().monitorExecutionDeregister(subLst);
	}
	
	@Test
	public void testSubmitSchedule() throws MALInteractionException, MALException {
		Schedule schedule = new Schedule();
		schId = consumer.getScheduleExecutionService().submitSchedule(schedule);
		assertTrue(schId != null && schId > 0);
	}
	
	@Test
	public void testUpdateSchedule() throws MALInteractionException, MALException {
		Schedule schedule = new Schedule();
		consumer.getScheduleExecutionService().updateSchedule(schId, schedule);
		assertTrue(true);
	}
	
	@Test
	public void testRemoveSchedule() throws MALInteractionException, MALException {
		consumer.getScheduleExecutionService().removeSchedule(schId);
		Schedule schedule = consumer.getScheduleExecutionService().getSchedule(schId);
		assertTrue(schedule == null);
	}
	
	@Test
	public void testGetSchedule() throws MALInteractionException,
			MALException {
		Schedule schedule = new Schedule();
		Long schId = consumer.getScheduleExecutionService().submitSchedule(schedule);
		schedule = consumer.getScheduleExecutionService().getSchedule(schId);
		assertTrue(schedule != null);
	}
	
	@Test
	public void testGetScheduleList() throws MALInteractionException,
			MALException {
		ScheduleFilter filter = new ScheduleFilter();
		LongList list = new LongList();
		list.add(schId);
		Schedule schedule = new Schedule();
		consumer.getScheduleExecutionService().submitSchedule(schedule);
		list = consumer.getScheduleExecutionService().getScheduleList(filter);
		assertTrue(list != null && list.size() == 1);
	}
	
	@Test
	public void testStartSchedule() throws MALInteractionException, MALException {
		Schedule schedule = new Schedule();
		Long schId = consumer.getScheduleExecutionService().submitSchedule(schedule);
		consumer.getScheduleExecutionService().startSchedule(schId);
		ScheduleStatus status = consumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.RUNNING);
	}
	
	@Test
	public void testPauseSchedule() throws MALInteractionException, MALException {
		Schedule schedule = new Schedule();
		Long schId = consumer.getScheduleExecutionService().submitSchedule(schedule);
		consumer.getScheduleExecutionService().startSchedule(schId);
		consumer.getScheduleExecutionService().pauseSchedule(schId);
		ScheduleStatus status = consumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.PAUSED);
	}
	
	@Test
	public void testResumeSchedule() throws MALInteractionException, MALException {
		Schedule schedule = new Schedule();
		Long schId = consumer.getScheduleExecutionService().submitSchedule(schedule);
		consumer.getScheduleExecutionService().startSchedule(schId);
		consumer.getScheduleExecutionService().pauseSchedule(schId);
		consumer.getScheduleExecutionService().resumeSchedule(schId);
		ScheduleStatus status = consumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.RUNNING);
	}
	
	@Test
	public void testTerminateSchedule() throws MALInteractionException, MALException {
		Schedule schedule = new Schedule();
		Long schId = consumer.getScheduleExecutionService().submitSchedule(schedule);
		consumer.getScheduleExecutionService().startSchedule(schId);
		consumer.getScheduleExecutionService().terminateSchedule(schId);
		ScheduleStatus status = consumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.ABORTED);
	}
	
	@Test
	public void testGetScheduleStatus() throws MALInteractionException, MALException {
		Schedule schedule = new Schedule();
		Long schId = consumer.getScheduleExecutionService().submitSchedule(schedule);
		consumer.getScheduleExecutionService().startSchedule(schId);
		ScheduleStatus status = consumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.RUNNING);
	}
	
	@Test
	public void subscribingTest() throws MALInteractionException, MALException, InterruptedException, IOException {
		Schedule schedule = new Schedule();
		Long schId = consumer.getScheduleExecutionService().submitSchedule(schedule);
		TestAdapter testAdapter = new TestAdapter();
		consumer.getScheduleExecutionService().asyncSubmitSchedule(schedule, testAdapter);
		Thread.sleep(300);
		consumer.getScheduleExecutionService().asyncUpdateSchedule(schId, schedule, testAdapter);
		Thread.sleep(300);
		consumer.getScheduleExecutionService().asyncRemoveSchedule(schId, testAdapter);
		assertTrue(testAdapter.counter > 0);
	}
	
	private class TestAdapter extends ScheduleExecutionAdapter {
		
		public int counter = 0;

		@Override
		public void submitScheduleResponseReceived(MALMessageHeader msgHeader,
				Long _Long0, Map qosProperties) {
			counter++;
			super.submitScheduleResponseReceived(msgHeader, _Long0, qosProperties);
		}

		@Override
		public void updateScheduleAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.updateScheduleAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void removeScheduleAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.removeScheduleAckReceived(msgHeader, qosProperties);
		}

		
		
	}

}
