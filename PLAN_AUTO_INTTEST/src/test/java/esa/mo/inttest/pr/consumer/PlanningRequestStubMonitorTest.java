package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.junit.Test;

import esa.mo.inttest.Util;

public class PlanningRequestStubMonitorTest extends PlanningRequestStubTestBase {

	private static final Logger LOG = Logger.getLogger(PlanningRequestStubMonitorTest.class.getName());
	
	private void enter(String msg) {
		LOG.entering(getClass().getName(), msg);
	}
	
	private void leave(String msg) {
		LOG.exiting(getClass().getName(), msg);
	}
	
	private void waitAndVerifyPr(final PrMonitor prMon) throws InterruptedException, Exception {
		Util.waitFor(prMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return prMon.haveData();
			}
		});
		// verify that we got pr notification
		assertNotNull(prMon.prStats);
		assertEquals(1, prMon.prStats.size());
		assertNotNull(prMon.prStats.get(0));
		assertFalse(prMon.prStats.get(0).isEmpty());
	}
	
	@Test
	public void testSubmitPlanningRequest() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testSubmitPlanningRequest");
		
		String subId = "subId1";
		final PrMonitor prMon = registerPrMonitor(subId);
		
		createAndSubmitPlanningRequest();
		
		waitAndVerifyPr(prMon);
		
		deRegisterPrMonitor(subId);
		
		leave("testSubmitPlanningRequest");
	}

	@Test
	public void testSubmitPlanningRequestWithTask() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testSubmitPlanningRequestWithTask");
		
		String prSubId = "subId2";
		final PrMonitor prMon = registerPrMonitor(prSubId);
		
		createAndSubmitPlanningRequestWithTask();
		
		waitAndVerifyPr(prMon);
		
		deRegisterPrMonitor(prSubId);
		
		leave("testSubmitPlanningRequestWithTask");
	}

	@Test
	public void testUpdatePlanningRequest() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testUpdatePlanningRequest");
		
		String prSubId = "subId4";
		final PrMonitor prMon = registerPrMonitor(prSubId);
		
		PlanningRequestInstanceDetails prInst = createAndSubmitPlanningRequest();
		
		Util.waitFor(prMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return prMon.haveData();
			}
		});
		
		// reset notify helpers
		prMon.clearData();
		
		updatePlanningRequestWithTask(prInst);
		
		waitAndVerifyPr(prMon);
		
		deRegisterPrMonitor(prSubId);
		
		leave("testUpdatePlanningRequest");
	}

	@Test
	public void testRemovePlanningRequest() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testRemovePlanningRequest");
		
		String prSubId = "subId6";
		final PrMonitor prMon = registerPrMonitor(prSubId);
		
		PlanningRequestInstanceDetails prInst = createAndSubmitPlanningRequestWithTask();
		
		Util.waitFor(prMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return prMon.haveData();
			}
		});
		// reset notify helpers
		prMon.clearData();
		
		removePlanningRequest(prInst);
		
		waitAndVerifyPr(prMon);
		
		deRegisterPrMonitor(prSubId);
		
		leave("testRemovePlanningRequest");
	}
}
