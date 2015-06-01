package esa.mo.inttest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleAdapter;
import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleStub;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
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
import esa.mo.inttest.Dumper;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProvider;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;
import esa.mo.inttest.pr.provider.Plugin;
import esa.mo.inttest.pr.provider.InstStore;
import esa.mo.inttest.sch.consumer.ScheduleConsumer;
import esa.mo.inttest.sch.consumer.ScheduleConsumerFactory;
import esa.mo.inttest.sch.provider.ScheduleProviderFactory;

/**
 * Demonstrate PR and SCH interaction - PR monitors SCH statuses and reacts with PR/Task status change.
 */
public class PrAndSchDemoTest {

	/**
	 * Implements ScheduleAdapter - monitors Schedule status changes, changes related Task status.
	 * Implements PR provider Plugin - creates Schedule when new PR has been submitted.
	 */
	public static class MySchProcessor extends ScheduleAdapter implements Plugin {
		
		// Schedule Stub for add schedules 
		private ScheduleStub schStub = null;
		// Pr Provider for updating Task status
		private PlanningRequestProvider prProv = null;
		// mapping (task inst id==schedule inst id) = sch inst
		private Map<Long, ScheduleInstanceDetails> schInsts = new HashMap<Long, ScheduleInstanceDetails>();
		
		public MySchProcessor(ScheduleStub stub) {
			this.schStub = stub;
		}
		public void setProv(PlanningRequestProvider prov) {
			prProv = prov;
		}
		protected void setTaskStatus(InstStore.TaskItem taskItem, InstanceState is, String comm) {
			// add (or update) task status
			StatusRecordList srl = new StatusRecordList();
			srl.add(Util.addOrUpdateStatus(taskItem.stat, is, Util.currentTime(), comm));
			
			TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
			taskStats.add(new TaskStatusDetails(taskItem.task.getId(), srl));
			
			PlanningRequestStatusDetails prStat = new PlanningRequestStatusDetails(taskItem.task.getPrInstId(), null, taskStats);
			
			try {
				prProv.publishPr(UpdateType.UPDATE, prStat);
			} catch (MALException e) {
				LOG.log(Level.INFO, "PrSchProcessor.setTaskStatus: {0}", e);
				assertTrue(false);
			} catch (MALInteractionException e) {
				LOG.log(Level.INFO, "PrSchProcessor.setTaskStatus: {0}", e);
				assertTrue(false);
			}
		}
		protected void updateTask(ScheduleStatusDetails schStat) {
			InstStore.TaskItem taskItem = prProv.getInstStore().findTaskItem(schStat.getSchInstId());
			assertNotNull(taskItem);
			// assuming task name matches schedule name
			assertTrue(taskItem.stat.getTaskInstId() == schStat.getSchInstId());
			assertTrue(taskItem.task.getId() == schStat.getSchInstId());
			
			InstanceState[] states = new InstanceState[] { InstanceState.INVALID, InstanceState.SCHEDULED,
					InstanceState.PLANNED, InstanceState.DISTRIBUTED_FOR_EXECUTION };
			String[] comments = new String[] { "sch terminated", "sch resumed", "sch paused", "sch started" };
			// assuming statuses come in order above
			for (int i = 0; i < states.length; ++i) {
				// look into schedule status
				StatusRecord sr = Util.findStatus(schStat.getStatus(), states[i]);
				if (null != sr) {
					// update task status
					setTaskStatus(taskItem, states[i], comments[i]);
					break;
				}
			}
		}
		@SuppressWarnings("rawtypes")
		public void monitorSchedulesNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
				ObjectIdList objIds, ScheduleStatusDetailsList schStats, Map qosProps) {
			LOG.log(Level.INFO, "{4}.monitorSchedulesNotifyReceived(id={0}, List:updHeaders={1}, List:objIds={2}, List:schStatuses={3})",
					new Object[] { id, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.schStats(schStats),
					Dumper.fromBroker("SchProvider", msgHdr) });

			for (int i = 0; i < updHdrs.size(); ++i) {
				UpdateHeader updHdr = updHdrs.get(i);
				if (UpdateType.CREATION == updHdr.getUpdateType()) {
					// ignore
				} else if (UpdateType.UPDATE == updHdr.getUpdateType()) {
					ScheduleStatusDetails schStat = schStats.get(i);
					updateTask(schStat);
				}
			}
		}
		protected Long addSchDef(ScheduleDefinitionDetails def) {
			Long id = null;
			try {
				def.setId(0L);
				ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
				schDefs.add(def);
				LongList schDefIds = schStub.addDefinition(schDefs);
				def.setId(schDefIds.get(0));
				id = schDefIds.get(0);
			} catch (MALException e) {
				LOG.log(Level.INFO, "add schedule def: {0}", e);
				assertTrue(false);
			} catch (MALInteractionException e) {
				LOG.log(Level.INFO, "add schedule def: {0}", e);
				assertTrue(false);
			}
			return id;
		}
		protected void addSchInst(ScheduleInstanceDetails schInst) {
			try {
				ScheduleInstanceDetailsList schInsts = new ScheduleInstanceDetailsList();
				schInsts.add(schInst);
				ScheduleStatusDetails schStat = schStub.submitSchedule(schInst);
				assertNotNull(schStat);
			} catch (MALException e) {
				LOG.log(Level.INFO, "add schedule inst: {0}", e);
			} catch (MALInteractionException e) {
				LOG.log(Level.INFO, "add schedule inst: {0}", e);
			}
		}
		protected void createSchForEachTask(PlanningRequestInstanceDetails prInst) {
			TaskInstanceDetailsList tasks = (null != prInst) ? prInst.getTasks() : null;
			for (int i = 0; (null != tasks) && (i < tasks.size()); ++i) {
				TaskInstanceDetails taskInst = tasks.get(i);
				// schdule def gets same name as task
				ScheduleDefinitionDetails schDef = ScheduleConsumer.createDef(""+taskInst.getId(), null);
				Long schDefId = addSchDef(schDef);
				// schedule inst gets same name as task
				ScheduleInstanceDetails schInst = ScheduleConsumer.createInst(taskInst.getId(), schDefId, taskInst.getComment(), null, null, null);
				// schedule id will be same as task id
				addSchInst(/*schDefId, taskInstIds.get(i),*/ schInst);
				schInsts.put(taskInst.getId(), schInst);
			}
		}
		public void onPrSubmit(PlanningRequestInstanceDetails pr) {
			// new pr - produce sch for each task
			createSchForEachTask(pr);
		}
		public void onPrUpdate(PlanningRequestInstanceDetails pr, PlanningRequestStatusDetails stat) {
			// nothing yet
		}
		public void onPrRemove(Long id) {
			// nothing yet
		}
	}
	
	private static final Logger LOG = Logger.getLogger(PrAndSchDemoTest.class.getName());
	
	private static final String PR_PROV = "PrProvider";
	private static final String SCH_PROV = "SchProvider";
	private static final String CLIENT = "PrUserAndMonitor"; // client log
	private static final String CLIENT2 = "SchControl"; // schedule control (executor) log
	private static final String CLIENT3 = PR_PROV+"2"; // pr provider (plugin) connection to sch
	
	private PlanningRequestProviderFactory prProvFct;
	private PlanningRequestConsumerFactory prConsFct;
	
	private ScheduleProviderFactory schProvFct;
	private ScheduleConsumerFactory schConsFct;
	// normal user operations
	private PlanningRequestConsumer prCons;
	// schedule executor operations
	private ScheduleConsumer schCons;
	// Pr provider (Plugin) connection to Schedule provider
	private ScheduleStub schConsStub;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		log2file = DemoUtils.getLogFlag();
		if (log2file) {
			String path = ".\\target\\demo_logs\\pr_and_sch\\";
			// log each consumer/provider lines to it's own file
			files = DemoUtils.createHandlers(path, CLIENT, CLIENT2, PR_PROV, SCH_PROV);
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
		String fn = "testInt.properties";
		
		prProvFct = new PlanningRequestProviderFactory();
		prProvFct.setPropertyFile(fn);
		prProvFct.start(PR_PROV);
		
		URI broker = prProvFct.getBrokerUri();
		
		schProvFct = new ScheduleProviderFactory();
		schProvFct.setPropertyFile(fn);
		schProvFct.setBrokerUri(broker);
		schProvFct.start(SCH_PROV);
		
		prConsFct = new PlanningRequestConsumerFactory();
		prConsFct.setPropertyFile(fn);
		prConsFct.setProviderUri(prProvFct.getProviderUri());
		prConsFct.setBrokerUri(broker);
		// normal user
		prCons = new PlanningRequestConsumer(prConsFct.start(CLIENT));
		
		schConsFct = new ScheduleConsumerFactory();
		schConsFct.setPropertyFile(fn);
		schConsFct.setProviderUri(schProvFct.getProviderUri());
		schConsFct.setBrokerUri(broker);
		// executor
		schCons = new ScheduleConsumer(schConsFct.start(CLIENT2));
		// pr (plugin) as sch consumer
		schConsStub = schConsFct.start(CLIENT3);
	}

	@After
	public void tearDown() throws Exception {
		schConsFct.stop(schConsStub);
		
		schConsFct.stop(schCons.getStub());
		
		prConsFct.stop(prCons.getStub());
		
		schProvFct.stop();
		
		prProvFct.stop();
	}
	
	private LongList addTaskDefs() throws MALException, MALInteractionException {
		TaskDefinitionDetails def = PlanningRequestConsumer.createTaskDef("some task def 1", "demo task def 1");
		def.setId(0L);
		TaskDefinitionDetailsList defs = new TaskDefinitionDetailsList();
		defs.add(def);
		
		return prCons.getStub().addDefinition(DefinitionType.TASK_DEF, defs);
	}
	
	private LongList addPrDef() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails def = PlanningRequestConsumer.createPrDef("some pr def 1", "demo pr def 1");
		def.setId(0L);
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		
		return prCons.getStub().addDefinition(DefinitionType.PLANNING_REQUEST_DEF, defs);
	}
	
	private void registerPrMonitor(String id, PlanningRequestConsumer pr) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsRegister(subId={0})", new Object[] { id, CLIENT + " -> " + PR_PROV });
		pr.getStub().monitorPlanningRequestsRegister(Util.createSub(id), pr);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsRegister() response: returning nothing", CLIENT + " <- " + PR_PROV);
	}
	
	private void deRegisterPrMonitor(String id, PlanningRequestConsumer pr) throws MALException, MALInteractionException {
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(id));
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsDeregister(subId={0})", new Object[] { id, CLIENT + " -> " + PR_PROV });
		pr.getStub().monitorPlanningRequestsDeregister(subs);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsDeregister() response: returning nothing", CLIENT + " <- " + PR_PROV);
	}

	private void registerSchMonitor(String id, ScheduleStub sc, MySchProcessor sp) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorSchedulesRegister(subId={0})", new Object[] { id, PR_PROV + " -> " + SCH_PROV });
		sc.monitorSchedulesRegister(Util.createSub(id), sp);
		LOG.log(Level.INFO, "{0}.monitorSchedulesRegister() response: returning nothing", PR_PROV + " <- " + SCH_PROV);
	}
	
	private void deRegisterSchMonitor(String id, ScheduleStub sc) throws MALException, MALInteractionException {
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(id));
		LOG.log(Level.INFO, "{1}.monitorSchedulesDeregister(subId={0})", new Object[] { id, PR_PROV + " -> " + SCH_PROV });
		sc.monitorSchedulesDeregister(subs);
		LOG.log(Level.INFO, "{0}.monitorSchedulesDeregister() response: returning nothing", PR_PROV + " <- " + SCH_PROV);
	}
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	private long generateId() {
		return lastId.incrementAndGet();
	}
	
	private PlanningRequestInstanceDetails addPrInst(Long prDefId, LongList taskDefIds) throws MALException, MALInteractionException {
		Long taskInstId = generateId();
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(taskInstId, taskDefIds.get(0), "demo task inst 1");
		
		Long prInstId = generateId();
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(prInstId, prDefId, "demo pr inst 1");
		taskInst.setPrInstId(prInst.getId());
		prInst.setTasks(PlanningRequestConsumer.createTasksList(taskInst));
		
		PlanningRequestStatusDetails prStat = prCons.getStub().submitPlanningRequest(prInst);
		
		assertNotNull(prStat);
		
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
	public void test() throws MALException, MALInteractionException {
		MySchProcessor mySchProc = new MySchProcessor(schConsStub);
		// set schedule processor to pr provider as plugin
		prProvFct.setPlugin(mySchProc);
		
		// register schedule processor to monitor schedules
		String sub6Id ="sub6Id";
		registerSchMonitor(sub6Id, schConsStub, mySchProc);
		
		// normal PR user adds defs and instances
		LongList taskDefIds = addTaskDefs();
		
		LongList prDefIds = addPrDef();
		
		String sub2Id = "sub2Id";
		registerPrMonitor(sub2Id, prCons);
		
		PlanningRequestInstanceDetails prInst = addPrInst(prDefIds.get(0), taskDefIds);
		LongList taskInstIds = new LongList();
		taskInstIds.add(prInst.getTasks().get(0).getId());
		
		// executor executes tasks
		schCons.getStub().start(taskInstIds);
		
		sleep(100); // give async jobs a sec
		
		schCons.getStub().pause(taskInstIds);
		
		sleep(100); // give async jobs a sec
		
		schCons.getStub().resume(taskInstIds);
		
		sleep(100); // give async jobs a sec
		
		schCons.getStub().terminate(taskInstIds);
		
		sleep(100); // give async jobs a sec
		
		deRegisterPrMonitor(sub2Id, prCons);
		
		deRegisterSchMonitor(sub6Id, schConsStub);
	}
}
