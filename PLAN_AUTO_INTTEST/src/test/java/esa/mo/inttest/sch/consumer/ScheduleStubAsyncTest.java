package esa.mo.inttest.sch.consumer;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.Callable;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleAdapter;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
import org.junit.Test;

import esa.mo.inttest.Util;

public class ScheduleStubAsyncTest extends ScheduleStubTestBase {

	protected ScheduleDefinitionDetails createDef(String n) {
		ScheduleDefinitionDetails schDef = new ScheduleDefinitionDetails();
		schDef.setName(new Identifier(n));
		schDef.setEventTypes(new ObjectTypeList());
		return schDef;
	}
	
	@Test
	public void testAsyncSubmitSchedule() throws MALException, MALInteractionException, InterruptedException, Exception {
		ScheduleDefinitionDetails schDef = createDef("test schedule def");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
		schInst.setId(1L);
		schInst.setSchDefId(schDefIds.get(0));
		
		Long schInstId = 1L;
		final boolean[] submitted = { false };
		
		MALMessage msg = schCons.asyncSubmitSchedule(schInst, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void submitScheduleResponseReceived(MALMessageHeader msgHeader,
					ScheduleStatusDetails schStat, Map qosProperties) {
				submitted[0] = true;
				synchronized (submitted) {
					submitted.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void submitScheduleErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(submitted, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return submitted[0];
			}
		});
		
		assertTrue(submitted[0]);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		ScheduleStatusDetailsList schStats = schCons.getScheduleStatus(schInstIds);
		
		assertNotNull(schStats);
		assertEquals(1, schStats.size());
		assertNotNull(schStats.get(0));
		
		msg.free();
	}
	
	@Test
	public void testAsyncUpdateSchedule() throws MALException, MALInteractionException, InterruptedException, Exception {
		ScheduleDefinitionDetails schDef = createDef("test schedule definition");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
		schInst.setId(1L);
		schInst.setSchDefId(schDefIds.get(0));
		
		Long schInstId = 1L;
		
		schCons.submitSchedule(schInst);
		
		schInst.setComment("updated description");
		
		final boolean[] updated = { false };
		
		MALMessage msg = schCons.asyncUpdateSchedule(schInst, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updateScheduleResponseReceived(MALMessageHeader msgHeader,
					ScheduleStatusDetails schStat, Map qosProps) {
				updated[0] = true;
				synchronized (updated) {
					updated.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void updateScheduleErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(updated, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return updated[0];
			}
		});
		
		assertTrue(updated[0]);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		ScheduleStatusDetailsList schStats = schCons.getScheduleStatus(schInstIds);
		
		assertNotNull(schStats);
		assertEquals(1, schStats.size());
		assertNotNull(schStats.get(0));
		
		msg.free();
	}
	
	@Test
	public void testAsyncRemoveSchedule() throws MALException, MALInteractionException, InterruptedException, Exception {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 1");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
		schInst.setId(1L);
		schInst.setSchDefId(schDefIds.get(0));
		
		Long schInstId = 1L;
		
		schCons.submitSchedule(schInst);
		
		final boolean[] removed = { false };
		
		MALMessage msg = schCons.asyncRemoveSchedule(schInstId, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void removeScheduleResponseReceived(MALMessageHeader msgHeader,
					ScheduleStatusDetails schStat, Map qosProps) {
				removed[0] = true;
				synchronized (removed) {
					removed.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void removeScheduleErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(removed, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return removed[0];
			}
		});
		
		assertTrue(removed[0]);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		ScheduleStatusDetailsList schStats = schCons.getScheduleStatus(schInstIds);
		
		assertNotNull(schStats);
		assertEquals(1, schStats.size());
		assertNull(schStats.get(0)); // no status
		
		msg.free();
	}
	
	@Test
	public void testAsyncPatchSchedule() throws MALException, MALInteractionException, InterruptedException, Exception {
		ScheduleDefinitionDetails schDef = createDef("test schdule def 2");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = createInst();
		schInst.setId(schInstId);
		schInst.setSchDefId(schDefIds.get(0));
		
		schCons.submitSchedule(schInst);
		
//		Long targetSchInstId = 2L;
//		SchedulePatchOperations patchOp = new SchedulePatchOperations();
//		patchOp.setScheduleInstName(new Identifier("patch schedule"));
		schInst.setComment("new modified comment");
		
		ScheduleInstanceDetailsList update = new ScheduleInstanceDetailsList();
		update.add(schInst);
		
		final boolean[] patched = { false };
		
		MALMessage msg = schCons.asyncPatchSchedule(null, update, null, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void patchScheduleAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				patched[0] = true;
				synchronized (patched) {
					patched.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void patchScheduleErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(patched, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return patched[0];
			}
		});
		
		assertTrue(patched[0]);
		
		msg.free();
	}
	
	@Test
	public void testAsyncGetScheduleStatus() throws MALException, MALInteractionException, InterruptedException, Exception {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 3");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
		schInst.setId(schInstId);
		schInst.setSchDefId(schDefIds.get(0));
		
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		final ScheduleStatusDetailsList[] stats = { null };
		
		MALMessage msg = schCons.asyncGetScheduleStatus(schInstIds, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void getScheduleStatusResponseReceived(MALMessageHeader msgHeader,
					ScheduleStatusDetailsList schStatus, Map qosProps) {
				stats[0] = schStatus;
				synchronized (stats) {
					stats.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void getScheduleStatusErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(stats, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != stats[0];
			}
		});
		
		assertNotNull(stats[0]);
		assertEquals(1, stats[0].size());
		assertNotNull(stats[0].get(0));
		
		msg.free();
	}
	
	@Test
	public void testAsyncMonitorSchedulesRegister() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSubId";
		Subscription sub = createSub(subId);
		
		final boolean[] registered = { false };
		
		MALMessage msg = schCons.asyncMonitorSchedulesRegister(sub, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorSchedulesRegisterAckReceived(MALMessageHeader msgHdr, Map qosProps) {
				registered[0] = true;
				synchronized (registered) {
					registered.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorSchedulesRegisterErrorReceived(MALMessageHeader msgHdr, MALStandardError error,
					Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(registered, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return registered[0];
			}
		});
		
		assertTrue(registered[0]);
		
		msg.free();
	}
	
	@Test
	public void testAsyncMonitorSchedulesDeregister() throws MALException, MALInteractionException, InterruptedException, Exception {
		String subId = "schSub2Id";
		Subscription sub = createSub(subId);
		
		schCons.monitorSchedulesRegister(sub, null);
		
		IdentifierList ids = new IdentifierList();
		ids.add(new Identifier(subId));
		
		final boolean[] dereg = { false };
		
		MALMessage msg = schCons.asyncMonitorSchedulesDeregister(ids, new ScheduleAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorSchedulesDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
				dereg[0] = true;
				synchronized (dereg) {
					dereg.notifyAll();
				}
			}
		});
		
		Util.waitFor(dereg, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return dereg[0];
			}
		});
		
		assertTrue(dereg[0]);
		
		msg.free();
	}
	
	protected ScheduleInstanceDetails createInst() {
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
		schInst.setTimingConstraints(new TimingDetailsList());
		return schInst;
	}
	
	@Test
	public void testAsyncStart() throws MALException, MALInteractionException, InterruptedException, Exception {
		Long schDefId = 1L;
		Long schInstId = 2L;
		ScheduleInstanceDetails schInst = createInst();
		schInst.setId(schInstId);
		schInst.setSchDefId(schDefId);
		
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		final boolean[] started = { false };
		
		MALMessage msg = schCons.asyncStart(schInstIds, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void startAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				started[0] = true;
				synchronized (started) {
					started.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void startErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(started, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return started[0];
			}
		});
		
		assertTrue(started[0]);
		
		msg.free();
	}
	
	@Test
	public void testAsyncPause() throws MALException, MALInteractionException, InterruptedException, Exception {
		Long schDefId = 1L;
		Long schInstId = 2L;
		ScheduleInstanceDetails schInst = createInst();
		schInst.setId(schInstId);
		schInst.setSchDefId(schDefId);
		
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		final boolean[] paused = { false };
		
		MALMessage msg = schCons.asyncPause(schInstIds, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void pauseAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				paused[0] = true;
				synchronized (paused) {
					paused.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void pauseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(paused, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return paused[0];
			}
		});
		
		assertTrue(paused[0]);
		
		msg.free();
	}
	
	@Test
	public void testAsyncResume() throws MALException, MALInteractionException, InterruptedException, Exception {
		Long schDefId = 1L;
		Long schInstId = 2L;
		ScheduleInstanceDetails schInst = createInst();
		schInst.setId(schInstId);
		schInst.setSchDefId(schDefId);
		
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		final boolean[] resumed = { false };
		
		MALMessage msg = schCons.asyncResume(schInstIds, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void resumeAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				resumed[0] = true;
				synchronized (resumed) {
					resumed.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void resumeErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(resumed, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return resumed[0];
			}
		});
		
		assertTrue(resumed[0]);
		
		msg.free();
	}
	
	@Test
	public void testAsyncTerminate() throws MALException, MALInteractionException, InterruptedException, Exception {
		Long schDefId = 1L;
		Long schInstId = 2L;
		ScheduleInstanceDetails schInst = createInst();
		schInst.setId(schInstId);
		schInst.setSchDefId(schDefId);
		
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		final boolean[] terminated = { false };
		
		MALMessage msg = schCons.asyncTerminate(schInstIds, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void terminateAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				terminated[0] = true;
				synchronized (terminated) {
					terminated.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void terminateErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(terminated, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return terminated[0];
			}
		});
		
		assertTrue(terminated[0]);
		
		msg.free();
	}
	
	@Test
	public void testAsyncListDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		IdentifierList schNames = new IdentifierList();
		schNames.add(new Identifier("*"));
		
		final LongList[] ids = { null };
		
		MALMessage msg = schCons.asyncListDefinition(schNames, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void listDefinitionResponseReceived(MALMessageHeader msgHeader, LongList schDefInstIds, Map qosProps) {
				ids[0] = schDefInstIds;
				synchronized (ids) {
					ids.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void listDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(ids, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != ids[0];
			}
		});
		// currently get nothing
		assertNotNull(ids[0]);
		assertEquals(0, ids[0].size());
		
		msg.free();
	}
	
	@Test
	public void testAsyncAddDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		ScheduleDefinitionDetails schDef = createDef("schedule test def 4");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		final LongList[] ids = { null };
		
		MALMessage msg = schCons.asyncAddDefinition(schDefs, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void addDefinitionResponseReceived(MALMessageHeader msgHeader, LongList schDefInstIds, Map qosProps) {
				ids[0] = schDefInstIds;
				synchronized (ids) {
					ids.notifyAll();
				}
			}
			
			@SuppressWarnings("rawtypes")
			public void addDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(ids, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return null != ids[0];
			}
		});
		
		assertNotNull(ids[0]);
		assertEquals(1, ids[0].size());
		assertNotNull(ids[0].get(0));
		
		IdentifierList schNames = new IdentifierList();
		schNames.add(new Identifier("*"));
		
		LongList schDefIds2 = schCons.listDefinition(schNames);
		
		assertNotNull(schDefIds2);
		// add()ed id is list()ed
		assertTrue(schDefIds2.contains(ids[0].get(0)));
		
		msg.free();
	}
	
	@Test
	public void testAsyncUpdateDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 5");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		schDefs.get(0).setDescription("updated description");
		
		final boolean[] updated = { false };
		
		MALMessage msg = schCons.asyncUpdateDefinition(schDefIds, schDefs, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void updateDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				updated[0] = true;
				synchronized (updated) {
					updated.notifyAll();
				}
			}

			@SuppressWarnings("rawtypes")
			public void updateDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(updated, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return updated[0];
			}
		});
		
		assertTrue(updated[0]);
		
		IdentifierList schNames = new IdentifierList();
		schNames.add(new Identifier("*"));
		
		LongList schDefIds2 = schCons.listDefinition(schNames);
		
		assertNotNull(schDefIds2);
		// add()ed schedule is still list()ed
		assertTrue(schDefIds2.contains(schDefIds.get(0)));
		
		msg.free();
	}

	@Test
	public void testAsyncRemoveDefinition() throws MALException, MALInteractionException, InterruptedException, Exception {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 6");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		final boolean[] removed = { false };
		
		MALMessage msg = schCons.asyncRemoveDefinition(schDefIds, new ScheduleAdapter() {
			
			@SuppressWarnings("rawtypes")
			public void removeDefinitionAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				removed[0] = true;
				synchronized (removed) {
					removed.notifyAll();
				}
			}

			@SuppressWarnings("rawtypes")
			public void removeDefinitionErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				assertTrue(false);
			}
		});
		
		Util.waitFor(removed, 1000, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return removed[0];
			}
		});
		
		assertTrue(removed[0]);
		
		IdentifierList schNames = new IdentifierList();
		schNames.add(new Identifier("*"));
		
		LongList schDefIds2 = schCons.listDefinition(schNames);
		
		assertNotNull(schDefIds2);
		// add()ed def is not list()ed anymore
		assertTrue(!schDefIds2.contains(schDefIds.get(0)));
		
		msg.free();
	}
}
