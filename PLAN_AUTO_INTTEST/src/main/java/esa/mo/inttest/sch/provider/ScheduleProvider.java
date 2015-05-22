package esa.mo.inttest.sch.provider;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.provider.MonitorSchedulesPublisher;
import org.ccsds.moims.mo.automation.schedule.provider.ScheduleInheritanceSkeleton;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
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
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;

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
	
	protected InstStore getInstStore() {
		return schInsts;
	}
	
	private ScheduleStatusDetails createStat(ScheduleInstanceDetails inst) {
		ScheduleStatusDetails stat = new ScheduleStatusDetails();
		stat.setSchInstId(inst.getId());
		stat.setStatus(Util.addOrUpdateStatus(stat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "created"));
		stat.setScheduleItemStatuses(new ScheduleItemStatusDetailsList()); // mandatory
		return stat;
	}
	
	private ScheduleItemStatusDetails createItemStat(ScheduleItemInstanceDetails inst) {
		ScheduleItemStatusDetails stat = new ScheduleItemStatusDetails();
		stat.setSchItemInstId(inst.getId());
		stat.setStatus(Util.addOrUpdateStatus(stat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "created"));
		return stat;
	}
	
	private ObjectId createObjId(Long id) {
		ObjectId oi = new ObjectId();
		oi.setKey(new ObjectKey(domain, id));
		oi.setType(Util.createObjType(new ScheduleInstanceDetails()));
		return oi;
	}
	
	protected void publish(UpdateType ut, ScheduleStatusDetails stat) throws MALException, MALInteractionException {
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
		if (null == schInst) {
			throw new MALException("schedule instance is null");
		}
		if (null == schInst.getSchDefId()) {
			throw new MALException("schedule definition id is null");
		}
		if (null == schInst.getId()) {
			throw new MALException("schedule instance id is null");
		}
		// create statuses
		ScheduleStatusDetails schStat = createStat(schInst);
		// create item statuses
		for (int i = 0; (null != schInst.getScheduleItems()) && (i < schInst.getScheduleItems().size()); ++i) {
			ScheduleItemInstanceDetails schItem = schInst.getScheduleItems().get(i);
			ScheduleItemStatusDetails schItemStat = createItemStat(schItem);
			schStat.getScheduleItemStatuses().add(schItemStat);
		}
		schInsts.add(schInst, schStat);
		// notify
		publish(UpdateType.CREATION, schStat);
		LOG.log(Level.INFO, "{1}.submitSchedule() response: returning schStatus={0}",
				new Object[] { Dumper.schStat(schStat), Dumper.sending(interaction) });
		return schStat;
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
		if (null == schInst) {
			throw new MALException("schedule instance is null");
		}
		if (null == schInst.getId()) {
			throw new MALException("schedule instance id is null");
		}
		if (null == schInst.getSchDefId()) {
			throw new MALException("schedule definition id is null");
		}
		ScheduleStatusDetails schStat = schInsts.update(schInst);
		schStat.setStatus(Util.addOrUpdateStatus(schStat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "modified"));
		// keep it simple - delete old item statuses and create new ones
		schStat.getScheduleItemStatuses().clear();
		for (int i = 0; (null != schInst.getScheduleItems()) && (i < schInst.getScheduleItems().size()); ++i) {
			ScheduleItemInstanceDetails schItem = schInst.getScheduleItems().get(i);
			ScheduleItemStatusDetails schItemStat = createItemStat(schItem);
			schStat.getScheduleItemStatuses().add(schItemStat);
		}
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
		if (null == schInstId) {
			throw new MALException("schedule instance id is null");
		}
		ScheduleStatusDetails schStat = schInsts.remove(schInstId);
		schStat.setStatus(Util.addOrUpdateStatus(schStat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "deleted"));
		for (int i = 0; (null != schStat.getScheduleItemStatuses()) && (i < schStat.getScheduleItemStatuses().size()); ++i) {
			ScheduleItemStatusDetails schItemStat = schStat.getScheduleItemStatuses().get(i);
			schItemStat.setStatus(Util.addOrUpdateStatus(schItemStat.getStatus(), InstanceState.LAST_MODIFIED,
					new Time(System.currentTimeMillis()), "deleted"));
		}
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
		if (null == toRemove && null == toUpdate && null == toAdd) {
			throw new MALException("schedule instance lists are null");
		}
		ScheduleStatusDetailsList schStats = schInsts.patch(toRemove, toUpdate, toAdd);
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
		if (null == schIds) {
			throw new MALException("schedule instance ids list is null");
		}
		if (schIds.isEmpty()) {
			throw new MALException("schedule instance ids list is empty");
		}
		ScheduleStatusDetailsList schStats = schInsts.list(schIds);
		LOG.log(Level.INFO, "{1}.getScheduleStatus() response: schStats[]={0}",
				new Object[] { Dumper.schStats(schStats), Dumper.sending(interaction) });
		return schStats;
	}

	private void start(LongList ids) throws MALException, MALInteractionException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule instance id[" + i + "] is null");
			}
			Item it = schInsts.findItem(id);
			if (null == it) {
				throw new MALException("no schedule instance with id: " + id);
			}
			it.stat.setStatus(Util.addOrUpdateStatus(it.stat.getStatus(), InstanceState.DISTRIBUTED_FOR_EXECUTION,
					new Time(System.currentTimeMillis()), "started"));
			publish(UpdateType.UPDATE, it.stat);
		}
	}
	
	/**
	 * Implements service start?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#start(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void start(LongList schInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.start(schInstId={0})", new Object[] { schInstIds, Dumper.received(interaction) });
		if (null == schInstIds) {
			throw new MALException("schedule instance ids list is null");
		}
		if (schInstIds.isEmpty()) {
			throw new MALException("schedule instance ids list is empty");
		}
		start(schInstIds);
		LOG.log(Level.INFO, "{0}.start() response: returning nothing", Dumper.sending(interaction));
	}

	private void pause(LongList ids) throws MALException, MALInteractionException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule instance id[" + i + "] is null");
			}
			Item it = schInsts.findItem(id);
			if (null == it) {
				throw new MALException("no schedule instance with id: " + id);
			}
			it.stat.setStatus(Util.addOrUpdateStatus(it.stat.getStatus(), InstanceState.PLANNED,
					new Time(System.currentTimeMillis()), "paused"));
			publish(UpdateType.UPDATE, it.stat);
		}
	}
	
	/**
	 * Implements service pause?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#pause(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void pause(LongList schInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.pause(schInstId={0})", new Object[] { schInstIds, Dumper.received(interaction) });
		if (null == schInstIds) {
			throw new MALException("schedule instance ids list is null");
		}
		if (schInstIds.isEmpty()) {
			throw new MALException("schedule instance ids list is empty");
		}
		pause(schInstIds);
		LOG.log(Level.INFO, "{0}.pause() response: returning nothing", Dumper.sending(interaction));
	}

	private void resume(LongList ids) throws MALException, MALInteractionException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule instance id[" + i + "] is null");
			}
			Item it = schInsts.findItem(id);
			if (null == it) {
				throw new MALException("no schedule instance with id: " + id);
			}
			it.stat.setStatus(Util.addOrUpdateStatus(it.stat.getStatus(), InstanceState.SCHEDULED,
					new Time(System.currentTimeMillis()), "resumed"));
			publish(UpdateType.UPDATE, it.stat);
		}
	}
	
	/**
	 * Implements service resume?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#resume(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void resume(LongList schInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.resume(schInstIds={0})", new Object[] { schInstIds, Dumper.received(interaction) });
		if (null == schInstIds) {
			throw new MALException("schedule instance ids list is null");
		}
		if (schInstIds.isEmpty()) {
			throw new MALException("schedule instance ids list is empty");
		}
		resume(schInstIds);
		LOG.log(Level.INFO, "{0}.resume() response: returning nothing", Dumper.sending(interaction));
	}

	private void terminate(LongList ids) throws MALException, MALInteractionException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule instance id[" + i + "] is null");
			}
			Item it = schInsts.findItem(id);
			if (null == it) {
				throw new MALException("no schedule instance with id: " + id);
			}
			it.stat.setStatus(Util.addOrUpdateStatus(it.stat.getStatus(), InstanceState.INVALID,
					new Time(System.currentTimeMillis()), "terminated"));
			publish(UpdateType.UPDATE, it.stat);
		}
	}
	
	/**
	 * Implements service termination.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#terminate(org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void terminate(LongList schInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.terminate(schInstIds={0})", new Object[] { schInstIds, Dumper.received(interaction) });
		if (null == schInstIds) {
			throw new MALException("schedule instance ids list is null");
		}
		if (schInstIds.isEmpty()) {
			throw new MALException("schedule instance ids list is empty");
		}
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
		if (null == schNames) {
			throw new MALException("schedule names list is null");
		}
		if (schNames.isEmpty()) {
			throw new MALException("schedule names list is empty");
		}
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
		if (null == schDefs) {
			throw new MALException("schedule definitions list is null");
		}
		if (schDefs.isEmpty()) {
			throw new MALException("schedule definitions list is empty");
		}
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
