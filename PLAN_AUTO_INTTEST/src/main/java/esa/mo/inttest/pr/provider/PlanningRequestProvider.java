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
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;

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
	
	// this is for async register
	@SuppressWarnings("rawtypes")
	@Override
	public void publishRegisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
		LOG.log(Level.FINE, "publisher registration ack received");
	}
	
	// this is for async register
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
	
	// this is for async de-register
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
		objId.setKey(new ObjectKey(domain, prInstId)); // mandatory
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
	
	public PlanningRequestResponseInstanceDetailsList submitPlanningRequest(Long prDefId, Long prInstId,
			PlanningRequestInstanceDetails prInst, LongList taskDefIds, LongList taskInstIds,
			MALInteraction interaction) throws MALInteractionException, MALException {
		enter("submitPlanningRequest");
		LOG.log(Level.FINE, "received pr add: prDefId={0}, prInstId={1}, prInst={2}, taskDefIds={3}, taskInstIds={4}",
				new Object[] { prDefId, prInstId, Dumper.prInst(prInst), taskDefIds, taskInstIds });
		if (null == prDefId) {
			throw new MALException("pr definition id not given");
		}
		if (null == prInstId) {
			throw new MALException("pr instance id not given");
		}
		if (null == prInst) {
			throw new MALException("pr instance not given");
		}
		Object[] old = prInsts.findPr(prInstId);
		if (null != old) {
			throw new MALException("pr instance id already exists: " + prInstId);
		}
		int tasksCount = (null != prInst.getTasks() ? prInst.getTasks().size() : 0);
		int taskDefIdCount = (null != taskDefIds ? taskDefIds.size() : 0);
		if (tasksCount != taskDefIdCount) {
			throw new MALException("pr tasks count does not match task definition id count");
		}
		int taskInstIdCount = (null != taskInstIds ? taskInstIds.size() : 0);
		if (taskDefIdCount != taskInstIdCount) {
			throw new MALException("task definition id count does not match task instance id count");
		}
		// checks done, start setting up pr status
		PlanningRequestStatusDetails prStat = new PlanningRequestStatusDetails();
		prStat.setPrInstName(prInst.getName()); // mandatory
		if (null == prStat.getStatus()) {
			prStat.setStatus(new StatusRecordList());
		}
		prStat.getStatus().add(new StatusRecord(InstanceState.LAST_MODIFIED, new Time(System.currentTimeMillis()), "created"));
		// now, task statuses
		if (null != prInst.getTasks()) {
			prStat.setTaskStatuses(new TaskStatusDetailsList());
		}
		for (int i = 0; (null != prInst.getTasks()) && (i < prInst.getTasks().size()); ++i) {
			TaskInstanceDetails taskInst = prInst.getTasks().get(i);
			if (taskInst == null) {
				throw new MALException("task instance[" + i + "] is null");
			}
			Long defId = taskDefIds.get(i);
			if (null == defId) {
				throw new MALException("task definition id[" + i + "] is null");
			}
			Long instId = taskInstIds.get(i);
			if (null == instId) {
				throw new MALException("task instance id[" + i + "] is null");
			}
			TaskStatusDetails taskStat = new TaskStatusDetails();
			taskStat.setTaskInstName(taskInst.getName()); // mandatory
			if (null == taskStat.getStatus()) {
				taskStat.setStatus(new StatusRecordList());
			}
			taskStat.getStatus().add(new StatusRecord(InstanceState.LAST_MODIFIED, new Time(System.currentTimeMillis()), "created"));
			// just add task status'es to pr status - they will be stored all together at once
			prStat.getTaskStatuses().add(taskStat);
		}
		// statuses created, now store
		prInsts.addPr(prDefId, prInstId, prInst, taskDefIds, taskInstIds, prStat);
		// .. and publish
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
				Long instId = taskInstIds.get(i);
				
				UpdateHeaderList updHdrs2 = new UpdateHeaderList();
				updHdrs2.add(createTaskUpdateHeader(UpdateType.CREATION));

				ObjectIdList objIds2 = new ObjectIdList();
				objIds2.add(createTaskObjectId(domain, instId));

				TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
				taskStats.add(taskStat);

				LOG.log(Level.INFO, "publishing task status creation");
				publishTask(updHdrs2, objIds2, taskStats);
			}
		}
		PlanningRequestResponseInstanceDetails prr = new PlanningRequestResponseInstanceDetails(prInst.getName(), "wat",
				prInst.getArgumentValues(), prInst.getArgumentDefNames());
		PlanningRequestResponseInstanceDetailsList resp = new PlanningRequestResponseInstanceDetailsList();
		resp.add(prr);
		LOG.log(Level.FINE, "returning response={0}", resp);
		leave("submitPlanningRequest");
		return resp;
	}
	
	private void addOrReplaceStatus(StatusRecordList srl, InstanceState is, Time time, String comm) {
		boolean found = false;
		for (StatusRecord sr: srl) {
			if (sr.getState() == is) {
				found = true;
				sr.setDate(time);
				sr.setComment(comm);
				break;
			}
		}
		if (!found) {
			srl.add(new StatusRecord(is, time, comm));
		}
	}
	
	public void updatePlanningRequest(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst,
			LongList taskDefIds, LongList taskInstIds, MALInteraction interaction) throws MALException, MALInteractionException {
		enter("updatePlanningRequest");
		LOG.log(Level.FINE, "received pr update: prDefId={0}, prInstId={1}, prInst={2}, taskDefIds={3}, taskInstIds={4}",
				new Object[] { prDefId, prInstId, Dumper.prInst(prInst), taskDefIds, taskInstIds });
		if (null == prDefId) {
			throw new MALException("no pr def id given");
		}
		if (null == prInstId) {
			throw new MALException("no pr instance id given");
		}
		if (null == prInst) {
			throw new MALException("no pr instance given");
		}
		Object[] old = prInsts.findPr(prInstId);
		if (null == old) {
			throw new MALException("no pr instance with id: " + prInstId);
		}
		int taskDefIdCount = (null != taskDefIds ? taskDefIds.size() : 0);
		if (prInst.getTasks().size() != taskDefIdCount) {
			throw new MALException("pr tasks count does not match task def id count");
		}
		int taskInstIdCount = (null != taskInstIds ? taskInstIds.size() : 0);
		if (taskInstIdCount != taskDefIdCount) {
			throw new MALException("task def id count does not match task inst id count");
		}
//		PlanningRequestInstanceDetails prInstOld = (PlanningRequestInstanceDetails)old[0];
		PlanningRequestStatusDetails prStatOld = (PlanningRequestStatusDetails)old[1];
		
		// straightforward replace - delete old items, add new items
		PlanningRequestStatusDetails prStatNew = new PlanningRequestStatusDetails();
		prStatNew.setPrInstName(prInst.getName()); // mandatory
		if (null == prStatNew.getStatus()) {
			prStatNew.setStatus(new StatusRecordList());
		}
		prStatNew.getStatus().add(new StatusRecord(InstanceState.LAST_MODIFIED, new Time(System.currentTimeMillis()), "created"));
		if (null != prInst.getTasks()) {
			prStatNew.setTaskStatuses(new TaskStatusDetailsList()); // init list
		}
		
		for (int i = 0; (null != prInst.getTasks()) && (i < prInst.getTasks().size()); ++i) {
			TaskInstanceDetails taskInst = prInst.getTasks().get(i);
			if (null == taskInst) {
				throw new MALException("task instance[" + i + "] is null");
			}
			Long defId = taskDefIds.get(i);
			if (null == defId) {
				throw new MALException("task def id[" + i + "] is null");
			}
			Long instId = taskInstIds.get(i);
			if (null == instId) {
				throw new MALException("task inst id[" + i + "] is null");
			}
			TaskStatusDetails taskStat = new TaskStatusDetails();
			taskStat.setTaskInstName(taskInst.getName()); // mandatory
			if (null == taskStat.getStatus()) {
				taskStat.setStatus(new StatusRecordList());
			}
			taskStat.getStatus().add(new StatusRecord(InstanceState.LAST_MODIFIED, new Time(System.currentTimeMillis()), "created"));
			// just add task status'es to pr status - they will be stored all together at once
			prStatNew.getTaskStatuses().add(taskStat);
		}
		
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		// publish deletion of old stuff
		for (int i = 0; (null != prStatOld.getTaskStatuses()) && (i < prStatOld.getTaskStatuses().size()); ++i) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			updHdrs.add(createTaskUpdateHeader(UpdateType.DELETION));
			
			ObjectIdList objIds = new ObjectIdList();
			objIds.add(createTaskObjectId(domain, 1L)); // TODO where do i get task inst id?
			
			TaskStatusDetails taskStat = prStatOld.getTaskStatuses().get(i);
			addOrReplaceStatus(taskStat.getStatus(), InstanceState.LAST_MODIFIED, new Time(System.currentTimeMillis()), "deleted");
			
			TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
			taskStats.add(taskStat);
			
			LOG.log(Level.INFO, "publishing task status deletion");
			publishTask(updHdrs, objIds, taskStats);
		}
		
		UpdateHeaderList updHdrs = new UpdateHeaderList();
		updHdrs.add(createPrUpdateHeader(UpdateType.DELETION));

		ObjectIdList objIds = new ObjectIdList();
		objIds.add(createPrObjectId(domain, prInstId));

		addOrReplaceStatus(prStatOld.getStatus(), InstanceState.LAST_MODIFIED, new Time(System.currentTimeMillis()), "deleted");
		
		PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
		prStats.add(prStatOld);

		LOG.log(Level.INFO, "publishing pr status deletion");
		publishPr(updHdrs, objIds, prStats);
		
		// store new pr instance (including tasks) + new pr status (including task statuses)
		prInsts.updatePr(prInstId, prInst, prStatNew);
		// .. and publish new
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
		LOG.log(Level.FINE, "returning nothing");
		leave("updatePlanningRequest");
	}

	public void removePlanningRequest(Long prInstId, MALInteraction interaction)
			throws MALException, MALInteractionException {
		enter("removePlanningRequest");
		LOG.log(Level.FINE, "received pr remove: prInstId={0}", prInstId);
		if (prInstId == null) {
			throw new MALException("no pr instance id given");
		}
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
			
			TaskStatusDetails taskStat = prStatOld.getTaskStatuses().get(i);
			addOrReplaceStatus(taskStat.getStatus(), InstanceState.LAST_MODIFIED, new Time(System.currentTimeMillis()), "deleted");
			
			TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
			taskStats.add(taskStat);
			
			LOG.log(Level.INFO, "publishing task status deletion");
			publishTask(updHdrs, objIds, taskStats);
		}
		
		UpdateHeaderList updHdrs = new UpdateHeaderList();
		updHdrs.add(createPrUpdateHeader(UpdateType.DELETION));

		ObjectIdList objIds = new ObjectIdList();
		objIds.add(createPrObjectId(domain, prInstId));
		
		addOrReplaceStatus(prStatOld.getStatus(), InstanceState.LAST_MODIFIED, new Time(System.currentTimeMillis()), "deleted");
		
		PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
		prStats.add(prStatOld);

		LOG.log(Level.INFO, "publishing pr status deletion");
		publishPr(updHdrs, objIds, prStats);
		LOG.log(Level.FINE, "returning nothing");
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
			PlanningRequestStatusDetails prStat = null;
			Object[] old = prInsts.findPr(id);
			if (null != old) {
				prStat = (PlanningRequestStatusDetails)old[1];
			}
			stats.add(prStat);
		}
		leave("getPlanningRequestStatus");
		return stats;
	}
	
	public LongList listDefinition(DefinitionType defType, IdentifierList names, MALInteraction interaction)
			throws MALInteractionException, MALException {
		enter("listDefinition");
		LOG.log(Level.FINE, "received def names to list: defType={0}, names={1}", new Object[] { defType, Dumper.names(names) });
		if (null == defType) {
			throw new MALException("no definition type given");
		}
		if (names == null) {
			throw new MALException("no name list given");
		}
		if (names.isEmpty()) {
			throw new MALException("no names in list");
		}
		LongList ids = new LongList();
		if (DefinitionType.TASK_DEF == defType) {
			ids.addAll(taskDefs.listAll(names));
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			ids.addAll(prDefs.listAll(names));
		} else {
			throw new MALException("not supported definition type: " + defType);
		}
		LOG.log(Level.FINE, "returning defIds={0}", ids);
		leave("listDefinition");
		return ids;
	}
	
	@SuppressWarnings("rawtypes")
	public LongList addDefinition(DefinitionType defType, BaseDefinitionList defs, MALInteraction interaction)
			throws MALInteractionException, MALException {
		enter("addDefinition");
		LOG.log(Level.FINE, "received defs to add: defType={0}, defs={1}", new Object[] { defType, Dumper.baseDefs(defs) });
		if (null == defType) {
			throw new MALException("no definition type given");
		}
		if (null == defs) {
			throw new MALException("no definition list given");
		}
		if (defs.isEmpty()) {
			throw new MALException("no definitions in list");
		}
		LongList ids = new LongList();
		if (DefinitionType.TASK_DEF == defType) {
			TaskDefinitionDetailsList defs2 = (TaskDefinitionDetailsList)defs;
			ids.addAll(taskDefs.addAll(defs2));
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			PlanningRequestDefinitionDetailsList defs2 = (PlanningRequestDefinitionDetailsList)defs;
			ids.addAll(prDefs.addAll(defs2));
		} else {
			throw new MALException("not supported definition type: " + defType);
		}
		LOG.log(Level.FINE, "returning defIds={0}", ids);
		leave("addDefinition");
		return ids;
	}
	
	@SuppressWarnings("rawtypes")
	public void updateDefinition(DefinitionType defType, LongList defIds, BaseDefinitionList defs,
			MALInteraction interaction) throws MALInteractionException, MALException {
		enter("updateDefinition");
		LOG.log(Level.FINE, "received defs to update: defType={0}, defIds={1}, defs={2}",
				new Object[] { defType, defIds, Dumper.baseDefs(defs) });
		if (null == defType) {
			throw new MALException("no definition type given");
		}
		if (null == defIds) {
			throw new MALException("no definition ids list given");
		}
		if (null == defs) {
			throw new MALException("no definitions list given");
		}
		if (defIds.isEmpty()) {
			throw new MALException("no definition ids in list");
		}
		if (defIds.size() != defs.size()) {
			throw new MALException("definition ids count does not match definitions count");
		}
		if (DefinitionType.TASK_DEF == defType) {
			TaskDefinitionDetailsList defs2 = (TaskDefinitionDetailsList)defs;
			taskDefs.updateAll(defIds, defs2);
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			PlanningRequestDefinitionDetailsList defs2 = (PlanningRequestDefinitionDetailsList)defs;
			prDefs.updateAll(defIds, defs2);
		} else {
			throw new MALException("not supported definition type: " + defType);
		}
		LOG.log(Level.FINE, "returning nothing");
		leave("updateDefinition");
	}

	public void removeDefinition(DefinitionType defType, LongList defIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		enter("removeDefinition");
		LOG.log(Level.FINE, "received defs to remove: defType={0}, defIds={1}", new Object[] { defType, defIds });
		if (null == defType) {
			throw new MALException("no def type given");
		}
		if (null == defIds) {
			throw new MALException("no def id list given");
		}
		if (defIds.isEmpty()) {
			throw new MALException("no def ids in list");
		}
		if (DefinitionType.TASK_DEF == defType) {
			taskDefs.removeAll(defIds);
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			prDefs.removeAll(defIds);
		} else {
			throw new MALException("not supported definition type: " + defType);
		}
		LOG.log(Level.FINE, "returning nothing");
		leave("removeDefinition");
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
			stats.add(taskStat);
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
}
