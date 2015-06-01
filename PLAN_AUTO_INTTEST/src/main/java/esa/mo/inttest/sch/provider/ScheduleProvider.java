package esa.mo.inttest.sch.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.provider.MonitorSchedulesPublisher;
import org.ccsds.moims.mo.automation.schedule.provider.ScheduleInheritanceSkeleton;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;

/**
 * Schedule service implementation for testing.
 */
public class ScheduleProvider extends ScheduleInheritanceSkeleton {

	private static final Logger LOG = Logger.getLogger(ScheduleProvider.class.getName());
	
	private IdentifierList domain = new IdentifierList();
	private DefStore schDefs = new DefStore();
	private InstStore schInsts = new InstStore();
	private MonitorSchedulesPublisher schPub = null;
	private URI uri = null;
	private Plugin plugin = null;
	
	/**
	 * Default ctor.
	 */
	public ScheduleProvider() {
		domain.add(new Identifier("desd"));
	}
	
	/**
	 * Set domain to use.
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
	 * Set Schedules publisher to use.
	 * @param pub
	 */
	public void setSchPub(MonitorSchedulesPublisher pub) {
		schPub = pub;
	}
	
	/**
	 * @return instances storage
	 */
	public InstStore getInstStore() {
		return schInsts;
	}
	
	/**
	 * Factory sets plugin to use.
	 * @param plugin
	 */
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Plugin check and invoke in case of submission.
	 * @param sch
	 * @param stat
	 */
	protected void plugSubmitted(ScheduleInstanceDetailsList scheds) {
		if (null != plugin) {
			plugin.onSubmit(scheds);
		}
	}
	
	/**
	 * Plugin check and invoke in case of update.
	 * @param sch
	 * @param stat
	 */
	protected void plugUpdated(ScheduleInstanceDetailsList scheds, ScheduleStatusDetailsList stats) {
		if (null != plugin) {
			plugin.onUpdate(scheds, stats);
		}
	}
	
	/**
	 * Plugin check and invoke in case of removal.
	 * @param schId
	 */
	protected void plugRemoved(LongList schIds) {
		if (null != plugin) {
			plugin.onRemove(schIds);
		}
	}
	
	/**
	 * Plugin check and invoke in case of patching.
	 * @param removed
	 * @param updated
	 * @param added
	 * @param stats
	 */
	protected void plugPatched(ScheduleInstanceDetailsList removed, ScheduleInstanceDetailsList updated,
			ScheduleInstanceDetailsList added, ScheduleStatusDetailsList stats) {
		if (null != plugin) {
			plugin.onPatch(removed, updated, added, stats);
		}
	}
	
	/**
	 * Plugin check and invoke in case of start.
	 * @param ids
	 * @param stats
	 */
	protected void plugStarted(LongList ids, ScheduleStatusDetailsList stats) {
		if (null != plugin) {
			plugin.onStart(ids, stats);
		}
	}
	
	/**
	 * Plugin check and invoke in case of pause.
	 * @param ids
	 * @param stats
	 */
	protected void plugPaused(LongList ids, ScheduleStatusDetailsList stats) {
		if (null != plugin) {
			plugin.onPause(ids, stats);
		}
	}
	
	/**
	 * Plugin check and invoke in case of resume.
	 * @param ids
	 * @param stats
	 */
	protected void plugResumed(LongList ids, ScheduleStatusDetailsList stats) {
		if (null != plugin) {
			plugin.onResume(ids, stats);
		}
	}
	
	/**
	 * Plugin check and invoke in case of termination.
	 * @param ids
	 * @param stats
	 */
	protected void plugTerminated(LongList ids, ScheduleStatusDetailsList stats) {
		if (null != plugin) {
			plugin.onTerminate(ids, stats);
		}
	}
	
	/**
	 * Creates new schedule status.
	 * @param inst
	 * @return
	 */
	private ScheduleStatusDetails createStat(ScheduleInstanceDetails inst) {
		ScheduleStatusDetails stat = new ScheduleStatusDetails();
		stat.setSchInstId(inst.getId());
		Util.addOrUpdateStatus(stat, InstanceState.SUBMITTED, Util.currentTime(), "created");
		stat.setScheduleItemStatuses(new ScheduleItemStatusDetailsList()); // mandatory
		return stat;
	}
	
	/**
	 * Creates new schedule item status.
	 * @param inst
	 * @return
	 */
	private ScheduleItemStatusDetails createItemStat(ScheduleItemInstanceDetails inst) {
		ScheduleItemStatusDetails stat = new ScheduleItemStatusDetails();
		stat.setSchItemInstId(inst.getId());
		Util.addOrUpdateStatus(stat, InstanceState.SUBMITTED, Util.currentTime(), "created");
		return stat;
	}
	
	/**
	 * Creates new object id structure for publishing.
	 * @param id
	 * @return
	 */
	private ObjectId createObjId(Long id) {
		ObjectId oi = new ObjectId();
		oi.setKey(new ObjectKey(domain, id));
		oi.setType(Util.createObjType(new ScheduleInstanceDetails()));
		return oi;
	}
	
	/**
	 * Publishing.
	 * @param ut
	 * @param stat
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void publish(UpdateType ut, ScheduleStatusDetailsList stats) throws MALException, MALInteractionException {
		if (null != schPub) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			ObjectIdList objIds = new ObjectIdList();
			for (int i = 0; i < stats.size(); ++i) {
				ScheduleStatusDetails stat = stats.get(i);
				updHdrs.add(Util.createUpdateHeader(ut, uri));
				objIds.add(createObjId(stat.getSchInstId()));
			}
			schPub.publish(updHdrs, objIds, stats);
		} else {
			LOG.log(Level.INFO, "no schedules publiser set");
		}
	}
	/**
	 * Implements new Schedule instance submission to the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#submitSchedule(java.lang.Long, java.lang.Long, org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList submitSchedule(ScheduleInstanceDetailsList scheds,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.submitSchedule(List:schInst)\n  schInst[]={0}",
				new Object[] { Dumper.schInsts(scheds), Dumper.received(interaction) });
		Check.schInstList(scheds);
		Check.addSchInsts(scheds, schDefs, schInsts);
		// store instances and remember statuses
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (int i = 0; i < scheds.size(); ++i) {
			ScheduleInstanceDetails sch = scheds.get(i);
			ScheduleStatusDetails schStat = createStat(sch);
			if (null != sch.getScheduleItems()) {
				schStat.setScheduleItemStatuses(new ScheduleItemStatusDetailsList());
			}
			// create item statuses
			for (int j = 0; (null != sch.getScheduleItems()) && (j < sch.getScheduleItems().size()); ++j) {
				ScheduleItemInstanceDetails schItem = sch.getScheduleItems().get(j);
				ScheduleItemStatusDetails schItemStat = createItemStat(schItem);
				schStat.getScheduleItemStatuses().add(schItemStat);
			}
			schStats.add(schStat);
			schInsts.add(sch, schStat);
		}
		// notify plugin
		plugSubmitted(scheds);
		// and publish changes
		publish(UpdateType.CREATION, schStats);
		LOG.log(Level.INFO, "{1}.submitSchedule() response: returning schStatus={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	/**
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#getSchedule(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	public ScheduleInstanceDetailsList getSchedule(LongList schInstIds,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.getSchedule(List:schIds)\n  schIds[]={0}",
				new Object[] { schInstIds, Dumper.received(interaction) });
		Check.schInstIdList(schInstIds);
		Check.schInstIds(schInstIds);
		ScheduleInstanceDetailsList insts = schInsts.listInsts(schInstIds);
		LOG.log(Level.INFO, "{1}.getSchedule() response: schInsts[]={0}",
				new Object[] { Dumper.schInsts(insts), Dumper.sending(interaction) });
		return insts;
	}

	/**
	 * Generates status changes for removed schedule items.
	 * @param itemsOld
	 * @param itemsNew
	 * @return
	 */
	protected ScheduleItemStatusDetailsList removedItems(InstStore.SchItem itemsOld, ScheduleItemInstanceDetailsList itemsNew) {
		ScheduleItemStatusDetailsList changes = null;
		// put new item ids into list for easy lookup
		List<Long> newItems = new ArrayList<Long>();
		for (int i = 0; (null != itemsNew) && (i < itemsNew.size()); ++i) {
			ScheduleItemInstanceDetails newItem = itemsNew.get(i);
			newItems.add(newItem.getId());
		}
		ScheduleItemInstanceDetailsList oldItems = itemsOld.inst.getScheduleItems();
		// go through old items and create change status for removed ones
		for (int i = 0; (null != oldItems) && (i < oldItems.size()); ++i) {
			ScheduleItemInstanceDetails oldItem = oldItems.get(i);
			if (!newItems.contains(oldItem.getId())) {
				// old id not present in new ids list - generate removed status
				StatusRecordList srl = new StatusRecordList();
				srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "removed"));
				if (null == changes) {
					changes = new ScheduleItemStatusDetailsList();
				}
				changes.add(new ScheduleItemStatusDetails(oldItem.getId(), srl));
				// old status is discarded
				ScheduleItemStatusDetailsList oldStats = itemsOld.stat.getScheduleItemStatuses();
				for (int j = 0; (null != oldStats) && (j < oldStats.size()); ++j) {
					ScheduleItemStatusDetails oldStat = oldStats.get(j);
					if (oldItem.getId() == oldStat.getSchItemInstId()) {
						oldStats.remove(oldStat);
						break;
					}
				}
			}
		}
		return changes;
	}
	
	/**
	 * Generate status changes for added and updated schedule items.
	 * @param stats
	 * @param itemOld
	 * @param itemsNew
	 * @return
	 */
	protected ScheduleItemStatusDetailsList addedOrUpdatedItems(ScheduleItemStatusDetailsList stats,
			InstStore.SchItem itemOld, ScheduleItemInstanceDetailsList itemsNew) {
		ScheduleItemStatusDetailsList changes = stats;
		ScheduleItemInstanceDetailsList itemsOld = itemOld.inst.getScheduleItems();
		// put old items into map for easy lookup
		Map<Long, ScheduleItemInstanceDetails> oldItems = new HashMap<Long, ScheduleItemInstanceDetails>();
		for (int i = 0; (null != itemsOld) && (i < itemsOld.size()); ++i) {
			ScheduleItemInstanceDetails oldItem = itemsOld.get(i);
			oldItems.put(oldItem.getId(), oldItem);
		}
		// go through new items and create add or update status
		for (int i = 0; (null != itemsNew) && (i < itemsNew.size()); ++i) {
			ScheduleItemInstanceDetails newItem = itemsNew.get(i);
			ScheduleItemInstanceDetails oldItem = oldItems.get(newItem.getId());
			if (null == oldItem) {
				// no such item in old items list - generate add status
				StatusRecordList srl = new StatusRecordList();
				srl.add(new StatusRecord(InstanceState.SUBMITTED, Util.currentTime(), "added"));
				if (null == changes) {
					changes = new ScheduleItemStatusDetailsList();
				}
				ScheduleItemStatusDetails itemStat = new ScheduleItemStatusDetails(newItem.getId(), srl);
				changes.add(itemStat);
				// and add status to other item statuses
				if (null == itemOld.stat.getScheduleItemStatuses()) {
					itemOld.stat.setScheduleItemStatuses(new ScheduleItemStatusDetailsList());
				}
				itemOld.stat.getScheduleItemStatuses().add(itemStat);
			} else {
				// update - did item actually change?
				if (!oldItem.equals(newItem)) {
					// find item statuses list and update it
					ScheduleItemStatusDetailsList oldStats = itemOld.stat.getScheduleItemStatuses();
					StatusRecord sr = null;
					for (int j = 0; (null != oldStats) && (j < oldStats.size()); ++i) {
						ScheduleItemStatusDetails oldStat = oldStats.get(j);
						if (oldItem.getId() == oldStat.getSchItemInstId()) {
							sr = Util.addOrUpdateStatus(oldStat, InstanceState.LAST_MODIFIED, Util.currentTime(), "updated");
						}
					}
					// generate status change
					StatusRecordList srl = new StatusRecordList();
					srl.add(sr);
					if (null == changes) {
						changes = new ScheduleItemStatusDetailsList();
					}
					changes.add(new ScheduleItemStatusDetails(oldItem.getId(), srl));
				} // else no change
			}
		}
		return changes;
	}
	
	protected boolean didSchChange(ScheduleInstanceDetails oldSch, ScheduleInstanceDetails newSch) {
		// compare schedules ignoring schedule items
		ArgumentValueList oldArgs = oldSch.getArgumentValues();
		ArgumentValueList newArgs = newSch.getArgumentValues();
		boolean argsChange = (null != oldArgs) ? !oldArgs.equals(newArgs)
				: (null != newArgs) ? !newArgs.equals(oldArgs) : (oldArgs != newArgs);
		boolean commChange = (null != oldSch.getComment()) ? !oldSch.getComment().equals(newSch.getComment())
				: (null != newSch.getComment()) ? !newSch.getComment().equals(oldSch.getComment())
						: (oldSch.getComment() != newSch.getComment());
		boolean idChange = (null != oldSch.getId()) ? !oldSch.getId().equals(newSch.getId())
				: (null != newSch.getId()) ? !newSch.getId().equals(oldSch.getId())
						: (oldSch.getId() != newSch.getId());
		boolean defChange = (null != oldSch.getSchDefId()) ? !oldSch.getSchDefId().equals(newSch.getSchDefId())
				: (null != newSch.getSchDefId()) ? !newSch.getSchDefId().equals(oldSch.getSchDefId())
						: (oldSch.getSchDefId() != newSch.getSchDefId());
		TimingDetailsList oldTims = oldSch.getTimingConstraints();
		TimingDetailsList newTims = newSch.getTimingConstraints();
		boolean trigChange = (null != oldTims) ? !oldTims.equals(newTims)
				: (null != newTims) ? !newTims.equals(oldTims) : (oldTims != newTims);
		return argsChange || commChange || idChange || defChange || trigChange;
	}
	/**
	 * Implements Schedule modification in the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#updateSchedule(java.lang.Long, org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList updateSchedule(ScheduleInstanceDetailsList scheds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updateSchedule(schInst)\n  schInst={1}",
				new Object[] { Dumper.schInsts(scheds), Dumper.received(interaction) });
		Check.schInstList(scheds);
		List<InstStore.SchItem> items = Check.updateSchInsts(scheds, schDefs, schInsts);
		// track status changes
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (int i = 0; i < scheds.size(); ++i) {
			InstStore.SchItem itemOld = items.get(i);
			ScheduleInstanceDetails sch = scheds.get(i);
			// work through item differences
			ScheduleItemStatusDetailsList itemChanges = removedItems(itemOld, sch.getScheduleItems());
			itemChanges = addedOrUpdatedItems(itemChanges, itemOld, sch.getScheduleItems());
			// has schedule itself changed?
			boolean schChanged = didSchChange(itemOld.inst, sch);
			StatusRecordList srl = null;
			if (schChanged) {
				srl = new StatusRecordList();
				srl.add(new StatusRecord(InstanceState.LAST_MODIFIED, Util.currentTime(), "updated"));
			}
			if (null == itemChanges) { // FIXME temp workaround
				itemChanges = new ScheduleItemStatusDetailsList();
			}
			schStats.add(new ScheduleStatusDetails(sch.getId(), srl, itemChanges));
			schInsts.update(sch);
		}
		// notify plugin
		plugUpdated(scheds, schStats);
		// and publish
		publish(UpdateType.MODIFICATION, schStats);
		LOG.log(Level.INFO, "{1}.updateSchedule() response: returning schStatus={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	/**
	 * Implements Schedule removal from the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#removeSchedule(java.lang.Long, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList removeSchedule(LongList schIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.removeSchedule(schInstIds={0})",
				new Object[] { schIds, Dumper.received(interaction) });
		Check.schInstIdList(schIds);
		Check.schInstIds(schIds);
		List<InstStore.SchItem> items = Check.schInstsExist(schIds, schInsts);
		// that's it
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (int i = 0; i < schIds.size(); ++i) {
			InstStore.SchItem item = items.get(i);
			// item changes
			ScheduleItemStatusDetailsList itemStats = (null != item.inst.getScheduleItems()) ? new ScheduleItemStatusDetailsList() : null;
			for (int j = 0; (null != item.inst.getScheduleItems()) && (j < item.inst.getScheduleItems().size()); ++j) {
				ScheduleItemInstanceDetails schItem = item.inst.getScheduleItems().get(j);
				StatusRecordList srl = new StatusRecordList();
				srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "removed"));
				itemStats.add(new ScheduleItemStatusDetails(schItem.getId(), srl));
			}
			StatusRecordList srl = new StatusRecordList();
			srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "removed"));
			if (null == itemStats) { // FIXME temp workaround
				itemStats = new ScheduleItemStatusDetailsList();
			}
			schStats.add(new ScheduleStatusDetails(item.inst.getId(), srl, itemStats));
			// remove
			schInsts.remove(item.inst.getId());
		}
		// notify plugin
		plugRemoved(schIds);
		// and publish
		publish(UpdateType.DELETION, schStats);
		LOG.log(Level.INFO, "{1}.removeSchedule() response: returning schStatus={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	/**
	 * Implements Schedule patching.. how does it differ from updating?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#patchSchedule(java.lang.Long, java.lang.Long, org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails, org.ccsds.moims.mo.automation.schedule.structures.SchedulePatchOperations, java.lang.Long, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList patchSchedule(ScheduleInstanceDetailsList toRemove, ScheduleInstanceDetailsList toUpdate,
			ScheduleInstanceDetailsList toAdd, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{5}.patchSchedule(List:toRemove, List:toUpdate, List:toAdd)\n  toRemove={0}\n  toUpdate={1}\n  toAdd={2}",
				new Object[] { Dumper.schInsts(toRemove), Dumper.schInsts(toUpdate), Dumper.schInsts(toAdd), Dumper.received(interaction) });
		Check.patchLists(toRemove, toUpdate, toAdd);
		Check.patchRemove(toRemove, schInsts);
		Check.patchUpdate(toUpdate, schInsts);
		Check.patchAdd(toAdd, schInsts);
		
		ScheduleStatusDetailsList schStats = schInsts.patch(toRemove, toUpdate, toAdd);
		// notify plugin
		plugPatched(toRemove, toUpdate, toAdd, schStats);
		// and publish
		publish(UpdateType.MODIFICATION, schStats);
		LOG.log(Level.INFO, "{1}.patchSchedule() response: returning schStats={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	/**
	 * Implements Schedule status retrieval.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#getScheduleStatus(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList getScheduleStatus(LongList schIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.getScheduleStatus(List:schIds)\n  schIds[]={0}",
				new Object[] { schIds, Dumper.received(interaction) });
		Check.schInstIdList(schIds);
		
		ScheduleStatusDetailsList schStats = schInsts.listStats(schIds);
		
		LOG.log(Level.INFO, "{1}.getScheduleStatus() response: schStats[]={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	private ScheduleStatusDetails start(InstStore.SchItem item) throws MALException, MALInteractionException {
		// add status to all statuses
		StatusRecord sr = Util.addOrUpdateStatus(item.stat, InstanceState.DISTRIBUTED_FOR_EXECUTION,
				Util.currentTime(), "started");
		// return change
		StatusRecordList srl = new StatusRecordList();
		srl.add(sr);
		return new ScheduleStatusDetails(item.inst.getId(), srl, new ScheduleItemStatusDetailsList()); // FIXME
	}
	
	/**
	 * Implements schedule start
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#start(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList start(LongList schIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.start(schInstIds={0})", new Object[] { schIds, Dumper.received(interaction) });
		Check.schInstIdList(schIds);
		Check.schInstIds(schIds);
		List<InstStore.SchItem> items = Check.schInstsExist(schIds, schInsts);
		// store changes
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (int i = 0; i < schIds.size(); ++i) {
			InstStore.SchItem item = items.get(i);
			schStats.add(start(item));
		}
		// notify plugin
		plugStarted(schIds, schStats);
		
		publish(UpdateType.UPDATE, schStats);
		LOG.log(Level.INFO, "{1}.start() response: returning schStats={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	private ScheduleStatusDetails pause(InstStore.SchItem item) throws MALException, MALInteractionException {
		// update status
		StatusRecord sr = Util.addOrUpdateStatus(item.stat, InstanceState.PLANNED,
				Util.currentTime(), "paused");
		// return changes
		StatusRecordList srl = new StatusRecordList();
		srl.add(sr);
		return new ScheduleStatusDetails(item.inst.getId(), srl, new ScheduleItemStatusDetailsList()); // FIXME
	}
	
	/**
	 * Implements service pause?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#pause(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList pause(LongList schIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.pause(schInstIds={0})", new Object[] { schIds, Dumper.received(interaction) });
		Check.schInstIdList(schIds);
		Check.schInstIds(schIds);
		List<InstStore.SchItem> items = Check.schInstsExist(schIds, schInsts);
		// store changes
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (int i = 0; i < schIds.size(); ++i) {
			InstStore.SchItem item = items.get(i);
			schStats.add(pause(item));
		}
		// notify plugin
		plugPaused(schIds, schStats);
		
		publish(UpdateType.UPDATE, schStats);
		LOG.log(Level.INFO, "{1}.pause() response: returning schStats={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	private ScheduleStatusDetails resume(InstStore.SchItem item) throws MALException, MALInteractionException {
		// update stats
		StatusRecord sr = Util.addOrUpdateStatus(item.stat, InstanceState.SCHEDULED,
				Util.currentTime(), "resumed");
		// return change
		StatusRecordList srl = new StatusRecordList();
		srl.add(sr);
		return new ScheduleStatusDetails(item.inst.getId(), srl, new ScheduleItemStatusDetailsList()); // FIXME
	}
	
	/**
	 * Implements service resume?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#resume(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList resume(LongList schIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.resume(schInstIds={0})",
				new Object[] { schIds, Dumper.received(interaction) });
		Check.schInstIdList(schIds);
		Check.schInstIds(schIds);
		List<InstStore.SchItem> items = Check.schInstsExist(schIds, schInsts);
		// store changes
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (int i = 0; i < schIds.size(); ++i) {
			InstStore.SchItem item = items.get(i);
			schStats.add(resume(item));
		}
		// notify plugin
		plugResumed(schIds, schStats);
		
		publish(UpdateType.UPDATE, schStats);
		LOG.log(Level.INFO, "{1}.resume() response: returning schStats={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	private ScheduleStatusDetails terminate(InstStore.SchItem item) throws MALException, MALInteractionException {
		// updates stats
		StatusRecord sr = Util.addOrUpdateStatus(item.stat, InstanceState.INVALID,
				Util.currentTime(), "terminated");
		// return change
		StatusRecordList srl = new StatusRecordList();
		srl.add(sr);
		return new ScheduleStatusDetails(item.inst.getId(), srl, new ScheduleItemStatusDetailsList()); // FIXME
	}
	
	/**
	 * Implements service termination.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#terminate(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetailsList terminate(LongList schIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.terminate(schInstIds={0})",
				new Object[] { schIds, Dumper.received(interaction) });
		Check.schInstIdList(schIds);
		Check.schInstIds(schIds);
		List<InstStore.SchItem> items = Check.schInstsExist(schIds, schInsts);
		// store changes
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (int i = 0; i < schIds.size(); ++i) {
			InstStore.SchItem item = items.get(i);
			schStats.add(terminate(item));
		}
		// notify plugin
		plugTerminated(schIds, schStats);
		
		publish(UpdateType.UPDATE, schStats);
		LOG.log(Level.INFO, "{1}.terminate() response: returning schStats={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	/**
	 * Implments schedule definition retrieval.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#listDefinition(org.ccsds.moims.mo.mal.structures.IdentifierList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public LongList listDefinition(IdentifierList schNames, MALInteraction interaction) throws MALInteractionException,
			MALException {
		LOG.log(Level.INFO, "{1}.listDefinition(List:schNames)\n  schNames[]={0}",
				new Object[] { Dumper.names(schNames), Dumper.received(interaction) });
		Check.namesList(schNames);
		LongList schDefIds = schDefs.list(schNames);
		LOG.log(Level.INFO, "{1}.listDefinition() response: returning ids={0}",
				new Object[] { schDefIds, Dumper.sending(interaction) });
		return schDefIds;
	}

	/**
	 * Implements schedule definition addition to the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#addDefinition(org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public LongList addDefinition(ScheduleDefinitionDetailsList schDefs, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.addDefinition(List:schDefs)\n  schDefs[]={0}",
				new Object[] { Dumper.schDefs(schDefs), Dumper.received(interaction) });
		Check.defsList(schDefs);
		LongList schDefIds = this.schDefs.addAll(schDefs);
		LOG.log(Level.INFO, "{1}.addDefinition() response: returning ids={0}",
				new Object[] { schDefIds, Dumper.sending(interaction) });
		return schDefIds;
	}
	
	public ScheduleDefinitionDetailsList getDefinition(LongList schDefIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.getDefinition(schDefIds={0})",
				new Object[] { schDefIds, Dumper.received(interaction) });
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		for (int i = 0; i < schDefIds.size(); ++i) {
			Long id = schDefIds.get(i);
			ScheduleDefinitionDetails def = this.schDefs.find(id);
			schDefs.add(def);
		}
		LOG.log(Level.INFO, "{1}.getDefinition() response: returning schDefs={0}",
				new Object[] { Dumper.schDefs(schDefs), Dumper.sending(interaction) });
		return schDefs;
	}
	
	/**
	 * Implements schedule definition modification in the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#updateDefinition(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void updateDefinition(LongList schDefIds, ScheduleDefinitionDetailsList schDefs,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updateDefinition(List:schDefIds, List:schDefs)\n  schDefIds[]={0}\n  schDefs[]={1}",
				new Object[] { schDefIds, Dumper.schDefs(schDefs), Dumper.received(interaction) });
		if (null == schDefIds) {
			throw new MALException("schedule def id list is null");
		}
		if (null == schDefs) {
			throw new MALException("schedule defs list is null");
		}
		if (schDefIds.size() != schDefs.size()) {
			throw new MALException("schedule def ids list and schedule defs list are different size");
		}
		this.schDefs.updateAll(schDefIds, schDefs);
		LOG.log(Level.INFO, "{0}.updateDefinition() response: returning nothing", Dumper.sending(interaction));
	}

	/**
	 * Implements schedule definition removal from the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#removeDefinition(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void removeDefinition(LongList schInstIds, MALInteraction interaction) throws MALInteractionException,
			MALException {
		LOG.log(Level.INFO, "{1}.removeDefinition(List:schInstIds)\n  schInstIds[]={0}",
				new Object[] { schInstIds, Dumper.received(interaction) });
		if (null == schInstIds) {
			throw new MALException("schedule def ids list is null");
		}
		if (schInstIds.isEmpty()) {
			throw new MALException("schedule def ids list is empty");
		}
		schDefs.removeAll(schInstIds);
		LOG.log(Level.INFO, "{0}.removeDefinition() response: returning nothing", Dumper.sending(interaction));
	}
}
