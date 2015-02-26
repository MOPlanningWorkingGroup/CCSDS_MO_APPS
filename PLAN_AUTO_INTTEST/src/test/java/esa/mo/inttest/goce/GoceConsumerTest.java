package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import esa.mo.inttest.goce.GoceConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

/**
 * GOCE consumer test.
 */
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
		goce = new GoceConsumer(consFct.start());
		
//		goce = new GoceConsumer(consFct.getConsumer());
	}

	@After
	public void tearDown() throws Exception {
		if (consFct != null) {
			consFct.stop(goce.getStub());
		}
		goce = null;
		consFct = null;
		
		if (provFct != null) {
			provFct.stop();
		}
		provFct = null;
	}

	@Test
	public void testPpf() throws MALException, MALInteractionException, ParseException {
		goce.ppf();
	}

	@Test
	public void testPif() throws MALException, MALInteractionException, ParseException {
		goce.pif();
	}

	@Test
	public void testSpf() throws MALException, MALInteractionException, ParseException {
		goce.spf();
	}

	@Test
	public void testOpf() throws MALException, MALInteractionException, ParseException {
		goce.opf();
	}

}
