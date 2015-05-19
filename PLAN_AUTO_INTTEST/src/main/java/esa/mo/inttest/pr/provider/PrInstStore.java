package esa.mo.inttest.pr.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;

/**
 * PR instances storage.
 */
public class PrInstStore {

	/**
	 * Structure to hold PR def Id, PR inst Id, PR instance, PR status, task def Ids, task inst Ids.
	 */
	public final class Item {
		
		public PlanningRequestInstanceDetails pr;
		public PlanningRequestStatusDetails stat;
		
		public Item(PlanningRequestInstanceDetails pr, PlanningRequestStatusDetails stat) {
			this.pr = pr;
			this.stat = stat;
		}
	}
	
	private List<Item> prs = new ArrayList<Item>();
	
	/**
	 * Adds PR instance.
	 * @param prDefId
	 * @param prInstId
	 * @param prInst
	 * @param taskDefIds
	 * @param taskInstIds
	 * @param prStat
	 */
	public void addPr(PlanningRequestInstanceDetails prInst, PlanningRequestStatusDetails prStat) {
		prs.add(new Item(prInst, prStat));
	}
	
	/**
	 * Looks up PR inst by id.
	 * @param prInstId
	 * @return
	 */
	public Item findPr(Long prInstId) {
		Item rval = null;
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.pr.getId()) {
				rval = item;
				break;
			}
		}
		return rval;
	}
	
	/**
	 * Replaces PR status by id.
	 * @param prInstId
	 * @param prStat
	 */
	public void setPrStatus(Long prInstId, PlanningRequestStatusDetails prStat) {
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.pr.getId()) {
				item.stat = prStat;
				break;
			}
		}
	}
	
	/**
	 * Replaces PR inst and status by id.
	 * @param prInstId
	 * @param prInst
	 * @param prStat
	 */
	public void updatePr(PlanningRequestInstanceDetails prInst, PlanningRequestStatusDetails prStat) {
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInst.getId() == item.pr.getId()) {
				item.pr = prInst;
				item.stat = prStat;
				break;
			}
		}
	}
	
	/**
	 * Removes PR (Item) by id.
	 * @param prInstId
	 */
	public void removePr(Long prInstId) {
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.pr.getId()) {
				it.remove();
				break;
			}
		}
	}
	
	/**
	 * Looks up Task by id.
	 * @param taskInstId
	 * @return
	 */
	public TaskStatusDetails findTask(Long taskInstId) {
		TaskStatusDetails taskStat = null;
		Iterator<Item> it = prs.iterator();
		// go through prs
		while (it.hasNext() && (null == taskStat)) {
			Item item = it.next();
			// should have same amount of tasks instances and task statuses
			Iterator<TaskInstanceDetails> taskIt = item.pr.getTasks().iterator();
			Iterator<TaskStatusDetails> taskStatIt = item.stat.getTaskStatuses().iterator();
			// go through task instance ids and task statuses
			while (taskIt.hasNext() && taskStatIt.hasNext()) {
				TaskInstanceDetails task = taskIt.next();
				TaskStatusDetails ts = taskStatIt.next();
				if (taskInstId == task.getId()) {
					taskStat = ts;
					break;
				}
			}
		}
		return taskStat;
	}
	
	/**
	 * Replaces Task status by id.
	 * @param taskInstId
	 * @param taskStat
	 */
	public void setTaskStatus(Long taskInstId, TaskStatusDetails taskStat) {
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			Iterator<TaskInstanceDetails> taskIt = item.pr.getTasks().iterator();
			Iterator<TaskStatusDetails> statIt = item.stat.getTaskStatuses().iterator();
			while (taskIt.hasNext() && statIt.hasNext()) {
				TaskInstanceDetails task = taskIt.next();
				TaskStatusDetails ts = statIt.next();
				if (taskInstId == task.getId()) {
					int idx = item.stat.getTaskStatuses().indexOf(ts);
					item.stat.getTaskStatuses().set(idx, taskStat);
					break;
				}
			}
		}
	}
}
