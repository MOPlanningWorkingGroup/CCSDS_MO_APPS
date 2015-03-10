package esa.mo.inttest.pr.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;

public class PrInstStore {

	private final class Item {
		
		private Long defId;
		private Long instId;
		private PlanningRequestInstanceDetails pr;
		private PlanningRequestStatusDetails st;
		
		public Item(Long defId, Long instId, PlanningRequestInstanceDetails pr, PlanningRequestStatusDetails st) {
			this.defId = defId;
			this.instId = instId;
			this.pr = pr;
			this.st = st;
		}
	}
	
	private List<Item> prs = new ArrayList<Item>();
	
	public PrInstStore() {
	}

	public void addPr(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst, PlanningRequestStatusDetails prStat) {
		prs.add(new Item(prDefId, prInstId, prInst, prStat));
	}
	
	public Object[] findPr(Long prInstId) {
		Object[] rval = null;
		Iterator<Item> it = prs.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (prInstId == item.instId) {
				rval = new Object[] { item.pr, item.st, };
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
				item.st = prStat;
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
		return null; // TODO
	}
	
	public void setTaskStatus(Long taskStatusId, TaskStatusDetails taskStat) {
		// TODO
	}
}
