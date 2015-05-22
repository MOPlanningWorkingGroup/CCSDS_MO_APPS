package esa.mo.inttest.sch.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

/**
 * Schedule instances storage for testing.
 */
public class InstStore {

	/**
	 * Schedule instance + definition + status info.
	 */
	protected static class Item {
		protected ScheduleInstanceDetails inst;
		protected ScheduleStatusDetails stat;
	}
	
	List<Item> items = new ArrayList<InstStore.Item>();
	
	protected Item findItem(Long id) {
		Item it = null;
		for (Item i: items) {
			if (id == i.inst.getId()) {
				it = i;
				break;
			}
		}
		return it;
	}
	
	/**
	 * List instance statuses by id.
	 * @param ids
	 * @return
	 * @throws MALException
	 */
	public ScheduleStatusDetailsList list(LongList ids) throws MALException {
		ScheduleStatusDetailsList stats = new ScheduleStatusDetailsList();
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule instance id[" + i + "] is null");
			}
			Item it = findItem(id);
			ScheduleStatusDetails stat = (null != it) ? it.stat : null;
			stats.add(stat);
		}
		return stats;
	}
	
	/**
	 * Add schedule instance.
	 * @param defId
	 * @param instId
	 * @param inst
	 * @param stat
	 * @throws MALException
	 */
	public void add(ScheduleInstanceDetails inst, ScheduleStatusDetails stat)
			throws MALException {
		Item it = findItem(inst.getId());
		if (null != it) {
			throw new MALException("schedule instance already exists, id: " + /*instId*/inst.getId());
		}
		it = new Item();
		it.inst = inst;
		it.stat = stat;
		items.add(it);
	}
	
	/**
	 * Update (replace) schedule instance.
	 * @param instId
	 * @param inst
	 * @return
	 * @throws MALException
	 */
	public ScheduleStatusDetails update(ScheduleInstanceDetails inst) throws MALException {
		Item it = findItem(inst.getId());
		if (null == it) {
			throw new MALException("schedule instance does not exist, id: " + inst.getId());
		}
		it.inst = inst;
		return it.stat;
	}
	
	/**
	 * Remove schedule instance.
	 * @param instId
	 * @return
	 * @throws MALException
	 */
	public ScheduleStatusDetails remove(Long instId) throws MALException {
		Item it = findItem(instId);
		if (null == it) {
			throw new MALException("schedule instance does not exist, id: " + instId);
		}
		items.remove(it);
		return it.stat;
	}
	
	protected ArgumentValue findArg(Identifier name, ArgumentValueList list) {
		ArgumentValue av = null;
		for (int i = 0; (null != list) && (i < list.size()); ++i) {
			ArgumentValue av2 = list.get(i);
			if (name.equals(av2.getArgDefName())) {
				av = av2;
				break;
			}
		}
		return av;
	}
	
	protected TimingDetails findTiming(TriggerName name, TimingDetailsList timings) {
		TimingDetails t = null;
		for (int i = 0; (null != timings) && (i < timings.size()); ++i) {
			TimingDetails t2 = timings.get(i);
			if (name.equals(t2.getTriggerName())) {
				t = t2;
				break;
			}
		}
		return t;
	}
	
	protected ScheduleItemInstanceDetails findItem(Long id, ScheduleItemInstanceDetailsList items) {
		ScheduleItemInstanceDetails it = null;
		for (int i = 0; (items != null) && (i < items.size()); ++i) {
			ScheduleItemInstanceDetails it2 = items.get(i);
			if (id == it2.getId()) {
				it = it2;
				break;
			}
		}
		return it;
	}
	
	/**
	 * Patch schedule instance (whatever that means).
	 * @param defId
	 * @param srcId
	 * @param schInst
	 * @param patchOp
	 * @param targetId
	 * @return
	 * @throws MALException
	 */
	public ScheduleStatusDetailsList patch(ScheduleInstanceDetailsList remove, ScheduleInstanceDetailsList update,
			ScheduleInstanceDetailsList add) throws MALException {
		Map<Long, ScheduleStatusDetails> mods = new HashMap<Long, ScheduleStatusDetails>();
		// updates
		for (int i = 0; (null != update) && (i < update.size()); ++i) {
			ScheduleInstanceDetails srcSch = update.get(i);
			if (null == srcSch) {
				throw new MALException("update schedule instance[" + i + "] is null");
			}
			if (null == srcSch.getId()) {
				throw new MALException("update schedule instance[" + i + "].id is null");
			}
			Item it = findItem(srcSch.getId());
			if (null == it) {
				throw new MALException("no such schedule to update, id: " + srcSch.getId());
			}
			// update fields
			if (null != srcSch.getSchDefId()) {
				it.inst.setSchDefId(srcSch.getSchDefId());
				mods.put(it.inst.getId(), it.stat);
			}
			if (null != srcSch.getComment()) {
				it.inst.setComment(srcSch.getComment());
				mods.put(it.inst.getId(), it.stat);
			}
			for (int j = 0; (null != srcSch.getArgumentValues()) && (j < srcSch.getArgumentValues().size()); ++j) {
				ArgumentValue argVal = srcSch.getArgumentValues().get(j);
				if (null == argVal) {
					throw new MALException("update schedule instance[" + i + "].argVal[" + j + "] is null");
				}
				ArgumentValue trgArg = findArg(argVal.getArgDefName(), it.inst.getArgumentValues());
				if (null == trgArg) {
					throw new MALException("no such argument to update, id: " + srcSch.getId() + ", arg: " + argVal.getArgDefName());
				}
				// no null check here
				trgArg.setValue(argVal.getValue());
				mods.put(it.inst.getId(), it.stat);
			}
			for (int j = 0; (null != srcSch.getTimingConstraints()) && (j < srcSch.getTimingConstraints().size()); ++j) {
				TimingDetails tim = srcSch.getTimingConstraints().get(j);
				if (null == tim) {
					throw new MALException("update schedule instance[" + i + "].timing[" + j + "] is null");
				}
				TimingDetails trgTim = findTiming(tim.getTriggerName(), it.inst.getTimingConstraints());
				if (null == trgTim) {
					throw new MALException("no such timing to upate, id: " + srcSch.getId() + ", trigger: " + tim.getTriggerName());
				}
				// no null checks
				trgTim.setEarliestOffset(tim.getEarliestOffset());
				trgTim.setEventTrigger(tim.getEventTrigger());
				trgTim.setLatestOffset(tim.getLatestOffset());
				trgTim.setRepeat(tim.getRepeat());
				trgTim.setSeparation(tim.getSeparation());
				trgTim.setTimeTrigger(tim.getTimeTrigger());
				mods.put(it.inst.getId(), it.stat);
			}
			for (int j = 0; (null != srcSch.getScheduleItems()) && (j < srcSch.getScheduleItems().size()); ++j) {
				ScheduleItemInstanceDetails item = srcSch.getScheduleItems().get(j);
				if (null == item) {
					throw new MALException("update schedule instance[" + i + "].item[" + j + "] is null");
				}
				ScheduleItemInstanceDetails trgIt = findItem(item.getId(), srcSch.getScheduleItems());
				if (null == trgIt) {
					throw new MALException("no such item to update, schId: " + srcSch.getId() + ", itemId: " + item.getId());
				}
				// dont change schedule id
				// no null checks
				trgIt.setArgumentTypes(item.getArgumentTypes());
				trgIt.setArgumentValues(item.getArgumentValues());
				trgIt.setDelegateItem(item.getDelegateItem());
				trgIt.setTimingConstraints(item.getTimingConstraints());
				mods.put(it.inst.getId(), it.stat);
			}
		}
		// removals
		// additions
		// convert modifications list
		ScheduleStatusDetailsList schStats = new ScheduleStatusDetailsList();
		for (ScheduleStatusDetails schStat: mods.values()) {
			schStats.add(schStat);
		}
		return schStats;
	}
	
	// test support
	protected void setStatus(Long id, ScheduleStatusDetails stat) {
		Item it = findItem(id);
		if (null != it) {
			it.stat = stat;
		}
	}
}
