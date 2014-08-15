package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.scheduleexecution.consumer.ScheduleExecutionAdapter;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.Schedule;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleDefinition;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnit integration tests with consumer and provider.
 * @author krikse
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/applicationContext.xml")
public class ScheduleExecutionServiceTest {
	
	public static final Logger LOGGER = Logger
			.getLogger(ScheduleExecutionServiceTest.class.getName());
	
	@Autowired
	private ScheduleExecutionServiceProvider scheduleExecutionServiceProvider;
	
	@Autowired
	private ScheduleExecutionServiceConsumer scheduleExecutionServiceConsumer ;

	
	@Before
	public void testSetup() throws IOException, MALInteractionException, MALException {
		subscribeConsumer();
	}

	@After
	public void testCleanup() throws MALException,
			MALInteractionException {
		unsubscribeConsumer();
	}
	
	private void subscribeConsumer() throws MALInteractionException, MALException {
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
		ScheduleExecutionAdapter adapter = new ScheduleExecutionServiceConsumerAdapter();
		scheduleExecutionServiceConsumer.getScheduleExecutionService().subscribeRegister(
				subRequestWildcard, adapter);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().monitorExecutionRegister(
				subRequestWildcard, adapter);
	}
	
	private void unsubscribeConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier("SUB");
		final IdentifierList subLst = new IdentifierList();
		subLst.add(subscriptionId);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().subscribeDeregister(subLst);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().monitorExecutionDeregister(subLst);
	}
	
	@Test
	public void testSubmitSchedule() throws MALInteractionException, MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		assertTrue(schId != null);
	}
	
	@Test
	public void testUpdateSchedule() throws MALInteractionException, MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		schedule.setDescription("desc update");
		scheduleExecutionServiceConsumer.getScheduleExecutionService().updateSchedule(schId, schedule);
		assertTrue(schId != null);
	}
	
	@Test
	public void testRemoveSchedule() throws MALInteractionException, MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().removeSchedule(schId);
		schedule = scheduleExecutionServiceConsumer.getScheduleExecutionService().getSchedule(schId);
		assertTrue(schedule == null);
	}
	
	@Test
	public void testGetSchedule() throws MALInteractionException,
			MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().removeSchedule(schId);
		assertTrue(schId != null);
	}
	
	@Test
	public void testGetScheduleList() throws MALInteractionException,
			MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		LongList longList = scheduleExecutionServiceConsumer.getScheduleExecutionService().getScheduleList(null);
		assertTrue(longList.size() > 0);
	}
	
	@Test
	public void testStartSchedule() throws MALInteractionException, MALException {
		Schedule schedule = new Schedule();
		Long defId = addNewScheduleDefinition();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().startSchedule(schId);
		ScheduleStatus status = scheduleExecutionServiceConsumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.RUNNING);
	}
	
	@Test
	public void testPauseSchedule() throws MALInteractionException, MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().startSchedule(schId);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().pauseSchedule(schId);
		ScheduleStatus status = scheduleExecutionServiceConsumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.PAUSED);
	}
	
	@Test
	public void testResumeSchedule() throws MALInteractionException, MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().startSchedule(schId);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().pauseSchedule(schId);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().resumeSchedule(schId);
		ScheduleStatus status = scheduleExecutionServiceConsumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.RUNNING);
	}
	
	@Test
	public void testTerminateSchedule() throws MALInteractionException, MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().startSchedule(schId);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().terminateSchedule(schId);
		ScheduleStatus status = scheduleExecutionServiceConsumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.ABORTED);
	}
	
	@Test
	public void testGetScheduleStatus() throws MALInteractionException, MALException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		Long schId = scheduleExecutionServiceConsumer.getScheduleExecutionService().submitSchedule(defId, schedule);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().startSchedule(schId);
		ScheduleStatus status = scheduleExecutionServiceConsumer.getScheduleExecutionService().getScheduleStatus(schId);
		assertTrue(status.getState() == ScheduleState.RUNNING);
	}
	
	@Test
	public void subscribingTest() throws MALInteractionException, MALException, InterruptedException, IOException {
		Long defId = addNewScheduleDefinition();
		Schedule schedule = new Schedule();
		TestAdapter testAdapter = new TestAdapter();
		scheduleExecutionServiceConsumer.getScheduleExecutionService().asyncSubmitSchedule(defId, schedule, testAdapter);
		Thread.sleep(300);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().asyncUpdateSchedule(testAdapter.scheduleId, schedule, testAdapter);
		Thread.sleep(300);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().asyncRemoveSchedule(testAdapter.scheduleId, testAdapter);
		assertTrue(testAdapter.counter > 0);
	}
	
	private class TestAdapter extends ScheduleExecutionAdapter {
		
		public int counter = 0;
		public Long scheduleId;

		@Override
		public void submitScheduleResponseReceived(MALMessageHeader msgHeader,
				Long scheduleId, Map qosProperties) {
			counter++;
			this.scheduleId = scheduleId;
			super.submitScheduleResponseReceived(msgHeader, scheduleId, qosProperties);
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
	
	public void testAddAndListDefinition() throws MALInteractionException, MALException {
		ScheduleDefinition scheduleDefinition = new ScheduleDefinition();
		scheduleDefinition.setName("test");
		Long id = scheduleExecutionServiceConsumer.getScheduleExecutionService().addDefinition(scheduleDefinition);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		LongList longList = scheduleExecutionServiceConsumer.getScheduleExecutionService().listDefinition(identifierList);
		assertTrue(longList != null && longList.size() == 1 && longList.get(0).equals(id));
	}
	
	public void testUpdateDefinition() throws MALInteractionException, MALException {
		ScheduleDefinition scheduleDefinition = new ScheduleDefinition();
		scheduleDefinition.setName("test");
		Long id = scheduleExecutionServiceConsumer.getScheduleExecutionService().addDefinition(scheduleDefinition);
		scheduleDefinition.setName("test2");
		scheduleExecutionServiceConsumer.getScheduleExecutionService().updateDefinition(id, scheduleDefinition);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test2"));
		LongList longList = scheduleExecutionServiceConsumer.getScheduleExecutionService().listDefinition(identifierList);
		assertTrue(longList != null && longList.size() == 1 && longList.get(0).equals(id));
	}
	
	public void testRemoveDefinition() throws MALInteractionException, MALException {
		ScheduleDefinition scheduleDefinition = new ScheduleDefinition();
		scheduleDefinition.setName("test");
		Long id = scheduleExecutionServiceConsumer.getScheduleExecutionService().addDefinition(scheduleDefinition);
		scheduleExecutionServiceConsumer.getScheduleExecutionService().removeDefinition(id);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		LongList longList = scheduleExecutionServiceConsumer.getScheduleExecutionService().listDefinition(identifierList);
		assertTrue(longList != null && longList.size() == 1 && longList.get(0).equals(id));
	}
	
	private Long addNewScheduleDefinition() throws MALInteractionException, MALException {
		ScheduleDefinition scheduleDefinition = new ScheduleDefinition();
		scheduleDefinition.setName("scheduleDefinition");
		scheduleDefinition.setDescription("desc");
		return scheduleExecutionServiceConsumer.getScheduleExecutionService().addDefinition(scheduleDefinition);
	}

}
