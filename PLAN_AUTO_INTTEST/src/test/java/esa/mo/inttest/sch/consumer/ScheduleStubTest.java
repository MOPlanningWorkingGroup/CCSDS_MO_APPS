package esa.mo.inttest.sch.consumer;

import static org.junit.Assert.*;

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
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.junit.Test;

public class ScheduleStubTest extends ScheduleStubTestBase {

	@Test
	public void testScheduleStub() {
		assertNotNull(schCons);
	}

	@Test
	public void testGetConsumer() {
		assertNotNull(schCons.getConsumer());
	}
	
	protected ScheduleDefinitionDetails createDef(String n) {
		ScheduleDefinitionDetails schDef = new ScheduleDefinitionDetails();
		schDef.setName(new Identifier(n));
		schDef.setEventTypes(new ObjectTypeList());
		return schDef;
	}
	
	protected ScheduleInstanceDetails createInst(Long id, Long defId) {
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
		schInst.setId(id);
		schInst.setSchDefId(defId);
		return schInst;
	}
	
	protected void verifySch(ScheduleInstanceDetails sch) throws MALException, MALInteractionException {
		LongList schInstIds = new LongList();
		schInstIds.add(sch.getId());
		
		ScheduleStatusDetailsList schStats = schCons.getScheduleStatus(schInstIds);
		
		assertNotNull(schStats);
		assertEquals(1, schStats.size());
		assertNotNull(schStats.get(0));
	}
	
	protected void submitSchedule(ScheduleInstanceDetails sch) throws MALException, MALInteractionException {
		ScheduleInstanceDetailsList insts = new ScheduleInstanceDetailsList();
		insts.add(sch);
		
		ScheduleStatusDetailsList schStats = schCons.submitSchedule(insts);
		
		assertNotNull(schStats);
		assertFalse(schStats.isEmpty());
		
		ScheduleStatusDetails schStat = schStats.get(0);
		
		assertNotNull(schStat);
		assertEquals(sch.getId(), schStat.getSchInstId());
		
		StatusRecordList recs = schStat.getStatus();
		
		assertNotNull(recs);
		assertFalse(recs.isEmpty());
	}
	
	protected ScheduleInstanceDetails createAndSubmitSch() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("test schedule def");
		schDef.setId(0L);
		
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		assertNotNull(schDefIds);
		assertFalse(schDefIds.isEmpty());
		
		schDef.setId(schDefIds.get(0));
		
		Long schInstId = 1L;
		ScheduleInstanceDetails sch = createInst(schInstId, schDefIds.get(0));
		
		submitSchedule(sch);
		
		return sch;
	}
	
	@Test
	public void testSubmitSchedule() throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createAndSubmitSch();
		
		verifySch(schInst);
	}
	
	@Test
	public void testUpdateSchedule() throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createAndSubmitSch();
		
		schInst.setComment("updated description");
		
		ScheduleInstanceDetailsList insts = new ScheduleInstanceDetailsList();
		insts.add(schInst);
		
		schCons.updateSchedule(insts);
		
		verifySch(schInst);
	}
	
	@Test
	public void testRemoveSchedule() throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createAndSubmitSch();
		
		LongList ids = new LongList();
		ids.add(schInst.getId());
		
		schCons.removeSchedule(ids);
		
		ScheduleStatusDetailsList schStats = schCons.getScheduleStatus(ids);
		
		assertNotNull(schStats);
		assertEquals(1, schStats.size());
		assertNull(schStats.get(0)); // no status
	}
	
	@Test
	public void testPatchSchedule() throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createAndSubmitSch();
		
		schInst.setComment("new modified comment");
		
		ScheduleInstanceDetailsList update = new ScheduleInstanceDetailsList();
		update.add(schInst);
		
		ScheduleStatusDetailsList stats = schCons.patchSchedule(null, update, null);
		
		assertNotNull(stats);
		assertFalse(stats.isEmpty());
		
		ScheduleStatusDetails stat = stats.get(0);
		
		assertNotNull(stat);
		assertEquals(schInst.getId(), stat.getSchInstId());
	}
	
	@Test
	public void testGetScheduleStatus() throws MALException, MALInteractionException {
		ScheduleInstanceDetails inst = createAndSubmitSch();
		
		verifySch(inst);
	}
	
	@Test
	public void testMonitorSchedulesRegister() throws MALException, MALInteractionException {
		String subId = "schSubId";
		Subscription sub = createSub(subId);
		
		schCons.monitorSchedulesRegister(sub, new ScheduleAdapter() {
			// we wont be receiving any register callbacks since this is sync call
			// and we arent testing notify
		});
	}

	@Test
	public void testMonitorSchedulesDeregister() throws MALException, MALInteractionException {
		String subId = "schSub2Id";
		Subscription sub = createSub(subId);
		
		schCons.monitorSchedulesRegister(sub, null);
		
		IdentifierList ids = new IdentifierList();
		ids.add(new Identifier(subId));
		
		schCons.monitorSchedulesDeregister(ids);
	}
	
	@Test
	public void testStart() throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createAndSubmitSch();
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInst.getId());
		
		ScheduleStatusDetailsList stats = schCons.start(schInstIds);
		
		assertNotNull(stats);
		assertFalse(stats.isEmpty());
		
		ScheduleStatusDetails stat = stats.get(0);
		
		assertNotNull(stat);
		assertEquals(stat.getSchInstId(), schInst.getId());
		
		assertNotNull(stat.getStatus());
		assertFalse(stat.getStatus().isEmpty());
	}
	
	@Test
	public void testPause() throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createAndSubmitSch();
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInst.getId());
		
		ScheduleStatusDetailsList stats = schCons.pause(schInstIds);
		
		assertNotNull(stats);
		assertFalse(stats.isEmpty());
		
		ScheduleStatusDetails stat = stats.get(0);
		
		assertNotNull(stat);
		assertEquals(stat.getSchInstId(), schInst.getId());
		assertNotNull(stat.getStatus());
		assertFalse(stat.getStatus().isEmpty());
	}
	
	@Test
	public void testResume() throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createAndSubmitSch();
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInst.getId());
		
		ScheduleStatusDetailsList stats = schCons.resume(schInstIds);
		
		assertNotNull(stats);
		assertFalse(stats.isEmpty());
		
		ScheduleStatusDetails stat = stats.get(0);
		
		assertNotNull(stat);
		assertEquals(stat.getSchInstId(), schInst.getId());
		assertNotNull(stat.getStatus());
		assertFalse(stat.getStatus().isEmpty());
	}
	
	@Test
	public void testTerminate() throws MALException, MALInteractionException {
		ScheduleInstanceDetails schInst = createAndSubmitSch();
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInst.getId());
		
		ScheduleStatusDetailsList stats = schCons.terminate(schInstIds);
		
		assertNotNull(stats);
		assertFalse(stats.isEmpty());
		
		ScheduleStatusDetails stat = stats.get(0);
		
		assertNotNull(stat);
		assertEquals(stat.getSchInstId(), schInst.getId());
		assertNotNull(stat.getStatus());
		assertFalse(stat.getStatus().isEmpty());
	}
	
	@Test
	public void testListDefinition() throws MALException, MALInteractionException {
		IdentifierList schNames = new IdentifierList();
		schNames.add(new Identifier("*"));
		
		LongList schDefIds = schCons.listDefinition(schNames);
		// currently get nothing
		assertNotNull(schDefIds);
		assertEquals(0, schDefIds.size());
	}
	
	@Test
	public void testAddDefinition() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("schedule test def 4");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		
		assertNotNull(schDefIds);
		assertEquals(1, schDefIds.size());
		assertNotNull(schDefIds.get(0));
		
		schDef.setId(schDefIds.get(0));
		
		IdentifierList schNames = new IdentifierList();
		schNames.add(new Identifier("*"));
		
		LongList schDefIds2 = schCons.listDefinition(schNames);
		
		assertNotNull(schDefIds2);
		// add()ed id is list()ed
		assertTrue(schDefIds2.contains(schDefIds.get(0)));
	}
	
	@Test
	public void testUpdateDefinition() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 5");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		schDef.setId(schDefIds.get(0));
		schDef.setDescription("updated description");
		
		schCons.updateDefinition(schDefs);
		
		IdentifierList schNames = new IdentifierList();
		schNames.add(new Identifier("*"));
		
		LongList schDefIds2 = schCons.listDefinition(schNames);
		
		assertNotNull(schDefIds2);
		// add()ed schedule is still list()ed
		assertTrue(schDefIds2.contains(schDefIds.get(0)));
	}
	
	@Test
	public void testRemoveDefinition() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 6");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		schDef.setId(schDefIds.get(0));
		
		schCons.removeDefinition(schDefIds);
		
		IdentifierList schNames = new IdentifierList();
		schNames.add(new Identifier("*"));
		
		LongList schDefIds2 = schCons.listDefinition(schNames);
		
		assertNotNull(schDefIds2);
		// add()ed def is not list()ed anymore
		assertTrue(!schDefIds2.contains(schDefIds.get(0)));
	}
}
