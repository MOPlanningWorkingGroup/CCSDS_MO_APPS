/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.bepicolombo;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Handler;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.mo.inttest.DemoUtils;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

public class BepiColomboConsumerTest {

	private static final String PR_PROV = "PrProvider";
	private static final String CLIENT1 = "BepiUser";
	
	private PlanningRequestProviderFactory prProvFct;
	private PlanningRequestConsumerFactory prConsFct;
	
	private BepiColomboConsumer bepi;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		log2file = DemoUtils.getLogFlag();
		if (log2file) {
			// log each consumer/provider lines to it's own file
			String path = ".\\target\\demo_logs\\bepicolombo\\";
			files = DemoUtils.createHandlers(path, PR_PROV, CLIENT1);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
		
		prConsFct = new PlanningRequestConsumerFactory();
		prConsFct.setPropertyFile(props);
		prConsFct.setProviderUri(prProvFct.getProviderUri());
		prConsFct.setBrokerUri(prProvFct.getBrokerUri());
		
		bepi = new BepiColomboConsumer(new PlanningRequestConsumer(prConsFct.start(CLIENT1)));
	}

	@After
	public void tearDown() throws Exception {
		if (prConsFct != null) {
			prConsFct.stop(bepi.getConsumer().getStub());
		}
		bepi = null;
		prConsFct = null;
		
		if (prProvFct != null) {
			prProvFct.stop();
		}
		prProvFct = null;
	}

	@Test
	public void testCrf() throws MALException, MALInteractionException, ParseException {
		PlanningRequestStatusDetails prStat = bepi.crf();
		assertNotNull(prStat);
	}
}
