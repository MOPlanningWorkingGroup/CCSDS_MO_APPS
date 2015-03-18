package esa.mo.inttest.goce;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Map;
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
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
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
import org.junit.Before;
import org.junit.Test;

import esa.mo.inttest.goce.GoceConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.Dumper;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

/**
 * Simultaneous GOCE consumers test. One consumer managing defs, second managing instances, third only monitoring.
 */
public class ThreeGoceConsumersTest {

	/**
	 * Class receiving PR notifications.
	 */
	private final class PrMonitor extends PlanningRequestAdapter {
		
		protected PlanningRequestStatusDetailsList prStats = null;
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats,
				Map qosProps) {
			LOG.log(Level.INFO, "pr monitor notify: subId={0}, updateHeaders={1}, objectIds={2}, prStatuses={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats) });
			this.prStats = prStats;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
				Map qosProps) {
			LOG.log(Level.INFO, "pr monitor notify error");
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
				throws MALException {
			LOG.log(Level.INFO, "pr other notify");
		}
	}

	/**
	 * Class receiving Task notifications.
	 */
	private final class TaskMonitor extends PlanningRequestAdapter {
		
		protected TaskStatusDetailsList taskStats = null;
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
			LOG.log(Level.INFO, "task monitor notify: subId={0}, updateHeaders={1}, objectIds={2}, taskStatuses={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.taskStats(taskStats) });
			this.taskStats = taskStats;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
			LOG.log(Level.INFO, "task monitor notify error");
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
				throws MALException {
			LOG.log(Level.INFO, "task other notify");
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
						LOG.log(Level.WARNING, getName() + ": createPpfTaskDef: ", e);
						throw new RuntimeException(e);
					}
				}
				if (!prDefCreated) {
					sleeep(1000, 3000);
					try {
						prDefCreated = cons.createPpfPrDefIfMissing();
					} catch (Exception e) {
						prDefCreated = false;
						LOG.log(Level.WARNING, getName() + ": createPpfPrDef: ", e);
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
					LOG.log(Level.WARNING, getName() + ": createPpfInst", e);
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
		
		private Processor(PlanningRequestTestStub testProv, PlanningRequestStub prov) {
			this.testProv = testProv;
			this.prov = prov;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats,
				Map qosProps) {
			LOG.log(Level.INFO, "pr monitor notify: subId={0}, updateHeaders={1}, objectIds={2}, prStatuses={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats) });
			for (int i = 0; i < updHdrs.size(); ++i) {
				UpdateHeader hdr = updHdrs.get(i);
				if (UpdateType.CREATION == hdr.getUpdateType()) {
					ObjectId obj = objIds.get(i);
					Long id = obj.getKey().getInstId();
					newPrs.add(id); // got new pr instance id to process later
					LOG.log(Level.INFO, "added pr \"{0}/{1}\" for processing", new Object[] { id, prStats.get(i).getPrInstName() });
				}
			}
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
			LOG.log(Level.INFO, "task monitor notify: subId={0}, updateHeaders={1}, objectIds={2}, taskStatuses={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.taskStats(taskStats) });
			for (int i = 0; i < updHdrs.size(); ++i) {
				UpdateHeader hdr = updHdrs.get(i);
				if (UpdateType.CREATION == hdr.getUpdateType()) {
					ObjectId obj = objIds.get(i);
					Long id = obj.getKey().getInstId();
					newTasks.add(id); // got new task instance id to process later
					LOG.log(Level.INFO, "added task \"{0}/{1}\" for processing", new Object[] { id ,taskStats.get(i).getTaskInstName() });
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
				if ((null == csr) || (System.currentTimeMillis() >= (csr.getDate().getValue()+1000L))) {
					LOG.log(Level.INFO, "ACCEPTing task {0}", id);
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
						LOG.log(Level.INFO, "process: updTaskStatus: err: {0}", e);
					} catch (MALInteractionException e) {
						LOG.log(Level.INFO, "process: updTaskStatus: err: {0}", e);
					}
				} else {
					doRemove = false; // wait before removing
					LOG.log(Level.INFO, "waiting a sec before ACCEPTing task {0}", id);
				} // csr
			} else {
				LOG.log(Level.INFO, "task {0} is already ACCEPTed", id);
			} // asr
			return doRemove;
		}
		
		private void processTasks() {
			for (int i0 = 0; i0 < newTasks.size(); ++i0) {
				Long id = newTasks.get(i0);
				LOG.log(Level.INFO, "processing task {0}", id);
				LongList taskIds = new LongList();
				taskIds.add(id);
				TaskStatusDetailsList taskStats = null;
				try {
					taskStats = prov.getTaskStatus(taskIds);
				} catch (MALException e) {
					LOG.log(Level.INFO, "process: getTaskStatus: err: {0}", e);
				} catch (MALInteractionException e) {
					LOG.log(Level.INFO, "process: getTaskStats: err: {0}", e);
				}
				boolean doRemove = true;
				for (int i1 = 0; (null != taskStats) && (i1 < taskStats.size()); ++i1) {
					TaskStatusDetails stat = taskStats.get(i1);
					if (null != stat) {
						doRemove = checkTaskAccept(id, stat);
					} // stat
				} // for
				if (doRemove) {
					newTasks.remove(i0);
				}
			}
		}
		
		protected boolean areTasksAccepted(PlanningRequestStatusDetails prStat) {
			boolean ok = true;
			for (int i = 0; (null != prStat.getTaskStatuses()) && (i < prStat.getTaskStatuses().size()); ++i) {
				TaskStatusDetails taskStat = prStat.getTaskStatuses().get(i);
				StatusRecord asr = findStatus(taskStat.getStatus(), InstanceState.ACCEPTED);
				if (null == asr) {
					ok = false;
					LOG.log(Level.INFO, "task \"{0}\" of pr \"{1}\" is not ACCEPTED",
							new Object[] { taskStat.getTaskInstName(), prStat.getPrInstName() });
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
				if (tasksAcc && (null == csr || System.currentTimeMillis() >= (csr.getDate().getValue()+1000L))) {
					LOG.log(Level.INFO, "ACCEPTing pr {0}", id);
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
						LOG.log(Level.INFO, "process: updPrStatus: err: {0}", e);
					} catch (MALInteractionException e) {
						LOG.log(Level.INFO, "process: updPrStatus: err: {0}", e);
					}
				} else {
					doRemove = false; // wait before removing
					LOG.log(Level.INFO, "waiting a sec before ACCEPTing pr {0}", id);
				} // csr
			} else {
				LOG.log(Level.INFO, "pr {0} is already ACCEPTed", id);
			} // asr
			return doRemove;
		}
		
		private void processPrs() {
			for (int i0 = 0; i0 < newPrs.size(); ++i0) {
				Long id = newPrs.get(i0);
				LOG.log(Level.INFO, "processing pr {0}", id);
				LongList prIds = new LongList();
				prIds.add(id);
				PlanningRequestStatusDetailsList prStats = null;
				try {
					prStats = prov.getPlanningRequestStatus(prIds);
				} catch (MALException e) {
					LOG.log(Level.INFO, "process: getPrStatus: err: {0}", e);
				} catch (MALInteractionException e) {
					LOG.log(Level.INFO, "process: getPrStatus: err: {0}", e);
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
			}
		}
		
		/**
		 * Processes new Tasks an PRs.
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
				sleeep(100, 0);
				proc.process();
			}
			LOG.exiting(getName(), "run");
		}
	}
	
	private static final Logger LOG = Logger.getLogger(ThreeGoceConsumersTest.class.getName());
	
	private PlanningRequestProviderFactory provFct;
	
	private PlanningRequestConsumerFactory consFct;

	private GoceConsumer goce1;
	private GoceConsumer goce2;
	private GoceConsumer goce3;
	
	private PlanningRequestTestStub procTestProv;
	private PlanningRequestStub procProv;
	
	@Before
	public void setUp() throws Exception {
		LOG.entering(getClass().getName(), "setUp");
		String props = "testInt.properties";
		
		provFct = new PlanningRequestProviderFactory();
		provFct.setPropertyFile(props);
		provFct.start();
		
		consFct = new PlanningRequestConsumerFactory();
		consFct.setPropertyFile(props);
		consFct.setProviderUri(provFct.getProviderUri());
		consFct.setBrokerUri(provFct.getBrokerUri());
		consFct.setTestProviderUri(provFct.getTestProviderUri()); // testSupport connection
		
		goce1 = new GoceConsumer(consFct.start()); // start a new instance of consumer
		goce2 = new GoceConsumer(consFct.start());
		goce3 = new GoceConsumer(consFct.start());
		
		procTestProv = consFct.startTest(); // test support for status updates
		procProv = consFct.start();
		
		LOG.exiting(getClass().getName(), "setUp");
	}

	@After
	public void tearDown() throws Exception {
		LOG.entering(getClass().getName(), "tearDown");
		if (consFct != null) {
			consFct.stop(procProv);
			consFct.stopTest(procTestProv);
			consFct.stop(goce3.getStub());
			consFct.stop(goce2.getStub());
			consFct.stop(goce1.getStub());
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
		goce3.getStub().monitorTasksRegister(createSub(taskSubId), taskMon);
		String prSubId = "prCons3prSubId";
		PrMonitor prMon = new PrMonitor();
		goce3.getStub().monitorPlanningRequestsRegister(createSub(prSubId), prMon);
		
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
		
		LOG.log(Level.INFO, "sleeping..");
		sleeep(11*1000L, 0); // 10 sec
		LOG.log(Level.INFO, "waking..");
		
		worker1.interrupt();
		worker2.interrupt();
		
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
		
		IdentifierList prSubs = new IdentifierList();
		prSubs.add(new Identifier(prSubId2));
		procProv.monitorPlanningRequestsDeregister(prSubs);
		
		IdentifierList taskSubs = new IdentifierList();
		taskSubs.add(new Identifier(taskSubId2));
		procProv.monitorTasksDeregister(taskSubs);
		
		prSubs.clear();
		prSubs.add(new Identifier(prSubId));
		goce3.getStub().monitorPlanningRequestsDeregister(prSubs);
		
		taskSubs.clear();
		taskSubs.add(new Identifier(taskSubId));
		goce3.getStub().monitorTasksDeregister(taskSubs);
		
		assertNotNull(taskMon.taskStats); // assuming 'goce3' received at least one pr and task notification
		assertNotNull(prMon.prStats);
		
		LOG.exiting(getClass().getName(), "testPpf");
	}
}
