package esa.mo.inttest.pr.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;

public class PrInstStore {

	private final class Item {
		
		private Long defId;
		private Long instId;
		private PlanningRequestInstanceDetails pr;
		private LongList taskDefIds;
		private LongList taskInstIds;
		private PlanningRequestStatusDetails stat;
		
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
	
	public Object[] findPr(Long prInstId) {
		Object[] rval = null;
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.instId) {
				rval = new Object[] { item.pr, item.stat, };
				break;
			}
		}
		return rval;
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
	
	public void setTaskStatus(Long taskStatusId, TaskStatusDetails taskStat) {
		// TODO
	}
}
