package esa.mo.inttest.pr.provider;

import java.util.HashMap;
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

/**
 * Planning request provider for testing. Implemented as little as necessary.
 */
public class PlanningRequestProvider extends PlanningRequestInheritanceSkeleton implements MALPublishInteractionListener {

	private static final Logger LOG = Logger.getLogger(PlanningRequestProvider.class.getName());
	
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
		LOG.log(Level.INFO, "tasks publisher set to {0}", this.taskPub);
	}
	
	public void setPrPub(MonitorPlanningRequestsPublisher prPub) {
		this.prPub = prPub;
		LOG.log(Level.INFO, "pr publisher set to {0}", this.prPub);
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
	
	protected UpdateHeader createPrUpdateHeader() {
		UpdateHeader updHdr = new UpdateHeader();
		updHdr.setTimestamp(new Time(System.currentTimeMillis())); // mandatory
		updHdr.setSourceURI(new URI("uri")); // mandatory
		updHdr.setUpdateType(UpdateType.CREATION); // mandatory
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
	
	protected UpdateHeader createTaskUpdateHeader() {
		UpdateHeader updHdr2 = new UpdateHeader();
		updHdr2.setTimestamp(new Time(System.currentTimeMillis())); // all 4 mandatory
		updHdr2.setSourceURI(new URI("blah"));
		updHdr2.setUpdateType(UpdateType.CREATION);
		updHdr2.setKey(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		return updHdr2;
	}
	
	protected ObjectId createTaskObjectId(IdentifierList domain) {
		ObjectId objId2 = new ObjectId();
		objId2.setType(new ObjectType(TaskStatusDetails.AREA_SHORT_FORM,
				TaskStatusDetails.SERVICE_SHORT_FORM, TaskStatusDetails.AREA_VERSION,
				new UShort(1))); // both mandatory
		objId2.setKey(new ObjectKey(domain, 1L)); // TODO where do i get task instance id?
		return objId2;
	}
	
	public void submitPlanningRequest(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst,
			MALInteraction interaction) throws MALInteractionException, MALException {
		
		if (prDefId == null) {
			throw new MALException("pr definition id not given");
		}
		if (prInst == null) {
			throw new MALException("pr instance not given");
		}
		// TODO no instances stored, just publishing for testing
		PlanningRequestStatusDetails prStat = new PlanningRequestStatusDetails();
		prStat.setPrInstName(prInst.getName()); // mandatory

		UpdateHeaderList updHdrs = new UpdateHeaderList();
		updHdrs.add(createPrUpdateHeader());

		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));

		ObjectIdList objIds = new ObjectIdList();
		objIds.add(createPrObjectId(domain, prInstId));

		PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
		prStats.add(prStat);

		LOG.log(Level.INFO, "publishing pr status creation");
		publishPr(updHdrs, objIds, prStats);

		if (prInst.getTasks() != null && !prInst.getTasks().isEmpty()) {
			for (int i = 0; i < prInst.getTasks().size(); ++i) {
				TaskInstanceDetails taskInst = prInst.getTasks().get(i);
				if (taskInst == null) {
					throw new MALException("task instance[" + i + "] is null");
				}

				TaskStatusDetails taskStat = new TaskStatusDetails();
				taskStat.setTaskInstName(taskInst.getName()); // mandatory

				UpdateHeaderList updHdrs2 = new UpdateHeaderList();
				updHdrs2.add(createTaskUpdateHeader());

				ObjectIdList objIds2 = new ObjectIdList();
				objIds2.add(createTaskObjectId(domain));

				TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
				taskStats.add(taskStat);

				LOG.log(Level.INFO, "publishing task status creation");
				publishTask(updHdrs2, objIds2, taskStats);
			}
		}
	}

	public void updatePlanningRequest(Long prInstId, PlanningRequestInstanceDetails prDetails,
			MALInteraction interaction) throws MALException, MALInteractionException {
		
		if (prInstId == null) {
			throw new MALException("pr instance id not given");
		}
		if (prDetails == null) {
			throw new MALException("pr instance not given");
		}
		// TODO
	}

	public void removePlanningRequest(Long prInstId, MALInteraction interaction)
			throws MALException, MALInteractionException {
		
		if (prInstId == null) {
			throw new MALException("no pr instance id given");
		}
		// TODO
	}

	private PlanningRequestStatusDetails getPrStatus(Long id) {
		return null; // TODO
	}

	public PlanningRequestStatusDetailsList getPlanningRequestStatus(LongList prIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		
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
			stats.add(getPrStatus(id));
		}
		return stats;
	}

	private long lastPrId = 0L;
	private Map<Long, PlanningRequestDefinitionDetails> prDefs = new HashMap<Long, PlanningRequestDefinitionDetails>();

	private LongList getPrDefs(Identifier id) {
		LongList list = new LongList();
		list.addAll(prDefs.keySet()); // TODO
		return list;
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
			ids.addAll(getPrDefs(id));
		}
		return ids;
	}

	private Long addPrDef(PlanningRequestDefinitionDetails prDef) {
		Long id = new Long(++lastPrId);
		prDefs.put(id, prDef);
		return id;
	}

	public LongList addDefinition(PlanningRequestDefinitionDetailsList prDefs, MALInteraction interaction)
			throws MALInteractionException, MALException {
		
		if (prDefs == null) {
			throw new MALException("no pr definition list given");
		}
		if (prDefs.isEmpty()) {
			throw new MALException("no pr definitions in list");
		}
		LongList ids = new LongList();
		for (int i = 0; i < prDefs.size(); ++i) {
			PlanningRequestDefinitionDetails prDef = prDefs.get(i);
			if (prDef == null) {
				throw new MALException("pr definition[" + i + "] is null");
			}
			ids.add(addPrDef(prDef));
		}
		return ids;
	}

	private void updatePrDef(Long id, PlanningRequestDefinitionDetails prDef) {
		prDefs.put(id, prDef);
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
			updatePrDef(id, prDef);
		}
	}

	private void removePrDef(Long id) {
		prDefs.remove(id);
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
			removePrDef(id);
		}
	}

	private TaskStatusDetails getTaskStatus(Long id) {
		return null; // no such task TODO
	}

	public TaskStatusDetailsList getTaskStatus(LongList taskIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		
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
			stats.add(getTaskStatus(id));
		}
		return stats;
	}

	private void setTaskStatus(Long id, TaskStatusDetails stat) {
		// TODO
	}

	public void setTaskStatus(LongList taskIds, TaskStatusDetailsList taskStatus, MALInteraction interaction)
			throws MALInteractionException, MALException {
		
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
			setTaskStatus(id, stat);
		}
	}

	private long lastTaskId = 0L;
	private Map<Long, TaskDefinitionDetails> taskDefs = new HashMap<Long, TaskDefinitionDetails>();

	private LongList getTaskDefs(Identifier id) {
		LongList list = new LongList();
		list.addAll(taskDefs.keySet()); // TODO
		return list;
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
			ids.addAll(getTaskDefs(taskName));
		}
		return ids;
	}

	private Long addTaskDef(TaskDefinitionDetails def) {
		Long id = new Long(++lastTaskId);
		taskDefs.put(id, def);
		return id;
	}

	public LongList addTaskDefinition(TaskDefinitionDetailsList taskDefs, MALInteraction interaction)
			throws MALInteractionException, MALException {
		
		if (taskDefs == null) {
			throw new MALException("no task def list given");
		}
		if (taskDefs.isEmpty()) {
			throw new MALException("no task defs in list");
		}
		LongList ids = new LongList();
		for (int i = 0; i < taskDefs.size(); ++i) {
			TaskDefinitionDetails def = taskDefs.get(i);
			if (def == null) {
				throw new MALException("task def["+i+"] is null");
			}
			ids.add(addTaskDef(def));
		}
		return ids;
	}

	private void updateTaskDef(Long id, TaskDefinitionDetails def) {
		taskDefs.put(id, def);
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
			updateTaskDef(id, def);
		}
	}

	private void removeTaskDef(Long id) {
		taskDefs.remove(id);
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
			removeTaskDef(id);
		}
	}

}
