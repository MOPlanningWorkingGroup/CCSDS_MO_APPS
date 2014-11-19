package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.procedureexecution.consumer.ProcedureExecutionAdapter;
import org.ccsds.moims.mo.automation.procedureexecution.structures.Procedure;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureDefinition;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureInvocationDetails;
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
import org.ccsds.moims.mo.mal.automation.consumer.ProcedureExecutionServiceConsumer;
import org.ccsds.moims.mo.mal.automation.consumer.ProcedureExecutionServiceConsumerAdapter;
import org.ccsds.moims.mo.mal.automation.provider.ProcedureExecutionServiceProvider;
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
@ContextConfiguration("classpath*:**/applicationAutomationContext.xml")
public class ProcedureExecutionServiceTest {
	
	public static final Logger LOGGER = Logger
			.getLogger(ProcedureExecutionServiceTest.class.getName());
	
	@Autowired
	private ProcedureExecutionServiceProvider procedureExecutionServiceProvider;
	
	@Autowired
	private ProcedureExecutionServiceConsumer procedureExecutionServiceConsumer;
	
	@Before
	public void testSetup() throws IOException, MALInteractionException, MALException {
		subscribeConsumer();
	}

	@After
	public void testCleanup() throws MALException,
			MALInteractionException {
		unsubscribeConsumer();
	}
	
	private Long addNewProcedureDefinition() throws MALInteractionException, MALException {
		ProcedureDefinition procedureDefinition = new ProcedureDefinition();
		procedureDefinition.setName("procedureDefinition1");
		procedureDefinition.setDescription("description");
		// TODO
		return procedureExecutionServiceConsumer.getProcedureExecutionService().addDefinition(procedureDefinition);
	}
	
	private void subscribeConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier("SUB2");
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
		procedureExecutionServiceConsumer.getProcedureExecutionService().monitorExecutionRegister(
				subRequestWildcard, new ProcedureExecutionServiceConsumerAdapter());
	}
	
	private void unsubscribeConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier("SUB2");
		final IdentifierList subLst = new IdentifierList();
		subLst.add(subscriptionId);
		procedureExecutionServiceConsumer.getProcedureExecutionService().monitorExecutionDeregister(subLst);
	}
	
	@Test
	public void testStartProcedure() throws MALInteractionException, MALException {
		Long procedureDefId = addNewProcedureDefinition();
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		Long procId = procedureExecutionServiceConsumer.getProcedureExecutionService().start(procedureDefId, details);
		Procedure po = procedureExecutionServiceConsumer.getProcedureExecutionService().get(procId);
		assertTrue(po != null);
	}
	
	@Test
	public void testPauseProcedure() throws MALInteractionException, MALException {
		Long procedureDefId = addNewProcedureDefinition();
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		Long procId = procedureExecutionServiceConsumer.getProcedureExecutionService().start(procedureDefId, details);
		procedureExecutionServiceConsumer.getProcedureExecutionService().pause(procId);
		Procedure po = procedureExecutionServiceConsumer.getProcedureExecutionService().get(procId);
		assertTrue(po != null);
	}
	
	@Test
	public void testResumeProcedure() throws MALInteractionException, MALException {
		Long procedureDefId = addNewProcedureDefinition();
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		Long procedureId = procedureExecutionServiceConsumer.getProcedureExecutionService().start(procedureDefId, details);
		procedureExecutionServiceConsumer.getProcedureExecutionService().pause(procedureId);
		procedureExecutionServiceConsumer.getProcedureExecutionService().resume(procedureId);
		Procedure po = procedureExecutionServiceConsumer.getProcedureExecutionService().get(procedureId);
		assertTrue(po != null);
	}
	
	@Test
	public void testTerminateProcedure() throws MALInteractionException, MALException {
		Long procedureDefId = addNewProcedureDefinition();
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		Long procedureId = procedureExecutionServiceConsumer.getProcedureExecutionService().start(procedureDefId, details);
		procedureExecutionServiceConsumer.getProcedureExecutionService().terminate(procedureId);
		Procedure po = procedureExecutionServiceConsumer.getProcedureExecutionService().get(procedureId);
		assertTrue(po != null);
	}
	
	@Test
	public void monitorTest() throws MALInteractionException, MALException, InterruptedException, IOException {
		Long procedureDefId = addNewProcedureDefinition();
		TestAdapter testAdapter = new TestAdapter();
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		procedureExecutionServiceConsumer.getProcedureExecutionService().asyncStart(procedureDefId, details, testAdapter);
		Thread.sleep(300);
		procedureExecutionServiceConsumer.getProcedureExecutionService().asyncPause(testAdapter.procedureId, testAdapter);
		Thread.sleep(300);
		procedureExecutionServiceConsumer.getProcedureExecutionService().asyncResume(testAdapter.procedureId, testAdapter);
		Thread.sleep(300);
		procedureExecutionServiceConsumer.getProcedureExecutionService().asyncTerminate(testAdapter.procedureId, testAdapter);
		assertTrue(testAdapter.counter > 0);
	}
	
	private class TestAdapter extends ProcedureExecutionAdapter {
		
		public int counter = 0;
		public Long procedureId;

		@Override
		public void startResponseReceived(MALMessageHeader msgHeader,
				Long procedureId, Map qosProperties) {
			// TODO Auto-generated method stub
			counter++;
			this.procedureId = procedureId;
			super.startResponseReceived(msgHeader, procedureId, qosProperties);
		}

		@Override
		public void pauseAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.pauseAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void resumeAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.resumeAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void terminateAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.terminateAckReceived(msgHeader, qosProperties);
		}
		
	}
	
	public void testAddAndListDefinition() throws MALInteractionException, MALException {
		ProcedureDefinition procedureDefinition = new ProcedureDefinition();
		procedureDefinition.setName("test");
		Long id = procedureExecutionServiceConsumer.getProcedureExecutionService().addDefinition(procedureDefinition);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		LongList longList = procedureExecutionServiceConsumer.getProcedureExecutionService().listDefinition(identifierList);
		assertTrue(longList != null && longList.size() == 1 && longList.get(0).equals(id));
	}
	
	public void testUpdateDefinition() throws MALInteractionException, MALException {
		ProcedureDefinition procedureDefinition = new ProcedureDefinition();
		procedureDefinition.setName("test");
		Long id = procedureExecutionServiceConsumer.getProcedureExecutionService().addDefinition(procedureDefinition);
		procedureDefinition.setName("test2");
		procedureExecutionServiceConsumer.getProcedureExecutionService().updateDefinition(id, procedureDefinition);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test2"));
		LongList longList = procedureExecutionServiceConsumer.getProcedureExecutionService().listDefinition(identifierList);
		assertTrue(longList != null && longList.size() == 1 && longList.get(0).equals(id));
	}
	
	public void testRemoveDefinition() throws MALInteractionException, MALException {
		ProcedureDefinition procedureDefinition = new ProcedureDefinition();
		procedureDefinition.setName("test");
		Long id = procedureExecutionServiceConsumer.getProcedureExecutionService().addDefinition(procedureDefinition);
		procedureExecutionServiceConsumer.getProcedureExecutionService().removeDefinition(id);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		LongList longList = procedureExecutionServiceConsumer.getProcedureExecutionService().listDefinition(identifierList);
		assertTrue(longList != null && longList.size() == 1 && longList.get(0).equals(id));
	}

}
