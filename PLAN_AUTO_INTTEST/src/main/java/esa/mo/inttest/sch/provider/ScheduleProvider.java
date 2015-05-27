package esa.mo.inttest.sch.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.provider.MonitorSchedulesPublisher;
import org.ccsds.moims.mo.automation.schedule.provider.ScheduleInheritanceSkeleton;
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
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;
import esa.mo.inttest.sch.provider.InstStore.Item;

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
	protected void plugSubmitted(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
		if (null != plugin) {
			plugin.onSubmit(sch, stat);
		}
	}
	
	/**
	 * Plugin check and invoke in case of update.
	 * @param sch
	 * @param stat
	 */
	protected void plugUpdated(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
		if (null != plugin) {
			plugin.onUpdate(sch, stat);
		}
	}
	
	/**
	 * Plugin check and invoke in case of removal.
	 * @param schId
	 */
	protected void plugRemoved(Long schId) {
		if (null != plugin) {
			plugin.onRemove(schId);
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
	 * @param sch
	 * @param stat
	 */
	protected void plugStarted(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
		if (null != plugin) {
			plugin.onStart(sch, stat);
		}
	}
	
	/**
	 * Plugin check and invoke in case of pause.
	 * @param sch
	 * @param stat
	 */
	protected void plugPaused(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
		if (null != plugin) {
			plugin.onPause(sch, stat);
		}
	}
	
	/**
	 * Plugin check and invoke in case of resume.
	 * @param sch
	 * @param stat
	 */
	protected void plugResumed(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
		if (null != plugin) {
			plugin.onResume(sch, stat);
		}
	}
	
	/**
	 * Plugin check and invoke in case of termination.
	 * @param sch
	 * @param stat
	 */
	protected void plugTerminated(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
		if (null != plugin) {
			plugin.onTerminate(sch, stat);
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
	public void publish(UpdateType ut, ScheduleStatusDetails stat) throws MALException, MALInteractionException {
		if (null != schPub) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			updHdrs.add(Util.createUpdateHeader(ut, uri));
			ObjectIdList objIds = new ObjectIdList();
			objIds.add(createObjId(stat.getSchInstId()));
			ScheduleStatusDetailsList stats = new ScheduleStatusDetailsList();
			stats.add(stat);
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
	public ScheduleStatusDetails submitSchedule(ScheduleInstanceDetails schInst,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.submitSchedule(schInst)\n  schInst={0}",
				new Object[] { Dumper.schInst(schInst), Dumper.received(interaction) });
		// for nullpointers check we will
		Check.schInst(schInst);
		Check.schInstId(schInst.getId());
		Check.schDefId(schInst.getSchDefId());
		Check.schDefExists(schInst.getSchDefId(), schDefs);
		Check.schInstNoExist(schInst.getId(), schInsts);
		Check.listElements(schInst.getScheduleItems(), schInst.getId());
		// create statuses
		ScheduleStatusDetails schStat = createStat(schInst);
		// create item statuses
		for (int i = 0; (null != schInst.getScheduleItems()) && (i < schInst.getScheduleItems().size()); ++i) {
			ScheduleItemInstanceDetails schItem = schInst.getScheduleItems().get(i);
			ScheduleItemStatusDetails schItemStat = createItemStat(schItem);
			schStat.getScheduleItemStatuses().add(schItemStat);
		}
		schInsts.add(schInst, schStat);
		// notify plugin
		plugSubmitted(schInst, schStat);
		// and publish changes
		publish(UpdateType.CREATION, schStat);
		LOG.log(Level.INFO, "{1}.submitSchedule() response: returning schStatus={0}",
				new Object[] { Dumper.schStat(schStat), Dumper.sending(interaction) });
		return schStat;
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
	protected ScheduleItemStatusDetailsList removedItems(InstStore.Item itemsOld, ScheduleItemInstanceDetailsList itemsNew) {
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
			InstStore.Item itemOld, ScheduleItemInstanceDetailsList itemsNew) {
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
	
	/**
	 * Implements Schedule modification in the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#updateSchedule(java.lang.Long, org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetails updateSchedule(ScheduleInstanceDetails schInst, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updateSchedule(schInst)\n  schInst={1}",
				new Object[] { Dumper.schInst(schInst), Dumper.received(interaction) });
		Check.schInst(schInst);
		Check.schInstId(schInst.getId());
		Check.schDefId(schInst.getSchDefId());
		Check.schDefExists(schInst.getSchDefId(), schDefs);
		InstStore.Item itemOld = Check.schInstExists(schInst.getId(), schInsts);
		Check.listElements(schInst.getScheduleItems(), schInst.getId());
		// work through item differences
		ScheduleItemStatusDetailsList itemStatChanges = removedItems(itemOld, schInst.getScheduleItems());
		itemStatChanges = addedOrUpdatedItems(itemStatChanges, itemOld, schInst.getScheduleItems());
		// has schedule itself changed?
		ScheduleItemInstanceDetailsList oldItems = itemOld.inst.getScheduleItems();
		ScheduleItemInstanceDetailsList newItems = schInst.getScheduleItems();
		itemOld.inst.setScheduleItems(null);
		schInst.setScheduleItems(null);
		boolean schChanged = !itemOld.inst.equals(schInst);
		itemOld.inst.setScheduleItems(oldItems);
		schInst.setScheduleItems(newItems);
		
		schInsts.update(schInst);
		
		StatusRecordList srl = null;
		if (schChanged) {
			srl = new StatusRecordList();
			srl.add(new StatusRecord(InstanceState.LAST_MODIFIED, Util.currentTime(), "updated"));
		}
		if (null == itemStatChanges) { // FIXME temp workaround
			itemStatChanges = new ScheduleItemStatusDetailsList();
		}
		ScheduleStatusDetails schStat = new ScheduleStatusDetails(schInst.getId(), srl, itemStatChanges);
		// notify plugin
		plugUpdated(schInst, schStat);
		// and publish
		publish(UpdateType.MODIFICATION, schStat);
		LOG.log(Level.INFO, "{1}.updateSchedule() response: returning schStatus={0}",
				new Object[] { Dumper.schStat(schStat), Dumper.sending(interaction) });
		return schStat;
	}

	/**
	 * Implements Schedule removal from the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#removeSchedule(java.lang.Long, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public ScheduleStatusDetails removeSchedule(Long schInstId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.removeSchedule(schInstId={0})",
				new Object[] { schInstId, Dumper.received(interaction) });
		// checks
		Check.schInstId(schInstId);
		InstStore.Item item = Check.schInstExists(schInstId, schInsts);
		// remove
		schInsts.remove(schInstId);
		// notify plugin
		plugRemoved(schInstId);
		// gather changes
		ScheduleItemStatusDetailsList itemStats = (null != item.inst.getScheduleItems() && !item.inst.getScheduleItems().isEmpty()) ?
				new ScheduleItemStatusDetailsList() : null;
		for (int i = 0; (null != item.inst.getScheduleItems()) && (i < item.inst.getScheduleItems().size()); ++i) {
			ScheduleItemInstanceDetails schItem = item.inst.getScheduleItems().get(i);
			StatusRecordList srl = new StatusRecordList();
			srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "removed"));
			itemStats.add(new ScheduleItemStatusDetails(schItem.getId(), srl));
		}
		StatusRecordList srl = new StatusRecordList();
		srl.add(new StatusRecord(InstanceState.REMOVED, Util.currentTime(), "removed"));
		if (null == itemStats) { // FIXME temp workaround
			itemStats = new ScheduleItemStatusDetailsList();
		}
		ScheduleStatusDetails schStat = new ScheduleStatusDetails(schInstId, srl, itemStats);
		// and publish
		publish(UpdateType.DELETION, schStat);
		LOG.log(Level.INFO, "{1}.removeSchedule() response: returning schStatus={0}",
				new Object[] { Dumper.schStat(schStat), Dumper.sending(interaction) });
		return schStat;
	}

	/**
	 * Implements Schedule patching.. how does it differ from updating?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#patchSchedule(java.lang.Long, java.lang.Long, org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails, org.ccsds.moims.mo.automation.schedule.structures.SchedulePatchOperations, java.lang.Long, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void patchSchedule(ScheduleInstanceDetailsList toRemove, ScheduleInstanceDetailsList toUpdate,
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
		for (ScheduleStatusDetails schStat: schStats) {
			publish(UpdateType.MODIFICATION, schStat);
		}
		LOG.log(Level.INFO, "{0}.patchSchedule() response: returning nothing", Dumper.sending(interaction));
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

	private void start(LongList ids) throws MALException, MALInteractionException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			Item it = schInsts.findItem(id);
			if (null == it) {
				throw new MALException("no schedule instance with id: " + id);
			}
			StatusRecord sr = Util.addOrUpdateStatus(it.stat,
					InstanceState.DISTRIBUTED_FOR_EXECUTION, Util.currentTime(), "started");
			// notify plugin
			plugStarted(it.inst, it.stat);
			// publish change
			StatusRecordList srl = new StatusRecordList();
			srl.add(sr);
			ScheduleStatusDetails schStat = new ScheduleStatusDetails(id, srl, new ScheduleItemStatusDetailsList()); // FIXME
			publish(UpdateType.UPDATE, schStat);
		}
	}
	
	/**
	 * Implements schedule start
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#start(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void start(LongList schInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.start(schInstId={0})", new Object[] { schInstIds, Dumper.received(interaction) });
		Check.schInstIdList(schInstIds);
		Check.schInstIds(schInstIds);
		start(schInstIds);
		LOG.log(Level.INFO, "{0}.start() response: returning nothing", Dumper.sending(interaction));
	}

	private void pause(LongList ids) throws MALException, MALInteractionException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			Item it = schInsts.findItem(id);
			if (null == it) {
				throw new MALException("no schedule instance with id: " + id);
			}
			StatusRecord sr = Util.addOrUpdateStatus(it.stat, InstanceState.PLANNED,
					Util.currentTime(), "paused");
			// notify plugin
			plugPaused(it.inst, it.stat);
			// publish changes
			StatusRecordList srl = new StatusRecordList();
			srl.add(sr);
			ScheduleStatusDetails schStat = new ScheduleStatusDetails(id, srl, new ScheduleItemStatusDetailsList()); // FIXME
			publish(UpdateType.UPDATE, schStat);
		}
	}
	
	/**
	 * Implements service pause?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#pause(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void pause(LongList schInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.pause(schInstId={0})", new Object[] { schInstIds, Dumper.received(interaction) });
		Check.schInstIdList(schInstIds);
		Check.schInstIds(schInstIds);
		pause(schInstIds);
		LOG.log(Level.INFO, "{0}.pause() response: returning nothing", Dumper.sending(interaction));
	}

	private void resume(LongList ids) throws MALException, MALInteractionException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			Item it = schInsts.findItem(id);
			if (null == it) {
				throw new MALException("no schedule instance with id: " + id);
			}
			StatusRecord sr = Util.addOrUpdateStatus(it.stat, InstanceState.SCHEDULED,
					Util.currentTime(), "resumed");
			// notify plugin
			plugResumed(it.inst, it.stat);
			// publish
			StatusRecordList srl = new StatusRecordList();
			srl.add(sr);
			ScheduleStatusDetails schStat = new ScheduleStatusDetails(id, srl, new ScheduleItemStatusDetailsList()); // FIXME
			publish(UpdateType.UPDATE, schStat);
		}
	}
	
	/**
	 * Implements service resume?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#resume(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void resume(LongList schInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.resume(schInstIds={0})", new Object[] { schInstIds, Dumper.received(interaction) });
		Check.schInstIdList(schInstIds);
		Check.schInstIds(schInstIds);
		resume(schInstIds);
		LOG.log(Level.INFO, "{0}.resume() response: returning nothing", Dumper.sending(interaction));
	}

	private void terminate(LongList ids) throws MALException, MALInteractionException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			Item it = schInsts.findItem(id);
			if (null == it) {
				throw new MALException("no schedule instance with id: " + id);
			}
			StatusRecord sr = Util.addOrUpdateStatus(it.stat, InstanceState.INVALID,
					Util.currentTime(), "terminated");
			// notify plugin
			plugTerminated(it.inst, it.stat);
			// publish
			StatusRecordList srl = new StatusRecordList();
			srl.add(sr);
			ScheduleStatusDetails schStat = new ScheduleStatusDetails(id, srl, new ScheduleItemStatusDetailsList()); // FIXME
			publish(UpdateType.UPDATE, schStat);
		}
	}
	
	/**
	 * Implements service termination.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#terminate(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void terminate(LongList schInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.terminate(schInstIds={0})", new Object[] { schInstIds, Dumper.received(interaction) });
		Check.schInstIdList(schInstIds);
		Check.schInstIds(schInstIds);
		terminate(schInstIds);
		LOG.log(Level.INFO, "{0}.terminate() response: returning nothing", Dumper.sending(interaction));
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
