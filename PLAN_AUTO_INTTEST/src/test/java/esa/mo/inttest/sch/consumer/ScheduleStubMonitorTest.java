package esa.mo.inttest.sch.consumer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleAdapter;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.SchedulePatchOperations;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.junit.Test;

import esa.mo.inttest.Util;

public class ScheduleStubMonitorTest extends ScheduleStubTestBase {

	private static class SchMonitor extends ScheduleAdapter {
		
		protected List<Identifier> ids = new ArrayList<Identifier>();
		protected List<UpdateHeaderList> updHdrs = new ArrayList<UpdateHeaderList>();
		protected List<ObjectIdList> objIds = new ArrayList<ObjectIdList>();
		protected List<ScheduleStatusDetailsList> schStats = new ArrayList<ScheduleStatusDetailsList>();
		
		public void clear() {
			ids.clear();
			updHdrs.clear();
			objIds.clear();
			schStats.clear();
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorSchedulesRegisterAckReceived(MALMessageHeader msgHdr, Map qosProps) {
			LOG.log(Level.INFO, "monitorSchedulesRegisterAck");
			synchronized (this) {
				this.notifyAll();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorSchedulesRegisterErrorReceived(MALMessageHeader msgHdr, MALStandardError err, Map qosProps) {
			LOG.log(Level.INFO, "monitorSchedulesRegisterError");
			assertTrue(false);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorSchedulesDeregisterAckReceived(MALMessageHeader msgHdr, Map qosProps) {
			LOG.log(Level.INFO, "monitorSchedulesDeregisterAck");
			synchronized (this) {
				this.notifyAll();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorSchedulesNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updateHdrs,
				ObjectIdList objectIds, ScheduleStatusDetailsList scheduleStats, Map qosProps) {
			LOG.log(Level.INFO, "monitorSchedulesNotify");
			ids.add(id);
			updHdrs.add(updateHdrs);
			objIds.add(objectIds);
			schStats.add(scheduleStats);
			synchronized (this) {
				this.notifyAll();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorSchedulesNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError err, Map qosProps) {
			LOG.log(Level.INFO, "monitorSchedulesNotifyError");
			assertTrue(false);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void notifyReceivedFromOtherService(MALMessageHeader msgHdr, MALNotifyBody body, Map qosProps)
				throws MALException {
			LOG.log(Level.INFO, "notifyReceivedFromOtherService");
		}
	}
	
	private static final Logger LOG = Logger.getLogger(ScheduleStubTest.class.getName());
	
	protected ScheduleDefinitionDetails createDef(String n) {
		ScheduleDefinitionDetails schDef = new ScheduleDefinitionDetails();
		schDef.setName(new Identifier(n));
		schDef.setEventTypes(new ObjectTypeList());
		return schDef;
	}
	
	protected ScheduleInstanceDetails createInst() {
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
//		schInst.setName(new Identifier(n));
		return schInst;
	}
	
	protected Long createAndAddDef(String n) throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef(n);
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		LongList schDefIds = schCons.addDefinition(schDefs);
		schDef.setId(schDefIds.get(0));
		return schDefIds.get(0);
	}
	
	protected ScheduleInstanceDetails createAndSubmitInst(Long schDefId, Long schInstId, String n) throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createInst();
		schInst.setId(schInstId);
		schInst.setSchDefId(schDefId);
		schCons.submitSchedule(schInst);
		return schInst;
	}
	
	private void waitForSch(final SchMonitor sm) throws InterruptedException, Exception {
		Util.waitFor(sm, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return !sm.schStats.isEmpty();
			}
		});
	}
	
	private Long submitSchedule(final SchMonitor schMon) throws MALException, MALInteractionException,
			InterruptedException, Exception {
		
		Long schDefId = createAndAddDef("test schedule def");
		Long schInstId = 1L;
		createAndSubmitInst(schDefId, schInstId, "test schedule inst");
		
		waitForSch(schMon);
		
		return schInstId;
	}
	
	@Test
	public void testSubmitSchedule() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSubId";
		final SchMonitor schMon = new SchMonitor();
		schCons.monitorSchedulesRegister(createSub(subId), schMon);
		
		submitSchedule(schMon);
		
		assertFalse(schMon.schStats.isEmpty());
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		schCons.monitorSchedulesDeregister(subIds);
	}
	
	@Test
	public void testUpdateSchedule() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSub2Id";
		final SchMonitor schMon = new SchMonitor();
		schCons.monitorSchedulesRegister(createSub(subId), schMon);
		
		Long schDefId = createAndAddDef("test schedule def");
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = createAndSubmitInst(schDefId, schInstId, "test schedule inst");
		
		waitForSch(schMon);
		
		schInst.setComment("updated description");
		
		schMon.clear(); // clear submit info
		
		schCons.updateSchedule(schInst);
		
		waitForSch(schMon);
		
		assertFalse(schMon.schStats.isEmpty());
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		schCons.monitorSchedulesDeregister(subIds);
	}
	
	@Test
	public void testRemoveSchedule() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSub3Id";
		final SchMonitor schMon = new SchMonitor();
		schCons.monitorSchedulesRegister(createSub(subId), schMon);
		
		Long schInstId = submitSchedule(schMon);
		
		schMon.clear(); // clear submit info
		
		schCons.removeSchedule(schInstId);
		
		waitForSch(schMon);
		
		assertFalse(schMon.schStats.isEmpty());
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		schCons.monitorSchedulesDeregister(subIds);
	}
	
	@Test
	public void testPatchSchedule() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSub4Id";
		final SchMonitor schMon = new SchMonitor();
		schCons.monitorSchedulesRegister(createSub(subId), schMon);
		
		Long schDefId = createAndAddDef("test schedule def");
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = createAndSubmitInst(schDefId, schInstId, "test schedule inst");
		
		waitForSch(schMon);
		
		SchedulePatchOperations patchOp = new SchedulePatchOperations();
		patchOp.setScheduleInstName(new Identifier("patch schedule"));
		
		Long targetSchInstId = 2L;
		
		schMon.clear(); // clear submit info
		
		schCons.patchSchedule(schDefId, schInstId, schInst, patchOp, targetSchInstId);
		
		waitForSch(schMon);
		
		assertFalse(schMon.schStats.isEmpty());
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		schCons.monitorSchedulesDeregister(subIds);
	}
	
	@Test
	public void testStart() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSub5Id";
		final SchMonitor schMon = new SchMonitor();
		schCons.monitorSchedulesRegister(createSub(subId), schMon);
		
		Long schInstId = submitSchedule(schMon);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		schMon.clear(); // clear submit info
		
		schCons.start(schInstIds);
		
		waitForSch(schMon);
		
		assertFalse(schMon.schStats.isEmpty());
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		schCons.monitorSchedulesDeregister(subIds);
	}
	
	@Test
	public void testPause() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSub6Id";
		final SchMonitor schMon = new SchMonitor();
		schCons.monitorSchedulesRegister(createSub(subId), schMon);
		
		Long schInstId = submitSchedule(schMon);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		schMon.clear(); // clear submit info
		
		schCons.pause(schInstIds);
		
		waitForSch(schMon);
		
		assertFalse(schMon.schStats.isEmpty());
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		schCons.monitorSchedulesDeregister(subIds);
	}
	
	@Test
	public void testResume() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSub7Id";
		final SchMonitor schMon = new SchMonitor();
		schCons.monitorSchedulesRegister(createSub(subId), schMon);
		
		Long schInstId = submitSchedule(schMon);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		schMon.clear(); // clear submit info
		
		schCons.resume(schInstIds);
		
		waitForSch(schMon);
		
		assertFalse(schMon.schStats.isEmpty());
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		schCons.monitorSchedulesDeregister(subIds);
	}
	
	@Test
	public void testTerminate() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSub8Id";
		final SchMonitor schMon = new SchMonitor();
		schCons.monitorSchedulesRegister(createSub(subId), schMon);
		
		Long schInstId = submitSchedule(schMon);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		schMon.clear(); // clear submit info
		
		schCons.terminate(schInstIds);
		
		waitForSch(schMon);
		
		assertFalse(schMon.schStats.isEmpty());
		
		IdentifierList subIds = new IdentifierList();
		subIds.add(new Identifier(subId));
		schCons.monitorSchedulesDeregister(subIds);
	}
}
