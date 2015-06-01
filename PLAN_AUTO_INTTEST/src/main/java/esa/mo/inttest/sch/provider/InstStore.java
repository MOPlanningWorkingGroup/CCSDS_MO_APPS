package esa.mo.inttest.sch.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetailsList;
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
	 * Schedule instance + status info.
	 */
	public static final class SchItem {
		public ScheduleInstanceDetails inst;
		public ScheduleStatusDetails stat;
	}
	/**
	 * Schedule item instance + status.
	 */
	public static final class ItemItem {
		public ScheduleItemInstanceDetails inst;
		public ScheduleItemStatusDetails stat;
	}
	
	Map<Long, SchItem> scheds = new HashMap<Long, SchItem>();
	Map<Long, ItemItem> items = new HashMap<Long, InstStore.ItemItem>();
	
	public SchItem findSchItem(Long id) {
		return scheds.get(id);
	}
	
	public ItemItem findItemItem(Long id) {
		return items.get(id);
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
			SchItem it = findSchItem(id);
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
			SchItem it = findSchItem(id);
			ScheduleStatusDetails stat = (null != it) ? it.stat : null;
			stats.add(stat);
		}
		return stats;
	}
	
	protected void addItem(ScheduleItemInstanceDetails item, ScheduleItemStatusDetails stat) {
		ItemItem it = new ItemItem();
		it.inst = item;
		it.stat = stat;
		items.put(item.getId(), it);
	}
	
	protected void addItems(ScheduleItemInstanceDetailsList items, ScheduleItemStatusDetailsList stats) {
		for (int i = 0; (null != items) && (i < items.size()); ++i) {
			ScheduleItemInstanceDetails item = items.get(i);
			ScheduleItemStatusDetails stat = stats.get(i); // FIXME hopefully stat matches item
			addItem(item, stat);
		}
	}
	
	/**
	 * Add schedule instance.
	 * @param defId
	 * @param instId
	 * @param inst
	 * @param stat
	 * @throws MALException
	 */
	public void add(ScheduleInstanceDetails inst, ScheduleStatusDetails stat) {
		SchItem it = new SchItem();
		it.inst = inst;
		it.stat = stat;
		scheds.put(inst.getId(), it);
		addItems(inst.getScheduleItems(), stat.getScheduleItemStatuses());
	}
	
	protected void removeItem(Long id) {
		items.remove(id);
	}
	
	protected void removeItems(ScheduleInstanceDetails schOld, ScheduleInstanceDetails schNew) {
		List<Long> newItems = new ArrayList<Long>();
		// create lookup list of new items
		for (int i = 0; (null != schNew.getScheduleItems()) && (i < schNew.getScheduleItems().size()); ++i) {
			ScheduleItemInstanceDetails item = schNew.getScheduleItems().get(i);
			newItems.add(item.getId());
		}
		// remove items not present in new list
		for (int i = 0; (null != schOld.getScheduleItems()) && (i < schOld.getScheduleItems().size()); ++i) {
			ScheduleItemInstanceDetails item = schOld.getScheduleItems().get(i);
			if (!newItems.contains(item.getId())) {
				removeItem(item.getId());
			}
		}
	}
	
	protected void updateItems(ScheduleItemInstanceDetailsList items, ScheduleItemStatusDetailsList stats) {
		for (int i = 0; (null != items) && (i < items.size()); ++i) {
			ScheduleItemInstanceDetails item = items.get(i);
			ScheduleItemStatusDetails stat = stats.get(i); // FIXME hopefully stat matches item
			ItemItem it = findItemItem(item.getId());
			if (null == it) { // add
				addItem(item, stat);
			} else { // replace
				it.inst = item;
				it.stat = stat;
			}
		}
	}
	
	/**
	 * Update (replace) schedule instance.
	 * @param instId
	 * @param inst
	 * @return
	 * @throws MALException
	 */
	public void update(ScheduleInstanceDetails inst) {
		SchItem it = findSchItem(inst.getId());
		removeItems(it.inst, inst);
		it.inst = inst;
		updateItems(inst.getScheduleItems(), it.stat.getScheduleItemStatuses());
	}
	
	protected void removeItems(ScheduleItemInstanceDetailsList items) {
		for (int i = 0; (null != items) && (i < items.size()); ++i) {
			ScheduleItemInstanceDetails item = items.get(i);
			this.items.remove(item.getId());
		}
	}
	/**
	 * Remove schedule instance.
	 * @param instId
	 * @return
	 * @throws MALException
	 */
	public SchItem remove(Long id) {
		SchItem it = scheds.remove(id);
		if (null != it) {
			removeItems(it.inst.getScheduleItems());
		}
		return it;
	}
	
	/**
	 * Find argument by name.
	 * @param name
	 * @param list
	 * @return
	 */
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
	
	/**
	 * Find timing by name.
	 * @param name
	 * @param timings
	 * @return
	 */
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
	
	/**
	 * Find item by id.
	 * @param id
	 * @param items
	 * @return
	 */
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
	 * Update argument values.
	 * @param idx
	 * @param src
	 * @param trg
	 * @throws MALException
	 */
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
	
	/**
	 * Update timings.
	 * @param idx
	 * @param src
	 * @param trg
	 * @throws MALException
	 */
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
	
	/**
	 * Update schedule items.
	 * @param idx
	 * @param src
	 * @param trg
	 * @throws MALException
	 */
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
			SchItem it = findSchItem(srcSch.getId()); // storage item, not schedule item
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
			SchItem it = findSchItem(srcSch.getId());
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
			SchItem it = findSchItem(srcSch.getId());
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
}
