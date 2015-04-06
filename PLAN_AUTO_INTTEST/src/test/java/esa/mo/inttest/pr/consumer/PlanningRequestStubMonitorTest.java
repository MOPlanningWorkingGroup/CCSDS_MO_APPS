package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.junit.Test;

public class PlanningRequestStubMonitorTest extends PlanningRequestStubTestBase {

	private static final Logger LOG = Logger.getLogger(PlanningRequestStubMonitorTest.class.getName());
	
	private void enter(String msg) {
		LOG.entering(getClass().getName(), msg);
	}
	
	private void leave(String msg) {
		LOG.exiting(getClass().getName(), msg);
	}
	
	private void waitFor(Object o, long ms, Callable<Boolean> c) throws InterruptedException, Exception {
		synchronized (o) {
			long before = System.currentTimeMillis();
			long d = ms;
			do {
				o.wait(d);
				d = ms - (System.currentTimeMillis() - before);
			} while (!c.call() && (0 < d));
		}
	}
	
	private void waitAndVerifyPr(final PrMonitor prMon) throws InterruptedException, Exception {
		waitFor(prMon, 1000, new Callable<Boolean>() {
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
		
		waitFor(taskMon, 1000, new Callable<Boolean>() {
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
		
		waitFor(taskMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return null != taskMon.taskStats;
			}
		});
		
		waitFor(prMon, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return null != prMon.prStats;
			}
		});
		
		// reset notify helpers
		prMon.prStats = null;
		taskMon.taskStats = null;
		
		updatePlanningRequestWithTask(details);
		
		waitFor(taskMon, 1000, new Callable<Boolean>() {
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
	public void testRemovePlanningRequest() throws MALException, MALInteractionException {
		enter("testRemovePlanningRequest");
		
		String prSubId = "subId6";
		PrMonitor prMon = registerPrMonitor(prSubId);
		
		String taskSubId = "subId7";
		TaskMonitor taskMon = registerTaskMonitor(taskSubId);
		
		Object[] details = createAndSubmitPlanningRequestWithTask();
		Long prInstId = (Long)details[1];
		
		// reset notify helpers
		prMon.prStats = null;
		taskMon.taskStats = null;
		
		removePlanningRequest(prInstId);
		
		// verify that we got pr notification
		assertNotNull(prMon.prStats);
		assertEquals(1, prMon.prStats.size());
		assertNotNull(prMon.prStats.get(0));
		
		// verify that we got task notification
		assertNotNull(taskMon.taskStats);
		assertEquals(1, taskMon.taskStats.size());
		assertNotNull(taskMon.taskStats.get(0));
		
		deRegisterTaskMonitor(taskSubId);
		
		deRegisterPrMonitor(prSubId);
		
		leave("testRemovePlanningRequest");
	}
}
