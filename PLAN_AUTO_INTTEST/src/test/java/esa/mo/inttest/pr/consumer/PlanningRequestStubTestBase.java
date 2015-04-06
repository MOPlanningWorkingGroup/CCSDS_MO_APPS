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
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
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
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.After;
import org.junit.Before;

import java.util.AbstractMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.ca.consumer.ComArchiveConsumerFactory;
import esa.mo.inttest.ca.provider.ComArchiveProviderFactory;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

/**
 * Planning request stub test. Invokes provider methods using generated 'stub' class which includes MAL layer.
 */
public class PlanningRequestStubTestBase {
	
	protected final class TaskMonitor extends PlanningRequestAdapter {
		
		protected boolean registered = false;
		protected TaskStatusDetailsList taskStats = null;
		protected boolean deRegistered = false;
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
		{
			LOG.log(Level.INFO, "task monitor registration ack");
			registered = true;
			synchronized (this) {
				this.notifyAll();
			}
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
				Map qosProperties)
		{
			LOG.log(Level.INFO, "task monitor registration err");
			assertTrue(false);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier subId, UpdateHeaderList updHdrs,
				ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
			LOG.log(Level.INFO, "task monitor notify: subId={0},\n  updateHeaders={1},\n  objectIds={2},\n  taskStatuses={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.taskStats(taskStats) });
			this.taskStats = taskStats;
			synchronized (this) {
				this.notifyAll();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
				Map qosProps) {
			LOG.log(Level.INFO, "task monitor notify error: {0}", error);
			assertTrue(false);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
				throws MALException {
			LOG.log(Level.INFO, "task monitor other notify: {0}", body);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void monitorTasksDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties)
		{
			LOG.log(Level.INFO, "task monitor de-registration ack");
			deRegistered = true;
			synchronized (this) {
				this.notifyAll();
			}
		}
	}

	protected final class PrMonitor extends PlanningRequestAdapter {
		
		protected boolean registered = false;
		protected PlanningRequestStatusDetailsList prStats = null;
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
			LOG.log(Level.INFO, "pr monitor notify: subId={0}, updateHeaders={1}, objectIds={2}, prStatuses={3}",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats) });
			this.prStats = prStats;
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
		public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
				throws MALException {
			LOG.log(Level.INFO, "pr monitor other notify: {0}", body);
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

	protected PlanningRequestDefinitionDetails createPrDef(String id) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier(id)); // mandatory - encoding exception if missing/null
		return prDef;
	}
	
	protected Long submitPrDef(PlanningRequestDefinitionDetails prDef) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		LongList prDefIdList = prCons.addDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs);
		return prDefIdList.get(0);
	}
	
	protected PlanningRequestInstanceDetails createPrInst(PlanningRequestDefinitionDetails prDef,
			TaskInstanceDetailsList taskInsts) {
		
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setName(prDef.getName()); // mandatory
		prInst.setTasks(taskInsts);
		return prInst;
	}
	
	private long lastId = 0;
	
	protected Long generateId() {
		return ++lastId;
	}
	
	protected void storePrInst(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst) throws MALException,
			MALInteractionException {
		
		ObjectType objType = new ObjectType(PlanningHelper.PLANNING_AREA_NUMBER,
				PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NUMBER, PlanningHelper.PLANNING_AREA_VERSION,
				new UShort(1));
		
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("test"));
		
		ObjectDetails objDetail = new ObjectDetails(prDefId, null);
		
		ArchiveDetails arcDetail = new ArchiveDetails();
		arcDetail.setInstId(prInstId); // mandatory - i guess com archive does not generate id-s - we are
		arcDetail.setDetails(objDetail); // mandatory
		
		ArchiveDetailsList arcDetails = new ArchiveDetailsList();
		arcDetails.add(arcDetail);
		
		PlanningRequestInstanceDetailsList elements = new PlanningRequestInstanceDetailsList();
		elements.add(prInst);
		
		caCons.store(false, objType, domain, arcDetails, elements);
	}
	
	protected Object[] createAndSubmitPlanningRequest() throws MALException,
				MALInteractionException {
		
		PlanningRequestDefinitionDetails prDef = createPrDef("id1");
		Long prDefId = submitPrDef(prDef);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, null);
		Long prInstId = generateId();
		
		storePrInst(prDefId, prInstId, prInst);
		
		prCons.submitPlanningRequest(prDefId, prInstId, prInst, null, null);
		
		return new Object[] { prDefId, prDef, prInstId, prInst };
	}

	protected TaskDefinitionDetails createTaskDef(String id, String prDefName) {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier(id)); // mandatory
		taskDef.setPrDefName(new Identifier(prDefName)); // mandatory
		return taskDef;
	}
	
	protected Long submitTaskDef(TaskDefinitionDetails taskDef) throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		LongList taskDefIds = prCons.addDefinition(DefinitionType.TASK_DEF, taskDefs);
		return taskDefIds.get(0);
	}
	
	protected TaskInstanceDetails createTaskInst(TaskDefinitionDetails taskDef) {
		TaskInstanceDetails taskInst = new TaskInstanceDetails();
		taskInst.setName(taskDef.getName()); // mandatory
		taskInst.setPrName(taskDef.getPrDefName()); // mandatory
		return taskInst;
	}
	
	protected void storeTaskInst(Long taskDefId, Long taskInstId, TaskInstanceDetails taskInst) throws MALException,
			MALInteractionException {
		
		ObjectType objType = new ObjectType(TaskInstanceDetails.AREA_SHORT_FORM, TaskInstanceDetails.SERVICE_SHORT_FORM,
				TaskInstanceDetails.AREA_VERSION, new UShort(1));
		
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		
		ObjectDetails objDetail = new ObjectDetails();
		objDetail.setRelated(taskDefId);
		
		ArchiveDetails arcDetail = new ArchiveDetails();
		arcDetail.setInstId(taskInstId);
		arcDetail.setDetails(objDetail);
		
		ArchiveDetailsList arcDetails = new ArchiveDetailsList();
		arcDetails.add(arcDetail);
		
		TaskInstanceDetailsList elements = new TaskInstanceDetailsList();
		elements.add(taskInst);
		
		caCons.store(false, objType, domain, arcDetails, elements);
	}
	
	protected Object[] createAndSubmitPlanningRequestWithTask() throws MALException, MALInteractionException {
		
		String prDefName = "id1";
		TaskDefinitionDetails taskDef = createTaskDef("id2", prDefName);
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = createPrDef(prDefName);
		Long prDefId = submitPrDef(prDef);
		
		TaskInstanceDetails taskInst = createTaskInst(taskDef);
		Long taskInstId = generateId();
		storeTaskInst(taskDefId, taskInstId, taskInst);
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		PlanningRequestInstanceDetails prInst = createPrInst(prDef, taskInsts);
		Long prInstId = generateId();
		storePrInst(prDefId, prInstId, prInst);
		
		LongList taskDefIds = new LongList();
		taskDefIds.add(taskDefId);
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(taskInstId);
		
		prCons.submitPlanningRequest(prDefId, prInstId, prInst, taskDefIds, taskInstIds);
		
		return new Object[] { prDefId, prInstId, prInst, taskDefIds, taskInstIds };
	}

	protected void updatePlanningRequestWithTask(Object[] details) throws MALException, MALInteractionException {
		
		PlanningRequestDefinitionDetails prDef = (PlanningRequestDefinitionDetails)details[1];
		
		TaskDefinitionDetails taskDef = createTaskDef("id2", prDef.getName().getValue());
		Long taskDefId = submitTaskDef(taskDef);
		
		TaskInstanceDetails taskInst = createTaskInst(taskDef);
		Long taskInstId = generateId();
		storeTaskInst(taskDefId, taskInstId, taskInst);
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		PlanningRequestInstanceDetails prInst = (PlanningRequestInstanceDetails)details[3];
		prInst.setDescription("new updated desc");
		prInst.setTasks(taskInsts);
		
		Long prDefId = (Long)details[0];
		Long prInstId = (Long)details[2];
		
		LongList taskDefIds = new LongList();
		taskDefIds.add(taskDefId);
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(taskInstId);
		
		prCons.updatePlanningRequest(prDefId, prInstId, prInst, taskDefIds, taskInstIds);
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
	
	protected Map.Entry<LongList, PlanningRequestDefinitionDetailsList> addPrDef() throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier("new pr def"));
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		LongList prDefIds = prCons.addDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs);
		return new AbstractMap.SimpleEntry<LongList, PlanningRequestDefinitionDetailsList>(prDefIds, prDefs);
	}
	
	protected TaskMonitor registerTaskMonitor(String subId) throws MALException, MALInteractionException {
		Identifier id = new Identifier(subId);
		EntityRequestList entityList = new EntityRequestList();
		EntityKeyList keys = new EntityKeyList();
		keys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		entityList.add(new EntityRequest(null, true, true, true, false, keys));
		Subscription sub = new Subscription(id, entityList);
		TaskMonitor taskMon = new TaskMonitor();
		prCons.monitorTasksRegister(sub, taskMon);
		return taskMon;
	}
	
	protected void deRegisterTaskMonitor(String subId) throws MALException, MALInteractionException {
		IdentifierList subIdList = new IdentifierList();
		subIdList.add(new Identifier(subId));
		prCons.monitorTasksDeregister(subIdList);
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
	protected Object[] addTaskDef() throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefList = new TaskDefinitionDetailsList();
		TaskDefinitionDetails taskDef = createTaskDef("new task def", "new pr def");
		taskDefList.add(taskDef);
		LongList taskDefIdList = prCons.addDefinition(DefinitionType.TASK_DEF, taskDefList);
		return new Object[] { taskDefIdList, taskDefList };
	}

	protected void verifyPrStat(Long id) throws MALException, MALInteractionException {
		LongList prIds = new LongList();
		prIds.add(id);
		
		PlanningRequestStatusDetailsList prStats = prCons.getPlanningRequestStatus(prIds);
		
		assertNotNull(prStats);
		assertEquals(1, prStats.size());
		assertNotNull(prStats.get(0));
	}
}
