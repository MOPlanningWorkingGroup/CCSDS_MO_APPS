package esa.mo.inttest.pr.provider;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorTasksPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;

/**
 * Planning request provider for testing. Implemented as little as necessary.
 */
public class PlanningRequestProvider extends PlanningRequestInheritanceSkeleton implements MALPublishInteractionListener {

	private static final Logger LOG = Logger.getLogger(PlanningRequestProvider.class.getName());
	
	private TaskDefStore taskDefs = new TaskDefStore();
	private PrDefStore prDefs = new PrDefStore();
	private PrInstStore prInsts = new PrInstStore();
	private MonitorTasksPublisher taskPub = null;
	private MonitorPlanningRequestsPublisher prPub = null;
	
	public PlanningRequestProvider() {
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void publishRegisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
		LOG.log(Level.FINE, "publisher registration ack received");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void publishRegisterErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties)
			throws MALException {
		LOG.log(Level.FINE, "publisher registration error received");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void publishErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {
		LOG.log(Level.FINE, "publish error received");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void publishDeregisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
		LOG.log(Level.FINE, "publisher de-registration ack received");
	}

	public void setTaskPub(MonitorTasksPublisher taskPub) {
		this.taskPub = taskPub;
		LOG.log(Level.INFO, "tasks publisher {0}", (this.taskPub != null ? "set" : "unset"));
	}
	
	public void setPrPub(MonitorPlanningRequestsPublisher prPub) {
		this.prPub = prPub;
		LOG.log(Level.INFO, "pr publisher {0}", (this.prPub != null ? "set" : "unset"));
	}
	
	private void publishTask(UpdateHeaderList updHdrList, ObjectIdList objIdList, TaskStatusDetailsList taskStatsList)
			throws MALException, MALInteractionException {
		if (taskPub != null) {
			taskPub.publish(updHdrList, objIdList, taskStatsList);
		} else {
			LOG.log(Level.INFO, "no task publisher set");
		}
	}

	private void publishPr(UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats)
			throws MALException, MALInteractionException {
		if (prPub != null) {
			prPub.publish(updHdrs, objIds, prStats);
		} else {
			LOG.log(Level.INFO, "no pr publisher set");
		}
	}
	
	private void enter(String msg) {
		LOG.entering(getClass().getName(), msg);
	}
	
	private void leave(String msg) {
		LOG.exiting(getClass().getName(), msg);
	}
	
	protected UpdateHeader createPrUpdateHeader(UpdateType updType) {
		UpdateHeader updHdr = new UpdateHeader();
		updHdr.setTimestamp(new Time(System.currentTimeMillis())); // mandatory
		updHdr.setSourceURI(new URI("uri")); // mandatory
		updHdr.setUpdateType(updType); // mandatory
		updHdr.setKey(new EntityKey(new Identifier("*"), 0L, 0L, 0L)); // mandatory
		return updHdr;
	}
	
	protected ObjectId createPrObjectId(IdentifierList domain, Long prInstId) {
		ObjectId objId = new ObjectId();
		objId.setType(new ObjectType(PlanningRequestStatusDetails.AREA_SHORT_FORM,
				PlanningRequestStatusDetails.SERVICE_SHORT_FORM, PlanningRequestStatusDetails.AREA_VERSION,
				new UShort(1))); // mandatory
		objId.setKey(new ObjectKey(domain, (prInstId != null) ? prInstId : 1L)); // mandatory TODO if id not given
		return objId;
	}
	
	protected UpdateHeader createTaskUpdateHeader(UpdateType updType) {
		UpdateHeader updHdr2 = new UpdateHeader();
		updHdr2.setTimestamp(new Time(System.currentTimeMillis())); // all 4 mandatory
		updHdr2.setSourceURI(new URI("prProv"));
		updHdr2.setUpdateType(updType);
		updHdr2.setKey(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		return updHdr2;
	}
	
	protected ObjectId createTaskObjectId(IdentifierList domain, Long taskInstId) {
		ObjectId objId2 = new ObjectId();
		objId2.setType(new ObjectType(TaskStatusDetails.AREA_SHORT_FORM,
				TaskStatusDetails.SERVICE_SHORT_FORM, TaskStatusDetails.AREA_VERSION,
				new UShort(1))); // both mandatory
		objId2.setKey(new ObjectKey(domain, taskInstId));
		return objId2;
	}
	
	public void submitPlanningRequest(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst,
			LongList taskDefIds, LongList taskInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		enter("submitPlanningRequest");
		
		if (prDefId == null) {
			throw new MALException("pr definition id not given");
		}
		if (prInst == null) {
			throw new MALException("pr instance not given");
		}
		LOG.log(Level.FINE, "received prDefId={0}, prInstId={1}, prInst={2}",
				new Object[] { prDefId, prInstId, Dumper.prInst(prInst) });
		if (null != prInstId) {
			Object[] old = prInsts.findPr(prInstId);
			if (null != old) {
				throw new MALException("pr instance id already exists: " + prInstId);
			}
		} // oh, dear
		
		PlanningRequestStatusDetails prStat = new PlanningRequestStatusDetails();
		prStat.setPrInstName(prInst.getName()); // mandatory
		prStat.setLastModified(new StatusRecord(new Time(System.currentTimeMillis()), "created"));
		if (null != prInst.getTasks()) {
			prStat.setTaskStatuses(new TaskStatusDetailsList()); // init list
		}
		
		for (int i = 0; (null != prInst.getTasks()) && (i < prInst.getTasks().size()); ++i) {
			TaskInstanceDetails taskInst = prInst.getTasks().get(i);
			if (taskInst == null) {
				throw new MALException("task instance[" + i + "] is null");
			}
			TaskStatusDetails taskStat = new TaskStatusDetails();
			taskStat.setTaskInstName(taskInst.getName()); // mandatory
			taskStat.setLastModified(new StatusRecord(new Time(System.currentTimeMillis()), "created"));
			// just add task status'es to pr status - they will be stored all together at once
			prStat.getTaskStatuses().add(taskStat);
		}
		// statuses created, now store and publish
		prInsts.addPr(prDefId, prInstId, prInst, prStat);

		UpdateHeaderList updHdrs = new UpdateHeaderList();
		updHdrs.add(createPrUpdateHeader(UpdateType.CREATION));

		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));

		ObjectIdList objIds = new ObjectIdList();
		objIds.add(createPrObjectId(domain, prInstId));

		PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
		prStats.add(prStat);

		LOG.log(Level.INFO, "publishing pr status creation");
		publishPr(updHdrs, objIds, prStats);

		if (prStat.getTaskStatuses() != null && !prStat.getTaskStatuses().isEmpty()) {
			for (int i = 0; i < prStat.getTaskStatuses().size(); ++i) {
				TaskStatusDetails taskStat = prStat.getTaskStatuses().get(i);

				UpdateHeaderList updHdrs2 = new UpdateHeaderList();
				updHdrs2.add(createTaskUpdateHeader(UpdateType.CREATION));

				ObjectIdList objIds2 = new ObjectIdList();
				objIds2.add(createTaskObjectId(domain, 1L)); // TODO where do i get task inst id?

				TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
				taskStats.add(taskStat);

				LOG.log(Level.INFO, "publishing task status creation");
				publishTask(updHdrs2, objIds2, taskStats);
			}
		}
		LOG.log(Level.FINE, "returning nothing");
		leave("submitPlanningRequest");
	}

	public void updatePlanningRequest(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst,
			LongList taskDefIds, LongList taskInstIds, MALInteraction interaction) throws MALException, MALInteractionException {
		enter("updatePlanningRequest");
		
		if (prInstId == null) {
			throw new MALException("pr instance id not given");
		}
		if (prInst == null) {
			throw new MALException("pr instance not given");
		}
		LOG.log(Level.FINE, "received prInstId={0}, prInst={1}", new Object[] { prInstId, Dumper.prInst(prInst) });
		Object[] old = prInsts.findPr(prInstId);
		if (null == old) {
			throw new MALException("no pr instance with id: " + prInstId);
		}
//		PlanningRequestInstanceDetails prInstOld = (PlanningRequestInstanceDetails)old[0];
		PlanningRequestStatusDetails prStatOld = (PlanningRequestStatusDetails)old[1];
		
		// straightforward replace - delete old items, add new items
		PlanningRequestStatusDetails prStatNew = new PlanningRequestStatusDetails();
		prStatNew.setPrInstName(prInst.getName()); // mandatory
		prStatNew.setLastModified(new StatusRecord(new Time(System.currentTimeMillis()), "created"));
		if (null != prInst.getTasks()) {
			prStatNew.setTaskStatuses(new TaskStatusDetailsList()); // init list
		}
		
		for (int i = 0; (null != prInst.getTasks()) && (i < prInst.getTasks().size()); ++i) {
			TaskInstanceDetails taskInst = prInst.getTasks().get(i);
			if (taskInst == null) {
				throw new MALException("task instance[" + i + "] is null");
			}
			TaskStatusDetails taskStat = new TaskStatusDetails();
			taskStat.setTaskInstName(taskInst.getName()); // mandatory
			taskStat.setLastModified(new StatusRecord(new Time(System.currentTimeMillis()), "created"));
			// just add task status'es to pr status - they will be stored all together at once
			prStatNew.getTaskStatuses().add(taskStat);
		}
		
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		
		for (int i = 0; (null != prStatOld.getTaskStatuses()) && (i < prStatOld.getTaskStatuses().size()); ++i) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			updHdrs.add(createTaskUpdateHeader(UpdateType.DELETION));
			
			ObjectIdList objIds = new ObjectIdList();
			objIds.add(createTaskObjectId(domain, 1L)); // TODO where do i get task inst id?
			
			TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
			prStatOld.getTaskStatuses().get(i).setLastModified(new StatusRecord(new Time(System.currentTimeMillis()), "deleted"));
			taskStats.add(prStatOld.getTaskStatuses().get(i));
			
			LOG.log(Level.INFO, "publishing task status deletion");
			publishTask(updHdrs, objIds, taskStats);
		}
		
		UpdateHeaderList updHdrs = new UpdateHeaderList();
		updHdrs.add(createPrUpdateHeader(UpdateType.DELETION));

		ObjectIdList objIds = new ObjectIdList();
		objIds.add(createPrObjectId(domain, prInstId));

		PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
		prStatOld.setLastModified(new StatusRecord(new Time(System.currentTimeMillis()), "deleted"));
		prStats.add(prStatOld);

		LOG.log(Level.INFO, "publishing pr status deletion");
		publishPr(updHdrs, objIds, prStats);
		
		// store new pr instance (including tasks) + new pr status (including task statuses)
		prInsts.updatePr(prInstId, prInst, prStatNew);
		
		for (int i = 0; (null != prStatNew.getTaskStatuses()) && (i < prStatNew.getTaskStatuses().size()); ++i) {
			UpdateHeaderList updHdrs2 = new UpdateHeaderList();
			updHdrs2.add(createTaskUpdateHeader(UpdateType.CREATION));
			
			ObjectIdList objIds2 = new ObjectIdList();
			objIds2.add(createTaskObjectId(domain, 1L)); // TODO where do i get task instance id?
			
			TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
			taskStats.add(prStatNew.getTaskStatuses().get(i));
			
			LOG.log(Level.INFO, "publishing task status creation");
			publishTask(updHdrs2, objIds2, taskStats);
		}
		
		UpdateHeaderList updHdrs2 = new UpdateHeaderList();
		updHdrs2.add(createPrUpdateHeader(UpdateType.CREATION));

		ObjectIdList objIds2 = new ObjectIdList();
		objIds2.add(createPrObjectId(domain, prInstId));

		PlanningRequestStatusDetailsList prStats2 = new PlanningRequestStatusDetailsList();
		prStats2.add(prStatNew);

		LOG.log(Level.INFO, "publishing pr status creation");
		publishPr(updHdrs2, objIds2, prStats2);

		leave("updatePlanningRequest");
	}

	public void removePlanningRequest(Long prInstId, MALInteraction interaction)
			throws MALException, MALInteractionException {
		enter("removePlanningRequest");
		
		if (prInstId == null) {
			throw new MALException("no pr instance id given");
		}
		LOG.log(Level.FINE, "received prInstId={0}", prInstId);
		Object[] old = prInsts.findPr(prInstId);
		if (null == old) {
			throw new MALException("no pr instance with id: " + prInstId);
		}
//		PlanningRequestInstanceDetails prInstOld = (PlanningRequestInstanceDetails)old[0];
		PlanningRequestStatusDetails prStatOld = (PlanningRequestStatusDetails)old[1];
		
		prInsts.removePr(prInstId);
		
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		
		for (int i = 0; (null != prStatOld.getTaskStatuses()) && (i < prStatOld.getTaskStatuses().size()); ++i) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			updHdrs.add(createTaskUpdateHeader(UpdateType.DELETION));
			
			ObjectIdList objIds = new ObjectIdList();
			objIds.add(createTaskObjectId(domain, 1L)); // TODO where do i get task inst id?
			
			TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
			prStatOld.getTaskStatuses().get(i).setLastModified(new StatusRecord(new Time(System.currentTimeMillis()), "deleted"));
			taskStats.add(prStatOld.getTaskStatuses().get(i));
			
			LOG.log(Level.INFO, "publishing task status deletion");
			publishTask(updHdrs, objIds, taskStats);
		}
		
		UpdateHeaderList updHdrs = new UpdateHeaderList();
		updHdrs.add(createPrUpdateHeader(UpdateType.DELETION));

		ObjectIdList objIds = new ObjectIdList();
		objIds.add(createPrObjectId(domain, prInstId));

		PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
		prStatOld.setLastModified(new StatusRecord(new Time(System.currentTimeMillis()), "deleted"));
		prStats.add(prStatOld);

		LOG.log(Level.INFO, "publishing pr status deletion");
		publishPr(updHdrs, objIds, prStats);
		
		leave("removePlanningRequest");
	}

	public PlanningRequestStatusDetailsList getPlanningRequestStatus(LongList prIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		enter("getPlanningRequestStatus");
		
		if (prIds == null) {
			throw new MALException("no pr instance id list given");
		}
		if (prIds.isEmpty()) {
			throw new MALException("pr instance id list is empty");
		}
		PlanningRequestStatusDetailsList stats = new PlanningRequestStatusDetailsList(); 
		for (int i = 0; i < prIds.size(); ++i) {
			Long id = prIds.get(i);
			if (id == null) {
				throw new MALException("pr instance id[" + i + "] is null");
			}
			Object[] old = prInsts.findPr(id);
			if (null != old) {
				PlanningRequestStatusDetails prStat = (PlanningRequestStatusDetails)old[1];
				if (null != prStat) {
					stats.add(prStat);
				}
			}
		}
		leave("getPlanningRequestStatus");
		return stats;
	}

	public LongList listDefinition(IdentifierList prNames, MALInteraction interaction) throws MALInteractionException,
			MALException {
		
		if (prNames == null) {
			throw new MALException("no id list given");
		}
		if (prNames.isEmpty()) {
			throw new MALException("no ids in list");
		}
		LongList ids = new LongList();
		for (int i = 0; i < prNames.size(); ++i) {
			Identifier id = prNames.get(i);
			if (id == null) {
				throw new MALException("identifier[" + i + "] is null");
			}
			ids.addAll(prDefs.list(id));
		}
		return ids;
	}

	public LongList addDefinition(PlanningRequestDefinitionDetailsList prDefs, MALInteraction interaction)
			throws MALInteractionException, MALException {
		enter("addDefinition");
		if (prDefs == null) {
			throw new MALException("no pr definition list given");
		}
		LOG.log(Level.FINE, "received prDefs={0}", Dumper.prDefs(prDefs));
		if (prDefs.isEmpty()) {
			throw new MALException("no pr definitions in list");
		}
		LongList ids = new LongList();
		for (int i = 0; i < prDefs.size(); ++i) {
			PlanningRequestDefinitionDetails prDef = prDefs.get(i);
			if (prDef == null) {
				throw new MALException("pr definition[" + i + "] is null");
			}
			ids.add(this.prDefs.add(prDef));
		}
		LOG.log(Level.FINE, "returning prDefIds={0}", ids);
		leave("addDefinition");
		return ids;
	}

	public void updateDefinition(LongList prDefIds, PlanningRequestDefinitionDetailsList prDefs,
			MALInteraction interaction) throws MALInteractionException, MALException {
		
		if (prDefIds == null) {
			throw new MALException("no pr def ids list given");
		}
		if (prDefs == null) {
			throw new MALException("no pr defs list given");
		}
		if (prDefIds.isEmpty()) {
			throw new MALException("no pr def ids in list");
		}
		if (prDefIds.size() != prDefs.size()) {
			throw new MALException("pr def ids count does not match pr defs count");
		}
		for (int i = 0; i < prDefIds.size(); ++i) {
			Long id = prDefIds.get(i);
			if (id == null) {
				throw new MALException("pr def id[" + i + "] is null");
			}
			PlanningRequestDefinitionDetails prDef = prDefs.get(i);
			if (prDef == null) {
				throw new MALException("pr def[" + i + "] is null");
			}
			this.prDefs.update(id, prDef);
		}
	}

	public void removeDefinition(LongList prDefIds, MALInteraction interaction) throws MALInteractionException,
			MALException {
		
		if (prDefIds == null) {
			throw new MALException("no pr def id list given");
		}
		if (prDefIds.isEmpty()) {
			throw new MALException("no pr def ids in list");
		}
		for (int i = 0; i < prDefIds.size(); ++i) {
			Long id = prDefIds.get(i);
			if (id == null) {
				throw new MALException("pr def id[" + i + "] is null");
			}
			prDefs.remove(id);
		}
	}

	public TaskStatusDetailsList getTaskStatus(LongList taskIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		enter("getTaskStatus");
		
		if (taskIds == null) {
			throw new MALException("no task id list given");
		}
		if (taskIds.isEmpty()) {
			throw new MALException("task id list is empty");
		}
		TaskStatusDetailsList stats = new TaskStatusDetailsList();
		for (int i = 0; i < taskIds.size(); ++i) {
			Long id = taskIds.get(i);
			if (id == null) {
				throw new MALException("task id[" + i + "] is null");
			}
			TaskStatusDetails taskStat = prInsts.findTask(id);
			if (null != taskStat) {
				stats.add(taskStat);
			}
		}
		leave("getTaskStatus");
		return stats;
	}

	public void setTaskStatus(LongList taskIds, TaskStatusDetailsList taskStatus, MALInteraction interaction)
			throws MALInteractionException, MALException {
		enter("setTaskStatus");
		
		if (taskIds == null) {
			throw new MALException("no task id list given");
		}
		if (taskIds.isEmpty()) {
			throw new MALException("task id list is empty");
		}
		if (taskStatus == null) {
			throw new MALException("no task status list givene");
		}
		if (taskIds.size() != taskStatus.size()) {
			throw new MALException("task id count differs from task status count");
		}
		for (int i = 0; i < taskIds.size(); ++i) {
			Long id = taskIds.get(i);
			if (id == null) {
				throw new MALException("task id[" + i + "] is null");
			}
			TaskStatusDetails stat = taskStatus.get(i);
			if (stat == null) {
				throw new MALException("task status[" + i + "] is null");
			}
			TaskStatusDetails taskStat = prInsts.findTask(id);
			if (null == taskStat) {
				throw new MALException("no such task instance with id: " + id);
			}
			prInsts.setTaskStatus(id, stat);
		}
		leave("setTaskStatus");
	}

	public LongList listTaskDefinition(IdentifierList taskNames, MALInteraction interaction)
			throws MALInteractionException, MALException {
		
		if (taskNames == null) {
			throw new MALException("no task names list given");
		}
		if (taskNames.isEmpty()) {
			throw new MALException("no task names in list");
		}
		LongList ids = new LongList();
		for (int i = 0; i < taskNames.size(); ++i) {
			Identifier taskName = taskNames.get(i);
			if (taskName == null) {
				throw new MALException("identifier[" + i + "] is null");
			}
			ids.addAll(taskDefs.list(taskName));
		}
		return ids;
	}

	public LongList addTaskDefinition(TaskDefinitionDetailsList taskDefs, MALInteraction interaction)
			throws MALInteractionException, MALException {
		enter("addTaskDefinition");
		if (taskDefs == null) {
			throw new MALException("no task def list given");
		}
		LOG.log(Level.FINE, "received taskDefs={0}", Dumper.taskDefs(taskDefs));
		if (taskDefs.isEmpty()) {
			throw new MALException("no task defs in list");
		}
		LongList ids = new LongList();
		for (int i = 0; i < taskDefs.size(); ++i) {
			TaskDefinitionDetails def = taskDefs.get(i);
			if (def == null) {
				throw new MALException("task def["+i+"] is null");
			}
			ids.add(this.taskDefs.add(def));
		}
		LOG.log(Level.FINE, "returning taskDefIds={0}", ids);
		leave("addTaskDefinition");
		return ids;
	}

	public void updateTaskDefinition(LongList taskDefIds, TaskDefinitionDetailsList taskDefs,
			MALInteraction interaction) throws MALInteractionException, MALException {
		
		if (taskDefIds == null) {
			throw new MALException("no task def id list given");
		}
		if (taskDefIds.isEmpty()) {
			throw new MALException("no task def ids in list");
		}
		if (taskDefs == null) {
			throw new MALException("no task defs list given");
		}
		if (taskDefIds.size() != taskDefs.size()) {
			throw new MALException("def ids count does not match defs count");
		}
		for (int i = 0; i < taskDefIds.size(); ++i) {
			Long id = taskDefIds.get(i);
			if (id == null) {
				throw new MALException("task def id[" + i + "] is null");
			}
			TaskDefinitionDetails def = taskDefs.get(i);
			if (def == null) {
				throw new MALException("task def[" + i + "] is null");
			}
			this.taskDefs.update(id, def);
		}
	}

	public void removeTaskDefinition(LongList taskDefIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		
		if (taskDefIds == null) {
			throw new MALException("no task def id list given");
		}
		if (taskDefIds.isEmpty()) {
			throw new MALException("no task def ids in list");
		}
		for (int i = 0; i < taskDefIds.size(); ++i) {
			Long id = taskDefIds.get(i);
			if (id == null) {
				throw new MALException("task def id[" + i + "] is null");
			}
			this.taskDefs.remove(id);
		}
	}

}
