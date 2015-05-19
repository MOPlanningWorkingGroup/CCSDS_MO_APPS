package esa.mo.inttest.sch.provider;

import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.automation.schedule.structures.PatchOperation;
import org.ccsds.moims.mo.automation.schedule.structures.PatchOperationList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.SchedulePatchOperations;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;

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
	
	protected void patchTriggers(TriggerDetailsList src1, TriggerDetailsList src2,
			PatchOperationList pat, TriggerDetailsList trg) {
		TriggerDetailsList newTrigs = new TriggerDetailsList();
		for (int i = 0; (null != pat) && (i < pat.size()); ++i) {
			PatchOperation pOp = pat.get(i);
			if (PatchOperation.UPDATE == pOp) {
				// copy value from src2 to target
				trg.add(src2.get(i));
			} else if (PatchOperation.REMOVE == pOp) {
				// just dont copy it to target
			} else {
				// copy value from src1 to target
				trg.add(src1.get(i));
				if (PatchOperation.ADD == pOp) {
					// just store it for adding later to target
					newTrigs.add(src2.get(i));
				}
			}
		}
		trg.addAll(newTrigs);
	}
	
	protected void patchArgDefNames(IdentifierList src1, IdentifierList src2,
			PatchOperationList pat, IdentifierList trg) {
		IdentifierList newArgNames = new IdentifierList();
		for (int i = 0; (null != pat) && (i < pat.size()); ++i) {
			PatchOperation pOp = pat.get(i);
			if (PatchOperation.UPDATE == pOp) {
				// copy value from src2 to target
				trg.add(src2.get(i));
			} else if (PatchOperation.REMOVE == pOp) {
				// just dont copy it to target
			} else {
				// copy value from src1 to target
				trg.add(src1.get(i));
				if (PatchOperation.ADD == pOp) {
					newArgNames.add(src2.get(i));
				}
			}
		}
		trg.addAll(newArgNames);
	}
	
	protected void patchArgVals(AttributeValueList src1, AttributeValueList src2,
			PatchOperationList pat, AttributeValueList trg) {
		AttributeValueList newArgVals = new AttributeValueList();
		for (int i = 0; (null != pat) && (i < pat.size()); ++i) {
			PatchOperation pOp = pat.get(i);
			if (PatchOperation.UPDATE == pOp) {
				// copy value from src2 to target
				trg.add(src2.get(i));
			} else if (PatchOperation.REMOVE == pOp) {
				// just dont copy it to target
			} else {
				// copy value from src1 to target
				trg.add(src1.get(i));
				if (PatchOperation.ADD == pOp) {
					newArgVals.add(src2.get(i));
				}
			}
		}
		trg.addAll(newArgVals);
	}
	
	protected void patchItems(ScheduleInstanceDetails src1, ScheduleInstanceDetails src2,
			SchedulePatchOperations pat, ScheduleInstanceDetails trg) {
		ScheduleItemInstanceDetailsList newItems = new ScheduleItemInstanceDetailsList();
		for (int i = 0; (null != pat.getScheduleItems()) && (i < pat.getScheduleItems().size()); ++i) {
			PatchOperation pOp = pat.getScheduleItems().get(i);
			ScheduleItemInstanceDetails item1 = src1.getScheduleItems().get(i);
			ScheduleItemInstanceDetails item2 = src2.getScheduleItems().get(i);
			//pat.getScheduleInstName()
			if (PatchOperation.UPDATE == pOp) {
				trg.getScheduleItems().add(item2);
			} else if (PatchOperation.REMOVE == pOp) {
				// do nothing
			} else {
				trg.getScheduleItems().add(item1);
				if (PatchOperation.ADD == pOp) {
					newItems.add(item2);
				}
			}
		}
		trg.getScheduleItems().addAll(newItems);
	}
	
	protected void patch(ScheduleInstanceDetails src1, ScheduleInstanceDetails src2,
			SchedulePatchOperations pat, ScheduleInstanceDetails trg) {
		// pat.getScheduleInstName() - patch name? ignore
//		trg.setName(src2.getName());
		trg.setComment(src2.getComment());
		// patch triggers
		trg.setTimingConstraints(new TriggerDetailsList());
		patchTriggers(src1.getTimingConstraints(), src2.getTimingConstraints(),
				pat.getTimingConstraints(), trg.getTimingConstraints());
		// patch arg names
		trg.setArgumentDefNames(new IdentifierList());
		patchArgDefNames(src1.getArgumentDefNames(), src2.getArgumentDefNames(),
				pat.getArgumentDefNames(), trg.getArgumentDefNames());
		// patch arg values
		trg.setArgumentValues(new AttributeValueList());
		patchArgVals(src1.getArgumentValues(), src2.getArgumentValues(),
				pat.getArgumentValues(), trg.getArgumentValues());
		// patch items
		trg.setScheduleItems(new ScheduleItemInstanceDetailsList());
		patchItems(src1, src2, pat, trg);
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
	public ScheduleStatusDetails patch(Long defId, Long srcId, ScheduleInstanceDetails schInst,
			SchedulePatchOperations patchOp, Long targetId) throws MALException {
		Item srcIt = findItem(srcId);
		if (null == srcIt) {
			throw new MALException("source schedule instance does not exist, id: " + srcId);
		}
		if (srcId != targetId) {
			Item trgIt = findItem(targetId);
			if (null == trgIt) { // target is a new one - new id and instance
				trgIt = new Item();
				trgIt.inst = new ScheduleInstanceDetails();
				trgIt.inst.setId(targetId);
				trgIt.inst.setSchDefId(defId);
				patch(srcIt.inst, schInst, patchOp, trgIt.inst);
				trgIt.stat = new ScheduleStatusDetails();
				trgIt.stat.setSchInstId(schInst.getId());
				trgIt.stat.setScheduleItemStatuses(new ScheduleItemStatusDetailsList());
				items.add(trgIt);
			} else { // target already exists
				if (srcIt.inst.getSchDefId() != trgIt.inst.getSchDefId()) {
					throw new MALException("source and target have different definition ids: " +
							trgIt.inst.getSchDefId() + " vs " + trgIt.inst.getSchDefId());
				}
				patch(srcIt.inst, schInst, patchOp, trgIt.inst);
			}
		} else { // source and target are same id, but new instance
			ScheduleInstanceDetails old = srcIt.inst;
			srcIt.inst = new ScheduleInstanceDetails();
			srcIt.inst.setId(old.getId());
			srcIt.inst.setSchDefId(old.getSchDefId());
			patch(old, schInst, patchOp, srcIt.inst);
		}
		return srcIt.stat;
	}
	
	// test support
	protected void setStatus(Long id, ScheduleStatusDetails stat) {
		Item it = findItem(id);
		if (null != it) {
			it.stat = stat;
		}
	}
}
