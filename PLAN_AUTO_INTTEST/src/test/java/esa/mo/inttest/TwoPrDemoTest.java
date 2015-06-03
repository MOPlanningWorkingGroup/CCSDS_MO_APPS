package esa.mo.inttest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.mo.inttest.DemoUtils;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProvider;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;
import esa.mo.inttest.pr.provider.Plugin;
import esa.mo.inttest.pr.provider.InstStore;

/**
 * Demonstrate interaction between two PR providers (instrument&gs) and consumer.
 */
public class TwoPrDemoTest {

	/**
	 * Plugin attached to instrument pr provider. Remembers submitted prs and
	 * submits them to gs pr provider. Listens for notifications from gs pr and
	 * forwards them to consumer.
	 */
	public static class InstrPrProcessor extends PlanningRequestAdapter implements Plugin {
		
		private PlanningRequestStub gs;
		private PlanningRequestProvider instr;
		private Long gsPrDefId = null;
		private LongList gsTaskDefIds = null;
		private LongList prInstIds = new LongList();
		private List<LongList> taskInstIds = new ArrayList<LongList>();
		
		/**
		 * Ctor.
		 * @param pr
		 */
		public InstrPrProcessor(PlanningRequestStub pr) {
			this.gs = pr;
		}
		
		/**
		 * Parent provider this plugin is plugged into.
		 * @see esa.mo.inttest.pr.provider.Plugin#setProv(esa.mo.inttest.pr.provider.PlanningRequestProvider)
		 */
		public void setProv(PlanningRequestProvider pr) {
			this.instr = pr;
		}
		
		/**
		 * Set pr def id to use for submission to gs.
		 * @param id
		 */
		public void setGsPrDefId(Long id) {
			this.gsPrDefId = id;
		}
		
		/**
		 * Set task def ids to use for submission to gs.
		 * @param ids
		 */
		public void setGsTaskDefIds(LongList ids) {
			this.gsTaskDefIds = ids;
		}
		
		// forward pr status update from gs to instr consumer
		private void forward(PlanningRequestStatusDetails stat) {
			InstStore.PrItem it = instr.getInstStore().findPrItem(stat.getPrInstId());
			if (null != it) {
				StatusRecord sr = Util.findStatus(stat.getStatus(), InstanceState.PLAN_CONFLICT);
				InstanceState is = (null != sr) ? sr.getState() : InstanceState.PLAN_CONFLICT;
				Time t = (null != sr) ? sr.getTimeStamp() : Util.currentTime();
				String c = (null != sr) ? sr.getComment() : "planning conflict";
				// forward only change
				StatusRecordList srl = new StatusRecordList();
				srl.add(Util.addOrUpdateStatus(it.getStat(), is, t, c));
				PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
				prStats.add(new PlanningRequestStatusDetails(stat.getPrInstId(), srl, null));
				try {
					instr.publishPr(UpdateType.UPDATE, prStats);
				} catch (MALException e) {
					LOG.log(Level.INFO, "instr pr status forward error: mal: {0}", e);
				} catch (MALInteractionException e) {
					LOG.log(Level.INFO, "instr pr status forward error: mal interaction: {0}", e);
				}
			}
		}
		
		/**
		 * Pr notifications from gs pr.
		 * @see org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter#monitorPlanningRequestsNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.structures.Identifier, org.ccsds.moims.mo.mal.structures.UpdateHeaderList, org.ccsds.moims.mo.com.structures.ObjectIdList, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList, java.util.Map)
		 */
		@SuppressWarnings("rawtypes")
		public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHdr, Identifier id,
				UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats, Map qosProps)
		{
			LOG.log(Level.INFO, "{4}.monitorPlanningRequestsNotifyReceived(id={0}, List:updHdrs, List:objIds, " +
					"List:prStats, Map:qosProps)\n  updHdrs[]={1}\n  objIds[]={2}\n  prStats[]={3}",
					new Object[] { id, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats),
					Dumper.fromBroker(PR_PROV2, msgHdr) });
			for (int i = 0; i < updHdrs.size(); ++i) {
				UpdateHeader uh = updHdrs.get(i);
				if (UpdateType.UPDATE == uh.getUpdateType()) {
					PlanningRequestStatusDetails prStat = prStats.get(i);
					forward(prStat);
				}
			}
		}
		
		private void forwardTask(TaskStatusDetails stat) {
			InstStore.TaskItem taskItem = instr.getInstStore().findTaskItem(stat.getTaskInstId());
			if (null != taskItem) {
				StatusRecord sr = Util.findStatus(stat.getStatus(), InstanceState.PLAN_CONFLICT);
				InstanceState is = (null != sr) ? sr.getState() : InstanceState.PLAN_CONFLICT;
				Time t = (null != sr) ? sr.getTimeStamp() : Util.currentTime();
				String c = (null != sr) ? sr.getComment() : "planning conflict";
				// forward only change
				StatusRecordList srl = new StatusRecordList();
				srl.add(Util.addOrUpdateStatus(taskItem.getStat(), is, t, c));
				TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
				taskStats.add(new TaskStatusDetails(stat.getTaskInstId(), srl));
				PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
				prStats.add(new PlanningRequestStatusDetails(taskItem.getTask().getPrInstId(), null, taskStats));
				try {
					instr.publishPr(UpdateType.UPDATE, prStats);
				} catch (MALException e) {
					LOG.log(Level.INFO, "instr pr status forward error: mal: {0}", e);
				} catch (MALInteractionException e) {
					LOG.log(Level.INFO, "instr pr status forward error: mal interaction: {0}", e);
				}
			}
		}
		
		/**
		 * Task notifications from gs pr.
		 * @see org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter#monitorTasksNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.structures.Identifier, org.ccsds.moims.mo.mal.structures.UpdateHeaderList, org.ccsds.moims.mo.com.structures.ObjectIdList, org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList, java.util.Map)
		 */
		@SuppressWarnings("rawtypes")
		public void monitorTasksNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
				ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProperties)
		{
			LOG.log(Level.INFO, "{4}.monitorTasksNotifyReceived(id={0}, List:updHdrs, List:objIds, List:taskStats)" +
					"\n  updHdrs[]={1}\n  objIds[]={2}\n  taskStats[]={3}", new Object[] { id, Dumper.updHdrs(updHdrs),
					Dumper.objIds(objIds), Dumper.taskStats(taskStats), Dumper.fromBroker(PR_PROV2, msgHdr) });
			for (int i = 0; i < updHdrs.size(); ++i) {
				UpdateHeader uh = updHdrs.get(i);
				if (UpdateType.UPDATE == uh.getUpdateType()) {
					TaskStatusDetails taskStat = taskStats.get(i);
					forwardTask(taskStat);
				}
			}
		}
		
		/**
		 * Pr submission notification from plugin owner.
		 * @see esa.mo.inttest.pr.provider.Plugin#onPrSubmit(java.lang.Long, java.lang.Long, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails)
		 */
		public 	void onPrSubmit(PlanningRequestInstanceDetailsList prs) {
			for (PlanningRequestInstanceDetails pr : prs) {
				prInstIds.add(pr.getId());
				LongList taskIds = null;
				if (null != pr.getTasks() && !pr.getTasks().isEmpty()) {
					taskIds = new LongList();
					for (TaskInstanceDetails t: pr.getTasks()) {
						taskIds.add(t.getId());
					}
				}
				taskInstIds.add(taskIds);
			}
		}
		
		/**
		 * Pr update notification from plugin owner.
		 * @see esa.mo.inttest.pr.provider.Plugin#onPrUpdate(java.lang.Long, java.lang.Long, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails)
		 */
		public void onPrUpdate(PlanningRequestInstanceDetailsList prs, PlanningRequestStatusDetailsList stats) {
			// nothing
		}
		
		/**
		 * Pr removal notification from plugin owner.
		 * @see esa.mo.inttest.pr.provider.Plugin#onPrRemove(java.lang.Long)
		 */
		public void onPrRemove(LongList ids) {
			// nothing
		}
		
		/**
		 * Process (do planning) submitted prs.
		 * @throws MALException
		 * @throws MALInteractionException
		 */
		public void doPlanning() throws MALException, MALInteractionException {
			// process submitted prs
			for (int i = 0; i < prInstIds.size(); ++i) {
				Long prInstId = prInstIds.get(i);
				LongList taskIds = (null != taskInstIds && i < taskInstIds.size()) ? taskInstIds.get(i) : null;
				
				PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(prInstId, gsPrDefId, null);
				
				if (null != taskIds && null != gsTaskDefIds) {
					TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(taskIds.get(0), gsTaskDefIds.get(0), null);
					taskInst.setPrInstId(prInst.getId());
					TaskInstanceDetails taskInst2 = PlanningRequestConsumer.createTaskInst(taskIds.get(1), gsTaskDefIds.get(1), null);
					taskInst2.setPrInstId(prInst.getId());
					prInst.setTasks(new TaskInstanceDetailsList());
					prInst.getTasks().add(taskInst);
					prInst.getTasks().add(taskInst2);
				}
				PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
				insts.add(prInst);
				
				PlanningRequestStatusDetailsList prStats = gs.submitPlanningRequest(insts);
				
				assertNotNull(prStats);
				assertFalse(prStats.isEmpty());
			}
		}
	}
	
	/**
	 * Plugin attached to gs pr provider. Remembers submitted prs and processes them later.
	 */
	public static class GsPrProcessor implements Plugin {
		
		private PlanningRequestProvider prov;
		private LongList prInstIds = new LongList();
		
		/**
		 * Parent provider this plugin is plugged into.
		 * @see esa.mo.inttest.pr.provider.Plugin#setProv(esa.mo.inttest.pr.provider.PlanningRequestProvider)
		 */
		public void setProv(PlanningRequestProvider prov) {
			this.prov = prov;
		}
		
		/**
		 * Pr submission notification from plugin owner.
		 * @see esa.mo.inttest.pr.provider.Plugin#onPrSubmit(java.lang.Long, java.lang.Long, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails)
		 */
		public 	void onPrSubmit(PlanningRequestInstanceDetailsList prs) {
			for (PlanningRequestInstanceDetails pr : prs) {
				prInstIds.add(pr.getId());
			}
		}
		
		/**
		 * Pr update notification from plugin owner.
		 * @see esa.mo.inttest.pr.provider.Plugin#onPrUpdate(java.lang.Long, java.lang.Long, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails)
		 */
		public void onPrUpdate(PlanningRequestInstanceDetailsList prs, PlanningRequestStatusDetailsList stats) {
			// nothing
		}
		
		/**
		 * Pr removal notification from plugin owner.
		 * @see esa.mo.inttest.pr.provider.Plugin#onPrRemove(java.lang.Long)
		 */
		public void onPrRemove(LongList ids) {
			// nothing
		}
		
		/**
		 * Process (do planning) submitted prs.
		 * @throws MALException
		 * @throws MALInteractionException
		 */
		public void doPlanning() throws MALException, MALInteractionException {
			// process submitted prs
			for (Long id: prInstIds) {
				InstStore.PrItem it = prov.getInstStore().findPrItem(id);
				if (null != it) {
					// publish only change
					StatusRecordList srl = new StatusRecordList();
					srl.add(Util.addOrUpdateStatus(it.getStat(), InstanceState.PLAN_CONFLICT,
							Util.currentTime(), "planning conflict"));
					PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
					prStats.add(new PlanningRequestStatusDetails(it.getStat().getPrInstId(), srl, null));
					prov.publishPr(UpdateType.UPDATE, prStats);
				}
			}
		}
	}
	
	private static final Logger LOG = Logger.getLogger(TwoPrDemoTest.class.getName());
	
	private static final String PR_PROV1 = "InstrPrProvider";
	private static final String PR_PROV2 = "GsPrProvider";
	private static final String CLIENT = "powerUser"; // power client log
	private static final String CLIENT1 = "normalUser"; // normal client log
	private static final String CLIENT2 = CLIENT+"2"; // power client log (2nd connection)
	private static final String CLIENT3 = PR_PROV1+"2"; // instrument pr provider (plugin) connection to gs pr provider
	
	private static boolean log2file = false;
	private static List<Handler> files = null;
	
	private PlanningRequestProviderFactory prProvFct; // instrument
	private PlanningRequestProviderFactory gsPrProvFct; // groundStation
	private PlanningRequestConsumerFactory prConsFct;
	
	// power user operations to instrumentation pr prov
	private PlanningRequestConsumer powerInstrCons;
	// power user operations to gs pr prov
	private PlanningRequestConsumer powerGsCons;
	// normal user operations
	private PlanningRequestConsumer normalInstrCons;
	// instr Pr provider (Plugin) connection to gs Pr provider
	private PlanningRequestStub instrProv2GsProvCons;
	// instr & gs (sub-)domains
	private Identifier dom = new Identifier("desd");
	private Identifier instrSubDom = new Identifier("instr");
	private Identifier gsSubDom = new Identifier("gs");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		log2file = DemoUtils.getLogFlag();
		if (log2file) {
			String path = ".\\target\\demo_logs\\two_pr\\";
			// log each consumer/provider lines to it's own file
			files = DemoUtils.createHandlers(path, CLIENT, CLIENT1, PR_PROV1, PR_PROV2);
		}
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		if (log2file) {
			DemoUtils.removeHandlers(files);
		}
	}
	
	// provider has no domain
	// \ instr publisher runs in domain "desd.instr"
	// \ gs publisher runs in domain "desd.gs"
	// consumer runs in domain "desd"
	// \ instr subscriber listens to sub-domain "instr"
	// \ gs subscriber listens to sub-domain "gs"
	
	@Before
	public void setUp() throws Exception {
		String fn = "testInt.properties";
		
		IdentifierList instrDom = new IdentifierList();
		instrDom.add(dom);
		instrDom.add(instrSubDom);
		
		prProvFct = new PlanningRequestProviderFactory();
		prProvFct.setPropertyFile(fn);
		prProvFct.setDomain(instrDom); // publisher domain
		prProvFct.start(PR_PROV1);
		
		URI broker = prProvFct.getBrokerUri();
		
		IdentifierList gsDom = new IdentifierList();
		gsDom.add(dom);
		gsDom.add(gsSubDom);
		
		gsPrProvFct = new PlanningRequestProviderFactory();
		gsPrProvFct.setPropertyFile(fn);
		gsPrProvFct.setBrokerUri(broker);
		gsPrProvFct.setDomain(gsDom); // publisher domain
		gsPrProvFct.start(PR_PROV2);
		
		prConsFct = new PlanningRequestConsumerFactory();
		prConsFct.setPropertyFile(fn);
		prConsFct.setProviderUri(prProvFct.getProviderUri());
		prConsFct.setBrokerUri(broker);
		// consumer runs in default "desd" domain
		// power instr user to instr pr prov
		powerInstrCons = new PlanningRequestConsumer(prConsFct.start(CLIENT));
		// normal instr user to instr pr ptov
		normalInstrCons = new PlanningRequestConsumer(prConsFct.start(CLIENT1));
		normalInstrCons.setBrokerName(PR_PROV1); // for pretty notification logs
		// power gs user to gs pr prov
		prConsFct.setProviderUri(gsPrProvFct.getProviderUri());
		// consumer runs in default "desd" domain
		powerGsCons = new PlanningRequestConsumer(prConsFct.start(CLIENT2));
		// instr pr (plugin) as gs pr consumer
		instrProv2GsProvCons = prConsFct.start(CLIENT3);
	}
	
	@After
	public void tearDown() throws Exception {
		prConsFct.stop(instrProv2GsProvCons);
		prConsFct.stop(powerGsCons.getStub());
		prConsFct.stop(normalInstrCons.getStub());
		prConsFct.stop(powerInstrCons.getStub());
		
		gsPrProvFct.stop();
		prProvFct.stop();
	}
	
	private LongList gsAddPrDefs() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails def = PlanningRequestConsumer.createPrDef("gs pr def 1", "gs pr def 1");
		def.setId(0L);
		
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		
		LongList ids = powerGsCons.getStub().addDefinition(DefinitionType.PLANNING_REQUEST_DEF, defs);
		if (null != ids && !ids.isEmpty()) {
			def.setId(ids.get(0));
		}
		return ids;
	}
	
	private LongList instrAddPrDefs() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails def = PlanningRequestConsumer.createPrDef("instr pr def 1", "instr pr def 1");
		def.setId(0L);
		
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		
		LongList ids = powerInstrCons.getStub().addDefinition(DefinitionType.PLANNING_REQUEST_DEF, defs);
		if (null != ids && !ids.isEmpty()) {
			def.setId(ids.get(0));
		}
		return ids;
	}
	
	private void registerPrMonitor(String id, IdentifierList dom, PlanningRequestStub prs, PlanningRequestAdapter pra,
			String cl, String pr) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsRegister(subId={0})", new Object[] { id, cl + " -> " + pr });
		prs.monitorPlanningRequestsRegister(Util.createSub(id, dom), pra);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsRegister() response: returning nothing", cl + " <- " + pr);
	}
	
	private void deRegisterPrMonitor(String id, PlanningRequestStub prs, String cl, String pr)
			throws MALException, MALInteractionException {
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(id));
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsDeregister(subId={0})", new Object[] { id, cl + " -> " + pr });
		prs.monitorPlanningRequestsDeregister(subs);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsDeregister() response: returning nothing", cl + " <- " + pr);
	}

	private AtomicLong lastId = new AtomicLong(0L);
	
	private long generateId() {
		return lastId.incrementAndGet();
	}
	
	private PlanningRequestInstanceDetails addPrInsts(Long prDefId) throws MALException, MALInteractionException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(generateId(), prDefId, "instrument pr instance 1");
		
		PlanningRequestStatusDetails prStat = normalInstrCons.submitPr(prInst);
		
		assertNotNull(prStat);
		assertEquals(prInst.getId(), prStat.getPrInstId());
		// has status records
		assertNotNull(prStat.getStatus());
		assertFalse(prStat.getStatus().isEmpty());
		
		return prInst;
	}
	
	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {
			// ignore
		}
	}
	
	@Test
	public void testPrWithoutTasks() throws MALException, MALInteractionException {
		// instr pr plugin
		InstrPrProcessor instrPrProc = new InstrPrProcessor(instrProv2GsProvCons);
		prProvFct.setPlugin(instrPrProc);
		// gs pr plugin
		GsPrProcessor gsPrProc = new GsPrProcessor();
		gsPrProvFct.setPlugin(gsPrProc);
		
		// PR power user adds defs to gs prov
		LongList gsPrDefIds = gsAddPrDefs();
		
		assertNotNull(gsPrDefIds);
		assertFalse(gsPrDefIds.isEmpty());
		assertNotNull(gsPrDefIds.get(0));
		assertFalse(0L == gsPrDefIds.get(0));
		
		// tell instr processor to use that definition id
		instrPrProc.setGsPrDefId(gsPrDefIds.get(0));
		
		// PR power user adds defs to instr prov
		LongList instrPrDefIds = instrAddPrDefs();
		
		assertNotNull(instrPrDefIds);
		assertFalse(instrPrDefIds.isEmpty());
		assertNotNull(instrPrDefIds.get(0));
		assertFalse(0L == instrPrDefIds.get(0));
		
		// PR normal user subscribes to instr prov
		IdentifierList instrDom = new IdentifierList();
		instrDom.add(instrSubDom);
		String sub2Id = "instrPrSub";
		registerPrMonitor(sub2Id, instrDom, normalInstrCons.getStub(), normalInstrCons, CLIENT1, PR_PROV1);
		
		// instr prov subscribes to gs prov
		IdentifierList gsDom = new IdentifierList();
		gsDom.add(gsSubDom);
		String sub4Id = "gsPrSub";
		registerPrMonitor(sub4Id, gsDom, instrProv2GsProvCons, instrPrProc, PR_PROV1, PR_PROV2);
		
		// normal user submits instances
		PlanningRequestInstanceDetails prInst = addPrInsts(instrPrDefIds.get(0));
		
		assertNotNull(prInst);
		assertNotNull(prInst.getId());
		assertFalse(0L == prInst.getId());
		
		sleep(100);
		
		// at some point instr pr prov "does planning" and submits to gs pr prov
		instrPrProc.doPlanning();
		
		sleep(100);
		
		// at some point gs pr prov "does planning" and notifies results
		gsPrProc.doPlanning();
		
		sleep(100);
		
		// instr pr un-subscribes
		deRegisterPrMonitor(sub4Id, instrProv2GsProvCons, PR_PROV1, PR_PROV2);
		
		// pr normal user un-subscribes
		deRegisterPrMonitor(sub2Id, normalInstrCons.getStub(), CLIENT1, PR_PROV1);
	}
	
	private Object[] gsAddPrDefsWithTasks() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails def = PlanningRequestConsumer.createPrDef("gs pr def 2", "gs pr def 2");
		def.setId(0L);
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		
		LongList prDefIds = powerGsCons.getStub().addDefinition(DefinitionType.PLANNING_REQUEST_DEF, defs);
		def.setId(prDefIds.get(0));
		
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("gs task def 1", "gs task def 1");
		taskDef.setId(0L);
		TaskDefinitionDetails taskDef2 = PlanningRequestConsumer.createTaskDef("gs task def 2", "gs task def 2");
		taskDef2.setId(0L);
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		taskDefs.add(taskDef2);
		
		LongList taskDefIds = powerGsCons.getStub().addDefinition(DefinitionType.TASK_DEF, taskDefs);
		taskDef.setId(taskDefIds.get(0));
		taskDef2.setId(taskDefIds.get(1));
		
		return new Object[] { prDefIds, taskDefIds };
	}
	
	private Object[] instrAddPrDefsWithTasks() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails def = PlanningRequestConsumer.createPrDef("instr pr def 2", "instr pr def 2");
		def.setId(0L);
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		
		LongList prDefIds = powerInstrCons.getStub().addDefinition(DefinitionType.PLANNING_REQUEST_DEF, defs);
		def.setId(prDefIds.get(0));
		
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("instr task def 1", "instr task def 1");
		taskDef.setId(0L);
		TaskDefinitionDetails taskDef2 = PlanningRequestConsumer.createTaskDef("instr task def 2", "instr task def 2");
		taskDef2.setId(0L);
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		taskDefs.add(taskDef2);
		
		LongList taskDefIds = powerInstrCons.getStub().addDefinition(DefinitionType.TASK_DEF, taskDefs);
		taskDef.setId(taskDefIds.get(0));
		taskDef2.setId(taskDefIds.get(1));
		
		return new Object[] { prDefIds, taskDefIds };
	}
	
	private PlanningRequestInstanceDetails addPrInstsWithTasks(Long prDefId, LongList taskDefIds) throws MALException, MALInteractionException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(generateId(), prDefId, null);
		
		prInst.setTasks(new TaskInstanceDetailsList());
		prInst.getTasks().add(PlanningRequestConsumer.createTaskInst(generateId(), taskDefIds.get(0), null));
		prInst.getTasks().get(0).setPrInstId(prInst.getId());
		prInst.getTasks().add(PlanningRequestConsumer.createTaskInst(generateId(), taskDefIds.get(1), null));
		prInst.getTasks().get(1).setPrInstId(prInst.getId());
		
		PlanningRequestStatusDetails prStat = normalInstrCons.submitPr(prInst);
		
		assertNotNull(prStat);
		assertEquals(prInst.getId(), prStat.getPrInstId());
		// has status records
		assertNotNull(prStat.getStatus());
		assertFalse(prStat.getStatus().isEmpty());
		// has task statuses
		TaskStatusDetailsList taskStats = prStat.getTaskStatuses();
		assertNotNull(taskStats);
		assertEquals(2, taskStats.size());
		
		for (int i = 0; i < taskStats.size(); ++i) {
			TaskStatusDetails taskStat = taskStats.get(i);
			assertNotNull(taskStat);
			assertEquals(prInst.getTasks().get(i).getId(), taskStat.getTaskInstId());
			// has status records
			assertNotNull(taskStat.getStatus());
			assertFalse(taskStat.getStatus().isEmpty());
		}
		
		return prInst;
	}
	
	@Test
	public void testPrWith2Tasks() throws MALException, MALInteractionException {
		// instr pr plugin
		InstrPrProcessor instrPrProc = new InstrPrProcessor(instrProv2GsProvCons);
		prProvFct.setPlugin(instrPrProc);
		// gs pr plugin
		GsPrProcessor gsPrProc = new GsPrProcessor();
		gsPrProvFct.setPlugin(gsPrProc);
		
		// PR power user adds defs
		Object[] gsDefs = gsAddPrDefsWithTasks();
		
		LongList gsPrDefIds = (LongList)gsDefs[0];
		// one pr def
		assertNotNull(gsPrDefIds);
		assertFalse(gsPrDefIds.isEmpty());
		assertNotNull(gsPrDefIds.get(0));
		
		LongList gsTaskDefIds = (LongList)gsDefs[1];
		// two task defs
		assertNotNull(gsTaskDefIds);
		assertFalse(gsTaskDefIds.isEmpty());
		assertNotNull(gsTaskDefIds.get(0));
		assertNotNull(gsTaskDefIds.get(1));
		
		instrPrProc.setGsPrDefId(gsPrDefIds.get(0));
		instrPrProc.setGsTaskDefIds(gsTaskDefIds);
		
		Object[] instrDefs = instrAddPrDefsWithTasks();
		
		LongList instrPrDefIds = (LongList)instrDefs[0];
		// one pr def
		assertNotNull(instrPrDefIds);
		assertFalse(instrPrDefIds.isEmpty());
		assertNotNull(instrPrDefIds.get(0));
		
		LongList instrTaskDefIds = (LongList)instrDefs[1];
		// two ask defs
		assertNotNull(instrTaskDefIds);
		assertFalse(instrTaskDefIds.isEmpty());
		assertNotNull(instrTaskDefIds.get(0));
		assertNotNull(instrTaskDefIds.get(1));
		
		// PR normal user subscribes
		IdentifierList instrDom = new IdentifierList();
		instrDom.add(instrSubDom);
		String sub2Id = "instrPrSub";
		registerPrMonitor(sub2Id, instrDom, normalInstrCons.getStub(), normalInstrCons, CLIENT1, PR_PROV1);
		
		// instr pr subscribes to gs pr
		IdentifierList gsDom = new IdentifierList();
		gsDom.add(gsSubDom);
		String sub4Id = "gsPrSub";
		registerPrMonitor(sub4Id, gsDom, instrProv2GsProvCons, instrPrProc, PR_PROV1, PR_PROV2);
		
		// normal user submits instances
		PlanningRequestInstanceDetails prInst = addPrInstsWithTasks(instrPrDefIds.get(0), instrTaskDefIds);
		
		assertNotNull(prInst);
		assertNotNull(prInst.getId());
		assertFalse(0L == prInst.getId());
		
		sleep(100);
		
		// at some point instr pr prov "does planning" and submits to gs pr prov
		instrPrProc.doPlanning();
		
		sleep(100);
		
		// at some point gs pr prov "does planning" and notifies results
		gsPrProc.doPlanning();
		
		sleep(100);
		
		// instr pr un-subscribes
		deRegisterPrMonitor(sub4Id, instrProv2GsProvCons, PR_PROV1, PR_PROV2);
		
		// pr normal user un-subscribes
		deRegisterPrMonitor(sub2Id, normalInstrCons.getStub(), CLIENT1, PR_PROV1);
	}
}
