package esa.mo.inttest.pr.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;

/**
 * PR instances storage.
 */
public class PrInstStore {

	/**
	 * Structure to hold PR def Id, PR inst Id, PR instance, PR status, task def Ids, task inst Ids.
	 */
	public final class Item {
		
		public Long defId;
		public Long instId;
		public PlanningRequestInstanceDetails pr;
		public LongList taskDefIds;
		public LongList taskInstIds;
		public PlanningRequestStatusDetails stat;
		
		public Item(Long defId, Long instId, PlanningRequestInstanceDetails pr, LongList taskDefIds,
				LongList taskInstIds, PlanningRequestStatusDetails stat) {
			this.defId = defId;
			this.instId = instId;
			this.pr = pr;
			this.taskDefIds = taskDefIds;
			this.taskInstIds = taskInstIds;
			this.stat = stat;
		}
	}
	
	private List<Item> prs = new ArrayList<Item>();
	
	public PrInstStore() {
	}

	public void addPr(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst, LongList taskDefIds,
			LongList taskInstIds, PlanningRequestStatusDetails prStat) {
		prs.add(new Item(prDefId, prInstId, prInst, taskDefIds, taskInstIds, prStat));
	}
	
	public Item findPr(Long prInstId) {
		Item rval = null;
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.instId) {
				rval = item;
				break;
			}
		}
		return rval;
	}
	
	public void setPrStatus(Long prInstId, PlanningRequestStatusDetails prStat) {
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.instId) {
				item.stat = prStat;
				break;
			}
		}
	}
	
	public void updatePr(Long prInstId, PlanningRequestInstanceDetails prInst, PlanningRequestStatusDetails prStat) {
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.instId) {
				item.pr = prInst;
				item.stat = prStat;
				break;
			}
		}
	}
	
	public void removePr(Long prInstId) {
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.instId) {
				it.remove();
				break;
			}
		}
	}
	
	public TaskStatusDetails findTask(Long taskInstId) {
		TaskStatusDetails taskStat = null;
		Iterator<Item> it = prs.iterator();
		// go through prs
		while (it.hasNext()) {
			Item item = it.next();
			// should have same amount of tasks instance ids and task statuses
			Iterator<Long> idIt = item.taskInstIds.iterator();
			Iterator<TaskStatusDetails> taskStatIt = item.stat.getTaskStatuses().iterator();
			// go through task instance ids and task statuses
			while (idIt.hasNext() && taskStatIt.hasNext()) {
				Long id = idIt.next();
				TaskStatusDetails ts = taskStatIt.next();
				if (taskInstId == id) {
					taskStat = ts;
					break;
				}
			}
		}
		return taskStat;
	}
	
	public void setTaskStatus(Long taskInstId, TaskStatusDetails taskStat) {
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			Iterator<Long> idIt = item.taskInstIds.iterator();
			Iterator<TaskStatusDetails> statIt = item.stat.getTaskStatuses().iterator();
			while (idIt.hasNext() && statIt.hasNext()) {
				Long id = idIt.next();
				TaskStatusDetails ts = statIt.next();
				if (taskInstId == id) {
					int idx = item.stat.getTaskStatuses().indexOf(ts);
					item.stat.getTaskStatuses().set(idx, taskStat);
					break;
				}
			}
		}
	}
}
