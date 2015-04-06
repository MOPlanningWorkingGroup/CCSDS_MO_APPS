package esa.mo.inttest.sch;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleStub;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.automationprototype.scheduletest.consumer.ScheduleTestStub;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.mo.inttest.DemoUtils;
import esa.mo.inttest.sch.consumer.ScheduleConsumer;
import esa.mo.inttest.sch.consumer.ScheduleConsumerFactory;
import esa.mo.inttest.sch.provider.ScheduleProviderFactory;

public class ThreeSchedulersDemoTest {

	private static final Logger LOG = Logger.getLogger(ThreeSchedulersDemoTest.class.getName());
	
	private static final String PROVIDER = "SchProvider";
	private static final String BROKER = PROVIDER; // label broker as provider since it's part of provider
	private static final String CLIENT1 = "SchPowerUser";
	private static final String CLIENT2 = "SchNormalUser";
	private static final String CLIENT3 = "SchMonitorUser";
	
	private ScheduleProviderFactory provFct;
	private ScheduleConsumerFactory consFct;

	private ScheduleConsumer cons1;
	private ScheduleConsumer cons2;
	private ScheduleConsumer cons3;
	// test support
	private ScheduleTestStub testConsStub;
	private ScheduleStub consStub;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// use maven profile "gen-log-files" to turn logging to files on
		String val = System.getProperty("log2file");
		boolean log2file = (null != val) && "true".equalsIgnoreCase(val);
		System.out.println("writing to log files is turned on: "+log2file);
		if (log2file) {
			// trim down log spam
			DemoUtils.setLevels();
			// log each consumer/provider lines to it's own file
			Logger.getLogger("").addHandler(DemoUtils.createHandler(CLIENT1));
			Logger.getLogger("").addHandler(DemoUtils.createHandler(CLIENT2));
			Logger.getLogger("").addHandler(DemoUtils.createHandler(CLIENT3));
			Logger.getLogger("").addHandler(DemoUtils.createHandler(PROVIDER));
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
		
		// test support for updating schedule statuses
		consFct.setTestProviderUri(provFct.getTestProviderUri());
		testConsStub = consFct.startTest(PROVIDER+"0");
		consStub = consFct.start(PROVIDER+"1");
	}

	@After
	public void tearDown() throws Exception {
		// test support
		consFct.stopTest(testConsStub);
		consFct.stop(consStub);
		
		consFct.stop(cons3.getStub());
		consFct.stop(cons2.getStub());
		consFct.stop(cons1.getStub());
		provFct.stop();
	}
	
	private LongList addDefinitions() throws MALException, MALInteractionException {
		LongList defIds = new LongList();
		
		ScheduleDefinitionDetails def = cons1.createDef("test schedule definition 1", "test 1", null, null);
		ScheduleDefinitionDetailsList defs = new ScheduleDefinitionDetailsList();
		defs.add(def);
		LongList ids = cons1.getStub().addDefinition(defs);
		defIds.add(ids.get(0));
		
		ArgumentDefinitionDetailsList argDefs = cons1.addArgDef(null, "arg1", (byte)(Attribute.STRING_TYPE_SHORT_FORM&0xff), null);
		def = cons1.createDef("test schedule definition 2", "test 2", argDefs, null);
		defs.clear();
		defs.add(def);
		ids = cons1.getStub().addDefinition(defs);
		defIds.add(ids.get(0));
		
		ObjectTypeList eTypes = cons1.addObjType(null, new ScheduleInstanceDetails());
		def = cons1.createDef("test schedule definition 3", "test 3", null, eTypes);
		defs.clear();
		defs.add(def);
		ids = cons1.getStub().addDefinition(defs);
		defIds.add(ids.get(0));
		
		return defIds;
	}
	
	private void registerMonitor(String id, ScheduleConsumer sc, String n) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.monitorSchedulesRegister(subId={0})", new Object[] { id, n + " -> " + BROKER });
		sc.getStub().monitorSchedulesRegister(sc.createSub(id), sc);
		LOG.log(Level.INFO, "{0}.monitorSchedulesRegister() response: returning nothing", n + " <-" + BROKER);
	}
	
	private void deRegisterMonitor(String id, ScheduleConsumer sc, String n) throws MALException, MALInteractionException {
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
	
	private LongList addInstances(LongList defIds) throws MALException, MALInteractionException {
		LongList instIds = new LongList();
		
		ScheduleInstanceDetails inst = cons2.createInst("test schedule instance 1", "test 1", null, null, null, null);
		Long instId = generateId();
		cons2.getStub().submitSchedule(defIds.get(0), instId, inst);
		instIds.add(instId);
		
		IdentifierList argNames = cons2.addArgName(null, "arg1");
		AttributeValueList argVals = cons2.addArgValue(null, new Union("desd"));
		inst = cons2.createInst("test schedule instance 2", "test 2", argNames, argVals, null, null);
		instId = generateId();
		cons2.getStub().submitSchedule(defIds.get(1), instId, inst);
		instIds.add(instId);
		
		inst = cons2.createInst("test schedule instance 3", "test 3", null, null, null, null);
		instId = generateId();
		ObjectId objId = cons2.createObjId(new ScheduleInstanceDetails(), consFct.getDomain(), instId);
		ScheduleItemInstanceDetailsList items = cons2.addItem(null, "schedule item", inst.getName().getValue(), null,
				null, new TriggerDetailsList(), objId);
		inst.setScheduleItems(items);
		cons2.getStub().submitSchedule(defIds.get(2), instId, inst);
		instIds.add(instId);
		
		return instIds;
	}
	
	private void updateStatuses(final LongList created) throws MALException, MALInteractionException {
		ScheduleStatusDetailsList stats = consStub.getScheduleStatus(created);
		for (int i = 0; (null != stats) && (i < stats.size()); ++i) {
			ScheduleStatusDetails stat = stats.get(i);
			StatusRecord sr = cons1.findStatus(stat.getStatus(), InstanceState.ACCEPTED);
			if (null == sr) {
				stat.setStatus(cons1.addOrUpdate(stat.getStatus(), InstanceState.ACCEPTED,
						new Time(System.currentTimeMillis()), "accepted"));
			}
		}
		testConsStub.updateScheduleStatus(created, stats);
	}
	
	@Test
	public void test() throws MALException, MALInteractionException {
		LongList defIds = addDefinitions();
		
		String sub2Id = "sub2Id";
		registerMonitor(sub2Id, cons2, CLIENT2);
		
		String sub3Id = "sub3Id";
		registerMonitor(sub3Id, cons3, CLIENT3);
		
		LongList instIds = addInstances(defIds);
		
		updateStatuses(instIds);
		
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
