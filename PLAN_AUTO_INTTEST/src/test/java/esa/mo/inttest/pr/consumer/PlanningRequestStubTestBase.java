package esa.mo.inttest.pr.consumer;

import static org.junit.Assert.*;

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
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.junit.After;
import org.junit.Before;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;
import esa.mo.inttest.ca.consumer.ComArchiveConsumerFactory;
import esa.mo.inttest.ca.provider.ComArchiveProviderFactory;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

/**
 * Planning request stub test. Invokes provider methods using generated 'stub' class which includes MAL layer.
 */
public class PlanningRequestStubTestBase {
	
	protected final class PrMonitor extends PlanningRequestAdapter {
		
		protected boolean registered = false;
		protected List<PlanningRequestStatusDetailsList> prStats = new ArrayList<PlanningRequestStatusDetailsList>();
		protected boolean deRegistered = false;
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
		{
			LOG.log(Level.INFO, "pr monitor registration ack");
			registered = true;
			synchronized (this) {
				this.notifyAll();
			}
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
				Map qosProperties)
		{
			LOG.log(Level.INFO, "pr monitor registration err");
			assertTrue(false);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
				UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats, Map qosProps) {
			LOG.log(Level.INFO, "pr monitor notify: subId={0}, List:updateHeaders={1}, List:objectIds={2}, List:prStatuses={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats) });
			this.prStats.add(prStats);
			synchronized (this) {
				this.notifyAll();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
				Map qosProps) {
			LOG.log(Level.INFO, "pr monitor notify error: {0}", error);
			assertTrue(false);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorPlanningRequestsDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
		{
			LOG.log(Level.INFO, "pr monitor de-registration ack");
			deRegistered = true;
			synchronized (this) {
				this.notifyAll();
			}
		}
		
		public boolean haveData() {
			return !prStats.isEmpty();
		}
		
		public void clearData() {
			prStats.clear();
		}
	}

	private static final Logger LOG = Logger.getLogger(PlanningRequestStubTestBase.class.getName());
	
	private ComArchiveProviderFactory caProvFct;
	
	private PlanningRequestProviderFactory prProvFct;
	
	private PlanningRequestConsumerFactory prConsFct;
	protected PlanningRequestStub prCons;
	
	private ComArchiveConsumerFactory caConsFct;
	private ArchiveStub caCons;
	
	private void enter(String msg) {
		LOG.entering(getClass().getName(), msg);
	}
	
	private void leave(String msg) {
		LOG.exiting(getClass().getName(), msg);
	}
	
	@Before
	public void setUp() throws Exception {
		enter("setUp");
		
		String props = "testInt.properties";
		
		prProvFct = new PlanningRequestProviderFactory();
		prProvFct.setPropertyFile(props);
		prProvFct.start(null);
		
		URI sharedBrokerUri = prProvFct.getBrokerUri();
		
		caProvFct = new ComArchiveProviderFactory();
		caProvFct.setPropertyFile(props);
		caProvFct.setBrokerUri(sharedBrokerUri);
		caProvFct.start(null);
		
		caConsFct = new ComArchiveConsumerFactory();
		caConsFct.setPropertyFile(props);
		caConsFct.setProviderUri(caProvFct.getProviderUri());
		caConsFct.setBrokerUri(sharedBrokerUri);
		caCons = caConsFct.start(null);
		
		prConsFct = new PlanningRequestConsumerFactory();
		prConsFct.setPropertyFile(props);
		prConsFct.setProviderUri(prProvFct.getProviderUri());
		prConsFct.setBrokerUri(sharedBrokerUri);
		prCons = prConsFct.start(null);
		
		leave("setUp");
	}

	@After
	public void tearDown() throws Exception {
		enter("tearDown");
		if (prConsFct != null) {
			prConsFct.stop(prCons);
		}
		prCons = null;
		prConsFct = null;
		
		if (caConsFct != null) {
			caConsFct.stop(caCons);
		}
		caConsFct = null;
		
		if (caProvFct != null) {
			caProvFct.stop();
		}
		caProvFct = null;
		
		if (prProvFct != null) {
			prProvFct.stop();
		}
		prProvFct = null;
		leave("tearDown");
	}

	protected ArgumentDefinitionDetails createArgDef(String id, int i) {
		return new ArgumentDefinitionDetails(new Identifier(id), null, (byte)(i & 0xff), null, null, null, null);
	}
	
	protected ArgumentDefinitionDetailsList allAttrDefs() {
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(createArgDef("first", Attribute.BLOB_TYPE_SHORT_FORM));
		args.add(createArgDef("second", Attribute.BOOLEAN_TYPE_SHORT_FORM));
		args.add(createArgDef("third", Attribute.DOUBLE_TYPE_SHORT_FORM));
		args.add(createArgDef("fourth", Attribute.DURATION_TYPE_SHORT_FORM));
		args.add(createArgDef("fifth", Attribute.FINETIME_TYPE_SHORT_FORM));//5
		args.add(createArgDef("sixth", Attribute.FLOAT_TYPE_SHORT_FORM));
		args.add(createArgDef("seventh", Attribute.IDENTIFIER_TYPE_SHORT_FORM));
		args.add(createArgDef("eighth", Attribute.INTEGER_TYPE_SHORT_FORM));
		args.add(createArgDef("ninth", Attribute.LONG_TYPE_SHORT_FORM));
		args.add(createArgDef("tenth", Attribute.OCTET_TYPE_SHORT_FORM));//10
		args.add(createArgDef("eleventh", Attribute.SHORT_TYPE_SHORT_FORM));
		args.add(createArgDef("twelvth", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("thirteenth", Attribute.TIME_TYPE_SHORT_FORM));
		args.add(createArgDef("fourteenth", Attribute.UINTEGER_TYPE_SHORT_FORM));
		args.add(createArgDef("fifteenth", Attribute.ULONG_TYPE_SHORT_FORM));//15
		args.add(createArgDef("sixteenth", Attribute.UOCTET_TYPE_SHORT_FORM));
		args.add(createArgDef("seventeenth", Attribute.URI_TYPE_SHORT_FORM));
		args.add(createArgDef("eigthteenth", Attribute.USHORT_TYPE_SHORT_FORM));
		return args;
	}
	
	protected PlanningRequestDefinitionDetails createPrDef(String id) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef(id, null);
		prDef.setArgumentDefs(allAttrDefs());
		return prDef;
	}
	
	protected Long submitPrDef(PlanningRequestDefinitionDetails prDef) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		LongList prDefIdList = prCons.addDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs);
		return prDefIdList.get(0);
	}
	
	protected ArgumentValueList allArgVals() {
		ArgumentValueList vals = new ArgumentValueList();
		
		vals.add(new ArgumentValue(new Identifier("first"), new Blob(new byte[] { 1, 2, 3 })));
		vals.add(new ArgumentValue(new Identifier("second"), new Union(true)));
		vals.add(new ArgumentValue(new Identifier("third"), new Union((double)2.5)));
		vals.add(new ArgumentValue(new Identifier("fourth"), new Duration(100)));
		vals.add(new ArgumentValue(new Identifier("fifth"), new FineTime(500)));//5
		vals.add(new ArgumentValue(new Identifier("sixth"), new Union(1.5f)));
		vals.add(new ArgumentValue(new Identifier("seventh"), new Identifier("id")));
		vals.add(new ArgumentValue(new Identifier("eighth"), new Union(1)));
		vals.add(new ArgumentValue(new Identifier("ninth"), new Union(2L)));
		vals.add(new ArgumentValue(new Identifier("tenth"), new Union((byte)3)));//10
		vals.add(new ArgumentValue(new Identifier("eleventh"), new Union((short)4)));
		vals.add(new ArgumentValue(new Identifier("twelvth"), new Union("text")));
		vals.add(new ArgumentValue(new Identifier("thirteenth"), new Time(15000000)));
		vals.add(new ArgumentValue(new Identifier("fourteenth"), new UInteger(15)));
		vals.add(new ArgumentValue(new Identifier("fifteenth"), new ULong(new BigInteger("54325432"))));//15
		vals.add(new ArgumentValue(new Identifier("sixteenth"), new UOctet((short)35)));
		vals.add(new ArgumentValue(new Identifier("seventeenth"), new URI("uri")));
		vals.add(new ArgumentValue(new Identifier("eigthteenth"), new UShort(75)));
		
		return vals;
	}
	
	protected PlanningRequestInstanceDetails createPrInst(Long id, Long defId, TaskInstanceDetailsList taskInsts) {
		
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(id, defId, null);
		prInst.setTasks(taskInsts);
		prInst.setArgumentValues(allArgVals());
		return prInst;
	}
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	protected Long generateId() {
		return lastId.incrementAndGet();
	}
	
	protected void storePrInst(PlanningRequestInstanceDetails prInst) throws MALException,
			MALInteractionException {
		
		ObjectType objType = Util.createObjType(prInst);
		
		IdentifierList domain = prConsFct.getDomain();
		
		ObjectDetails objDetail = new ObjectDetails(prInst.getPrDefId(), null);
		
		ArchiveDetails arcDetail = new ArchiveDetails();
		arcDetail.setInstId(prInst.getId()); // mandatory - i guess com archive does not generate id-s - we are
		arcDetail.setDetails(objDetail); // mandatory
		
		ArchiveDetailsList arcDetails = new ArchiveDetailsList();
		arcDetails.add(arcDetail);
		
		PlanningRequestInstanceDetailsList elements = new PlanningRequestInstanceDetailsList();
		elements.add(prInst);
		
		caCons.store(false, objType, domain, arcDetails, elements);
	}
	
	protected PlanningRequestInstanceDetails createAndSubmitPlanningRequest() throws MALException,
				MALInteractionException {
		
		PlanningRequestDefinitionDetails prDef = createPrDef("id1");
		prDef.setId(0L); // dummy
		Long prDefId = submitPrDef(prDef);
		prDef.setId(prDefId);
		
		PlanningRequestInstanceDetails prInst = createPrInst(generateId(), prDefId, null);
		
		storePrInst(prInst);
		
		prCons.submitPlanningRequest(prInst);
		
		return prInst;
	}

	protected TaskDefinitionDetails createTaskDef(String id) {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef(id, null);
		taskDef.setArgumentDefs(allAttrDefs());
		return taskDef;
	}
	
	protected Long submitTaskDef(TaskDefinitionDetails taskDef) throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		LongList taskDefIds = prCons.addDefinition(DefinitionType.TASK_DEF, taskDefs);
		return taskDefIds.get(0);
	}
	
	protected TaskInstanceDetails createTaskInst(Long id, Long defId) {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(id, defId, null);
		taskInst.setArgumentValues(allArgVals());
		return taskInst;
	}
	
	protected void storeTaskInst(TaskInstanceDetails taskInst) throws MALException,
			MALInteractionException {
		
		ObjectType objType = Util.createObjType(taskInst);
		
		IdentifierList domain = prConsFct.getDomain();
		
		ObjectDetails objDetail = new ObjectDetails();
		objDetail.setRelated(taskInst.getTaskDefId());
		
		ArchiveDetails arcDetail = new ArchiveDetails();
		arcDetail.setInstId(taskInst.getId());
		arcDetail.setDetails(objDetail);
		
		ArchiveDetailsList arcDetails = new ArchiveDetailsList();
		arcDetails.add(arcDetail);
		
		TaskInstanceDetailsList elements = new TaskInstanceDetailsList();
		elements.add(taskInst);
		
		caCons.store(false, objType, domain, arcDetails, elements);
	}
	
	protected PlanningRequestInstanceDetails createAndSubmitPlanningRequestWithTask() throws MALException, MALInteractionException {
		
		TaskDefinitionDetails taskDef = createTaskDef("id2");
		taskDef.setId(0L); // dummy
		Long taskDefId = submitTaskDef(taskDef);
		taskDef.setId(taskDefId);
		
		PlanningRequestDefinitionDetails prDef = createPrDef("id1");
		prDef.setId(0L);
		Long prDefId = submitPrDef(prDef);
		prDef.setId(prDefId);
		
		TaskInstanceDetails taskInst = createTaskInst(generateId(), taskDefId);
		Long prId = generateId();
		taskInst.setPrInstId(prId);
		storeTaskInst(taskInst);
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prId, prDefId, taskInsts);
		storePrInst(prInst);
		
		prCons.submitPlanningRequest(prInst);
		
		return prInst;
	}

	protected void updatePlanningRequestWithTask(PlanningRequestInstanceDetails prInst) throws MALException, MALInteractionException {
		
		TaskDefinitionDetails taskDef = createTaskDef("id2");
		taskDef.setId(0L);
		Long taskDefId = submitTaskDef(taskDef);
		taskDef.setId(taskDefId);
		
		TaskInstanceDetails taskInst = createTaskInst(generateId(), taskDefId);
		taskInst.setPrInstId(prInst.getId());
		storeTaskInst(taskInst);
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		prInst.setComment("new updated desc");
		prInst.setTasks(taskInsts);
		
		prCons.updatePlanningRequest(prInst);
	}
	
	protected void removePlanningRequest(Long prInstId) throws MALException, MALInteractionException {
		prCons.removePlanningRequest(prInstId);
	}
	
	protected PlanningRequestStatusDetailsList getPlanningRequestStatus(Long prInstId) throws MALException, MALInteractionException {
		LongList prInstIds = new LongList();
		prInstIds.add(prInstId);
		return prCons.getPlanningRequestStatus(prInstIds);
	}

	protected PrMonitor registerPrMonitor(String subId) throws MALException, MALInteractionException {
		Identifier id = new Identifier(subId);
		EntityRequestList entityList = new EntityRequestList();
		EntityKeyList keys = new EntityKeyList();
		keys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityList.add(new EntityRequest(null, true, true, true, false, keys));
		Subscription sub = new Subscription(id, entityList);
		PrMonitor prReg = new PrMonitor();
		prCons.monitorPlanningRequestsRegister(sub, prReg);
		return prReg;
	}
	
	protected void deRegisterPrMonitor(String subId) throws MALException, MALInteractionException {
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier(subId));
		prCons.monitorPlanningRequestsDeregister(subIdList);
	}
	
	protected LongList listPrDefs(String id) throws MALException, MALInteractionException {
		IdentifierList ids = new IdentifierList();
		ids.add(new Identifier(id));
		return prCons.listDefinition(DefinitionType.PLANNING_REQUEST_DEF, ids);
	}
	
	protected PlanningRequestDefinitionDetails addPrDef() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setId(0L); // mandatory, dummy
		prDef.setName(new Identifier("new pr def")); // mandatory
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		LongList prDefIds = prCons.addDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs);
		prDef.setId(prDefIds.get(0));
		return prDef;
	}
	
	protected LongList listTaskDefs(String f) throws MALException, MALInteractionException {
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier(f));
		return prCons.listDefinition(DefinitionType.TASK_DEF, idList);
	}
	
	/**
	 * Creates dummy task def and submits it to PR provider.
	 * @return Object[] { taskDefIdList, taskDefList }
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	protected TaskDefinitionDetails addTaskDef() throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefList = new TaskDefinitionDetailsList();
		TaskDefinitionDetails taskDef = createTaskDef("new task def");
		taskDef.setId(0L); // mandatory, dummy
		taskDefList.add(taskDef);
		LongList taskDefIdList = prCons.addDefinition(DefinitionType.TASK_DEF, taskDefList);
		taskDef.setId(taskDefIdList.get(0));
		return taskDef;
	}

	protected void verifyPrStat(Long id) throws MALException, MALInteractionException {
		LongList prIds = new LongList();
		prIds.add(id);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNotNull(prStats.get(0));
		assertEquals(id, prStats.get(0).getPrInstId());
	}
}
