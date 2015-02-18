package esa.mo.plan.consumer;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import esa.mo.plan.provider.PlanningRequestProviderFactory;

public class GoceConsumerTest {

	private PlanningRequestProviderFactory provFct;
	
	private PlanningRequestConsumerFactory consFct;

	private GoceConsumer goce;
	
	@Before
	public void setUp() throws Exception {
		String props = "testInt.properties";
		
		provFct = new PlanningRequestProviderFactory();
		provFct.setPropertyFile(props);
		provFct.start();
		
		consFct = new PlanningRequestConsumerFactory();
		consFct.setPropertyFile(props);
		consFct.setProviderUri(provFct.getProviderUri());
		consFct.setBrokerUri(provFct.getBrokerUri());
		consFct.start();
		
		goce = new GoceConsumer(consFct.getConsumer());
	}

	@After
	public void tearDown() throws Exception {
		goce = null;
		
		if (consFct != null) {
			consFct.stop();
		}
		consFct = null;
		
		if (provFct != null) {
			provFct.stop();
		}
		provFct = null;
	}

	@Test
	public void testPayloadPlan1() throws MALException, MALInteractionException, ParseException {
		goce.payloadPlan1();
	}

	@Test
	public void testPayloadPlan2() throws MALException, MALInteractionException, ParseException {
		goce.payloadPlan2();
	}

	@Test
	public void testIncrementPlan() throws MALException, MALInteractionException, ParseException {
		goce.incrementPlan();
	}

}
