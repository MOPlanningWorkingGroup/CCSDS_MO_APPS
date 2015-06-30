/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.goce;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Handler;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.mo.inttest.DemoUtils;
import esa.mo.inttest.goce.GoceConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;
import esa.mo.inttest.sch.consumer.ScheduleConsumerFactory;
import esa.mo.inttest.sch.provider.ScheduleProviderFactory;

/**
 * GOCE consumer test. Tests four GOCE example files using provider stub.
 */
public class GoceConsumerTest {

	private static final String PR_PROV = "PrProvider";
	private static final String CLIENT1 = "GoceUser";
	private static final String SCH_PROV = "SchProvider";
	private static final String CLIENT2 = "GoceUser2";
	
	private PlanningRequestProviderFactory prProvFct;
	private PlanningRequestConsumerFactory prConsFct;
	private ScheduleProviderFactory schProvFct;
	private ScheduleConsumerFactory schConsFct;
	
	private GoceConsumer goce;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		log2file = DemoUtils.getLogFlag();
		if (log2file) {
			// log each consumer/provider lines to it's own file
			String path = ".\\target\\demo_logs\\goce\\";
			files = DemoUtils.createHandlers(path, PR_PROV, CLIENT1, SCH_PROV);
		}
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		if (log2file) {
			DemoUtils.removeHandlers(files);
		}
	}

	@Before
	public void setUp() throws Exception {
		String props = "testInt.properties";
		
		prProvFct = new PlanningRequestProviderFactory();
		prProvFct.setPropertyFile(props);
		prProvFct.start(PR_PROV);
		
		schProvFct = new ScheduleProviderFactory();
		schProvFct.setPropertyFile(props);
		schProvFct.setBrokerUri(prProvFct.getBrokerUri());
		schProvFct.start(SCH_PROV);
		
		prConsFct = new PlanningRequestConsumerFactory();
		prConsFct.setPropertyFile(props);
		prConsFct.setProviderUri(prProvFct.getProviderUri());
		prConsFct.setBrokerUri(prProvFct.getBrokerUri());
		
		schConsFct = new ScheduleConsumerFactory();
		schConsFct.setPropertyFile(props);
		schConsFct.setProviderUri(schProvFct.getProviderUri());
		schConsFct.setBrokerUri(prProvFct.getBrokerUri());
		
		goce = new GoceConsumer(prConsFct.start(CLIENT1), schConsFct.start(CLIENT2));
	}

	@After
	public void tearDown() throws Exception {
		if (prConsFct != null) {
			prConsFct.stop(goce.getPrStub());
		}
		goce = null;
		prConsFct = null;
		
		if (prProvFct != null) {
			prProvFct.stop();
		}
		prProvFct = null;
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

	@Test
	public void testSist() throws MALException, MALInteractionException, ParseException {
		goce.sist();
	}
}
