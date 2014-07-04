package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.proceduredefinitionservice.structures.ProcedureDefinition;
import org.ccsds.moims.mo.automation.procedureexecutionservice.consumer.ProcedureExecutionServiceAdapter;
import org.ccsds.moims.mo.automation.procedureexecutionservice.structures.ProcedureInvocationDetails;
import org.ccsds.moims.mo.automation.procedureexecutionservice.structures.ProcedureOccurrence;
import org.ccsds.moims.mo.automation.procedureexecutionservice.structures.ProcedureState;
import org.ccsds.moims.mo.automation.procedureexecutionservice.structures.ProcedureStatus;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.automation.consumer.ProcedureExecutionServiceConsumer;
import org.ccsds.moims.mo.mal.automation.consumer.ProcedureExecutionServiceConsumerAdapter;
import org.ccsds.moims.mo.mal.automation.provider.ProcedureExecutionServiceProvider;
import org.ccsds.moims.mo.mal.automation.service.ProcedureDefinitionServiceImpl;
import org.ccsds.moims.mo.mal.automation.service.ProcedureExecutionServiceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit integration tests with consumer and provider.
 * @author krikse
 *
 */
public class ProcedureExecutionServiceTest {
	
	public static final Logger LOGGER = Logger
			.getLogger(ProcedureExecutionServiceTest.class.getName());
	private static ProcedureExecutionServiceAdapter adapter = null;
	private static ProcedureExecutionServiceProvider provider = null;
	private static ProcedureExecutionServiceConsumer consumer = null;
	
	@BeforeClass
	public static void testSetup() throws IOException, MALInteractionException, MALException {
		adapter = new ProcedureExecutionServiceConsumerAdapter();
		// set up provider
		provider = new ProcedureExecutionServiceProvider(new ProcedureExecutionServiceImpl(), new ProcedureDefinitionServiceImpl());
		provider.setPropertyFile("/demoProvider.properties");
		provider.start();

		// set up consumer
		consumer = new ProcedureExecutionServiceConsumer();
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
		consumer.getProcedureExecutionService().monitorRegister(
				subRequestWildcard, adapter);
	}
	
	private static void unsubscribeConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier("SUB2");
		final IdentifierList subLst = new IdentifierList();
		subLst.add(subscriptionId);
		consumer.getProcedureExecutionService().monitorDeregister(subLst);
	}
	
	@Test
	public void testStartProcedure() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procId = 12L; //ProcedureDefinitionServiceTest.consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		details.setProcId(procId);
		consumer.getProcedureExecutionService().startProcedure(procId, details);
		ProcedureOccurrence po = consumer.getProcedureExecutionService().getProcedure(procId);
		assertTrue(po != null && po.getStatus().getState() == ProcedureState.RUNNING);
	}
	
	@Test
	public void testPauseProcedure() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procId = 13L; //ProcedureDefinitionServiceTest.consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		details.setProcId(procId);
		consumer.getProcedureExecutionService().startProcedure(procId, details);
		consumer.getProcedureExecutionService().pauseProcedure(procId);
		ProcedureOccurrence po = consumer.getProcedureExecutionService().getProcedure(procId);
		assertTrue(po != null && po.getStatus().getState() == ProcedureState.PAUSED);
	}
	
	@Test
	public void testResumeProcedure() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procId = 14L; //ProcedureDefinitionServiceTest.consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		details.setProcId(procId);
		consumer.getProcedureExecutionService().startProcedure(procId, details);
		consumer.getProcedureExecutionService().pauseProcedure(procId);
		consumer.getProcedureExecutionService().resumeProcedure(procId);
		ProcedureOccurrence po = consumer.getProcedureExecutionService().getProcedure(procId);
		assertTrue(po != null && po.getStatus().getState() == ProcedureState.RUNNING);
	}
	
	@Test
	public void testTerminateProcedure() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procId = 15L; //ProcedureDefinitionServiceTest.consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		details.setProcId(procId);
		consumer.getProcedureExecutionService().startProcedure(procId, details);
		consumer.getProcedureExecutionService().terminateProcedure(procId);
		ProcedureOccurrence po = consumer.getProcedureExecutionService().getProcedure(procId);
		assertTrue(po != null && po.getStatus().getState() == ProcedureState.ABORTED);
	}
	
	@Test
	public void testGetStatus() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procId = 16L; //;ProcedureDefinitionServiceTest.consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		details.setProcId(procId);
		consumer.getProcedureExecutionService().startProcedure(procId, details);
		ProcedureStatus ps = consumer.getProcedureExecutionService().getStatus(procId);
		assertTrue(ps != null && ps.getState() == ProcedureState.RUNNING);
	}
	
	@Test
	public void monitorTest() throws MALInteractionException, MALException, InterruptedException, IOException {
		TestAdapter testAdapter = new TestAdapter();
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procId = 17L; //ProcedureDefinitionServiceTest.consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		ProcedureInvocationDetails details = new ProcedureInvocationDetails();
		details.setProcId(procId);
		consumer.getProcedureExecutionService().asyncStartProcedure(procId, details, testAdapter);
		Thread.sleep(300);
		consumer.getProcedureExecutionService().asyncPauseProcedure(procId, testAdapter);
		Thread.sleep(300);
		consumer.getProcedureExecutionService().asyncResumeProcedure(procId, testAdapter);
		Thread.sleep(300);
		consumer.getProcedureExecutionService().asyncTerminateProcedure(procId, testAdapter);
		assertTrue(testAdapter.counter > 0);
	}
	
	private class TestAdapter extends ProcedureExecutionServiceAdapter {
		
		public int counter = 0;

		@Override
		public void startProcedureAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.startProcedureAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void pauseProcedureAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.pauseProcedureAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void resumeProcedureAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.resumeProcedureAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void terminateProcedureAckReceived(MALMessageHeader msgHeader,
				Map qosProperties) {
			counter++;
			super.terminateProcedureAckReceived(msgHeader, qosProperties);
		}

		
		
	}

}
