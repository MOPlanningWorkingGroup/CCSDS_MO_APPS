package esa.mo.inttest.pr.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;

/**
 * Planning request provider for testing. Implemented as little as necessary.
 */
public class PlanningRequestProvider extends PlanningRequestInheritanceSkeleton {

	private static final Logger LOG = Logger.getLogger(PlanningRequestProvider.class.getName());
	
	private TaskDefStore taskDefs = new TaskDefStore();
	private PrDefStore prDefs = new PrDefStore();
	private InstStore prInsts = new InstStore();
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
	public InstStore getInstStore() {
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
	protected void plugPrSubmitted(PlanningRequestInstanceDetails pr) {
		if (null != plugin) {
			plugin.onPrSubmit(pr);
		}
	}
	
	/**
	 * Notify plugin of PR update.
	 * @param prs
	 * @param stats
	 */
	protected void plugPrUpdated(PlanningRequestInstanceDetails pr, PlanningRequestStatusDetails stat) {
		if (null != plugin) {
			plugin.onPrUpdate(pr, stat);
		}
	}
	
	/**
	 * Notify plugin of PR removal.
	 * @param prInstIds
	 */
	protected void plugPrRemoved(Long prId) {
		if (null != plugin) {
			plugin.onPrRemove(prId);
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
	public void publishPr(UpdateType updType, PlanningRequestStatusDetails stat)
			throws MALException, MALInteractionException {
		if (prPub != null) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			updHdrs.add(Util.createUpdateHeader(updType, uri));
			
			ObjectIdList objIds = new ObjectIdList();
			objIds.add(createPrObjectId(stat.getPrInstId()));
			
			PlanningRequestStatusDetailsList stats = new PlanningRequestStatusDetailsList();
			stats.add(stat);
			try {
				prPub.publish(updHdrs, objIds, stats);
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
		Util.addOrUpdateStatus(prStat, InstanceState.SUBMITTED, Util.currentTime(), "created");
		return prStat;
	}
	
	private TaskStatusDetails createTaskStatus(TaskInstanceDetails taskInst) {
		TaskStatusDetails taskStat = new TaskStatusDetails();
		taskStat.setTaskInstId(taskInst.getId());
		Util.addOrUpdateStatus(taskStat, InstanceState.SUBMITTED, Util.currentTime(), "created");
		return taskStat;
	}
	
	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#submitPlanningRequest(org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public PlanningRequestStatusDetails submitPlanningRequest(PlanningRequestInstanceDetails pr,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.submitPlanningRequest(prInst)\n  prInst={0}",
				new Object[] { Dumper.prInst(pr), Dumper.received(interaction) });
		Check.prInst(pr);
		Check.prDefId(pr.getPrDefId());
		Check.prDefExists(pr.getPrDefId(), prDefs);
		Check.prInstId(pr.getId());
		Check.prInstNoExist(pr.getId(), prInsts);
		Check.listElements(pr.getTasks(), pr.getId());
		Check.prArgs(pr, prDefs);
		Check.tasksArgs(pr.getTasks(), taskDefs);
		// checks done, start storing instances and remembering statuses
		PlanningRequestStatusDetails stat = createPrStatus(pr);
		// have tasks? will have task statuses..
		if (null != pr.getTasks()) {
			stat.setTaskStatuses(new TaskStatusDetailsList());
		}
		// create task statuses, if any
		for (int j = 0; (null != pr.getTasks()) && (j < pr.getTasks().size()); ++j) {
			TaskInstanceDetails task = pr.getTasks().get(j);
			TaskStatusDetails taskStat = createTaskStatus(task);
			stat.getTaskStatuses().add(taskStat);
		}
		// statuses created, now store
		prInsts.addPr(pr, stat);
		// notify plugin
		plugPrSubmitted(pr);
		// .. and publish changes
		publishPr(UpdateType.CREATION, stat);
		// .. and respond
		LOG.log(Level.INFO, "{1}.submitPlanningRequest() response: returning prStatus={0}",
				new Object[] { Dumper.prStat(stat), Dumper.sending(interaction) });
		return stat;
	}
	
	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#getPlanningRequest(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public PlanningRequestInstanceDetailsList getPlanningRequest(LongList prInstIds,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.getPlanningRequest(List:prIds)\n  prIds[]={0}",
				new Object[] { prInstIds, Dumper.received(interaction) });
		Check.prInstIdList(prInstIds);
		Check.prInstIds(prInstIds);
		PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList(); 
		for (int i = 0; i < prInstIds.size(); ++i) {
			InstStore.PrItem item = prInsts.findPrItem(prInstIds.get(i));
			PlanningRequestInstanceDetails prInst = (null != item) ? item.pr : null;
			insts.add(prInst);
		}
		LOG.log(Level.INFO, "{1}.getPlanningRequest() response: returning prInstances={0}",
				new Object[] { Dumper.prInsts(insts), Dumper.sending(interaction) });
		return insts;
	}
	
	/**
	 * Generates list of statuses for removed tasks - exist in old, but not in new list.
	 * Discards old statuses.
	 * @param prNew
	 * @param prOld
	 * @return
	 */
	protected TaskStatusDetailsList removedTasks(InstStore.PrItem itemOld, TaskInstanceDetailsList tasksNew) {
		TaskStatusDetailsList changes = null;
		// put new task ids into list for easy lookup
		List<Long> newTasks = new ArrayList<Long>();
		for (int i = 0; (null != tasksNew) && (i < tasksNew.size()); ++i) {
			TaskInstanceDetails newTask = tasksNew.get(i);
			newTasks.add(newTask.getId());
		}
		TaskInstanceDetailsList oldTasks = itemOld.pr.getTasks();
		// go through old items and check for disappearance
		for (int i = 0; (null != oldTasks) && (i < oldTasks.size()); ++i) {
			TaskInstanceDetails taskOld = oldTasks.get(i);
			if (!newTasks.contains(taskOld.getId())) {
				// doesn't exist in new list - removed
				StatusRecordList srl = new StatusRecordList();
				srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "removed"));
				if (null == changes) {
					changes = new TaskStatusDetailsList();
				}
				changes.add(new TaskStatusDetails(taskOld.getId(), srl));
				// old statuses are discarded
			}
		}
		return changes;
	}
	
	/**
	 * Generates list of statuses for updated tasks - exist in both lists.
	 * @param stats
	 * @param itemNew
	 * @param itemOld
	 * @return
	 */
	protected TaskStatusDetailsList addedOrUpdatedTasks(TaskStatusDetailsList stats,
			InstStore.PrItem itemOld, TaskInstanceDetailsList tasksNew) {
		TaskStatusDetailsList changes = stats;
		TaskInstanceDetailsList tasksOld = itemOld.pr.getTasks();
		// put old tasks into map for easy lookup
		Map<Long, TaskInstanceDetails> oldTasks = new HashMap<Long, TaskInstanceDetails>();
		for (int i = 0; (null != tasksOld) && (i < tasksOld.size()); ++i) {
			TaskInstanceDetails taskOld = tasksOld.get(i);
			oldTasks.put(taskOld.getId(), taskOld);
		}
		// go through new items and check for add/update
		for (int i = 0; (null != tasksNew) && (i < tasksNew.size()); ++i) {
			TaskInstanceDetails newTask = tasksNew.get(i);
			TaskInstanceDetails oldTask = oldTasks.get(newTask.getId());
			if (null == oldTask) {
				// no old task - addition, add to changes list
				StatusRecordList srl = new StatusRecordList();
				srl.add(new StatusRecord(InstanceState.SUBMITTED, Util.currentTime(), "added"));
				TaskStatusDetails taskStat = new TaskStatusDetails(newTask.getId(), srl);
				if (null == changes) {
					changes = new TaskStatusDetailsList();
				}
				changes.add(taskStat);
				// and add to all stats list
				if (null == itemOld.stat.getTaskStatuses()) {
					itemOld.stat.setTaskStatuses(new TaskStatusDetailsList());
				}
				itemOld.stat.getTaskStatuses().add(taskStat);
			} else {
				// old task exists, check for change
				if (!oldTask.equals(newTask)) {
					// changed, find task status
					TaskStatusDetailsList oldStats = itemOld.stat.getTaskStatuses();
					StatusRecord sr = null;
					for (int j = 0; (null != oldStats) && (null == sr) && (j < oldStats.size()); ++j) {
						TaskStatusDetails oldStat = oldStats.get(j);
						if (oldTask.getId() == oldStat.getTaskInstId()) {
							sr = Util.addOrUpdateStatus(oldStat, InstanceState.LAST_MODIFIED,
									Util.currentTime(), "updated");
						}
					}
					// add to changes
					StatusRecordList srl = new StatusRecordList();
					srl.add(sr);
					if (null == changes) {
						changes = new TaskStatusDetailsList();
					}
					changes.add(new TaskStatusDetails(oldTask.getId(), srl));
				} // else no change, ignore
			}
		}
		return changes;
	}
	
	protected boolean didPrChange(PlanningRequestInstanceDetails oldPr, PlanningRequestInstanceDetails newPr) {
		// compare PRs field by field except tasks
		boolean argsChange = (null != oldPr.getArgumentValues()) ? !oldPr.getArgumentValues().equals(newPr.getArgumentValues())
				: (null != newPr.getArgumentValues()) ? !newPr.getArgumentValues().equals(oldPr.getArgumentValues())
						: (oldPr.getArgumentValues() != newPr.getArgumentValues());
		boolean commChange = (null != oldPr.getComment()) ? !oldPr.getComment().equals(newPr.getComment())
				: (null != newPr.getComment()) ? !newPr.getComment().equals(oldPr.getComment()) : (oldPr != newPr);
		boolean idChange = (null != oldPr.getId()) ? !oldPr.getId().equals(newPr.getId())
				: (null != newPr.getId()) ? !newPr.equals(oldPr.getId()) : (oldPr.getComment() != newPr.getComment());
		boolean defChange = (null != oldPr.getPrDefId()) ? !oldPr.getPrDefId().equals(newPr.getPrDefId())
				: (null != newPr.getPrDefId()) ? !newPr.getPrDefId().equals(oldPr.getPrDefId()) : (oldPr.getPrDefId() != newPr.getPrDefId());
		boolean trigsChange = (null != oldPr.getTimingConstraints()) ? !oldPr.getTimingConstraints().equals(newPr.getTimingConstraints())
				: (null != newPr.getTimingConstraints()) ? !newPr.getTimingConstraints().equals(oldPr.getTimingConstraints())
						: (oldPr.getTimingConstraints() != newPr.getTimingConstraints());
		return argsChange || commChange || idChange || defChange || trigsChange;
	}
	
	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#updatePlanningRequest(org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public PlanningRequestStatusDetails updatePlanningRequest(PlanningRequestInstanceDetails pr,
			MALInteraction interaction) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.updatePlanningRequest(prInst)\n  prInst={0}",
				new Object[] { Dumper.prInst(pr), Dumper.received(interaction) });
		Check.prInst(pr);
		Check.prDefId(pr.getPrDefId());
		Check.prDefExists(pr.getPrDefId(), prDefs);
		Check.prInstId(pr.getId());
		InstStore.PrItem itemOld = Check.prInstExists(pr.getId(), prInsts);
		Check.listElements(pr.getTasks(), pr.getId());
		// generate changed statuses for tasks
		TaskStatusDetailsList taskChanges = removedTasks(itemOld, pr.getTasks());
		taskChanges = addedOrUpdatedTasks(taskChanges, itemOld, pr.getTasks());
		// now pr change
		boolean prChanged = didPrChange(itemOld.pr, pr);
		StatusRecordList srl = null;
		if (prChanged) {
			srl = new StatusRecordList();
			srl.add(Util.addOrUpdateStatus(itemOld.stat, InstanceState.LAST_MODIFIED, Util.currentTime(), "updated"));
		}
		PlanningRequestStatusDetails stat = new PlanningRequestStatusDetails(pr.getId(), srl, taskChanges);
		// store new pr instance (including tasks) + new pr status (including task statuses)
		prInsts.updatePr(pr);
		// notify plugin
		plugPrUpdated(pr, stat);
		// .. and publish changes
		publishPr(UpdateType.MODIFICATION, stat);
		
		LOG.log(Level.INFO, "{1}.updatePlanningRequest() response: returning prStatus={0}",
				new Object[] { Dumper.prStat(stat), Dumper.sending(interaction) });
		return stat;
	}

	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#removePlanningRequest(java.lang.Long, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public PlanningRequestStatusDetails removePlanningRequest(Long prId, MALInteraction interaction)
			throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.removePlanningRequest(prInstId={0})",
				new Object[] { prId, Dumper.received(interaction) });
		Check.prInstId(prId);
		InstStore.PrItem item = Check.prInstExists(prId, prInsts);
		// gather task changes
		TaskStatusDetailsList taskStats = (null != item.pr.getTasks() && !item.pr.getTasks().isEmpty()) ? new TaskStatusDetailsList(): null;
		for (int j = 0; (null != item.pr.getTasks()) && (j < item.pr.getTasks().size()); ++j) {
			TaskInstanceDetails task = item.pr.getTasks().get(j);
			StatusRecordList srl = new StatusRecordList();
			srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "removed"));
			taskStats.add(new TaskStatusDetails(task.getId(), srl));
		}
		// pr change
		StatusRecordList srl = new StatusRecordList();
		srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "deleted"));
		PlanningRequestStatusDetails stat = new PlanningRequestStatusDetails(prId, srl, taskStats);
		// and remove
		prInsts.removePr(prId);
		// notify plugin
		plugPrRemoved(prId);
		// publish changes
		publishPr(UpdateType.DELETION, stat);
		LOG.log(Level.INFO, "{1}.removePlanningRequest() response: returning prStatus={0}",
				new Object[] { Dumper.prStat(stat), Dumper.sending(interaction) });
		return stat;
	}

	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#getPlanningRequestStatus(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public PlanningRequestStatusDetailsList getPlanningRequestStatus(LongList prIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.getPlanningRequestStatus(List:prInstIds)\n  prInstIds[]={0}",
				new Object[] { prIds, Dumper.received(interaction) });
		Check.prInstIdList(prIds);
		Check.prInstIds(prIds);
		PlanningRequestStatusDetailsList stats = new PlanningRequestStatusDetailsList(); 
		for (int i = 0; i < prIds.size(); ++i) {
			InstStore.PrItem item = prInsts.findPrItem(prIds.get(i));
			PlanningRequestStatusDetails prStat = (null != item) ? item.stat : null;
			stats.add(prStat);
		}
		LOG.log(Level.INFO, "{1}.getPlanningRequestStatus() response: returning prStatuses={0}",
				new Object[] { Dumper.prStats(stats), Dumper.sending(interaction) });
		return stats;
	}
	
	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#listDefinition(org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType, org.ccsds.moims.mo.mal.structures.IdentifierList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public LongList listDefinition(DefinitionType defType, IdentifierList names, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.listDefinition(defType={0}, List:names)\n  names[]={1}",
				new Object[] { defType, Dumper.names(names), Dumper.received(interaction) });
		Check.defType(defType);
		Check.nameList(names);
		LongList ids = new LongList();
		if (DefinitionType.TASK_DEF == defType) {
			ids.addAll(taskDefs.listAll(names));
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			ids.addAll(prDefs.listAll(names));
		}
		LOG.log(Level.INFO, "{1}.listDefinition() response: returning defIds={0}",
				new Object[] { ids, Dumper.sending(interaction) });
		return ids;
	}
	
	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#addDefinition(org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType, org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
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
		}
		LOG.log(Level.INFO, "{1}.addDefinition() response: returning defIds={0}",
				new Object[] { ids, Dumper.sending(interaction) });
		return ids;
	}
	
	@SuppressWarnings("rawtypes")
	public BaseDefinitionList getDefinition(DefinitionType defType, LongList baseDefIds,
			MALInteraction interaction) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{2}.getDefinition(defType={0}, List:baseDefIds={1})",
				new Object[] { defType, baseDefIds, Dumper.received(interaction) });
		Check.defType(defType);
		Check.defIdList(baseDefIds);
		BaseDefinitionList baseDefs = null;
		if (DefinitionType.TASK_DEF == defType) {
			baseDefs = new TaskDefinitionDetailsList();
			for (int i = 0; i < baseDefIds.size(); ++i) {
				Long id = baseDefIds.get(i);
				TaskDefinitionDetails taskDef = taskDefs.find(id);
				baseDefs.add(taskDef);
			}
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			baseDefs = new PlanningRequestDefinitionDetailsList();
			for (int i = 0; i < baseDefIds.size(); ++i) {
				Long id = baseDefIds.get(i);
				PlanningRequestDefinitionDetails prDef = prDefs.find(id);
				baseDefs.add(prDef);
			}
		}
		LOG.log(Level.INFO, "{1}.addDefinition() response: returning baseDefs={0}",
				new Object[] { Dumper.baseDefs(baseDefs), Dumper.sending(interaction) });
		return baseDefs;
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
		}
	}
	
	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#updateDefinition(org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@SuppressWarnings("rawtypes")
	public void updateDefinition(DefinitionType defType, LongList defIds, BaseDefinitionList baseDefs,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{3}.updateDefinition(defType={0}, List:defIds, List:baseDefs)\n  defIds[]={1}\n  baseDefs[]={2}",
				new Object[] { defType, defIds, Dumper.baseDefs(baseDefs), Dumper.received(interaction) });
		Check.defType(defType);
		Check.defIdList(defIds);
		Check.baseDefList(baseDefs);
		Check.defLists(defIds, baseDefs);
		Check.defsExist(defIds, defType, prDefs, taskDefs);
		Check.defTypes(baseDefs, defType);
		updateBaseDefs(defType, defIds, baseDefs);
		LOG.log(Level.INFO, "{0}.updateDefinition() response: returning nothing", Dumper.sending(interaction));
	}

	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#removeDefinition(org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public void removeDefinition(DefinitionType defType, LongList defIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.removeDefinition(defType={0}, List:defIds)\n  defIds[]={1}",
				new Object[] { defType, defIds, Dumper.received(interaction) });
		Check.defType(defType);
		Check.defIdList(defIds);
		Check.defsExist(defIds, defType, prDefs, taskDefs);
		if (DefinitionType.TASK_DEF == defType) {
			taskDefs.removeAll(defIds);
		} else if (DefinitionType.PLANNING_REQUEST_DEF == defType) {
			prDefs.removeAll(defIds);
		}
		LOG.log(Level.INFO, "{0}.removeDefinition() response: returning nothing", Dumper.sending(interaction));
	}

	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#getTaskStatus(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public TaskStatusDetailsList getTaskStatus(LongList taskIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.getTaskStatus(List:taskIds)\n  taskIds[]={0}",
				new Object[] { taskIds, Dumper.received(interaction) });
		Check.taskInstIdList(taskIds);
		Check.taskInstIds(taskIds);
		TaskStatusDetailsList stats = new TaskStatusDetailsList();
		for (int i = 0; i < taskIds.size(); ++i) {
			InstStore.TaskItem item = prInsts.findTaskItem(taskIds.get(i));
			TaskStatusDetails stat = (null != item) ? item.stat : null;
			stats.add(stat);
		}
		LOG.log(Level.INFO, "{1}.getTaskStatus() response: returning taskStatuses={0}",
				new Object[] { Dumper.taskStats(stats), Dumper.sending(interaction) });
		return stats;
	}
}
