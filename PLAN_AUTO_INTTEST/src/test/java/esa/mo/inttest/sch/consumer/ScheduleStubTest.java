package esa.mo.inttest.sch.consumer;

import static org.junit.Assert.*;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleAdapter;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
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
	
	protected ScheduleInstanceDetails createInst(Long id, Long defId/*, String n*/) {
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
		schInst.setId(id);
		schInst.setSchDefId(defId);
//		schInst.setName(new Identifier(n));
		return schInst;
	}
	
	protected void verifySch(Long id) throws MALException, MALInteractionException {
		LongList schInstIds = new LongList();
		schInstIds.add(id);
		
		ScheduleStatusDetailsList schStats = schCons.getScheduleStatus(schInstIds);
		
		assertNotNull(schStats);
		assertEquals(1, schStats.size());
		assertNotNull(schStats.get(0));
	}
	
	@Test
	public void testSubmitSchedule() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("test schedule def");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		schDef.setId(schDefIds.get(0));
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefIds.get(0));
		
		schCons.submitSchedule(schInst);
		
		verifySch(schInstId);
	}
	
	@Test
	public void testUpdateSchedule() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("test schedule definition");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		schDef.setId(schDefIds.get(0));
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefIds.get(0));
		
		schCons.submitSchedule(schInst);
		
		schInst.setComment("updated description");
		
		schCons.updateSchedule(schInst);
		
		verifySch(schInstId);
	}
	
	@Test
	public void testRemoveSchedule() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 1");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		schDef.setId(schDefIds.get(0));
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefIds.get(0));
		
		schCons.submitSchedule(schInst);
		
		schCons.removeSchedule(schInstId);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		ScheduleStatusDetailsList schStats = schCons.getScheduleStatus(schInstIds);
		
		assertNotNull(schStats);
		assertEquals(1, schStats.size());
		assertNull(schStats.get(0)); // no status
	}
	
	@Test
	public void testPatchSchedule() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 2");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		schDef.setId(schDefIds.get(0));
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefIds.get(0));
		
		schCons.submitSchedule(schInst);
		
//		Long targetSchInstId = 2L;
//		SchedulePatchOperations patchOp = new SchedulePatchOperations();
//		patchOp.setScheduleInstName(new Identifier("patch schedule"));
		schInst.setComment("new modified comment");
		
		ScheduleInstanceDetailsList update = new ScheduleInstanceDetailsList();
		update.add(schInst);
		
		schCons.patchSchedule(null, update, null);
	}
	
	@Test
	public void testGetScheduleStatus() throws MALException, MALInteractionException {
		ScheduleDefinitionDetails schDef = createDef("test schedule def 3");
		schDef.setId(0L);
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		
		LongList schDefIds = schCons.addDefinition(schDefs);
		schDef.setId(schDefIds.get(0));
		
		Long schInstId = 1L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefIds.get(0));
		
		schCons.submitSchedule(schInst);
		
		verifySch(schInstId);
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
		Long schDefId = 1L;
		Long schInstId = 2L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefId);
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		schCons.start(schInstIds);
	}
	
	@Test
	public void testPause() throws MALException, MALInteractionException {
		Long schDefId = 1L;
		Long schInstId = 2L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefId);
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		schCons.pause(schInstIds);
	}
	
	@Test
	public void testResume() throws MALException, MALInteractionException {
		Long schDefId = 1L;
		Long schInstId = 2L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefId);
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		schCons.resume(schInstIds);
	}
	
	@Test
	public void testTerminate() throws MALException, MALInteractionException {
		Long schDefId = 1L;
		Long schInstId = 2L;
		ScheduleInstanceDetails schInst = createInst(schInstId, schDefId);
		schCons.submitSchedule(schInst);
		
		LongList schInstIds = new LongList();
		schInstIds.add(schInstId);
		
		schCons.terminate(schInstIds);
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
		
		schCons.updateDefinition(schDefIds, schDefs);
		
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
