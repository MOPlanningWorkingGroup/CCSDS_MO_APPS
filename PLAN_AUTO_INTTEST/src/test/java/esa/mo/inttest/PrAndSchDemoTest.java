package esa.mo.inttest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
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
import org.ccsds.moims.mo.automationprototype.scheduletest.consumer.ScheduleTestStub;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningprototype.planningrequesttest.consumer.PlanningRequestTestStub;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.mo.inttest.DemoUtils;
import esa.mo.inttest.Dumper;
import esa.mo.inttest.ca.consumer.ComArchiveConsumerFactory;
import esa.mo.inttest.ca.provider.ComArchiveProviderFactory;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;
import esa.mo.inttest.sch.consumer.ScheduleConsumer;
import esa.mo.inttest.sch.consumer.ScheduleConsumerFactory;
import esa.mo.inttest.sch.provider.ScheduleProviderFactory;

/**
 * Demonstrate PR and SCH interaction - PR monitors SCH statuses and reacts with PR/Task status change.
 */
public class PrAndSchDemoTest {

	public static class MyComArcAdapter<T extends Element> extends ArchiveAdapter {
		
		protected ArchiveDetailsList arcDets = new ArchiveDetailsList();
		protected ElementList<T> elems;
		protected boolean responded = false;
		
		public MyComArcAdapter(ElementList<T> el) {
			elems = el;
		}
		
		@SuppressWarnings("rawtypes")
		public void queryUpdateReceived(MALMessageHeader msgHdr, ObjectType objType,
				IdentifierList domain, ArchiveDetailsList objDets, ElementList objBodies,
				Map qosProps) {
			LOG.log(Level.INFO, "CA.queryUpdateReceived(objType={0}, dom={1}, objInfo={2}, objs={3})",
					new Object[] { Dumper.objType(objType), Dumper.names(domain), Dumper.arcDets(objDets), Dumper.els(objBodies) });
			arcDets.addAll(objDets);
			elems.addAll(objBodies);
		}

		@SuppressWarnings("rawtypes")
		public void queryResponseReceived(MALMessageHeader msgHdr, ObjectType objType,
				IdentifierList domain, ArchiveDetailsList objDets, ElementList objBodies,
				Map qosProps) {
			LOG.log(Level.INFO, "CA.queryResponseReceived(objType={0}, dom={1}, objInfo={2}, objs={3})",
					new Object[] { Dumper.objType(objType), Dumper.names(domain), Dumper.arcDets(objDets), Dumper.els(objBodies) });
			if (null != objDets && null != objBodies) {
				arcDets.addAll(objDets);
				elems.addAll(objBodies);
			}
			responded = true;
			synchronized (this) {
				notifyAll();
			}
		}

		@SuppressWarnings("rawtypes")
		public void queryAckErrorReceived(MALMessageHeader msgHeader, MALStandardError err,
				Map qosProps) {
			LOG.log(Level.INFO, "CA.queryAckErrorReceived: {0}", err);
			assertTrue(false);
		}

		@SuppressWarnings("rawtypes")
		public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError err,
				Map qosProps) {
			LOG.log(Level.INFO, "CA.queryUpdateErrorReceived: {0}", err);
			assertTrue(false);
		}

		@SuppressWarnings("rawtypes")
		public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError err,
				Map qosProps) {
			LOG.log(Level.INFO, "CA.queryResponseErrorReceived: {0}", err);
			assertTrue(false);
		}
	}

	private static final class MySchAdapter extends ScheduleAdapter {
		
		private PrAndSchDemoTest test;
		
		public MySchAdapter(PrAndSchDemoTest test) {
			this.test = test;
		}
		
		@SuppressWarnings("rawtypes")
		public void monitorSchedulesNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
				ObjectIdList objIds, ScheduleStatusDetailsList schStats, Map qosProps) {
			try {
				test.scheduleNotify(msgHdr, id, updHdrs, objIds, schStats, qosProps);
			} catch (MALInteractionException e) {
				LOG.log(Level.INFO, "scheduleNotify error: {0}", e);
				assertTrue(false);
			} catch (MALException e) {
				LOG.log(Level.INFO, "scheduleNotify error: {0}", e);
				assertTrue(false);
			} catch (InterruptedException e) {
				LOG.log(Level.INFO, "scheduleNotify error: {0}", e);
				assertTrue(false);
			} catch (Exception e) {
				LOG.log(Level.INFO, "scheduleNotify error: {0}", e);
				assertTrue(false);
			}
		}
	}
	
	private static final Logger LOG = Logger.getLogger(PrAndSchDemoTest.class.getName());
	
	private static final String CA_PROV = "CaProvider";
	private static final String PR_PROV = "PrProvider";
	private static final String BROKER = PR_PROV; // label broker as provider since it's part of provider
	private static final String SCH_PROV = "SchProvider";
	private static final String CLIENT = "normalUser";
	private static final String CLIENT1 = CLIENT+"1"; // client connection to Pr
	private static final String CLIENT2 = CLIENT+"2"; // client connection to Sch
	
	private ComArchiveProviderFactory caProvFct;
	private ComArchiveConsumerFactory caConsFct;
	
	private PlanningRequestProviderFactory prProvFct;
	private PlanningRequestConsumerFactory prConsFct;
	
	private ScheduleProviderFactory schProvFct;
	private ScheduleConsumerFactory schConsFct;
	// normal user operations
	private PlanningRequestConsumer prCons;
	private ScheduleConsumer schCons;
	// test support
	private ArchiveStub caConsStub;
	private PlanningRequestTestStub testPrConsStub;
	private PlanningRequestStub prConsStub;
	private ScheduleTestStub testSchConsStub;
	private ScheduleStub schConsStub;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		String val = System.getProperty("log2file");
		log2file = (null != val) && "true".equalsIgnoreCase(val);
		System.out.println("writing to log files is turned on: "+log2file);
		if (log2file) {
			Dumper.setBroker(BROKER);
			// trim down log spam
			DemoUtils.setLevels();
			// log each consumer/provider lines to it's own file
			files = new ArrayList<Handler>();
			files.add(DemoUtils.createHandler(CLIENT));
			files.add(DemoUtils.createHandler(PR_PROV));
			files.add(DemoUtils.createHandler(SCH_PROV));
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
			Dumper.setBroker("Broker"); // restore
		}
	}
	
	@Before
	public void setUp() throws Exception {
		String fn = "testInt.properties";
		
		caProvFct = new ComArchiveProviderFactory();
		caProvFct.setPropertyFile(fn);
		caProvFct.start(CA_PROV);
		
		URI broker = caProvFct.getBrokerUri();
		
		prProvFct = new PlanningRequestProviderFactory();
		prProvFct.setPropertyFile(fn);
		prProvFct.setBrokerUri(broker);
		prProvFct.start(PR_PROV);
		
		schProvFct = new ScheduleProviderFactory();
		schProvFct.setPropertyFile(fn);
		schProvFct.setBrokerUri(broker);
		schProvFct.start(SCH_PROV);
		
		prConsFct = new PlanningRequestConsumerFactory();
		prConsFct.setPropertyFile(fn);
		prConsFct.setProviderUri(prProvFct.getProviderUri());
		prConsFct.setBrokerUri(broker);
		prCons = new PlanningRequestConsumer(prConsFct.start(CLIENT1));
		
		schConsFct = new ScheduleConsumerFactory();
		schConsFct.setPropertyFile(fn);
		schConsFct.setProviderUri(schProvFct.getProviderUri());
		schConsFct.setBrokerUri(broker);
		schCons = new ScheduleConsumer(schConsFct.start(CLIENT2));
		
		caConsFct = new ComArchiveConsumerFactory();
		caConsFct.setPropertyFile(fn);
		caConsFct.setProviderUri(caProvFct.getProviderUri());
		caConsFct.setBrokerUri(broker);
		// test support for updating PR statuses - ideally these should be part of provider(s)
		caConsStub = caConsFct.start(CA_PROV+"7");
		prConsFct.setTestProviderUri(prProvFct.getTestProviderUri());
		testPrConsStub = prConsFct.startTest(PR_PROV+"3");
		prConsStub = prConsFct.start(PR_PROV+"4");
		schConsFct.setTestProviderUri(schProvFct.getTestProviderUri());
		testSchConsStub = schConsFct.startTest(SCH_PROV+"5");
		schConsStub = schConsFct.start(SCH_PROV+"6");
	}

	@After
	public void tearDown() throws Exception {
		// test support
		schConsFct.stopTest(testSchConsStub);
		schConsFct.stop(schConsStub);
		prConsFct.stopTest(testPrConsStub);
		prConsFct.stop(prConsStub);
		
		schConsFct.stop(schCons.getStub());
		
		prConsFct.stop(prCons.getStub());
		
		schProvFct.stop();
		
		prProvFct.stop();
	}
	
	private void queryComArcForSch(final ScheduleInstanceDetailsList scheds) throws MALException, MALInteractionException, InterruptedException, Exception {
		ObjectType objType = Util.createObjType(new ScheduleInstanceDetails());
		final MyComArcAdapter<ScheduleInstanceDetails> schAdapter = new MyComArcAdapter<ScheduleInstanceDetails>(scheds);
		
		LOG.log(Level.INFO, "doing ComArchive.query<Schedules>()");
		
		caConsStub.query(true, objType, null, null, schAdapter);
		
		Util.waitFor(schAdapter, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return schAdapter.responded;
			}
		});
		assertNotNull(schAdapter.responded);
		
		LOG.log(Level.INFO, "ComArchive.query() returned {0} ScheduleInstances", scheds.size());
	}
	
	private ScheduleInstanceDetails findSch(Identifier id, ScheduleInstanceDetailsList scheds) {
		ScheduleInstanceDetails inst = null;
		for (int i = 0; i < scheds.size(); ++i) {
			ScheduleInstanceDetails inst2 = scheds.get(i);
			if (null != inst2 && inst2.getName().equals(id)) {
				inst = inst2;
			}
		}
		assertNotNull(inst);
		
		LOG.log(Level.INFO, "found ScheduleInstance with name={0}", id);
		
		return inst;
	}
	
	private Attribute findArg(ScheduleInstanceDetails inst, String name) {
		Attribute attr = null;
		for (int i = 0; i < inst.getArgumentDefNames().size(); ++i) {
			Identifier argName = inst.getArgumentDefNames().get(i);
			AttributeValue argVal = inst.getArgumentValues().get(i);
			if (argName.getValue().equals(name)) {
				attr = argVal.getValue();
			}
		}
		assertNotNull(attr);
		
		LOG.log(Level.INFO, "Schedule with name={0} refers to Task with name={1}", new Object[] { inst.getName(), attr });
		
		return attr;
	}
	
	private ArchiveDetailsList queryComArcForTask(final TaskInstanceDetailsList tasks) throws MALException, MALInteractionException, InterruptedException, Exception {
		ObjectType objType2 = Util.createObjType(new TaskInstanceDetails());
		final MyComArcAdapter<TaskInstanceDetails> taskAdapter = new MyComArcAdapter<TaskInstanceDetails>(tasks);
		
		LOG.log(Level.INFO, "ComArchive.query<Tasks>()");
		
		caConsStub.query(true, objType2, null, null, taskAdapter);
		
		Util.waitFor(taskAdapter, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return taskAdapter.responded;
			}
		});
		assertNotNull(taskAdapter.responded);
		
		LOG.log(Level.INFO, "ComArchive.query() returned {0} TaskInstances", tasks.size());
		
		return taskAdapter.arcDets;
	}
	
	private Long findTask(Identifier taskId, ArchiveDetailsList infos, TaskInstanceDetailsList tasks) {
		TaskInstanceDetails task = null;
		Long id = null;
		for (int i = 0; i < tasks.size(); ++i) {
			TaskInstanceDetails inst = tasks.get(i);
			if (null != inst && inst.getName().equals(taskId)) {
				task = inst;
				id = infos.get(i).getInstId();
			}
		}
		assertNotNull(task);
		assertNotNull(id);
		
		LOG.log(Level.INFO, "TaskInstance with name={0} has id {1}", new Object[] { taskId, id });
		
		return id;
	}
	
	private void processSch(Identifier schId, InstanceState is, String comm) throws MALException, MALInteractionException, InterruptedException, Exception {
		
		final ScheduleInstanceDetailsList scheds = new ScheduleInstanceDetailsList();
		queryComArcForSch(scheds);
		
		ScheduleInstanceDetails schInst = findSch(schId, scheds);
		
		Identifier taskId = (Identifier)findArg(schInst, "taskName");
		
		final TaskInstanceDetailsList tasks = new TaskInstanceDetailsList();
		ArchiveDetailsList infos = queryComArcForTask(tasks);
		
		Long taskInstId = findTask(taskId, infos, tasks);
		
		// update task status
		LongList taskInstIds = new LongList();
		taskInstIds.add(taskInstId);
		
		TaskStatusDetailsList taskStats = prConsStub.getTaskStatus(taskInstIds);
		
		for (TaskStatusDetails stat : taskStats) {
			stat.setStatus(Util.addOrUpdateStatus(stat.getStatus(), is, Util.currentTime(), comm));
		}
		
		testPrConsStub.updateTaskStatus(taskInstIds, taskStats);
	}
	
	@SuppressWarnings("rawtypes")
	private void scheduleNotify(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
			ObjectIdList objIds, ScheduleStatusDetailsList schStats, Map qosProps) throws MALException, MALInteractionException, InterruptedException, Exception {
		
		InstanceState[] states = new InstanceState[] { InstanceState.INVALID, InstanceState.SCHEDULED, InstanceState.PLANNED, InstanceState.DISTRIBUTED_FOR_EXECUTION };
		String[] comments = new String[] { "sch terminated", "sch resumed", "sch paused", "sch started" };
		
		for (ScheduleStatusDetails stat: schStats) {
			Identifier schId = stat.getScheduleInstName();
			for (int i = 0; i < states.length; ++i) {
				StatusRecord sr = Util.findStatus(stat.getStatus(), states[i]);
				if (null != sr) {
					processSch(schId, states[i], comments[i]);
					break;
				}
			}
		}
	}
	
	private LongList addTaskDefs() throws MALException, MALInteractionException {
		LongList defIds = new LongList();
		
		ArgumentDefinitionDetails arg = new ArgumentDefinitionDetails(new Identifier("scheduleName"), null,
				Util.attrType(Attribute.IDENTIFIER_TYPE_SHORT_FORM), null, null, null, null);
		
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(arg);
		
		TaskDefinitionDetails def = PlanningRequestConsumer.createTaskDef("some task def 1", null);
		def.setArgumentDefs(args);
		
		TaskDefinitionDetailsList defs = new TaskDefinitionDetailsList();
		defs.add(def);
		
		LongList ids = prCons.getStub().addDefinition(DefinitionType.TASK_DEF, defs);
		defIds.add(ids.get(0));
		
		return defIds;
	}
	
	private LongList addPrDefs() throws MALException, MALInteractionException {
		LongList defIds = new LongList();
		
		PlanningRequestDefinitionDetails def = PlanningRequestConsumer.createPrDef("some pr def 1", null);
		
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		
		LongList ids = prCons.getStub().addDefinition(DefinitionType.PLANNING_REQUEST_DEF, defs);
		defIds.add(ids.get(0));
		
		return defIds;
	}
	
	private LongList addSchDefs() throws MALException, MALInteractionException {
		LongList defIds = new LongList();
		
		ArgumentDefinitionDetails arg = new ArgumentDefinitionDetails(new Identifier("taskName"), null,
				Util.attrType(Attribute.IDENTIFIER_TYPE_SHORT_FORM), null, null, null, null);
		
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(arg);
		
		ScheduleDefinitionDetails def = ScheduleConsumer.createDef("some schedule def 1", null);
		def.setArgumentDefs(args);
		
		ScheduleDefinitionDetailsList defs = new ScheduleDefinitionDetailsList();
		defs.add(def);
		
		LongList ids = schCons.getStub().addDefinition(defs);
		defIds.add(ids.get(0));
		
		return defIds;
	}
	
	private void registerTaskMonitor(String id, PlanningRequestConsumer pr, String n) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorTasksRegister(subId={0})", new Object[] { id, n + " -> " + BROKER });
		pr.getStub().monitorTasksRegister(Util.createSub(id), pr);
		LOG.log(Level.INFO, "{0}.monitorTasksRegister() response: returning nothing", n + " <-" + BROKER);
	}
	
	private void deRegisterTaskMonitor(String id, PlanningRequestConsumer pr, String n) throws MALException, MALInteractionException {
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(id));
		LOG.log(Level.INFO, "{1}.monitorTasksDeregister(subId={0})", new Object[] { id, n + " -> " + BROKER });
		pr.getStub().monitorTasksDeregister(subs);
		LOG.log(Level.INFO, "{0}.monitorTasksDeregister() response: returning nothing", n + " <- " + BROKER);
	}
	
	private void registerPrMonitor(String id, PlanningRequestConsumer pr, String n) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsRegister(subId={0})", new Object[] { id, n + " -> " + BROKER });
		pr.getStub().monitorPlanningRequestsRegister(Util.createSub(id), pr);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsRegister() response: returning nothing", n + " <-" + BROKER);
	}
	
	private void deRegisterPrMonitor(String id, PlanningRequestConsumer pr, String n) throws MALException, MALInteractionException {
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(id));
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestsDeregister(subId={0})", new Object[] { id, n + " -> " + BROKER });
		pr.getStub().monitorPlanningRequestsDeregister(subs);
		LOG.log(Level.INFO, "{0}.monitorPlanningRequestsDeregister() response: returning nothing", n + " <- " + BROKER);
	}

	private void registerSchMonitor(String id, ScheduleConsumer sc, String n) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorSchedulesRegister(subId={0})", new Object[] { id, n + " -> " + BROKER });
		sc.getStub().monitorSchedulesRegister(Util.createSub(id), sc);
		LOG.log(Level.INFO, "{0}.monitorSchedulesRegister() response: returning nothing", n + " <-" + BROKER);
	}
	
	private void deRegisterSchMonitor(String id, ScheduleConsumer sc, String n) throws MALException, MALInteractionException {
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(id));
		LOG.log(Level.INFO, "{1}.monitorSchedulesDeregister(subId={0})", new Object[] { id, n + " -> " + BROKER });
		sc.getStub().monitorSchedulesDeregister(subs);
		LOG.log(Level.INFO, "{0}.monitorSchedulesDeregister() response: returning nothing", n + " <- " + BROKER);
	}
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	private long generateId() {
		return lastId.incrementAndGet();
	}
	
	private void storeTaskInst(Long id, TaskInstanceDetails inst) throws MALException, MALInteractionException {
		ObjectType ot = Util.createObjType(inst);
		IdentifierList dom = caConsFct.getDomain();
		ObjectDetails objDet = new ObjectDetails(null, null);
		Identifier network = null;
		FineTime ts = Util.currentFineTime();
		URI provUri = new URI("some://uri");
		ArchiveDetails objInfo = new ArchiveDetails(id, objDet, network, ts, provUri);
		ArchiveDetailsList objInfos = new ArchiveDetailsList();
		objInfos.add(objInfo);
		TaskInstanceDetailsList objs = new TaskInstanceDetailsList();
		objs.add(inst);
		caConsStub.store(false, ot, dom, objInfos, objs);
	}
	
	private void storePrInst(Long id, PlanningRequestInstanceDetails inst) throws MALException, MALInteractionException {
		ObjectType ot = Util.createObjType(inst);
		IdentifierList dom = caConsFct.getDomain();
		ObjectDetails objDet = new ObjectDetails(null, null);
		Identifier network = null;
		FineTime ts = Util.currentFineTime();
		URI provUri = new URI("some://provider");
		ArchiveDetails objInfo = new ArchiveDetails(id, objDet, network, ts, provUri);
		ArchiveDetailsList objInfos = new ArchiveDetailsList();
		objInfos.add(objInfo);
		PlanningRequestInstanceDetailsList objs = new PlanningRequestInstanceDetailsList();
		objs.add(inst);
		caConsStub.store(false, ot, dom, objInfos, objs);
	}
	
	private Object[] addPrInsts(LongList prDefIds, LongList taskDefIds) throws MALException, MALInteractionException {
		String prName = "test pr instance 1";
		
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst("test task instance 1", null, prName);
		Long taskInstId = generateId();
		
		storeTaskInst(taskInstId, taskInst);
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(taskInstId);
		
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(prName, null);
		prInst.setTasks(PlanningRequestConsumer.createTasksList(taskInst));
		Long prInstId = generateId();
		
		storePrInst(prInstId, prInst);
		
		PlanningRequestResponseInstanceDetailsList resps = prCons.getStub().submitPlanningRequest(prDefIds.get(0), prInstId, prInst, taskDefIds, taskInstIds);
		PlanningRequestResponseInstanceDetails prr = resps.get(0);
		assertNotNull(prr);
		
		LongList prInstIds = new LongList();
		prInstIds.add(prInstId);
		
		return new Object[] { prInstIds, prInst, taskInstIds };
	}
	
	private void storeSchInst(Long id, ScheduleInstanceDetails inst) throws MALException, MALInteractionException {
		ObjectType ot = Util.createObjType(inst);
		IdentifierList dom = caConsFct.getDomain();
		ObjectDetails objDet = new ObjectDetails(null, null);
		Identifier network = null;
		FineTime ts = Util.currentFineTime();
		URI provUri = new URI("some://schedule");
		ArchiveDetails objInfo = new ArchiveDetails(id, objDet, network, ts, provUri);
		ArchiveDetailsList objInfos = new ArchiveDetailsList();
		objInfos.add(objInfo);
		ScheduleInstanceDetailsList objs = new ScheduleInstanceDetailsList();
		objs.add(inst);
		caConsStub.store(false, ot, dom, objInfos, objs);
	}
	
	private Object[] addSchInsts(LongList defIds, TaskInstanceDetailsList tasks) throws MALException, MALInteractionException {
		IdentifierList argNames = new IdentifierList();
		argNames.add(new Identifier("taskName"));
		
		AttributeValueList argValues = new AttributeValueList();
		argValues.add(new AttributeValue(tasks.get(0).getName())); // Identifier type
		
		ScheduleInstanceDetails inst = schCons.createInst("test schedule instance 1", "test 1", argNames, argValues, null, null);
		Long id = generateId();
		
		storeSchInst(id, inst);
		
		schCons.getStub().submitSchedule(defIds.get(0), id, inst);
		
		LongList instIds = new LongList();
		instIds.add(id);
		
		ScheduleInstanceDetailsList insts = new ScheduleInstanceDetailsList();
		insts.add(inst);
		
		return new Object[] { instIds, insts };
	}
	
	private void updateTasks(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst, LongList taskDefIds,
			LongList taskInstIds, ScheduleInstanceDetailsList scheds) throws MALException, MALInteractionException {
		for (int i = 0; i < scheds.size(); ++i) {
			ScheduleInstanceDetails sch = scheds.get(i);
			IdentifierList argNames = new IdentifierList();
			argNames.add(new Identifier("scheduleName"));
			AttributeValueList argValues = new AttributeValueList();
			argValues.add(new AttributeValue(sch.getName()));
			
			Long taskId = taskInstIds.get(i);
			TaskInstanceDetails taskInst = prInst.getTasks().get(i);
			taskInst.setArgumentDefNames(argNames);
			taskInst.setArgumentValues(argValues);
			storeTaskInst(taskId, taskInst);
			storePrInst(prInstId, prInst);
			prCons.getStub().updatePlanningRequest(prDefId, prInstId, prInst, taskDefIds, taskInstIds);
		}
	}
	
	private void updateTaskStatuses(LongList taskInstIds, TaskStatusDetailsList taskStats) throws MALException, MALInteractionException {
		for (int i = 0; i < taskStats.size(); ++i) {
			TaskStatusDetails taskStat = taskStats.get(i);
			StatusRecord sr = Util.findStatus(taskStat.getStatus(), InstanceState.ACCEPTED);
			if (null == sr) {
				taskStat.getStatus().add(new StatusRecord(InstanceState.ACCEPTED, Util.currentTime(), "accepted"));
				LongList taskInstIds2 = new LongList();
				taskInstIds2.add(taskInstIds.get(i));
				TaskStatusDetailsList taskStats2 = new TaskStatusDetailsList();
				taskStats2.add(taskStat);
				testPrConsStub.updateTaskStatus(taskInstIds2, taskStats2);
			}
		}
	}
	
	private void updatePrStatuses(final LongList prInstIds, final LongList taskInstIds) throws MALException, MALInteractionException {
		PlanningRequestStatusDetailsList prStats = prCons.getStub().getPlanningRequestStatus(prInstIds);
		for (int i = 0; (null != prStats) && (i < prStats.size()); ++i) {
			PlanningRequestStatusDetails prStat = prStats.get(i);
			StatusRecord sr = Util.findStatus(prStat.getStatus(), InstanceState.ACCEPTED);
			if (null == sr) {
				// check tasks first
				updateTaskStatuses(taskInstIds, prStat.getTaskStatuses());
				prStat.getStatus().add(new StatusRecord(InstanceState.ACCEPTED, Util.currentTime(), "accepted"));
				testPrConsStub.updatePrStatus(prInstIds, prStats);
			}
		}
	}
	
	private void updateSchStatuses(final LongList instIds) throws MALException, MALInteractionException {
		ScheduleStatusDetailsList stats = schConsStub.getScheduleStatus(instIds);
		for (int i = 0; (null != stats) && (i < stats.size()); ++i) {
			ScheduleStatusDetails stat = stats.get(i);
			StatusRecord sr = Util.findStatus(stat.getStatus(), InstanceState.ACCEPTED);
			if (null == sr) {
				stat.getStatus().add(new StatusRecord(InstanceState.ACCEPTED, Util.currentTime(), "accepted"));
			}
		}
		testSchConsStub.updateScheduleStatus(instIds, stats);
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
		LongList taskDefIds = addTaskDefs();
		
		LongList prDefIds = addPrDefs();
		
		LongList schDefIds = addSchDefs();
		
		String sub1Id = "sub1Id";
		registerTaskMonitor(sub1Id, prCons, CLIENT1);
		
		String sub2Id = "sub2Id";
		registerPrMonitor(sub2Id, prCons, CLIENT1);
		
		String sub3Id = "sub3Id";
		registerSchMonitor(sub3Id, schCons, CLIENT2);
		
		String sub6Id ="sub6Id";
		MySchAdapter mySch = new MySchAdapter(this);
		schConsStub.monitorSchedulesRegister(Util.createSub(sub6Id), mySch);
		
		Object[] details = addPrInsts(prDefIds, taskDefIds);
		LongList prInstIds = (LongList)details[0];
		PlanningRequestInstanceDetails prInst = (PlanningRequestInstanceDetails)details[1];
		LongList taskInstIds = (LongList)details[2];
		
		Object[] details2 = addSchInsts(schDefIds, prInst.getTasks());
		LongList schInstIds = (LongList)details2[0];
		ScheduleInstanceDetailsList schInsts = (ScheduleInstanceDetailsList)details2[1];
		
		updateTasks(prDefIds.get(0), prInstIds.get(0), prInst, taskDefIds, taskInstIds, schInsts);
		
		updatePrStatuses(prInstIds, taskInstIds);
		
		updateSchStatuses(schInstIds);
		
		schConsStub.start(schInstIds);
		
		schConsStub.pause(schInstIds);
		
		schConsStub.resume(schInstIds);
		
		schConsStub.terminate(schInstIds);
		
		sleep(100); // give async jobs a sec
		
		IdentifierList sub6 = new IdentifierList();
		sub6.add(new Identifier(sub6Id));
		schConsStub.monitorSchedulesDeregister(sub6);
		
		deRegisterSchMonitor(sub3Id, schCons, CLIENT2);
		
		deRegisterPrMonitor(sub2Id, prCons, CLIENT1);
		
		deRegisterTaskMonitor(sub1Id, prCons, CLIENT1);
	}
}
