package esa.mo.inttest.pr.provider;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorTasksPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;

/**
 * Planning request provider for testing. Implemented as little as necessary.
 */
public class PlanningRequestProvider extends PlanningRequestInheritanceSkeleton {

	private static final Logger LOG = Logger.getLogger(PlanningRequestProvider.class.getName());
	
	private TaskDefStore taskDefs = new TaskDefStore();
	private PrDefStore prDefs = new PrDefStore();
	private RespDefStore respDefs = new RespDefStore();
	private PrInstStore prInsts = new PrInstStore();
	private MonitorTasksPublisher taskPub = null;
	private MonitorPlanningRequestsPublisher prPub = null;
	private IdentifierList domain = null;
	private URI uri = null;
	private Plugin plugin = null;
	
	/**
	 * Set provider domain to operate in.
	 * @param domain
	 */
	public void setDomain(IdentifierList domain) {
		this.domain = domain;
	}
	
	/**
	 * Set Uri to use.
	 * @param u
	 */
	public void setUri(URI u) {
		uri = u;
	}
	
	/**
	 * Return instances storage.
	 * @return
	 */
	public PrInstStore getInstStore() {
		return prInsts;
	}
	
	/**
	 * Sets PR processing plugin.
	 * @param p
	 */
	public void setPlugin(Plugin p) {
		plugin = p;
	}
	
	/**
	 * Set provider task publisher.
	 * @param taskPub
	 */
	public void setTaskPub(MonitorTasksPublisher taskPub) {
		this.taskPub = taskPub;
	}
	
	/**
	 * Set provider PR publisher.
	 * @param prPub
	 */
	public void setPrPub(MonitorPlanningRequestsPublisher prPub) {
		this.prPub = prPub;
	}
	
	/**
	 * Notify plugin of PR submission.
	 * @param prDefId
	 * @param prInstId
	 * @param prInst
	 * @param taskDefIds
	 * @param taskInstIds
	 * @param prStat
	 */
	protected void plugPrSubmitted(PlanningRequestInstanceDetails prInst, PlanningRequestStatusDetails prStat) {
		if (null != plugin) {
			plugin.onPrSubmit(prInst, prStat);
		}
	}
	
	/**
	 * Notify plugin of PR update.
	 * @param prDefId
	 * @param prInstId
	 * @param prInst
	 * @param taskDefIds
	 * @param taskInstIds
	 * @param prStat
	 */
	protected void plugPrUpdated(PlanningRequestInstanceDetails prInst, PlanningRequestStatusDetails prStat) {
		if (null != plugin) {
			plugin.onPrUpdate(prInst, prStat);
		}
	}
	
	/**
	 * Notify plugin of PR removal.
	 * @param prInstId
	 */
	protected void plugPrRemoved(Long prInstId) {
		if (null != plugin) {
			plugin.onPrRemove(prInstId);
		}
	}
	
	/**
	 * Publish Task event.
	 * @param updType
	 * @param taskInstId
	 * @param taskStat
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void publishTask(UpdateType updType, TaskStatusDetails taskStat)
			throws MALException, MALInteractionException {
		if (taskPub != null) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			updHdrs.add(Util.createUpdateHeader(updType, uri));
			
			ObjectIdList objIds = new ObjectIdList();
			objIds.add(createTaskObjectId(taskStat.getTaskInstId()));
			
			TaskStatusDetailsList taskStats = new TaskStatusDetailsList();
			taskStats.add(taskStat);
			try {
				taskPub.publish(updHdrs, objIds, taskStats);
			} catch (IllegalArgumentException e) {
				LOG.log(Level.INFO, "task publish error: illegal argument: {0}", e);
				throw e;
			} catch (MALException e) {
				LOG.log(Level.INFO, "task public error: mal: {0}", e);
				throw e;
			} catch (MALInteractionException e) {
				LOG.log(Level.INFO, "task publish error: mal interaction: {0}", e);
				throw e;
			}
		} else {
			LOG.log(Level.INFO, "no task publisher set");
		}
	}
	
	/**
	 * Publish PR event.
	 * @param updType
	 * @param prInstId
	 * @param prStat
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void publishPr(UpdateType updType, PlanningRequestStatusDetails prStat)
			throws MALException, MALInteractionException {
		if (prPub != null) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			updHdrs.add(Util.createUpdateHeader(updType, uri));
			
			ObjectIdList objIds = new ObjectIdList();
			objIds.add(createPrObjectId(prStat.getPrInstId()));
			
			PlanningRequestStatusDetailsList prStats = new PlanningRequestStatusDetailsList();
			prStats.add(prStat);
			try {
				prPub.publish(updHdrs, objIds, prStats);
			} catch (IllegalArgumentException e) {
				LOG.log(Level.INFO, "pr publish error: illegal argument: {0}", e);
				throw e;
			} catch (MALException e) {
				LOG.log(Level.INFO, "pr public error: mal: {0}", e);
				throw e;
			} catch (MALInteractionException e) {
				LOG.log(Level.INFO, "pr publish error: mal interaction: {0}", e);
				throw e;
			}
		} else {
			LOG.log(Level.INFO, "no pr publisher set");
		}
	}
	
	protected ObjectId createPrObjectId(Long prInstId) {
		ObjectId objId = new ObjectId();
		objId.setType(Util.createObjType(new PlanningRequestStatusDetails())); // mandatory
		objId.setKey(new ObjectKey(domain, prInstId)); // mandatory
		return objId;
	}
	
	protected ObjectId createTaskObjectId(Long taskInstId) {
		ObjectId objId2 = new ObjectId();
		objId2.setType(Util.createObjType(new TaskStatusDetails())); // both mandatory
		objId2.setKey(new ObjectKey(domain, taskInstId));
		return objId2;
	}
	
	private PlanningRequestStatusDetails createPrStatus(PlanningRequestInstanceDetails prInst) {
		PlanningRequestStatusDetails prStat = new PlanningRequestStatusDetails();
		prStat.setPrInstId(prInst.getId());
		prStat.setStatus(Util.addOrUpdateStatus(prStat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "created"));
		return prStat;
	}
	
	private TaskStatusDetails createTaskStatus(TaskInstanceDetails taskInst) {
		TaskStatusDetails taskStat = new TaskStatusDetails();
		taskStat.setTaskInstId(taskInst.getId());
		taskStat.setStatus(Util.addOrUpdateStatus(taskStat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "created"));
		return taskStat;
	}
	
	protected PlanningRequestResponseInstanceDetails createResponse(PlanningRequestInstanceDetails prInst) {
		Identifier name = new Identifier("dummy");
		return new PlanningRequestResponseInstanceDetails(/*prInst.getName()*/name, Util.currentTime(),
				prInst.getArgumentValues(), prInst.getArgumentDefNames());
	}
	
	public PlanningRequestResponseInstanceDetailsList submitPlanningRequest(
			PlanningRequestInstanceDetails prInst, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{5}.submitPlanningRequest(prInst)\n  prInst={2}",
				new Object[] { Dumper.prInst(prInst), Dumper.received(interaction) });
		Check.prInst(prInst);
		Check.prDefId(prInst.getPrDefId());
		Check.prDefExists(prInst.getPrDefId(), prDefs);
		Check.prInstId(prInst.getId());
		Check.prInstNoExist(prInst.getId(), prInsts);
		Check.listElements(prInst.getTasks(), prInst.getId());
		Check.prArgs(prInst, prDefs);
		Check.tasksArgs(prInst.getTasks(), taskDefs);
		// checks done, start setting up pr status
		PlanningRequestStatusDetails prStat = createPrStatus(prInst);
		// have tasks? will have task statuses..
		if (null != prInst.getTasks()) {
			prStat.setTaskStatuses(new TaskStatusDetailsList());
		}
		for (int i = 0; (null != prInst.getTasks()) && (i < prInst.getTasks().size()); ++i) {
			TaskInstanceDetails taskInst = prInst.getTasks().get(i);
			TaskStatusDetails taskStat = createTaskStatus(taskInst);
			// just add task status'es to pr status - they will be stored all together at once
			prStat.getTaskStatuses().add(taskStat);
		}
		// statuses created, now store
		prInsts.addPr(prInst, prStat);
		// notify plugin
		plugPrSubmitted(prInst, prStat);
		// .. and publish
		publishPr(UpdateType.CREATION, prStat);
		
		for (int i = 0; (null != prStat.getTaskStatuses()) && (i < prStat.getTaskStatuses().size()); ++i) {
			TaskStatusDetails taskStat = prStat.getTaskStatuses().get(i);
			publishTask(UpdateType.CREATION, taskStat);
		}
		// .. and respond
		PlanningRequestResponseInstanceDetails prr = createResponse(prInst);
		
		PlanningRequestResponseInstanceDetailsList resp = new PlanningRequestResponseInstanceDetailsList();
		resp.add(prr);
		
		LOG.log(Level.INFO, "{1}.submitPlanningRequest() response: returning prResponse={0}",
				new Object[] { Dumper.prResps(resp), Dumper.sending(interaction) });
		return resp;
	}
	
	public void updatePlanningRequest(PlanningRequestInstanceDetails prInst,
			MALInteraction interaction) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.updatePlanningRequest(prInst)\n  prInst={0}",
				new Object[] { Dumper.prInst(prInst), Dumper.received(interaction) });
		Check.prInst(prInst);
		Check.prDefId(prInst.getPrDefId());
		Check.prDefExists(prInst.getPrDefId(), prDefs);
		Check.prInstId(prInst.getId());
		PrInstStore.Item old = Check.prInstExists(prInst.getId(), prInsts);
		Check.listElements(prInst.getTasks(), prInst.getId());
		// straightforward replace - delete old items, add new items
		PlanningRequestStatusDetails prStatNew = createPrStatus(prInst);
		if (null != prInst.getTasks()) {
			prStatNew.setTaskStatuses(new TaskStatusDetailsList()); // init list
		}
		
		for (int i = 0; (null != prInst.getTasks()) && (i < prInst.getTasks().size()); ++i) {
			TaskInstanceDetails taskInst = prInst.getTasks().get(i);
			TaskStatusDetails taskStat = createTaskStatus(taskInst);
			// just add task status'es to pr status - they will be stored all together at once
			prStatNew.getTaskStatuses().add(taskStat);
		}
		
		// publish deletion of old stuff
		for (int i = 0; (null != old.stat.getTaskStatuses()) && (i < old.stat.getTaskStatuses().size()); ++i) {
			TaskStatusDetails taskStat = old.stat.getTaskStatuses().get(i);
			taskStat.setStatus(Util.addOrUpdateStatus(taskStat.getStatus(), InstanceState.LAST_MODIFIED,
					new Time(System.currentTimeMillis()), "deleted"));
			publishTask(UpdateType.DELETION, taskStat);
		}
		
		old.stat.setStatus(Util.addOrUpdateStatus(old.stat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "deleted"));
		
		publishPr(UpdateType.DELETION, old.stat);
		// store new pr instance (including tasks) + new pr status (including task statuses)
		prInsts.updatePr(prInst, prStatNew);
		// notify plugin
		plugPrUpdated(prInst, prStatNew);
		// .. and publish new
		for (int i = 0; (null != prStatNew.getTaskStatuses()) && (i < prStatNew.getTaskStatuses().size()); ++i) {
			TaskStatusDetails taskStat = prStatNew.getTaskStatuses().get(i);
			publishTask(UpdateType.CREATION, taskStat);
		}
		publishPr(UpdateType.CREATION, prStatNew);
		
		LOG.log(Level.INFO, "{0}.updatePlanningRequest() response: returning nothing", Dumper.sending(interaction));
	}

	public void removePlanningRequest(Long prInstId, MALInteraction interaction)
			throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.removePlanningRequest(prInstId={0})",
				new Object[] { prInstId, Dumper.received(interaction) });
		Check.prInstId(prInstId);
		PrInstStore.Item old = Check.prInstExists(prInstId, prInsts);
		// that's it
		prInsts.removePr(prInstId);
		// notify plugin
		plugPrRemoved(prInstId);
		// publish
		for (int i = 0; (null != old.stat.getTaskStatuses()) && (i < old.stat.getTaskStatuses().size()); ++i) {
			TaskStatusDetails taskStat = old.stat.getTaskStatuses().get(i);
			taskStat.setStatus(Util.addOrUpdateStatus(taskStat.getStatus(), InstanceState.LAST_MODIFIED,
					new Time(System.currentTimeMillis()), "deleted"));
			publishTask(UpdateType.DELETION, taskStat);
		}
		
		old.stat.setStatus(Util.addOrUpdateStatus(old.stat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "deleted"));
		
		publishPr(UpdateType.DELETION, old.stat);
		LOG.log(Level.INFO, "{0}.removePlanningRequest() response: returning nothing", Dumper.sending(interaction));
	}

	public PlanningRequestStatusDetailsList getPlanningRequestStatus(LongList prIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.getPlanningRequestStatus(List:prInstIds)\n  prInstIds[]={0}",
				new Object[] { prIds, Dumper.received(interaction) });
		Check.prInstIdList(prIds);
		Check.prInstIds(prIds);
		PlanningRequestStatusDetailsList stats = new PlanningRequestStatusDetailsList(); 
		for (int i = 0; i < prIds.size(); ++i) {
			PrInstStore.Item old = prInsts.findPr(prIds.get(i));
			PlanningRequestStatusDetails prStat = (null != old) ? old.stat : null;
			stats.add(prStat);
		}
		LOG.log(Level.INFO, "{1}.getPlanningRequestStatus() response: returning prStatuses={0}",
				new Object[] { Dumper.prStats(stats), Dumper.sending(interaction) });
		return stats;
	}
	
	public LongList listDefinition(DefinitionType defType, IdentifierList names, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.listDefinition(defType={0}, List:names)\n  names[]={1}",
				new Object[] { defType, Dumper.names(names), Dumper.received(interaction) });
		Check.defType(defType);
		Check.idList(names);
		LongList ids = new LongList();
		if (DefinitionType.TASK_DEF == defType) {
			ids.addAll(taskDefs.listAll(names));
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			ids.addAll(prDefs.listAll(names));
		} else if (DefinitionType.PLANNING_REQUEST_RESPONSE_DEF == defType) {
			ids.addAll(respDefs.listAll(names));
		}
		LOG.log(Level.INFO, "{1}.listDefinition() response: returning defIds={0}",
				new Object[] { ids, Dumper.sending(interaction) });
		return ids;
	}
	
	@SuppressWarnings("rawtypes")
	public LongList addDefinition(DefinitionType defType, BaseDefinitionList defs, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.addDefinition(defType={0}, List:baseDefs)\n  baseDefs[]={1}",
				new Object[] { defType, Dumper.baseDefs(defs), Dumper.received(interaction) });
		Check.defType(defType);
		Check.baseDefList(defs);
		Check.defTypes(defs, defType);
		LongList ids = new LongList();
		if (DefinitionType.TASK_DEF == defType) {
			TaskDefinitionDetailsList defs2 = (TaskDefinitionDetailsList)defs;
			ids.addAll(taskDefs.addAll(defs2));
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			PlanningRequestDefinitionDetailsList defs2 = (PlanningRequestDefinitionDetailsList)defs;
			ids.addAll(prDefs.addAll(defs2));
		} else if (DefinitionType.PLANNING_REQUEST_RESPONSE_DEF == defType) {
			PlanningRequestResponseDefinitionDetailsList defs2 = (PlanningRequestResponseDefinitionDetailsList)defs;
			ids.addAll(respDefs.addAll(defs2));
		}
		LOG.log(Level.INFO, "{1}.addDefinition() response: returning defIds={0}",
				new Object[] { ids, Dumper.sending(interaction) });
		return ids;
	}
	
	@SuppressWarnings("rawtypes")
	protected void updateBaseDefs(DefinitionType defType, LongList defIds, BaseDefinitionList baseDefs)
			throws MALException {
		if (DefinitionType.TASK_DEF == defType) {
			TaskDefinitionDetailsList defs2 = (TaskDefinitionDetailsList)baseDefs;
			taskDefs.updateAll(defIds, defs2);
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			PlanningRequestDefinitionDetailsList defs2 = (PlanningRequestDefinitionDetailsList)baseDefs;
			prDefs.updateAll(defIds, defs2);
		} else if (DefinitionType.PLANNING_REQUEST_RESPONSE_DEF == defType) {
			PlanningRequestResponseDefinitionDetailsList defs2 = (PlanningRequestResponseDefinitionDetailsList)baseDefs;
			respDefs.updateAll(defIds, defs2);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void updateDefinition(DefinitionType defType, LongList defIds, BaseDefinitionList baseDefs,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{3}.updateDefinition(defType={0}, List:defIds, List:baseDefs)\n  defIds[]={1}\n  baseDefs[]={2}",
				new Object[] { defType, defIds, Dumper.baseDefs(baseDefs), Dumper.received(interaction) });
		Check.defType(defType);
		Check.defIdList(defIds);
		Check.baseDefList(baseDefs);
		Check.defLists(defIds, baseDefs);
		Check.defsExist(defIds, defType, prDefs, taskDefs, respDefs);
		Check.defTypes(baseDefs, defType);
		updateBaseDefs(defType, defIds, baseDefs);
		LOG.log(Level.INFO, "{0}.updateDefinition() response: returning nothing", Dumper.sending(interaction));
	}

	public void removeDefinition(DefinitionType defType, LongList defIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.removeDefinition(defType={0}, List:defIds)\n  defIds[]={1}",
				new Object[] { defType, defIds, Dumper.received(interaction) });
		Check.defType(defType);
		Check.defIdList(defIds);
		Check.defsExist(defIds, defType, prDefs, taskDefs, respDefs);
		if (DefinitionType.TASK_DEF == defType) {
			taskDefs.removeAll(defIds);
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			prDefs.removeAll(defIds);
		} else if (DefinitionType.PLANNING_REQUEST_RESPONSE_DEF == defType) {
			respDefs.removeAll(defIds);
		}
		LOG.log(Level.INFO, "{0}.removeDefinition() response: returning nothing", Dumper.sending(interaction));
	}

	public TaskStatusDetailsList getTaskStatus(LongList taskIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.getTaskStatus(List:taskIds)\n  taskIds[]={0}",
				new Object[] { taskIds, Dumper.received(interaction) });
		Check.taskInstIdList(taskIds);
		Check.taskInstIds(taskIds);
		TaskStatusDetailsList stats = new TaskStatusDetailsList();
		for (int i = 0; i < taskIds.size(); ++i) {
			TaskStatusDetails taskStat = prInsts.findTask(taskIds.get(i));
			stats.add(taskStat);
		}
		LOG.log(Level.INFO, "{1}.getTaskStatus() response: returning taskStatuses={0}",
				new Object[] { Dumper.taskStats(stats), Dumper.sending(interaction) });
		return stats;
	}
}
