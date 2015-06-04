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
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * Schedule instances storage for testing.
 */
public class InstStore {

	/**
	 * Schedule instance + status info.
	 */
	public static final class SchItem {
		
		protected ScheduleInstanceDetails sch;
		protected ScheduleStatusDetails stat;
		
		public SchItem(ScheduleInstanceDetails inst, ScheduleStatusDetails stat) {
			this.sch = inst;
			this.stat = stat;
		}
		
		public ScheduleInstanceDetails getSch() {
			return sch;
		}
		
		public ScheduleStatusDetails getStat() {
			return stat;
		}
	}
	
	/**
	 * Schedule item instance + status.
	 */
	public static final class ItemItem {
		
		protected ScheduleItemInstanceDetails item;
		protected ScheduleItemStatusDetails stat;
		
		public ItemItem(ScheduleItemInstanceDetails inst, ScheduleItemStatusDetails stat) {
			this.item = inst;
			this.stat = stat;
		}
		
		public ScheduleItemInstanceDetails getItem() {
			return item;
		}
		
		public ScheduleItemStatusDetails getStat() {
			return stat;
		}
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
			ScheduleInstanceDetails inst = (null != it) ? it.sch : null;
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
	public ScheduleStatusDetailsList listStats(LongList ids) {
		ScheduleStatusDetailsList stats = new ScheduleStatusDetailsList();
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			SchItem it = findSchItem(id);
			ScheduleStatusDetails stat = (null != it) ? it.stat : null;
			stats.add(stat);
		}
		return stats;
	}
	
	protected void addItem(ScheduleItemInstanceDetails item, ScheduleItemStatusDetails stat) {
		items.put(item.getId(), new ItemItem(item, stat));
	}
	
	protected void addItems(ScheduleItemInstanceDetailsList items, ScheduleItemStatusDetailsList stats) {
		for (int i = 0; (null != items) && (i < items.size()); ++i) {
			ScheduleItemInstanceDetails item = items.get(i);
			ScheduleItemStatusDetails stat = stats.get(i);
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
	public void add(ScheduleInstanceDetails sch, ScheduleStatusDetails stat) {
		scheds.put(sch.getId(), new SchItem(sch, stat));
		addItems(sch.getScheduleItems(), stat.getScheduleItemStatuses());
	}
	
	protected void removeItem(Long id) {
		items.remove(id);
	}
	
	protected void removeOldItems(ScheduleInstanceDetails schOld, ScheduleInstanceDetails schNew) {
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
			ScheduleItemStatusDetails stat = stats.get(i);
			ItemItem it = findItemItem(item.getId());
			if (null == it) { // add
				addItem(item, stat);
			} else { // replace
				it.item = item;
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
		removeOldItems(it.sch, inst);
		it.sch = inst;
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
			removeItems(it.sch.getScheduleItems());
		}
		return it;
	}
}
