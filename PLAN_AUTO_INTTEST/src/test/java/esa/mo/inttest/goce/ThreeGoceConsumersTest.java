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

import org.ccsds.moims.mo.com.structures.ObjectId;
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
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.ccsds.moims.mo.planningprototype.planningrequesttest.consumer.PlanningRequestTestStub;
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
 * Three consumers demo. One consumer managing defs, second managing instances, third only monitoring.
 */
public class ThreeGoceConsumersTest {

	/**
	 * Class receiving PR notifications.
	 */
	private final class PrMonitor extends PlanningRequestAdapter {
		
		protected PlanningRequestStatusDetailsList prStats = null;
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHdr, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats,
				Map qosProps) {
			LOG.log(Level.INFO, "{4}.monitorPlanningRequestsNotifyReceived(subId={0}, List:updateHeaders, " +
					"List:objectIds, List:prStatuses)\n  updateHeaders[]={1}\n  objectIds[]={2}\n  prStatuses[]={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats),
					"PrProvider -> "+Dumper.fromBroker("PrProvider", msgHdr) });
			this.prStats = prStats;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError error,
				Map qosProps) {
			LOG.log(Level.INFO, "{1}.monitorPlanningRequestsNotifyErrorReceived(error={0})",
					new Object[] { error, Dumper.fromBroker("PrProvider", msgHdr) });
		}
	}

	/**
	 * Class receiving Task notifications.
	 */
	private final class TaskMonitor extends PlanningRequestAdapter {
		
		protected TaskStatusDetailsList taskStats = null;
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksNotifyReceived(MALMessageHeader msgHdr, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
			LOG.log(Level.INFO, "{4}.monitorTasksNotifyReceived(subId={0}, List:updateHeaders, " +
				"List:objectIds, List:taskStatuses)\n  updateHeaders[]={1}\n  objectIds[]={2}\n  taskStatuses[]={3}",
				new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.taskStats(taskStats),
				Dumper.fromBroker("PrProvider", msgHdr) });
			this.taskStats = taskStats;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError error, Map qosProps) {
			LOG.log(Level.INFO, "{1}.monitorTasksNotifyErrorReceived(error={0})",
					new Object[] { error, Dumper.fromBroker("PrProvider", msgHdr) });
		}
	}

	/**
	 * Goce1 worker thread - adds definitions to Provider.
	 */
	private final class Worker1Thread extends Thread {
		
		private GoceConsumer cons;
		
		private Worker1Thread(String name, GoceConsumer cons) {
			super(name);
			this.cons = cons;
		}
		
		@Override
		public void run() {
			LOG.entering(getName(), "run");
			boolean taskDefCreated = false;
			boolean prDefCreated = false;
			while (!isInterrupted() && !taskDefCreated && !prDefCreated) {
				if (!taskDefCreated) {
					sleeep(1000, 3000);
					try {
						taskDefCreated = cons.createPpfTaskDefIfMissing();
					} catch (Exception e) {
						taskDefCreated = false;
						LOG.log(Level.WARNING, getName() + ": createPpfTaskDef: {0}", e);
						throw new RuntimeException(e);
					}
				}
				if (!prDefCreated) {
					sleeep(1000, 3000);
					try {
						prDefCreated = cons.createPpfPrDefIfMissing();
					} catch (Exception e) {
						prDefCreated = false;
						LOG.log(Level.WARNING, getName() + ": createPpfPrDef: {0}", e);
						throw new RuntimeException(e);
					}
				}
			}
			LOG.exiting(getName(), "run");
		}
	}

	/**
	 * Goce2 worker thread - submits instances to Provider.
	 */
	private final class Worker2Thread extends Thread {
		
		private GoceConsumer cons;
		
		private Worker2Thread(String name, GoceConsumer cons) {
			super(name);
			this.cons = cons;
		}

		@Override
		public void run() {
			LOG.entering(getName(), "run");
			boolean created = false;
			while (!isInterrupted() && !created) {
				sleeep(1000, 3000);
				try {
					created = cons.createPpfInstsIfMissingAndDefsExist();
				} catch (Exception e) {
					created = false;
					LOG.log(Level.WARNING, getName() + ": createPpfInst: {0}", e);
					throw new RuntimeException(e);
				}
			}
			LOG.exiting(getName(), "run");
		}
	}

	/**
	 * Third worker - monitors PR and Task statuses and adds ACCEPT status to them.
	 */
	private final class Processor extends PlanningRequestAdapter {
		
		private PlanningRequestTestStub testProv;
		private PlanningRequestStub prov;
		
		private List<Long> newTasks = Collections.synchronizedList(new LongList());
		private List<Long> newPrs = Collections.synchronizedList(new LongList());
		
		private String clientName;
		
		private Processor(PlanningRequestTestStub testProv, PlanningRequestStub prov) {
			this.testProv = testProv;
			this.prov = prov;
			clientName = prov.getConsumer().getURI().getValue();
			int i = clientName.indexOf('-');
			clientName = clientName.substring(i+1);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHdr, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats,
				Map qosProps) {
			for (int i = 0; i < updHdrs.size(); ++i) {
				UpdateHeader hdr = updHdrs.get(i);
				if (UpdateType.CREATION == hdr.getUpdateType()) {
					ObjectId obj = objIds.get(i);
					Long id = obj.getKey().getInstId();
					newPrs.add(id); // got new pr instance id to process later
				}
			}
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
			for (int i = 0; i < updHdrs.size(); ++i) {
				UpdateHeader hdr = updHdrs.get(i);
				if (UpdateType.CREATION == hdr.getUpdateType()) {
					ObjectId obj = objIds.get(i);
					Long id = obj.getKey().getInstId();
					newTasks.add(id); // got new task instance id to process later
				}
			}
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
		
		private boolean checkTaskAccept(Long id, TaskStatusDetails stat) {
			boolean doRemove = true;
			StatusRecord asr = findStatus(stat.getStatus(), InstanceState.ACCEPTED);
			if (null == asr) {
				// wait a sec before accepting
				StatusRecord csr = findStatus(stat.getStatus(), InstanceState.LAST_MODIFIED);
				if ((null == csr) || (System.currentTimeMillis() >= (csr.getTimeStamp().getValue()+1000L))) {
					asr = new StatusRecord(InstanceState.ACCEPTED, new Time(System.currentTimeMillis()), "accepted");
					if (null == stat.getStatus()) {
						stat.setStatus(new StatusRecordList());
					}
					stat.getStatus().add(asr);
					LongList taskIds = new LongList();
					taskIds.add(id);
					TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
					taskStats.add(stat);
					try {
						testProv.updateTaskStatus(taskIds, taskStats);
					} catch (MALException e) {
						LOG.log(Level.WARNING, "{1}: process: updateTaskStatus: err: {0}", new Object[] { e, clientName });
					} catch (MALInteractionException e) {
						LOG.log(Level.WARNING, "{1}: process: updateTaskStatus: err: {0}", new Object[] { e, clientName });
					}
				} else {
					doRemove = false; // wait before removing
				} // csr
			} // asr
			return doRemove;
		}
		
		private void processTasks() {
			for (int i0 = 0; i0 < newTasks.size(); ++i0) {
				Long id = newTasks.get(i0);
				LongList taskIds = new LongList();
				taskIds.add(id);
				TaskStatusDetailsList taskStats = null;
				try {
					taskStats = prov.getTaskStatus(taskIds);
				} catch (MALException e) {
					LOG.log(Level.WARNING, "{1}: process: getTaskStatus: err: {0}", new Object[] { e, clientName });
				} catch (MALInteractionException e) {
					LOG.log(Level.WARNING, "{1}: process: getTaskStats: err: {0}", new Object[] { e, clientName });
				}
				boolean doRemove = true;
				for (int i1 = 0; (null != taskStats) && (i1 < taskStats.size()); ++i1) {
					TaskStatusDetails stat = taskStats.get(i1);
					if (null != stat) {
						doRemove = checkTaskAccept(id, stat);
					} // stat
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
		
		private boolean checkPrAccept(Long id, PlanningRequestStatusDetails stat) {
			boolean doRemove = true;
			StatusRecord asr = findStatus(stat.getStatus(), InstanceState.ACCEPTED);
			if (null == asr) {
				// wait a sec before accepting
				StatusRecord csr = findStatus(stat.getStatus(), InstanceState.LAST_MODIFIED);
				// accept pr only after tasks are accepted
				boolean tasksAcc = areTasksAccepted(stat);
				if (tasksAcc && (null == csr || System.currentTimeMillis() >= (csr.getTimeStamp().getValue()+1000L))) {
					asr = new StatusRecord(InstanceState.ACCEPTED, new Time(System.currentTimeMillis()), "accepted");
					if (null == stat.getStatus()) {
						stat.setStatus(new StatusRecordList());
					}
					stat.getStatus().add(asr);
					LongList prIds = new LongList();
					prIds.add(id);
					PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
					prStats.add(stat);
					try {
						testProv.updatePrStatus(prIds, prStats);
					} catch (MALException e) {
						LOG.log(Level.WARNING, "{1}: process: updPrStatus: err: {0}", new Object[] { e, clientName });
					} catch (MALInteractionException e) {
						LOG.log(Level.WARNING, "{1}: process: updPrStatus: err: {0}", new Object[] { e, clientName });
					}
				} else {
					doRemove = false; // wait before removing
				} // csr
			} // asr
			return doRemove;
		}
		
		private void processPrs() {
			for (int i0 = 0; i0 < newPrs.size(); ++i0) {
				Long id = newPrs.get(i0);
				LongList prIds = new LongList();
				prIds.add(id);
				PlanningRequestStatusDetailsList prStats = null;
				try {
					prStats = prov.getPlanningRequestStatus(prIds);
				} catch (MALException e) {
					LOG.log(Level.WARNING, "{1}: process: getPrStatus: err: {0}", new Object[] { e, clientName });
				} catch (MALInteractionException e) {
					LOG.log(Level.WARNING, "{1}: process: getPrStatus: err: {0}", new Object[] { e, clientName });
				}
				boolean doRemove = true;
				for (int i1 = 0; (null != prStats) && (i1 < prStats.size()); ++i1) {
					PlanningRequestStatusDetails stat = prStats.get(i1);
					if (null != stat) {
						doRemove = checkPrAccept(id, stat);
					} // stat
				} // for
				if (doRemove) {
					newPrs.remove(i0);
				}
			} // for newPrs
		}
		
		/**
		 * Processes new Tasks and PRs.
		 */
		public void process() {
			processTasks();
			processPrs();
		}
	}
	
	/**
	 * Goce4 worker thread.
	 */
	private final class Worker3Thread extends Thread {
		
		private Processor proc;
		
		private Worker3Thread(String name, Processor proc) {
			super(name);
			this.proc = proc;
		}
		
		@Override
		public void run() {
			LOG.entering(getName(), "run");
			while (!isInterrupted()) {
				sleeep(250, 0);
				proc.process();
			}
			LOG.exiting(getName(), "run");
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
	
	private PlanningRequestTestStub procTestProv;
	private PlanningRequestStub procProv;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		log2file = DemoUtils.getLogFlag();
		if (log2file) {
			// trim down log spam
			DemoUtils.setLevels();
			// log each consumer/provider lines to it's own file
			files = new ArrayList<Handler>();
			String path = ".\\target\\demo_logs\\pr\\";
			files.add(DemoUtils.createHandler(CLIENT1, path));
			files.add(DemoUtils.createHandler(CLIENT2, path));
			files.add(DemoUtils.createHandler(CLIENT3, path));
			files.add(DemoUtils.createHandler(PROVIDER, path));
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
		consFct.setTestProviderUri(provFct.getTestProviderUri()); // testSupport connection
		
		goce1 = new GoceConsumer(consFct.start(CLIENT1), null); // start a new instance of consumer
		goce2 = new GoceConsumer(consFct.start(CLIENT2), null);
		goce3 = new GoceConsumer(consFct.start(CLIENT3), null);
		
		procTestProv = consFct.startTest(PROVIDER+"0"); // cons/prov names need to be unique within RMI
		procProv = consFct.start(PROVIDER+"1");
		
		LOG.exiting(getClass().getName(), "setUp");
	}

	@After
	public void tearDown() throws Exception {
		LOG.entering(getClass().getName(), "tearDown");
		if (consFct != null) {
			consFct.stop(procProv);
			consFct.stopTest(procTestProv);
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
	private void sleeep(long n, long x) {
		try {
			int d = (int)(x - n);
			long t = (d > 0 ? new Random().nextInt(d) : 0) + n;
			Thread.sleep(t);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	
	@Test
	public void testPpf() throws MALException, MALInteractionException, ParseException {
		LOG.entering(getClass().getName(), "testPpf");
		
		// goce1/worker1 submits definitions
		Thread worker1 = new Worker1Thread("Goce1", goce1);
		
		// goce2/worker2 submits instances
		Thread worker2 = new Worker2Thread("Goce2", goce2);
		
		// goce3 just monitors prs and tasks
		String taskSubId = "prCons3taskSubId";
		TaskMonitor taskMon = new TaskMonitor();
		LOG.log(Level.INFO, "{1}.monitorTasksRegister(subId={0})",
				new Object[] { taskSubId, CLIENT3+" -> "+PROVIDER });
		goce3.getPrStub().monitorTasksRegister(createSub(taskSubId), taskMon);
		LOG.log(Level.INFO, "{0}.monitorTasksRegister() response: returning nothing", CLIENT3+" <- "+PROVIDER);
		
		String prSubId = "prCons3prSubId";
		PrMonitor prMon = new PrMonitor();
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsRegister(subId={0})",
				new Object[] { prSubId, CLIENT3+" -> "+PROVIDER });
		goce3.getPrStub().monitorPlanningRequestsRegister(createSub(prSubId), prMon);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsRegister() response: returning nothing", CLIENT3+" <- "+PROVIDER);
		
		// processor/worker3 monitors and changes task and pr statuses
		Processor proc = new Processor(procTestProv, procProv);
		
		String taskSubId2 = "prCons4TaskSub";
		procProv.monitorTasksRegister(createSub(taskSubId2), proc);
		
		String prSubId2 = "prCons4PrSub";
		procProv.monitorPlanningRequestsRegister(createSub(prSubId2), proc);
		
		Thread worker3 = new Worker3Thread("TestProcessor", proc);
		
		worker1.start();
		worker2.start();
		worker3.start();
		
		sleeep(11*1000L, 0); // 10 sec
		
		worker1.interrupt();
		worker2.interrupt();
		worker3.interrupt();
		
		try {
			worker1.join(4*1000L);
		} catch (InterruptedException e) {
			LOG.log(Level.WARNING, "worker1 interrupted: ", e);
		}
		try {
			worker2.join(4*1000L);
		} catch (InterruptedException e) {
			LOG.log(Level.WARNING, "worker2 interrupted: ", e);
		}
		try {
			worker3.join(4*1000L);
		} catch (InterruptedException e) {
			LOG.log(Level.WARNING, "worker3 interrupted: ", e);
		}
		
		IdentifierList prSubs = new IdentifierList();
		prSubs.add(new Identifier(prSubId2));
		procProv.monitorPlanningRequestsDeregister(prSubs);
		
		IdentifierList taskSubs = new IdentifierList();
		taskSubs.add(new Identifier(taskSubId2));
		procProv.monitorTasksDeregister(taskSubs);
		
		prSubs.clear();
		prSubs.add(new Identifier(prSubId));
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsDeregister(subId={0})",
				new Object[] { prSubId, CLIENT3+" -> "+PROVIDER });
		goce3.getPrStub().monitorPlanningRequestsDeregister(prSubs);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsDeregister() response: returning nothing",
				CLIENT3+" <- "+PROVIDER);
		
		taskSubs.clear();
		taskSubs.add(new Identifier(taskSubId));
		LOG.log(Level.INFO, "{1}.monitorTasksDeregister(subId={0})",
				new Object[] { taskSubId, CLIENT3+" -> "+PROVIDER });
		goce3.getPrStub().monitorTasksDeregister(taskSubs);
		LOG.log(Level.INFO, "{0}.monitorTasksDeregister() response: returning nothing", CLIENT3+" <- "+PROVIDER);
		
		assertNotNull(taskMon.taskStats); // assuming 'goce3' received at least one pr and task notification
		assertNotNull(prMon.prStats);
		
		LOG.exiting(getClass().getName(), "testPpf");
	}
}
