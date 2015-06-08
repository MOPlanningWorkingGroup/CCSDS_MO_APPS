package esa.mo.inttest.pr.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;

/**
 * PR instances storage.
 */
public class InstStore {

	/**
	 * Structure to hold PR instance & PR status.
	 */
	public static final class PrItem {
		
		protected PlanningRequestInstanceDetails pr;
		protected PlanningRequestStatusDetails stat;
		
		/**
		 * Ctor.
		 * @param pr
		 * @param stat
		 */
		public PrItem(PlanningRequestInstanceDetails pr, PlanningRequestStatusDetails stat) {
			this.pr = pr;
			this.stat = stat;
		}
		
		/**
		 * Returns PR instance.
		 * @return
		 */
		public PlanningRequestInstanceDetails getPr() {
			return pr;
		}
		
		/**
		 * Returns PR status.
		 * @return
		 */
		public PlanningRequestStatusDetails getStat() {
			return stat;
		}
	}
	
	/**
	 * Structure to hold Task instance & status.
	 */
	public static final class TaskItem {
		
		protected TaskInstanceDetails task;
		protected TaskStatusDetails stat;
		
		/**
		 * Ctor.
		 * @param task
		 * @param stat
		 */
		public TaskItem(TaskInstanceDetails task, TaskStatusDetails stat) {
			this.task = task;
			this.stat = stat;
		}
		
		/**
		 * Returns Task instance.
		 * @return
		 */
		public TaskInstanceDetails getTask() {
			return task;
		}
		
		/**
		 * Returns Task stats.
		 * @return
		 */
		public TaskStatusDetails getStat() {
			return stat;
		}
	}
	
	private Map<Long, PrItem> prs = new HashMap<Long, PrItem>();
	private Map<Long, TaskItem> tasks = new HashMap<Long, InstStore.TaskItem>();
	
	protected void addTask(TaskInstanceDetails task, TaskStatusDetails stat) {
		tasks.put(task.getId(), new TaskItem(task, stat));
	}
	
	protected void addTasks(TaskInstanceDetailsList tasks, TaskStatusDetailsList stats) {
		for (int i = 0; (null != tasks) && (i < tasks.size()); ++i) {
			TaskInstanceDetails t = tasks.get(i);
			TaskStatusDetails s = stats.get(i);
			addTask(t, s);
		}
	}
	
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
		prs.put(prInst.getId(), new PrItem(prInst, prStat));
		addTasks(prInst.getTasks(), prStat.getTaskStatuses());
	}
	
	/**
	 * Looks up PR store item by id.
	 * @param prInstId
	 * @return
	 */
	public PrItem findPrItem(Long prInstId) {
		return prs.get(prInstId);
	}
	
	/**
	 * Looks up Task store item by id.
	 * @param taskInstId
	 * @return
	 */
	public TaskItem findTaskItem(Long taskInstId) {
		return tasks.get(taskInstId);
	}
	
	protected void removeTasks(PlanningRequestInstanceDetails prOld, PlanningRequestInstanceDetails prNew) {
		List<Long> newTasks = new ArrayList<Long>();
		// create lookup list of new tasks
		for (int i = 0; (null != prNew.getTasks()) && (i < prNew.getTasks().size()); ++i) {
			TaskInstanceDetails task = prNew.getTasks().get(i);
			newTasks.add(task.getId());
		}
		// old task is not present in new tasks list - remove it
		for (int i = 0; (null != prOld.getTasks()) && (i < prOld.getTasks().size()); ++i) {
			TaskInstanceDetails task = prOld.getTasks().get(i);
			if (!newTasks.contains(task.getId())) {
				removeTask(task.getId());
			}
		}
	}
	
	protected void updateTasks(TaskInstanceDetailsList tasks, TaskStatusDetailsList stats) {
		for (int i = 0; (null != tasks) && (i < tasks.size()); ++i) {
			TaskInstanceDetails t = tasks.get(i);
			TaskStatusDetails s = stats.get(i);
			TaskItem item = findTaskItem(t.getId());
			if (null != item) {
				item.task = t;
				item.stat = s;
			} else { // new one
				addTask(t, s);
			}
		}
	}
	
	/**
	 * Replaces PR inst by id.
	 * @param prInstId
	 * @param prInst
	 * @param prStat
	 */
	public void updatePr(PlanningRequestInstanceDetails prInst) {
		PrItem item = findPrItem(prInst.getId());
		removeTasks(item.pr, prInst);
		item.pr = prInst;
		updateTasks(prInst.getTasks(), item.stat.getTaskStatuses()); // add or update
	}
	
	protected void removeTask(Long taskId) {
		tasks.remove(taskId);
	}
	
	protected void removeTasks(TaskInstanceDetailsList tasks) {
		for (int i = 0; (null != tasks) && (i < tasks.size()); ++i) {
			TaskInstanceDetails t = tasks.get(i);
			removeTask(t.getId());
		}
	}
	
	/**
	 * Removes PR (Item) by id.
	 * @param prInstId
	 */
	public PrItem removePr(Long prInstId) {
		PrItem item = prs.remove(prInstId);
		if (null != item) {
			removeTasks(item.pr.getTasks());
		}
		return item;
	}
}
