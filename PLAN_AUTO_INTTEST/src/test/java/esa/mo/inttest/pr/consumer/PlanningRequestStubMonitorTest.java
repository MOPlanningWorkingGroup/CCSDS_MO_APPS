package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
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
				return null != prMon.prStats;
			}
		});
		// verify that we got pr notification
		assertNotNull(prMon.prStats);
		assertEquals(1, prMon.prStats.size());
		assertNotNull(prMon.prStats.get(0));
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
		
		String taskSubId = "subId3";
		final TaskMonitor taskMon = registerTaskMonitor(taskSubId);
		
		createAndSubmitPlanningRequestWithTask();
		
		Util.waitFor(taskMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return null != taskMon.taskStats;
			}
		});
		
		waitAndVerifyPr(prMon);
		
		// verify that we got task notification
		assertNotNull(taskMon.taskStats);
		assertEquals(1, taskMon.taskStats.size());
		assertNotNull(taskMon.taskStats.get(0));
		
		deRegisterPrMonitor(taskSubId);
		deRegisterPrMonitor(prSubId);
		
		leave("testSubmitPlanningRequestWithTask");
	}

	@Test
	public void testUpdatePlanningRequest() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testUpdatePlanningRequest");
		
		String prSubId = "subId4";
		final PrMonitor prMon = registerPrMonitor(prSubId);
		
		String taskSubId = "subId5";
		final TaskMonitor taskMon = registerTaskMonitor(taskSubId);
		
		Object[] details = createAndSubmitPlanningRequest();
		
		Util.waitFor(taskMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return null != taskMon.taskStats;
			}
		});
		
		Util.waitFor(prMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return null != prMon.prStats;
			}
		});
		
		// reset notify helpers
		prMon.prStats = null;
		taskMon.taskStats = null;
		
		updatePlanningRequestWithTask(details);
		
		Util.waitFor(taskMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return null != taskMon.taskStats;
			}
		});
		
		waitAndVerifyPr(prMon);
		
		// verify that we got task notification
		assertNotNull(taskMon.taskStats);
		assertEquals(1, taskMon.taskStats.size());
		assertNotNull(taskMon.taskStats.get(0));
		
		deRegisterTaskMonitor(taskSubId);
		
		deRegisterPrMonitor(prSubId);
		
		leave("testUpdatePlanningRequest");
	}

	@Test
	public void testRemovePlanningRequest() throws MALException, MALInteractionException, InterruptedException, Exception {
		enter("testRemovePlanningRequest");
		
		String prSubId = "subId6";
		PrMonitor prMon = registerPrMonitor(prSubId);
		
		String taskSubId = "subId7";
		final TaskMonitor taskMon = registerTaskMonitor(taskSubId);
		
		Object[] details = createAndSubmitPlanningRequestWithTask();
		Long prInstId = (Long)details[1];
		
		// reset notify helpers
		prMon.prStats = null;
		taskMon.taskStats = null;
		
		removePlanningRequest(prInstId);
		
		Util.waitFor(taskMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return null != taskMon.taskStats;
			}
		});
		
		waitAndVerifyPr(prMon);
		
		// verify that we got task notification
		assertNotNull(taskMon.taskStats);
		assertEquals(1, taskMon.taskStats.size());
		assertNotNull(taskMon.taskStats.get(0));
		
		deRegisterTaskMonitor(taskSubId);
		
		deRegisterPrMonitor(prSubId);
		
		leave("testRemovePlanningRequest");
	}
}
