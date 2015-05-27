package esa.mo.inttest.sch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleStub;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.mo.inttest.DemoUtils;
import esa.mo.inttest.Util;
import esa.mo.inttest.sch.consumer.ScheduleConsumer;
import esa.mo.inttest.sch.consumer.ScheduleConsumerFactory;
import esa.mo.inttest.sch.provider.InstStore;
import esa.mo.inttest.sch.provider.Plugin;
import esa.mo.inttest.sch.provider.ScheduleProvider;
import esa.mo.inttest.sch.provider.ScheduleProviderFactory;

public class ThreeSchedulersDemoTest {

	public static class Processor implements Plugin {
		
		protected ScheduleProvider prov = null;
		protected List<Long> submitted = new ArrayList<Long>();
		
		public void setProv(ScheduleProvider prov) {
			this.prov = prov;
		}
		
		public void onSubmit(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
			submitted.add(sch.getId());
		}
		
		public void onUpdate(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
			// ignore
		}
		
		public void onRemove(Long id) {
			// ignore
		}
		public void onPatch(ScheduleInstanceDetailsList removed, ScheduleInstanceDetailsList updated,
				ScheduleInstanceDetailsList added, ScheduleStatusDetailsList stats) {
			// ignore
		}
		
		public void onStart(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
			// ignore
		}
		
		public void onPause(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
			// ignore
		}
		
		public void onResume(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
			// ignore
		}
		
		public void onTerminate(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
			// ignore
		}
		
		public void acceptSubmitted() throws MALException, MALInteractionException {
			for (Long id: submitted) {
				InstStore.Item item = prov.getInstStore().findItem(id);
				StatusRecord asr = Util.findStatus(item.stat.getStatus(), InstanceState.ACCEPTED);
				if (null == asr) {
					asr = new StatusRecord(InstanceState.ACCEPTED, Util.currentTime(), "accepted");
					item.stat.getStatus().add(asr);
					// all statuses list was updated, now publish changed status
					StatusRecordList srl = new StatusRecordList();
					srl.add(asr);
					ScheduleStatusDetails stat = new ScheduleStatusDetails(id, srl, new ScheduleItemStatusDetailsList());
					prov.publish(UpdateType.UPDATE, stat);
				}
			}
		}
	}
	
	private static final Logger LOG = Logger.getLogger(ThreeSchedulersDemoTest.class.getName());
	
	private static final String PROVIDER = "SchProvider";
	private static final String CLIENT1 = "SchPowerUser";
	private static final String CLIENT2 = "SchNormalUser";
	private static final String CLIENT3 = "SchMonitorUser";
	
	private ScheduleProviderFactory provFct;
	private ScheduleConsumerFactory consFct;

	private ScheduleConsumer cons1;
	private ScheduleConsumer cons2;
	private ScheduleConsumer cons3;
	private ScheduleStub consStub;
	
	private static boolean log2file = false;
	private static List<Handler> files = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		log2file = DemoUtils.getLogFlag();
		if (log2file) {
			// log each consumer/provider lines to it's own file
			String path = ".\\target\\demo_logs\\sch\\";
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
		String fn = "testInt.properties";
		
		provFct = new ScheduleProviderFactory();
		provFct.setPropertyFile(fn);
		provFct.start(PROVIDER);
		
		consFct = new ScheduleConsumerFactory();
		consFct.setPropertyFile(fn);
		consFct.setProviderUri(provFct.getProviderUri());
		consFct.setBrokerUri(provFct.getBrokerUri());
		cons1 = new ScheduleConsumer(consFct.start(CLIENT1));
		cons2 = new ScheduleConsumer(consFct.start(CLIENT2));
		cons3 = new ScheduleConsumer(consFct.start(CLIENT3));
		
		consStub = consFct.start(PROVIDER+"1");
	}

	@After
	public void tearDown() throws Exception {
		consFct.stop(consStub);
		
		consFct.stop(cons3.getStub());
		consFct.stop(cons2.getStub());
		consFct.stop(cons1.getStub());
		provFct.stop();
	}
	
	private LongList addDefinitions() throws MALException, MALInteractionException {
		LongList defIds = new LongList();
		
		ScheduleDefinitionDetails def = ScheduleConsumer.createDef("test schedule definition 1", "test 1");
		def.setId(0L);
		ScheduleDefinitionDetailsList defs = new ScheduleDefinitionDetailsList();
		defs.add(def);
		LongList ids = cons1.getStub().addDefinition(defs);
		defIds.add(ids.get(0));
		
		ArgumentDefinitionDetailsList argDefs = ScheduleConsumer.addArgDef(null, "arg1", Util.attrType(Attribute.STRING_TYPE_SHORT_FORM), null);
		def = ScheduleConsumer.createDef("test schedule definition 2", "test 2");
		def.setId(0L);
		def.setArgumentDefs(argDefs);
		defs.clear();
		defs.add(def);
		ids = cons1.getStub().addDefinition(defs);
		defIds.add(ids.get(0));
		
		ObjectTypeList eTypes = ScheduleConsumer.addObjType(null, new ScheduleInstanceDetails());
		def = ScheduleConsumer.createDef("test schedule definition 3", "test 3");
		def.setId(0L);
		def.setEventTypes(eTypes);
		defs.clear();
		defs.add(def);
		ids = cons1.getStub().addDefinition(defs);
		defIds.add(ids.get(0));
		
		return defIds;
	}
	
	private void registerMonitor(String id, ScheduleConsumer sc, String n) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorSchedulesRegister(subId={0})", new Object[] { id, n + " -> " + PROVIDER });
		sc.getStub().monitorSchedulesRegister(Util.createSub(id), sc);
		LOG.log(Level.INFO, "{0}.monitorSchedulesRegister() response: returning nothing", n + " <- " + PROVIDER);
	}
	
	private void deRegisterMonitor(String id, ScheduleConsumer sc, String n) throws MALException, MALInteractionException {
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(id));
		LOG.log(Level.INFO, "{1}.monitorSchedulesDeregister(subId={0})", new Object[] { id, n + " -> " + PROVIDER });
		sc.getStub().monitorSchedulesDeregister(subs);
		LOG.log(Level.INFO, "{0}.monitorSchedulesDeregister() response: returning nothing", n + " <- " + PROVIDER);
	}
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	private long generateId() {
		return lastId.incrementAndGet();
	}
	
	private LongList addInstances(LongList defIds) throws MALException, MALInteractionException {
		LongList instIds = new LongList();
		
		Long instId = generateId();
		ScheduleInstanceDetails inst = ScheduleConsumer.createInst(instId, defIds.get(0), "test 1", null, null, null);
		
		cons2.getStub().submitSchedule(inst);
		instIds.add(instId);
		
		ArgumentValueList argVals = ScheduleConsumer.addArgValue(null, "arg1", new Union("desd"));
		
		instId = generateId();
		inst = ScheduleConsumer.createInst(instId, defIds.get(1), "test 2", argVals, null, null);
		
		cons2.getStub().submitSchedule(inst);
		instIds.add(instId);
		
		instId = generateId();
		inst = ScheduleConsumer.createInst(instId, defIds.get(2), "test 3", null, null, null);
		ObjectId objId = ScheduleConsumer.createObjId(new ScheduleInstanceDetails(), consFct.getDomain(), instId);
		ScheduleItemInstanceDetailsList items = ScheduleConsumer.addItem(null, generateId(), inst.getId(),
				null, null, new TimingDetailsList(), objId);
		inst.setScheduleItems(items);
		
		cons2.getStub().submitSchedule(inst);
		instIds.add(instId);
		
		return instIds;
	}
	
	@Test
	public void test() throws MALException, MALInteractionException {
		Processor p = new Processor();
		provFct.setPlugin(p);
		
		LongList defIds = addDefinitions();
		
		String sub2Id = "sub2Id";
		registerMonitor(sub2Id, cons2, CLIENT2);
		
		String sub3Id = "sub3Id";
		registerMonitor(sub3Id, cons3, CLIENT3);
		
		LongList instIds = addInstances(defIds);
		
		p.acceptSubmitted();
		
		// start all three
		consStub.start(instIds);
		
		// terminate first
		LongList term = new LongList();
		term.add(instIds.get(0));
		consStub.terminate(term);
		
		// pause 2nd & 3rd
		LongList paus = new LongList();
		paus.add(instIds.get(1));
		paus.add(instIds.get(2));
		consStub.pause(paus);
		
		// resume 3rd
		LongList resu = new LongList();
		resu.add(instIds.get(2));
		consStub.resume(resu);
		
		deRegisterMonitor(sub3Id, cons3, CLIENT3);
		
		deRegisterMonitor(sub2Id, cons2, CLIENT2);
	}
}
