package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.proceduredefinition.consumer.ProcedureDefinitionAdapter;
import org.ccsds.moims.mo.automation.proceduredefinition.structures.ProcedureDefinition;
import org.ccsds.moims.mo.automation.proceduredefinition.structures.ProcedureDefinitionFilter;
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
import org.ccsds.moims.mo.mal.automation.consumer.ProcedureDefinitionServiceConsumer;
import org.ccsds.moims.mo.mal.automation.consumer.ProcedureDefinitionServiceConsumerAdapter;
import org.ccsds.moims.mo.mal.automation.provider.ProcedureDefinitionServiceProvider;
import org.ccsds.moims.mo.mal.automation.service.ProcedureDefinitionServiceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit integration tests with consumer and provider.
 * @author krikse
 *
 */
public class ProcedureDefinitionServiceTest {

	public static final Logger LOGGER = Logger
			.getLogger(ProcedureDefinitionServiceTest.class.getName());
	private static ProcedureDefinitionAdapter adapter = null;
	private static ProcedureDefinitionServiceProvider provider = null;
	private static ProcedureDefinitionServiceConsumer consumer = null;
	
	@BeforeClass
	public static void testSetup() throws IOException, MALInteractionException, MALException {
		adapter = new ProcedureDefinitionServiceConsumerAdapter();
		// set up provider
		provider = new ProcedureDefinitionServiceProvider(new ProcedureDefinitionServiceImpl());
		provider.setPropertyFile("/demoProvider.properties");
		provider.start();

		// set up consumer
		consumer = new ProcedureDefinitionServiceConsumer();
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
		final Identifier subscriptionId = new Identifier("SUB1");
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
		consumer.getProcedureDefinitionService().subscribeRegister(
				subRequestWildcard, adapter);
	}
	
	private static void unsubscribeConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier("SUB1");
		final IdentifierList subLst = new IdentifierList();
		subLst.add(subscriptionId);
		consumer.getProcedureDefinitionService().subscribeDeregister(subLst);
	}
	
	@Test
	public void testAddProcedureDefinition() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procDefId = consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		assertTrue(procDefId != null && procDefId > 0);
	}
	
	@Test
	public void testRemoveProcedureDefinition() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procDefId = consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		consumer.getProcedureDefinitionService().removeProcedureDefinition(procDefId);
		assertTrue(true);
	}
	
	@Test
	public void testUpdateProcedureDefinition() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procDefId = consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		consumer.getProcedureDefinitionService().updateProcedureDefinition(procDefId, pd);
		assertTrue(true);
	}
	
	@Test
	public void testGetProcedureDefinition() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		Long procDefId = consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		pd = consumer.getProcedureDefinitionService().getProcedureDefinition(procDefId);
		assertTrue(pd != null);
	}
	
	@Test
	public void testGetProcedureDefinitionList() throws MALInteractionException, MALException {
		ProcedureDefinition pd = new ProcedureDefinition();
		consumer.getProcedureDefinitionService().addProcedureDefinition(pd);
		LongList list = consumer.getProcedureDefinitionService().getProcedureDefinitionList(new ProcedureDefinitionFilter());
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void subscribingTest() throws MALInteractionException, MALException, InterruptedException, IOException {
		TestAdapter testAdapter = new TestAdapter();
		ProcedureDefinition pd = new ProcedureDefinition();
		consumer.getProcedureDefinitionService().asyncAddProcedureDefinition(pd, testAdapter);
		Long procDefId = testAdapter.procDefId;
		Thread.sleep(300);
		consumer.getProcedureDefinitionService().asyncUpdateProcedureDefinition(procDefId, pd, testAdapter);
		Thread.sleep(300);
		consumer.getProcedureDefinitionService().asyncRemoveProcedureDefinition(procDefId, testAdapter);
		Thread.sleep(300);
		assertTrue(testAdapter.counter > 0);
	}
	
	private class TestAdapter extends ProcedureDefinitionAdapter {
		
		public int counter = 0;
		private Long procDefId;

		public Long getProcDefId() {
			return procDefId;
		}

		@Override
		public void addProcedureDefinitionResponseReceived(
				MALMessageHeader msgHeader, Long _Long0, Map qosProperties) {
			counter++;
			procDefId = _Long0;
			super.addProcedureDefinitionResponseReceived(msgHeader, _Long0, qosProperties);
		}

		@Override
		public void removeProcedureDefinitionAckReceived(
				MALMessageHeader msgHeader, Map qosProperties) {
			counter++;
			super.removeProcedureDefinitionAckReceived(msgHeader, qosProperties);
		}

		@Override
		public void updateProcedureDefinitionAckReceived(
				MALMessageHeader msgHeader, Map qosProperties) {
			counter++;
			super.updateProcedureDefinitionAckReceived(msgHeader, qosProperties);
		}
		
	}
	
}
