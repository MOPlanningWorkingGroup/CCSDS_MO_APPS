package esa.mo.inttest.sch.provider;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;

import esa.mo.inttest.Util;
import esa.mo.inttest.sch.provider.InstStore.SchItem;

/**
 * Patch (incremental change) operation logic.
 */
public class Patcher {

	private InstStore store;
	
	public Patcher(InstStore store) {
		this.store = store;
	}
	
	protected void removeArgs(ArgumentValueList src, ArgumentValueList trg) {
		for (int j = 0; j < src.size(); ++j) {
			ArgumentValue srcArg = src.get(j);
			ArgumentValue trgArg = Util.findArg(srcArg.getArgDefName(), trg);
			trg.remove(trgArg);
		}
	}
	
	protected void removeTimings(TimingDetailsList src, TimingDetailsList trg) {
		for (int j = 0; j < src.size(); ++j) {
			TimingDetails srcTim = src.get(j);
			TimingDetails trgTim = Util.findTiming(srcTim.getTriggerName(), trg);
			trg.remove(trgTim);
		}
	}
	
	protected void removeItems(ScheduleItemInstanceDetailsList src, ScheduleItemInstanceDetailsList trg) {
		for (int j = 0; j < src.size(); ++j) {
			ScheduleItemInstanceDetails srcItem = src.get(j);
			ScheduleItemInstanceDetails trgItem = Util.findItem(srcItem.getId(), trg);
			trg.remove(trgItem);
			store.removeItem(srcItem.getId());
		}
	}
	
	protected void patchRemove(Map<Long, ScheduleStatusDetails> mods,
			ScheduleInstanceDetails srcSch, SchItem it) {
		// remove field values
		// can't remove def id?
		if (null != srcSch.getComment()) {
			it.sch.setComment(null);
			mods.put(it.sch.getId(), it.stat);
		}
		if (null != srcSch.getArgumentValues() && !srcSch.getArgumentValues().isEmpty()) {
			// have arg to remove
			removeArgs(srcSch.getArgumentValues(), it.sch.getArgumentValues());
			mods.put(it.sch.getId(), it.stat);
		}
		if (null != srcSch.getTimingConstraints() && !srcSch.getTimingConstraints().isEmpty()) {
			// have timing to remove
			removeTimings(srcSch.getTimingConstraints(), it.sch.getTimingConstraints());
			mods.put(it.sch.getId(), it.stat);
		}
		if (null != srcSch.getScheduleItems() && !srcSch.getScheduleItems().isEmpty()) {
			// have items to remove
			removeItems(srcSch.getScheduleItems(), it.sch.getScheduleItems());
			mods.put(it.sch.getId(), it.stat);
		}
	}

	/**
	 * Update argument values.
	 * @param idx
	 * @param src
	 * @param trg
	 * @throws MALException
	 */
	protected void updateArgs(ArgumentValueList src, ArgumentValueList trg) {
		for (int j = 0; j < src.size(); ++j) {
			ArgumentValue argVal = src.get(j);
			ArgumentValue trgArg = Util.findArg(argVal.getArgDefName(), trg);
			// no null check here
			trgArg.setValue(argVal.getValue());
		}
	}

	/**
	 * Update timings.
	 * @param idx
	 * @param src
	 * @param trg
	 * @throws MALException
	 */
	protected void updateTimings(TimingDetailsList src, TimingDetailsList trg) {
		for (int j = 0; j < src.size(); ++j) {
			TimingDetails tim = src.get(j);
			TimingDetails trgTim = Util.findTiming(tim.getTriggerName(), trg);
			// no null checks
			trgTim.setEarliestOffset(tim.getEarliestOffset());
			trgTim.setEventTrigger(tim.getEventTrigger());
			trgTim.setLatestOffset(tim.getLatestOffset());
			trgTim.setRepeat(tim.getRepeat());
			trgTim.setSeparation(tim.getSeparation());
			trgTim.setTimeTrigger(tim.getTimeTrigger());
		}
	}

	/**
	 * Update schedule items.
	 * @param idx
	 * @param src
	 * @param trg
	 * @throws MALException
	 */
	protected void updateItems(ScheduleItemInstanceDetailsList src, ScheduleItemInstanceDetailsList trg) {
		for (int j = 0; j < src.size(); ++j) {
			ScheduleItemInstanceDetails item = src.get(j);
			ScheduleItemInstanceDetails trgIt = Util.findItem(item.getId(), trg);
			// dont change schedule id
			// no null checks
			trgIt.setArgumentTypes(item.getArgumentTypes());
			trgIt.setArgumentValues(item.getArgumentValues());
			trgIt.setDelegateItem(item.getDelegateItem());
			trgIt.setTimingConstraints(item.getTimingConstraints());
		}
	}

	protected void patchUpdate(Map<Long, ScheduleStatusDetails> mods,
			ScheduleInstanceDetails srcSch, SchItem it) {
		// update fields
		// can't update def id
//		if (null != srcSch.getSchDefId()) {
//			it.sch.setSchDefId(srcSch.getSchDefId());
//			mods.put(it.sch.getId(), it.stat);
//		}
		if (null != srcSch.getComment()) {
			it.sch.setComment(srcSch.getComment());
			mods.put(it.sch.getId(), it.stat);
		}
		if (null != srcSch.getArgumentValues() && !srcSch.getArgumentValues().isEmpty()) {
			// have something to update
			updateArgs(srcSch.getArgumentValues(), it.sch.getArgumentValues());
			mods.put(it.sch.getId(), it.stat);
		}
		if (null != srcSch.getTimingConstraints() && !srcSch.getTimingConstraints().isEmpty()) {
			// have something to update
			updateTimings(srcSch.getTimingConstraints(), it.sch.getTimingConstraints());
			mods.put(it.sch.getId(), it.stat);
		}
		if (null != srcSch.getScheduleItems() && !srcSch.getScheduleItems().isEmpty()) {
			// have something to update
			updateItems(srcSch.getScheduleItems(), it.sch.getScheduleItems());
			mods.put(it.sch.getId(), it.stat);
		}
	}

	protected void addArgs(ArgumentValueList src, ArgumentValueList trg) {
		for (int j = 0; j < src.size(); ++j) {
			ArgumentValue srcArg = src.get(j);
			trg.add(srcArg);
		}
	}

	protected void addTimings(TimingDetailsList src, TimingDetailsList trg) {
		for (int j = 0; j < src.size(); ++j) {
			TimingDetails srcTim = src.get(j);
			trg.add(srcTim);
		}
	}

	protected void addItems(ScheduleItemInstanceDetailsList src, ScheduleItemInstanceDetailsList trg) {
		for (int j = 0; j < src.size(); ++j) {
			ScheduleItemInstanceDetails srcItem = src.get(j);
			trg.add(srcItem);
		}
	}

	protected void patchAdd(Map<Long, ScheduleStatusDetails> mods, ScheduleInstanceDetails srcSch,
			SchItem it) {
		// no fields to add
		if (null != srcSch.getArgumentValues() && !srcSch.getArgumentValues().isEmpty()) {
			if (null == it.sch.getArgumentValues()) {
				it.sch.setArgumentValues(new ArgumentValueList());
			}
			// have args to add
			addArgs(srcSch.getArgumentValues(), it.sch.getArgumentValues());
			mods.put(it.sch.getId(), it.stat);
		}
		if (null != srcSch.getTimingConstraints() && !srcSch.getTimingConstraints().isEmpty()) {
			if (null == it.sch.getTimingConstraints()) {
				it.sch.setTimingConstraints(new TimingDetailsList());
			}
			// have timing to add
			addTimings(srcSch.getTimingConstraints(), it.sch.getTimingConstraints());
			mods.put(it.sch.getId(), it.stat);
		}
		if (null != srcSch.getScheduleItems() && !srcSch.getScheduleItems().isEmpty()) {
			if (null == it.sch.getScheduleItems()) {
				it.sch.setScheduleItems(new ScheduleItemInstanceDetailsList());
			}
			// have items to add
			addItems(srcSch.getScheduleItems(), it.sch.getScheduleItems());
			mods.put(it.sch.getId(), it.stat);
		}
	}

	public ScheduleStatusDetailsList patch(ScheduleInstanceDetailsList changes) {
		// TODO changed item statuses
		Map<Long, ScheduleStatusDetails> mods = new HashMap<Long, ScheduleStatusDetails>();
		// removals
		for (int i = 0; i < changes.size(); ++i) {
			ScheduleInstanceDetails srcSch = changes.get(i);
			if (ScheduleType.INCREMENT_REMOVE == srcSch.getScheduleType()) {
				SchItem it = store.findSchItem(srcSch.getId());
				patchRemove(mods, srcSch, it);
			}
		}
		// updates
		for (int i = 0; i < changes.size(); ++i) {
			ScheduleInstanceDetails srcSch = changes.get(i);
			if (ScheduleType.INCREMENT_UPDATE == srcSch.getScheduleType()) {
				SchItem it = store.findSchItem(srcSch.getId());
				patchUpdate(mods, srcSch, it);
			}
		}
		// additions
		for (int i = 0; i < changes.size(); ++i) {
			ScheduleInstanceDetails srcSch = changes.get(i);
			if (ScheduleType.INCREMENT_ADD == srcSch.getScheduleType()) {
				SchItem it = store.findSchItem(srcSch.getId());
				patchAdd(mods, srcSch, it);
			}
		}
		// convert modifications list
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (ScheduleStatusDetails schStat: mods.values()) {
			schStats.add(schStat);
		}
		return schStats;
	}
}
