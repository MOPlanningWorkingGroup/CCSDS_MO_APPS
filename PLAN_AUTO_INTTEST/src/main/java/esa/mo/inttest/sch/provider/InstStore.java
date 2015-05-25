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
	 * List instances by id.
	 * @param ids
	 * @return
	 * @throws MALException
	 */
	public ScheduleInstanceDetailsList listInsts(LongList ids) throws MALException {
		ScheduleInstanceDetailsList insts = new ScheduleInstanceDetailsList();
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule instance id[" + i + "] is null");
			}
			Item it = findItem(id);
			ScheduleInstanceDetails inst = (null != it) ? it.inst : null;
			insts.add(inst);
		}
		return insts;
	}
	
	/**
	 * List instance statuses by id.
	 * @param ids
	 * @return
	 * @throws MALException
	 */
	public ScheduleStatusDetailsList listStats(LongList ids) throws MALException {
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
	
	protected void updateArgs(int idx, ArgumentValueList src, ArgumentValueList trg) throws MALException {
		for (int j = 0; j < src.size(); ++j) {
			ArgumentValue argVal = src.get(j);
			if (null == argVal) {
				throw new MALException("update schedule instance[" + idx + "].argVal[" + j + "] is null");
			}
			ArgumentValue trgArg = findArg(argVal.getArgDefName(), trg);
			if (null == trgArg) {
				throw new MALException("no such argument to update, instance[" + idx + "].arg[" + j + "]: " +argVal.getArgDefName());
			}
			// no null check here
			trgArg.setValue(argVal.getValue());
		}
	}
	
	protected void updateTimings(int idx, TimingDetailsList src, TimingDetailsList trg) throws MALException {
		for (int j = 0; (j < src.size()); ++j) {
			TimingDetails tim = src.get(j);
			if (null == tim) {
				throw new MALException("update schedule instance[" + idx + "].timing[" + j + "] is null");
			}
			TimingDetails trgTim = findTiming(tim.getTriggerName(), trg);
			if (null == trgTim) {
				throw new MALException("no such timing to update, instance[" + idx + "].timing[" + j + "]: " + tim.getTriggerName());
			}
			// no null checks
			trgTim.setEarliestOffset(tim.getEarliestOffset());
			trgTim.setEventTrigger(tim.getEventTrigger());
			trgTim.setLatestOffset(tim.getLatestOffset());
			trgTim.setRepeat(tim.getRepeat());
			trgTim.setSeparation(tim.getSeparation());
			trgTim.setTimeTrigger(tim.getTimeTrigger());
		}
	}
	
	protected void updateItems(int idx, ScheduleItemInstanceDetailsList src,
			ScheduleItemInstanceDetailsList trg) throws MALException {
		for (int j = 0; j < src.size(); ++j) {
			ScheduleItemInstanceDetails item = src.get(j);
			if (null == item) {
				throw new MALException("update schedule instance[" + idx + "].item[" + j + "] is null");
			}
			ScheduleItemInstanceDetails trgIt = findItem(item.getId(), trg);
			if (null == trgIt) {
				throw new MALException("no such item to update, instance[" + idx + "].item[" + j + "]: " + item.getId());
			}
			// dont change schedule id
			// no null checks
			trgIt.setArgumentTypes(item.getArgumentTypes());
			trgIt.setArgumentValues(item.getArgumentValues());
			trgIt.setDelegateItem(item.getDelegateItem());
			trgIt.setTimingConstraints(item.getTimingConstraints());
		}
	}
	
	/**
	 * Patch schedule instances - update fields, add/remove elements from lists.
	 * @param remove
	 * @param update
	 * @param add
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
			Item it = findItem(srcSch.getId()); // storage item, not schedule item
			if (null == it) {
				throw new MALException("no such schedule to update, instance[" + i + "]: " + srcSch.getId());
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
			if (null != srcSch.getArgumentValues() && !srcSch.getArgumentValues().isEmpty()) {
				if (null == it.inst.getArgumentValues()) {
					it.inst.setArgumentValues(new ArgumentValueList());
				}
				// have something to update
				updateArgs(i, srcSch.getArgumentValues(), it.inst.getArgumentValues());
				mods.put(it.inst.getId(), it.stat);
			}
			if (null != srcSch.getTimingConstraints() && !srcSch.getTimingConstraints().isEmpty()) {
				if (null == it.inst.getTimingConstraints()) {
					it.inst.setTimingConstraints(new TimingDetailsList());
				}
				// have something to update
				updateTimings(i, srcSch.getTimingConstraints(), it.inst.getTimingConstraints());
				mods.put(it.inst.getId(), it.stat);
			}
			if (null != srcSch.getScheduleItems() && !srcSch.getScheduleItems().isEmpty()) {
				if (null == it.inst.getScheduleItems()) {
					it.inst.setScheduleItems(new ScheduleItemInstanceDetailsList());
				}
				// have something to update
				updateItems(i, srcSch.getScheduleItems(), it.inst.getScheduleItems());
				mods.put(it.inst.getId(), it.stat);
			}
		}
		// removals
		for (int i = 0; (null != remove) && (i < remove.size()); ++i) {
			ScheduleInstanceDetails srcSch = remove.get(i);
			if (null == srcSch) {
				throw new MALException("remove schedule instance[" + i + "] is null");
			}
			if (null == srcSch.getId()) {
				throw new MALException("remove schedule intance[" + i + "].id is null");
			}
			Item it = findItem(srcSch.getId());
			if (null == it) {
				throw new MALException("no such schedule to remove from, instance[" + i + "]: " + srcSch.getId());
			}
			// remove field values
			// can't remove def id
			if (null != srcSch.getComment()) {
				it.inst.setComment(null);
				mods.put(it.inst.getId(), it.stat);
			}
			if (null != srcSch.getArgumentValues() && !srcSch.getArgumentValues().isEmpty()) {
				if (null == it.inst.getArgumentValues()) {
					it.inst.setArgumentValues(new ArgumentValueList());
				}
				// have arg to remove
				for (int j = 0; j < srcSch.getArgumentValues().size(); ++j) {
					ArgumentValue srcArg = srcSch.getArgumentValues().get(j);
					if (null == srcArg) {
						throw new MALException("remove schedule instance[" + i + "].argVal[" + j + "] is null");
					}
					ArgumentValue trgArg = findArg(srcArg.getArgDefName(), it.inst.getArgumentValues());
					if (null == trgArg) {
						throw new MALException("no such argument to remove, instance[" + i + "].arg[" + j + "]: " + srcArg.getArgDefName());
					}
					it.inst.getArgumentValues().remove(trgArg);
				}
				mods.put(it.inst.getId(), it.stat);
			}
			if (null != srcSch.getTimingConstraints() && !srcSch.getTimingConstraints().isEmpty()) {
				if (null == it.inst.getTimingConstraints()) {
					it.inst.setTimingConstraints(new TimingDetailsList());
				}
				// have timing to remove
				for (int j = 0; j < srcSch.getTimingConstraints().size(); ++j) {
					TimingDetails srcTim = srcSch.getTimingConstraints().get(j);
					if (null == srcTim) {
						throw new MALException("remove schedule instance[" + i + "].timing[" + j +"] is null");
					}
					TimingDetails trgTim = findTiming(srcTim.getTriggerName(), it.inst.getTimingConstraints());
					if (null == trgTim) {
						throw new MALException("no such timing to remove, instance[" + i + "].timing[" + j + "]: " + srcTim.getTriggerName());
					}
					it.inst.getTimingConstraints().remove(trgTim);
				}
				mods.put(it.inst.getId(), it.stat);
			}
			if (null != srcSch.getScheduleItems() && !srcSch.getScheduleItems().isEmpty()) {
				if (null == it.inst.getScheduleItems()) {
					it.inst.setScheduleItems(new ScheduleItemInstanceDetailsList());
				}
				// have items to remove
				for (int j = 0; j < srcSch.getScheduleItems().size(); ++j) {
					ScheduleItemInstanceDetails srcItem = srcSch.getScheduleItems().get(j);
					if (null == srcItem) {
						throw new MALException("remove schedule instance[" + i + "].item[" + j + "] is null");
					}
					ScheduleItemInstanceDetails trgItem = findItem(srcItem.getId(), it.inst.getScheduleItems());
					if (null == trgItem) {
						throw new MALException("no such item to remove, instance[" + i + "].item[" + j + "]: " + srcItem.getId());
					}
					it.inst.getScheduleItems().remove(trgItem);
				}
				mods.put(it.inst.getId(), it.stat);
			}
		}
		// additions
		for (int i = 0; (null != add) && (i < add.size()); ++i) {
			ScheduleInstanceDetails srcSch = add.get(i);
			if (null == srcSch) {
				throw new MALException("add schedule instance[" + i + "] is null");
			}
			if (null == srcSch.getId()) {
				throw new MALException("add schedule instance[" + i + "].id is null");
			}
			Item it = findItem(srcSch.getId());
			if (null == it) {
				throw new MALException("no such schedule to add to, instance[" + i + "]: " + srcSch.getId());
			}
			// no fields to add
			if (null != srcSch.getArgumentValues() && !srcSch.getArgumentValues().isEmpty()) {
				if (null == it.inst.getArgumentValues()) {
					it.inst.setArgumentValues(new ArgumentValueList());
				}
				// have args to add
				for (int j = 0; i < srcSch.getArgumentValues().size(); ++j) {
					ArgumentValue srcArg = srcSch.getArgumentValues().get(j);
					if (null == srcArg) {
						throw new MALException("add schedule instance[" + i + "].argVal[" + j + "] is null");
					}
					ArgumentValue trgArg = findArg(srcArg.getArgDefName(), it.inst.getArgumentValues());
					if (null != trgArg) {
						throw new MALException("schedule already has argVal, instance[" + i + "].argVal[" + j + "]: " + srcArg.getArgDefName());
					}
					it.inst.getArgumentValues().add(srcArg);
				}
				mods.put(it.inst.getId(), it.stat);
			}
			if (null != srcSch.getTimingConstraints() && !srcSch.getTimingConstraints().isEmpty()) {
				if (null == it.inst.getTimingConstraints()) {
					it.inst.setTimingConstraints(new TimingDetailsList());
				}
				// have timing to add
				for (int j = 0; j < srcSch.getTimingConstraints().size(); ++j) {
					TimingDetails srcTim = srcSch.getTimingConstraints().get(j);
					if (null == srcTim) {
						throw new MALException("add schedule instance[" + i + "].timing[" + j +"] is null");
					}
					TimingDetails trgTim = findTiming(srcTim.getTriggerName(), it.inst.getTimingConstraints());
					if (null != trgTim) {
						throw new MALException("schedule already has timing, instance[" + i + "].timing[" + j + "]: " + srcTim.getTriggerName());
					}
					it.inst.getTimingConstraints().add(trgTim);
				}
				mods.put(it.inst.getId(), it.stat);
			}
			if (null != srcSch.getScheduleItems() && !srcSch.getScheduleItems().isEmpty()) {
				if (null == it.inst.getScheduleItems()) {
					it.inst.setScheduleItems(new ScheduleItemInstanceDetailsList());
				}
				// have items to add
				for (int j = 0; j < srcSch.getScheduleItems().size(); ++j) {
					ScheduleItemInstanceDetails srcItem = srcSch.getScheduleItems().get(j);
					if (null == srcItem) {
						throw new MALException("add schedule instance[" + i + "].item[" + j + "] is null");
					}
					ScheduleItemInstanceDetails trgItem = findItem(srcItem.getId(), it.inst.getScheduleItems());
					if (null != trgItem) {
						throw new MALException("already has item, instance[" + i + "].item[" + j + "]: " + srcItem.getId());
					}
					it.inst.getScheduleItems().add(trgItem);
				}
				mods.put(it.inst.getId(), it.stat);
			}
		}
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
