package esa.mo.inttest.sch.provider;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.provider.MonitorSchedulesPublisher;
import org.ccsds.moims.mo.automation.schedule.provider.ScheduleInheritanceSkeleton;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.SchedulePatchOperations;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
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
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;

import esa.mo.inttest.Dumper;
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
	 * Set Schedules publisher to use.
	 * @param pub
	 */
	public void setSchPub(MonitorSchedulesPublisher pub) {
		schPub = pub;
	}
	
	protected InstStore getInstStore() {
		return schInsts;
	}
	
	private StatusRecordList addOrUpdate(StatusRecordList srl, InstanceState state, Time time, String comm) {
		StatusRecordList list = (null != srl) ? srl : new StatusRecordList();
		StatusRecord sr = null;
		for (int i = 0; (null == sr) && (i < list.size()); ++i) {
			if (list.get(i).getState() == state) {
				sr = list.get(i);
			}
		}
		if (null == sr) {
			sr = new StatusRecord(state, time, comm);
			list.add(sr);
		} else {
			sr.setTimeStamp(time);
			sr.setComment(comm);
		}
		return list;
	}
	
	private ScheduleStatusDetails createStat(ScheduleInstanceDetails inst) {
		ScheduleStatusDetails stat = new ScheduleStatusDetails();
		stat.setScheduleInstName(inst.getName());
		stat.setStatus(addOrUpdate(stat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "created"));
		stat.setScheduleItemStatuses(new ScheduleItemStatusDetailsList()); // mandatory
		return stat;
	}
	
	private ScheduleItemStatusDetails createItemStat(ScheduleItemInstanceDetails inst) {
		ScheduleItemStatusDetails stat = new ScheduleItemStatusDetails();
		stat.setScheduleItemInstName(inst.getScheduleItemInstName());
		stat.setStatus(addOrUpdate(stat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "created"));
		return stat;
	}
	
	private UpdateHeader createUpdateHeader(UpdateType ut) {
		EntityKey ek = new EntityKey();
		ek.setFirstSubKey(new Identifier("*"));
		ek.setSecondSubKey(0L);
		ek.setThirdSubKey(0L);
		ek.setFourthSubKey(0L);
		URI srcUri = new URI("uri://provider");
		UpdateHeader uh = new UpdateHeader();
		uh.setKey(ek);
		uh.setSourceURI(srcUri);
		uh.setTimestamp(new Time(System.currentTimeMillis()));
		uh.setUpdateType(ut);
		return uh;
	}
	
	private ObjectId createObjId(Long id) {
		ObjectKey ok = new ObjectKey();
		ok.setDomain(domain); // mandatory
		ok.setInstId(id);
		ObjectType ot = new ObjectType();
		ot.setArea(ScheduleInstanceDetails.AREA_SHORT_FORM);
		ot.setNumber(new UShort(ScheduleInstanceDetails.TYPE_SHORT_FORM));
		ot.setService(ScheduleInstanceDetails.SERVICE_SHORT_FORM);
		ot.setVersion(ScheduleInstanceDetails.AREA_VERSION);
		ObjectId oi = new ObjectId();
		oi.setKey(ok);
		oi.setType(ot);
		return oi;
	}
	
	protected void publish(UpdateType ut, Long instId, ScheduleStatusDetails stat) throws MALException, MALInteractionException {
		if (null != schPub) {
			UpdateHeaderList updHdrs = new UpdateHeaderList();
			updHdrs.add(createUpdateHeader(ut));
			ObjectIdList objIds = new ObjectIdList();
			objIds.add(createObjId(instId));
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
	public void submitSchedule(Long schDefId, Long schInstId, ScheduleInstanceDetails schInst,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{3}.submitSchedule(schDefId={0}, schInstId={1}, schInst)\n  schInst={2}",
				new Object[] { schDefId, schInstId, Dumper.schInst(schInst), Dumper.received(interaction) });
		// for nullpointers check we will
		if (null == schDefId) {
			throw new MALException("schedule definition id is null");
		}
		if (null == schInstId) {
			throw new MALException("schedule instance id is null");
		}
		if (null == schInst) {
			throw new MALException("schedule instance is null");
		}
		// create statuses
		ScheduleStatusDetails schStat = createStat(schInst);
		// create item statuses
		for (int i = 0; (null != schInst.getScheduleItems()) && (i < schInst.getScheduleItems().size()); ++i) {
			ScheduleItemInstanceDetails schItem = schInst.getScheduleItems().get(i);
			ScheduleItemStatusDetails schItemStat = createItemStat(schItem);
			schStat.getScheduleItemStatuses().add(schItemStat);
		}
		schInsts.add(schDefId, schInstId, schInst, schStat);
		// notify
		publish(UpdateType.CREATION, schInstId, schStat);
		LOG.log(Level.INFO, "{0}.submitSchedule() response: returning nothing", Dumper.sending(interaction));
	}

	/**
	 * Implements Schedule modification in the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#updateSchedule(java.lang.Long, org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void updateSchedule(Long schInstId, ScheduleInstanceDetails schInst, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updateSchedule(schInstId={0}, schInst)\n  schInst={1}",
				new Object[] { schInstId, Dumper.schInst(schInst), Dumper.received(interaction) });
		if (null == schInstId) {
			throw new MALException("schedule instance id is null");
		}
		if (null == schInst) {
			throw new MALException("schedule instance is null");
		}
		ScheduleStatusDetails schStat = schInsts.update(schInstId, schInst);
		schStat.setStatus(addOrUpdate(schStat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "modified"));
		// keep it simple - delete old item statuses and create new ones
		schStat.getScheduleItemStatuses().clear();
		for (int i = 0; (null != schInst.getScheduleItems()) && (i < schInst.getScheduleItems().size()); ++i) {
			ScheduleItemInstanceDetails schItem = schInst.getScheduleItems().get(i);
			ScheduleItemStatusDetails schItemStat = createItemStat(schItem);
			schStat.getScheduleItemStatuses().add(schItemStat);
		}
		publish(UpdateType.MODIFICATION, schInstId, schStat);
		LOG.log(Level.INFO, "{0}.updateSchedule() response: returning nothing", Dumper.sending(interaction));
	}

	/**
	 * Implements Schedule removal from the system.
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#removeSchedule(java.lang.Long, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void removeSchedule(Long schInstId, MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{1}.removeSchedule(schInstId={0})",
				new Object[] { schInstId, Dumper.received(interaction) });
		if (null == schInstId) {
			throw new MALException("schedule instance id is null");
		}
		ScheduleStatusDetails schStat = schInsts.remove(schInstId);
		schStat.setStatus(addOrUpdate(schStat.getStatus(), InstanceState.LAST_MODIFIED,
				new Time(System.currentTimeMillis()), "deleted"));
		for (int i = 0; (null != schStat.getScheduleItemStatuses()) && (i < schStat.getScheduleItemStatuses().size()); ++i) {
			ScheduleItemStatusDetails schItemStat = schStat.getScheduleItemStatuses().get(i);
			schItemStat.setStatus(addOrUpdate(schItemStat.getStatus(), InstanceState.LAST_MODIFIED,
					new Time(System.currentTimeMillis()), "deleted"));
		}
		publish(UpdateType.DELETION, schInstId, schStat);
		LOG.log(Level.INFO, "{0}.removeSchedule() response: returning nothing", Dumper.sending(interaction));
	}

	/**
	 * Implements Schedule patching.. how does it differ from updating?
	 * @see org.ccsds.moims.mo.automation.schedule.provider.ScheduleHandler#patchSchedule(java.lang.Long, java.lang.Long, org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails, org.ccsds.moims.mo.automation.schedule.structures.SchedulePatchOperations, java.lang.Long, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public void patchSchedule(Long schDefId, Long schInstId, ScheduleInstanceDetails schInst,
			SchedulePatchOperations patchOp, Long targetSchInstId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{5}.patchSchedule(schDefId={0}, schInstId={1}, schInst, patchOp, targetSchInstId={2})\n  schInst={3}\n  patchOp={4}",
				new Object[] { schDefId, schInstId, targetSchInstId, Dumper.schInst(schInst), Dumper.schPatchOp(patchOp), Dumper.received(interaction) });
		if (null == schDefId) {
			throw new MALException("schedule definition id is null");
		}
		if (null == schInstId) {
			throw new MALException("source schedule instance id is null");
		}
		if (null == schInst) {
			throw new MALException("source schedule instance is null");
		}
		if (null == patchOp) {
			throw new MALException("patch operation is null");
		}
		if (null == targetSchInstId) {
			throw new MALException("target schedule instance id is null");
		}
		ScheduleStatusDetails schStat = schInsts.patch(schDefId, schInstId, schInst, patchOp, targetSchInstId);
		publish(UpdateType.MODIFICATION, targetSchInstId, schStat);
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
			it.stat.setStatus(addOrUpdate(it.stat.getStatus(), InstanceState.DISTRIBUTED_FOR_EXECUTION,
					new Time(System.currentTimeMillis()), "started"));
			publish(UpdateType.UPDATE, id, it.stat);
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
			it.stat.setStatus(addOrUpdate(it.stat.getStatus(), InstanceState.PLANNED,
					new Time(System.currentTimeMillis()), "paused"));
			publish(UpdateType.UPDATE, id, it.stat);
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
			it.stat.setStatus(addOrUpdate(it.stat.getStatus(), InstanceState.SCHEDULED,
					new Time(System.currentTimeMillis()), "resumed"));
			publish(UpdateType.UPDATE, id, it.stat);
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
			it.stat.setStatus(addOrUpdate(it.stat.getStatus(), InstanceState.INVALID,
					new Time(System.currentTimeMillis()), "terminated"));
			publish(UpdateType.UPDATE, id, it.stat);
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
