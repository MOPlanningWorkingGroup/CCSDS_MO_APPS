package esa.mo.inttest.pr.provider;

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
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
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
	private PrInstStore prInsts = new PrInstStore();
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
	public PlanningRequestStatusDetails submitPlanningRequest(PlanningRequestInstanceDetails prInst,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.submitPlanningRequest(prInst)\n  prInst={0}",
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
		// .. and publish changes
		publishPr(UpdateType.CREATION, prStat);
		// .. and respond
		LOG.log(Level.INFO, "{1}.submitPlanningRequest() response: returning prStatus={0}",
				new Object[] { Dumper.prStat(prStat), Dumper.sending(interaction) });
		return prStat;
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
			PrInstStore.PrItem item = prInsts.findPrItem(prInstIds.get(i));
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
	protected TaskStatusDetailsList removedTasks(PrInstStore.PrItem itemNew, PrInstStore.PrItem itemOld) {
		TaskStatusDetailsList change = null;
		for (int i = 0; (null != itemOld.pr.getTasks()) && (i < itemOld.pr.getTasks().size()); ++i) {
			TaskInstanceDetails taskOld = itemOld.pr.getTasks().get(i);
			int j = 0;
			for ( ; (null != itemNew.pr.getTasks()) && (j < itemNew.pr.getTasks().size()); ++j) {
				TaskInstanceDetails taskNew = itemNew.pr.getTasks().get(j);
				if (taskNew.getId() == taskOld.getId()) {
					break;
				}
			}
			if ((null == itemNew.pr.getTasks()) || (j >= itemNew.pr.getTasks().size())) {
				// doesn't exist in new list - removed
				if (null == change) {
					change = new TaskStatusDetailsList();
				}
				// changed status
				StatusRecordList srl = new StatusRecordList();
				srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "removed"));
				change.add(new TaskStatusDetails(taskOld.getId(), srl));
				// old statuses are discarded
			}
		}
		return change;
	}
	
	/**
	 * Generates list of statuses for updated tasks - exist in both lists.
	 * All statuses from old ones are copied to new ones.
	 * @param stats
	 * @param itemNew
	 * @param itemOld
	 * @return
	 */
	protected TaskStatusDetailsList updatedTasks(TaskStatusDetailsList stats,
			PrInstStore.PrItem itemNew, PrInstStore.PrItem itemOld) {
		TaskStatusDetailsList change = stats;
		for (int i = 0; (null != itemOld.pr.getTasks()) && (i < itemOld.pr.getTasks().size()); ++i) {
			TaskInstanceDetails taskOld = itemOld.pr.getTasks().get(i);
			int j = 0;
			for ( ; (null != itemNew.pr.getTasks()) && (j < itemNew.pr.getTasks().size()); ++j) {
				TaskInstanceDetails taskNew = itemNew.pr.getTasks().get(j);
				if (taskNew.getId() == taskOld.getId()) {
					// exists in both
					if (null == itemNew.stat.getTaskStatuses()) {
						itemNew.stat.setTaskStatuses(new TaskStatusDetailsList());
					}
					// copy task status
					PrInstStore.TaskItem taskItem = getInstStore().findTaskItem(taskNew.getId());
					itemNew.stat.getTaskStatuses().add(taskItem.stat);
					if (!taskNew.equals(taskOld)) {
						// modified
						if (null == change) {
							change = new TaskStatusDetailsList();
						}
						// modify all statuses list
						StatusRecord sr = Util.addOrUpdateStatus(taskItem.stat,
								InstanceState.LAST_MODIFIED, Util.currentTime(), "updated");
						// changed status
						StatusRecordList srl = new StatusRecordList();
						srl.add(sr);
						change.add(new TaskStatusDetails(taskOld.getId(), srl));
					}
					break;
				}
			}
		}
		return change;
	}
	
	/**
	 * Generates list of statuses for new tasks.
	 * Creates statuses for new tasks.
	 * @param stats
	 * @param itemNew
	 * @param itemOld
	 * @return
	 */
	protected TaskStatusDetailsList addedTasks(TaskStatusDetailsList stats,
			PrInstStore.PrItem itemNew, PrInstStore.PrItem itemOld) {
		TaskStatusDetailsList change = stats;
		for (int i = 0; (null != itemNew.pr.getTasks()) && (i < itemNew.pr.getTasks().size()); ++i) {
			TaskInstanceDetails taskNew = itemNew.pr.getTasks().get(i);
			int j = 0;
			for ( ; (null != itemOld.pr.getTasks()) && (j < itemOld.pr.getTasks().size()); ++j) {
				TaskInstanceDetails taskOld = itemOld.pr.getTasks().get(j);
				if (taskNew.getId() == taskOld.getId()) {
					break;
				}
			}
			if ((null == itemOld.pr.getTasks()) || (j >= itemOld.pr.getTasks().size())) {
				// doesn't exist in old list - added
				if (null == change) {
					change = new TaskStatusDetailsList();
				}
				// changed status
				StatusRecordList srl = new StatusRecordList();
				srl.add(new StatusRecord(InstanceState.SUBMITTED, Util.currentTime(), "added"));
				change.add(new TaskStatusDetails(taskNew.getId(), srl));
				// .. is the only one
				if (null == itemNew.stat.getTaskStatuses()) {
					itemNew.stat.setTaskStatuses(new TaskStatusDetailsList());
				}
				itemNew.stat.getTaskStatuses().add(new TaskStatusDetails(taskNew.getId(), srl));
			}
		}
		return change;
	}
	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#updatePlanningRequest(org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public PlanningRequestStatusDetails updatePlanningRequest(PlanningRequestInstanceDetails prInst,
			MALInteraction interaction) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.updatePlanningRequest(prInst)\n  prInst={0}",
				new Object[] { Dumper.prInst(prInst), Dumper.received(interaction) });
		Check.prInst(prInst);
		Check.prDefId(prInst.getPrDefId());
		Check.prDefExists(prInst.getPrDefId(), prDefs);
		Check.prInstId(prInst.getId());
		PrInstStore.PrItem itemOld = Check.prInstExists(prInst.getId(), prInsts);
		Check.listElements(prInst.getTasks(), prInst.getId());
		
		// generate status for pr
		PlanningRequestStatusDetails prStatNew = new PlanningRequestStatusDetails();
		prStatNew.setPrInstId(prInst.getId());
		prStatNew.setStatus(itemOld.stat.getStatus()); // copy all from old
		PrInstStore.PrItem itemNew = new PrInstStore.PrItem(prInst, prStatNew);
		// clear tasks for equal() call without tasks
		TaskInstanceDetailsList oldTasks = itemOld.pr.getTasks();
		itemOld.pr.setTasks(null);
		TaskInstanceDetailsList newTasks = prInst.getTasks();
		prInst.setTasks(null);
		boolean prChanged = !prInst.equals(itemOld.pr);
		// and restore tasks
		itemOld.pr.setTasks(oldTasks);
		prInst.setTasks(newTasks);
		
		// generate changed statuses for tasks
		TaskStatusDetailsList taskStats = removedTasks(itemNew, itemOld);
		taskStats = updatedTasks(taskStats, itemNew, itemOld);
		taskStats = addedTasks(taskStats, itemNew, itemOld);
		// changed statuses
		PlanningRequestStatusDetails prChange = new PlanningRequestStatusDetails(prInst.getId(), null, taskStats);
		
		if (prChanged) {
			// copy pr status from old to new
			itemNew.stat.setStatus(itemOld.stat.getStatus());
			// and update one
			StatusRecord sr = Util.addOrUpdateStatus(itemNew.stat,
					InstanceState.LAST_MODIFIED, Util.currentTime(), "updated");
			// publish changed one
			StatusRecordList srl = new StatusRecordList();
			srl.add(sr);
			prChange.setStatus(srl);
		}
		
		// store new pr instance (including tasks) + new pr status (including task statuses)
		prInsts.updatePr(prInst, itemNew.stat);
		// notify plugin
		plugPrUpdated(prInst, itemNew.stat);
		// .. and publish changes
		publishPr(UpdateType.MODIFICATION, prChange);
		
		LOG.log(Level.INFO, "{1}.updatePlanningRequest() response: returning prStatus={0}",
				new Object[] { Dumper.prStat(itemOld.stat), Dumper.sending(interaction) });
		return itemOld.stat;
	}

	/**
	 * @see org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestHandler#removePlanningRequest(java.lang.Long, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public PlanningRequestStatusDetails removePlanningRequest(Long prInstId, MALInteraction interaction)
			throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "{1}.removePlanningRequest(prInstId={0})",
				new Object[] { prInstId, Dumper.received(interaction) });
		Check.prInstId(prInstId);
		PrInstStore.PrItem item = Check.prInstExists(prInstId, prInsts);
		// that's it
		prInsts.removePr(prInstId);
		// notify plugin
		plugPrRemoved(prInstId);
		// gather changes
		TaskStatusDetailsList taskStats = (null != item.pr.getTasks() && !item.pr.getTasks().isEmpty()) ? new TaskStatusDetailsList(): null;
		for (int i = 0; (null != item.pr.getTasks()) && (i < item.pr.getTasks().size()); ++i) {
			TaskInstanceDetails task = item.pr.getTasks().get(i);
			StatusRecordList srl = new StatusRecordList();
			srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "deleted"));
			TaskStatusDetails taskStat = new TaskStatusDetails(task.getId(), srl);
			taskStats.add(taskStat);
		}
		StatusRecordList srl = new StatusRecordList();
		srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "deleted"));
		PlanningRequestStatusDetails prStat = new PlanningRequestStatusDetails(prInstId, srl, taskStats);
		// publish changes
		publishPr(UpdateType.DELETION, prStat);
		LOG.log(Level.INFO, "{1}.removePlanningRequest() response: returning prStatus={0}",
				new Object[] { Dumper.prStat(item.stat), Dumper.sending(interaction) });
		return item.stat;
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
			PrInstStore.PrItem item = prInsts.findPrItem(prIds.get(i));
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
		Check.idList(names);
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
			PrInstStore.TaskItem item = prInsts.findTaskItem(taskIds.get(i));
			TaskStatusDetails stat = (null != item) ? item.stat : null;
			stats.add(stat);
		}
		LOG.log(Level.INFO, "{1}.getTaskStatus() response: returning taskStatuses={0}",
				new Object[] { Dumper.taskStats(stats), Dumper.sending(interaction) });
		return stats;
	}
}
