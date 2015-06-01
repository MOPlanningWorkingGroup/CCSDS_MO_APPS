package esa.mo.inttest.goce;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.mo.inttest.DemoUtils;
import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;
import esa.mo.inttest.goce.GoceConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProvider;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;
import esa.mo.inttest.pr.provider.Plugin;
import esa.mo.inttest.pr.provider.InstStore;

/**
 * Three consumers demo. One consumer managing defs, second managing instances, third only monitoring.
 */
public class ThreeGoceConsumersTest {

	/**
	 * Class receiving PR notifications.
	 */
	private final class PrMonitor extends PlanningRequestAdapter {
		
		protected List<PlanningRequestStatusDetailsList> prStats = new ArrayList<PlanningRequestStatusDetailsList>();
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHdr, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats,
				Map qosProps) {
			LOG.log(Level.INFO, "{4}.monitorPlanningRequestsNotifyReceived(subId={0}, List:updateHeaders, " +
					"List:objectIds, List:prStatuses)\n  updateHeaders[]={1}\n  objectIds[]={2}\n  prStatuses[]={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats),
					Dumper.fromBroker(PROVIDER, msgHdr) });
			this.prStats.add(prStats);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError error,
				Map qosProps) {
			LOG.log(Level.INFO, "{1}.monitorPlanningRequestsNotifyErrorReceived(error={0})",
					new Object[] { error, Dumper.fromBroker(PROVIDER, msgHdr) });
		}
	}

	/**
	 * PR plugin - monitors new PRs and Tasks and add ACCEPTED statuses later.
	 */
	private final class Processor implements Plugin {
		
		private PlanningRequestProvider prov = null;
		
		protected List<Long> newTasks = Collections.synchronizedList(new LongList());
		protected List<Long> newPrs = Collections.synchronizedList(new LongList());
		
		public void setProv(PlanningRequestProvider prov) {
			this.prov = prov;
		}
		
		public void onPrSubmit(PlanningRequestInstanceDetails pr) {
			newPrs.add(pr.getId());
			for (int j = 0; (null != pr.getTasks()) && (j < pr.getTasks().size()); ++j) {
				TaskInstanceDetails task = pr.getTasks().get(j);
				newTasks.add(task.getId());
			}
		}
		
		public void onPrUpdate(PlanningRequestInstanceDetails pr, PlanningRequestStatusDetails stat) {
			// ignore
		}
		
		public void onPrRemove(Long id) {
			// ignore
		}
		
		protected StatusRecord findStatus(StatusRecordList srl, InstanceState stat) {
			StatusRecord sr = null;
			for (int i = 0; (null != srl) && (i < srl.size()); ++i) {
				StatusRecord r = srl.get(i);
				if (null != r && stat == r.getState()) {
					sr = r;
					break;
				}
			}
			return sr;
		}
		
		private boolean checkTaskAccept(Long prId, Long taskId, TaskStatusDetails stat)
				throws MALException, MALInteractionException {
			boolean doRemove = true;
			StatusRecord asr = findStatus(stat.getStatus(), InstanceState.ACCEPTED);
			if (null == asr) {
				// wait a sec before accepting
				StatusRecord csr = findStatus(stat.getStatus(), InstanceState.SUBMITTED);
				if ((null == csr) || (System.currentTimeMillis() >= (csr.getTimeStamp().getValue()+1000L))) {
					asr = new StatusRecord(InstanceState.ACCEPTED, Util.currentTime(), "accepted");
					if (null == stat.getStatus()) {
						stat.setStatus(new StatusRecordList());
					}
					stat.getStatus().add(asr);
					// all statuses updated, now publish status change
					StatusRecordList changes = new StatusRecordList();
					changes.add(asr);
					TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
					taskStats.add(new TaskStatusDetails(taskId, changes));
					PlanningRequestStatusDetails prStat = new PlanningRequestStatusDetails(prId, null, taskStats);
					prov.publishPr(UpdateType.UPDATE, prStat);
				} else {
					doRemove = false; // wait before removing
				} // csr
			} // asr
			return doRemove;
		}
		
		private void processTasks() throws MALException, MALInteractionException {
			for (int i0 = 0; i0 < newTasks.size(); ++i0) {
				Long taskId = newTasks.get(i0);
				InstStore.TaskItem item = prov.getInstStore().findTaskItem(taskId);
				boolean doRemove = true;
				if (null != item) {
					doRemove = checkTaskAccept(item.task.getPrInstId(), taskId, item.stat);
				} // for taskStats
				if (doRemove) {
					newTasks.remove(i0);
				}
			} // for newTasks
		}
		
		protected boolean areTasksAccepted(PlanningRequestStatusDetails prStat) {
			boolean ok = true;
			for (int i = 0; (null != prStat.getTaskStatuses()) && (i < prStat.getTaskStatuses().size()); ++i) {
				TaskStatusDetails taskStat = prStat.getTaskStatuses().get(i);
				StatusRecord asr = findStatus(taskStat.getStatus(), InstanceState.ACCEPTED);
				if (null == asr) {
					ok = false;
					break;
				}
			}
			return ok;
		}
		
		private boolean checkPrAccept(Long prId, PlanningRequestStatusDetails stat) throws MALException, MALInteractionException {
			boolean doRemove = true;
			StatusRecord asr = findStatus(stat.getStatus(), InstanceState.ACCEPTED);
			if (null == asr) {
				// wait a sec before accepting
				StatusRecord csr = findStatus(stat.getStatus(), InstanceState.LAST_MODIFIED);
				// accept pr only after tasks are accepted
				boolean tasksAcc = areTasksAccepted(stat);
				if (tasksAcc && (null == csr || System.currentTimeMillis() >= (csr.getTimeStamp().getValue()+1000L))) {
					asr = new StatusRecord(InstanceState.ACCEPTED, Util.currentTime(), "accepted");
					if (null == stat.getStatus()) {
						stat.setStatus(new StatusRecordList());
					}
					stat.getStatus().add(asr);
					//  all statuses updated, now publish status change
					StatusRecordList srl = new StatusRecordList();
					srl.add(asr);
					PlanningRequestStatusDetails prStat = new PlanningRequestStatusDetails(prId, srl, null);
					prov.publishPr(UpdateType.UPDATE, prStat);
				} else {
					doRemove = false; // wait before removing
				} // csr
			} // asr
			return doRemove;
		}
		
		private void processPrs() throws MALException, MALInteractionException {
			for (int i0 = 0; i0 < newPrs.size(); ++i0) {
				Long prId = newPrs.get(i0);
				InstStore.PrItem item = prov.getInstStore().findPrItem(prId);
				boolean doRemove = true;
				if (null != item) {
					doRemove = checkPrAccept(prId, item.stat);
				} // for
				if (doRemove) {
					newPrs.remove(i0);
				}
			} // for newPrs
		}
		
		/**
		 * Processes new Tasks and PRs.
		 */
		public void process() throws MALException, MALInteractionException {
			processTasks();
			processPrs();
		}
	}
	
	private static final Logger LOG = Logger.getLogger(ThreeGoceConsumersTest.class.getName());
	
	private static final String PROVIDER = "PrProvider";
	private static final String CLIENT1 = "PrPowerUser";
	private static final String CLIENT2 = "PrNormalUser";
	private static final String CLIENT3 = "PrMonitorUser";
	
	private PlanningRequestProviderFactory provFct;
	
	private PlanningRequestConsumerFactory consFct;

	private GoceConsumer goce1;
	private GoceConsumer goce2;
	private GoceConsumer goce3;
	
	private PlanningRequestStub procProv;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		log2file = DemoUtils.getLogFlag();
		if (log2file) {
			// log each consumer/provider lines to it's own file
			String path = ".\\target\\demo_logs\\pr\\";
			files = DemoUtils.createHandlers(path, CLIENT1, CLIENT2, CLIENT3, PROVIDER);
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
		LOG.entering(getClass().getName(), "setUp");
		String props = "testInt.properties";
		
		provFct = new PlanningRequestProviderFactory();
		provFct.setPropertyFile(props);
		provFct.start(PROVIDER);
		
		consFct = new PlanningRequestConsumerFactory();
		consFct.setPropertyFile(props);
		consFct.setProviderUri(provFct.getProviderUri());
		consFct.setBrokerUri(provFct.getBrokerUri());
		
		goce1 = new GoceConsumer(consFct.start(CLIENT1), null); // start a new instance of consumer
		goce2 = new GoceConsumer(consFct.start(CLIENT2), null);
		goce3 = new GoceConsumer(consFct.start(CLIENT3), null);
		
		procProv = consFct.start(PROVIDER+"1");
		
		LOG.exiting(getClass().getName(), "setUp");
	}

	@After
	public void tearDown() throws Exception {
		LOG.entering(getClass().getName(), "tearDown");
		if (consFct != null) {
			consFct.stop(procProv);
			consFct.stop(goce3.getPrStub());
			consFct.stop(goce2.getPrStub());
			consFct.stop(goce1.getPrStub());
		}
		goce3 = null;
		goce2 = null;
		goce1 = null;
		consFct = null;
		
		if (provFct != null) {
			provFct.stop();
		}
		provFct = null;
		LOG.exiting(getClass().getName(), "tearDown");
	}

	private Subscription createSub(String subId) {
		EntityKeyList entKeys = new EntityKeyList();
		entKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		
		EntityRequestList entReqs = new EntityRequestList();
		entReqs.add(new EntityRequest(null, true, true, true, false, entKeys));
		
		Subscription sub = new Subscription();
		sub.setSubscriptionId(new Identifier(subId));
		sub.setEntities(entReqs);
		return sub;
	}
	
	/**
	 * Sleep somewhere inbetween minimum and maximum millis.
	 * @param n
	 * @param x
	 */
	private void sleep(long n, long x) {
		try {
			int d = (int)(x - n);
			long t = (d > 0 ? new Random().nextInt(d) : 0) + n;
			Thread.sleep(t);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	
	protected void registerMonitor(String id, PrMonitor mon) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsRegister(subId={0})",
				new Object[] { id, CLIENT3+" -> "+PROVIDER });
		goce3.getPrStub().monitorPlanningRequestsRegister(createSub(id), mon);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsRegister() response: returning nothing",
				CLIENT3+" <- "+PROVIDER);
	}
	
	protected void unRegisterMonitor(String id) throws MALException, MALInteractionException {
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(id));
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsDeregister(subId={0})",
				new Object[] { id, CLIENT3+" -> "+PROVIDER });
		goce3.getPrStub().monitorPlanningRequestsDeregister(subs);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsDeregister() response: returning nothing",
				CLIENT3+" <- "+PROVIDER);
	}
	
	@Test
	public void testPpf() throws MALException, MALInteractionException, ParseException {
		// goce3 just monitors prs
		String prSubId = "prCons3prSubId";
		PrMonitor prMon = new PrMonitor();
		registerMonitor(prSubId, prMon);
		
		// processor/plugin monitors and changes task and pr statuses
		Processor proc = new Processor();
		provFct.setPlugin(proc);
		
		// super user registers definitions
		goce1.createPpfTaskDefIfMissing();
		goce1.createPpfPrDefIfMissing();
		
		// normal user submits instances
		goce2.createPpfInstsIfMissingAndDefsExist();
		
		// a sec later provider changes statuses
		boolean b1 = false;
		boolean b2 = false;
		// give the loop 10 seconds before failing
		long before = System.currentTimeMillis();
		do {
			sleep(100, 100);
			proc.process();
			b1 = proc.newTasks.isEmpty();
			b2 = proc.newPrs.isEmpty();
		} while (false == b1 && false == b2 && (System.currentTimeMillis() < before+10*1000L));
		
		assertTrue(proc.newTasks.isEmpty());
		assertTrue(proc.newPrs.isEmpty());
		
		// slight delay for notifications to travel before de-registration
		sleep(100, 100);
		
		unRegisterMonitor(prSubId);
		
		assertFalse(prMon.prStats.isEmpty());
		// 2 submit notifications + 1 task notif + 1 pr notif + 1 task notif + 1 pr notif = 6
		assertEquals(6, prMon.prStats.size());
	}
}
