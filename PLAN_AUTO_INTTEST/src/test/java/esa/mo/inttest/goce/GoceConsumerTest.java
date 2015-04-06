package esa.mo.inttest.goce;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.mo.inttest.DemoUtils;
import esa.mo.inttest.Dumper;
import esa.mo.inttest.goce.GoceConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

/**
 * GOCE consumer test. Tests four GOCE example files using provider stub.
 */
public class GoceConsumerTest {

	private static final String PROVIDER = "GoceProvider";
	private static final String BROKER = PROVIDER; // label broker as provider since it's part of provider
	private static final String CLIENT1 = "GoceUser";
	
	private PlanningRequestProviderFactory provFct;
	
	private PlanningRequestConsumerFactory consFct;
	
	private GoceConsumer goce;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		String val = System.getProperty("log2file");
		log2file = (null != val) && "true".equalsIgnoreCase(val);
		System.out.println("writing to log files is turned on: "+log2file);
		if (log2file) {
			Dumper.setBroker(BROKER);
			// trim down log spam
			DemoUtils.setLevels();
			// log each consumer/provider lines to it's own file
			files = new ArrayList<Handler>();
			files.add(DemoUtils.createHandler(PROVIDER));
			for (Handler h: files) {
				Logger.getLogger("").addHandler(h);
			}
		}
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		if (log2file) {
			for (Handler h: files) {
				Logger.getLogger("").removeHandler(h);
			}
			Dumper.setBroker("Broker");
		}
	}

	@Before
	public void setUp() throws Exception {
		String props = "testInt.properties";
		
		provFct = new PlanningRequestProviderFactory();
		provFct.setPropertyFile(props);
		provFct.start(PROVIDER);
		
		consFct = new PlanningRequestConsumerFactory();
		consFct.setPropertyFile(props);
		consFct.setProviderUri(provFct.getProviderUri());
		consFct.setBrokerUri(provFct.getBrokerUri());
		
		goce = new GoceConsumer(consFct.start(CLIENT1));
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
