package esa.mo.inttest.bepicolombo;

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
import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

public class BepiColomboConsumerTest {

	private static final String PR_PROV = "PrProvider";
	private static final String CLIENT1 = "BepiUser";
//	private static final String SCH_PROV = "SchProvider";
//	private static final String CLIENT2 = "GoceUser2";
	
	private PlanningRequestProviderFactory prProvFct;
	private PlanningRequestConsumerFactory prConsFct;
//	private ScheduleProviderFactory schProvFct;
//	private ScheduleConsumerFactory schConsFct;
	
	private BepiColomboConsumer bepi;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		log2file = DemoUtils.getLogFlag();
		if (log2file) {
			// trim down log spam
			DemoUtils.setLevels();
			// log each consumer/provider lines to it's own file
			files = new ArrayList<Handler>();
			String path = ".\\target\\demo_logs\\bepicolombo\\";
			files.add(DemoUtils.createHandler(PR_PROV, path));
			files.add(DemoUtils.createHandler(CLIENT1, path));
//			files.add(DemoUtils.createHandler(SCH_PROV, path));
			for (Handler h: files) {
				Logger.getLogger("").addHandler(h);
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (log2file) {
			for (Handler h: files) {
				Logger.getLogger("").removeHandler(h);
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		String props = "testInt.properties";
		
		prProvFct = new PlanningRequestProviderFactory();
		prProvFct.setPropertyFile(props);
		prProvFct.start(PR_PROV);
		
//		schProvFct = new ScheduleProviderFactory();
//		schProvFct.setPropertyFile(props);
//		schProvFct.setBrokerUri(prProvFct.getBrokerUri());
//		schProvFct.start(SCH_PROV);
		
		prConsFct = new PlanningRequestConsumerFactory();
		prConsFct.setPropertyFile(props);
		prConsFct.setProviderUri(prProvFct.getProviderUri());
		prConsFct.setBrokerUri(prProvFct.getBrokerUri());
		
//		schConsFct = new ScheduleConsumerFactory();
//		schConsFct.setPropertyFile(props);
//		schConsFct.setProviderUri(schProvFct.getProviderUri());
//		schConsFct.setBrokerUri(prProvFct.getBrokerUri());
		
		bepi = new BepiColomboConsumer(new PlanningRequestConsumer(prConsFct.start(CLIENT1)/*, schConsFct.start(CLIENT2)*/));
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
		bepi.crf();
	}

}
