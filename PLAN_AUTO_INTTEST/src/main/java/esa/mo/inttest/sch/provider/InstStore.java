package esa.mo.inttest.sch.provider;

import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.automation.schedule.structures.PatchOperation;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.SchedulePatchOperations;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.LongList;

public class InstStore {

	protected static class Item {
		protected Long defId;
		protected Long instId;
		protected ScheduleInstanceDetails inst;
		protected ScheduleStatusDetails stat;
	}
	
	List<Item> items = new ArrayList<InstStore.Item>();
	
	protected Item findItem(Long id) {
		Item it = null;
		for (Item i: items) {
			if (id == i.instId) {
				it = i;
				break;
			}
		}
		return it;
	}
	
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
	
	public void add(Long defId, Long instId, ScheduleInstanceDetails inst, ScheduleStatusDetails stat)
			throws MALException {
		Item it = findItem(instId);
		if (null != it) {
			throw new MALException("schedule instance already exists, id: " + instId);
		}
		it = new Item();
		it.defId = defId;
		it.instId = instId;
		it.inst = inst;
		it.stat = stat;
		items.add(it);
	}
	
	public ScheduleStatusDetails update(Long instId, ScheduleInstanceDetails inst) throws MALException {
		Item it = findItem(instId);
		if (null == it) {
			throw new MALException("schedule instance does not exist, id: " + instId);
		}
		it.inst = inst;
		return it.stat;
	}
	
	public ScheduleStatusDetails remove(Long instId) throws MALException {
		Item it = findItem(instId);
		if (null == it) {
			throw new MALException("schedule instance does not exist, id: " + instId);
		}
		items.remove(it);
		return it.stat;
	}
	
	public ScheduleStatusDetails patch(Long defId, Long instId, ScheduleInstanceDetails schInst,
			SchedulePatchOperations patchOp, Long targetId) throws MALException {
		Item it = findItem(instId);
		if (null == it) {
			throw new MALException("schedule instance does not exist, id: " + instId);
		}
		if (instId != targetId) {
			it = findItem(targetId);
			if (null == it) {
				// new one
				it = new Item();
				it.defId = defId;
				it.instId = targetId;
				it.inst = schInst;
				it.stat = new ScheduleStatusDetails();
				it.stat.setScheduleInstName(schInst.getName());
				it.stat.setScheduleItemStatuses(new ScheduleItemStatusDetailsList());
				items.add(it);
			} else {
				// TODO exists
			}
		} // else keep old
		return it.stat;
	}
}
