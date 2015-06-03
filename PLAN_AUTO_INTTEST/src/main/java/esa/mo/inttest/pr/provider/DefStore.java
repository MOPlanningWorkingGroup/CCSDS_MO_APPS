package esa.mo.inttest.pr.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;

/**
 * PR and Task definitions storage.
 */
public class DefStore {

	private AtomicLong lastId = new AtomicLong(0L);
	private Map<Long, PlanningRequestDefinitionDetails> prDefs = new HashMap<Long, PlanningRequestDefinitionDetails>();
	private Map<Long, TaskDefinitionDetails> taskDefs = new HashMap<Long, TaskDefinitionDetails>();
	
	/**
	 * Look up a def by id.
	 * @param id
	 * @return
	 */
	public BaseDefinition find(DefinitionType type, Long id) {
		BaseDefinition def = null;
		if (DefinitionType.PLANNING_REQUEST_DEF == type) {
			def = prDefs.get(id);
		} else if (DefinitionType.TASK_DEF == type) {
			def = taskDefs.get(id);
		}
		return def;
	}
	
	protected LongList listPrs(Identifier id) {
		LongList list = new LongList();
		Iterator<PlanningRequestDefinitionDetails> it = prDefs.values().iterator();
		while (it.hasNext()) {
			PlanningRequestDefinitionDetails pr = it.next();
			if ("*".equals(id.getValue()) || // all filter
					pr.getName().getValue().contains(id.getValue())) { // simple "contains" filter
				list.add(pr.getId());
			}
		}
		return list;
	}
	
	protected LongList listTasks(Identifier id) {
		LongList list = new LongList();
		Iterator<TaskDefinitionDetails> it = taskDefs.values().iterator();
		while (it.hasNext()) {
			TaskDefinitionDetails task = it.next();
			if ("*".equals(id.getValue()) || // all filter
					task.getName().getValue().contains(id.getValue())) {
				list.add(task.getId());
			}
		}
		return list;
	}
	
	/**
	 * Look up defs by identifier.
	 * @param id
	 * @return
	 */
	public LongList list(DefinitionType dt, Identifier id) {
		LongList list = new LongList();
		if (DefinitionType.PLANNING_REQUEST_DEF == dt) {
			list.addAll(listPrs(id));
		} else if (DefinitionType.TASK_DEF == dt) {
			list.addAll(listTasks(id));
		}
		return list;
	}
	
	/**
	 * Look up defs by identifiers.
	 * @param ids
	 * @return
	 * @throws MALException
	 */
	public LongList listAll(DefinitionType dt, IdentifierList ids) {
		LongList list = new LongList();
		for (int i = 0; i < ids.size(); ++i) {
			Identifier id = ids.get(i);
			list.addAll(list(dt, id));
		}
		return list;
	}
	
	private long generateId() {
		return lastId.incrementAndGet();
	}
	
	protected Long addPr(PlanningRequestDefinitionDetails def) {
		Long id = new Long(generateId());
		def.setId(id);
		prDefs.put(id, def);
		return id;
	}
	
	protected Long addTask(TaskDefinitionDetails def) {
		Long id = new Long(generateId());
		def.setId(id);
		taskDefs.put(id, def);
		return id;
	}
	
	/**
	 * Adds single PR def and returns id.
	 * @param prDef
	 * @return
	 */
	public Long add(DefinitionType dt, BaseDefinition def) {
		Long id = null;
		if (DefinitionType.PLANNING_REQUEST_DEF == dt) {
			id = addPr((PlanningRequestDefinitionDetails)def);
		} else if (DefinitionType.TASK_DEF == dt) {
			id = addTask((TaskDefinitionDetails)def);
		}
		return id;
	}
	
	/**
	 * Adds several PR defs and returns list of ids.
	 * @param defs
	 * @return
	 * @throws MALException
	 */
	public LongList addAll(DefinitionType dt, BaseDefinitionList<? extends BaseDefinition> defs) {
		LongList ids = new LongList();
		for (int i = 0; i < defs.size(); ++i) {
			BaseDefinition def = defs.get(i);
			ids.add(add(dt, def));
		}
		return ids;
	}
	
	@SuppressWarnings("unchecked")
	protected BaseDefinitionList<BaseDefinition> createList(DefinitionType dt) {
		BaseDefinitionList<? extends BaseDefinition> list = null;
		if (DefinitionType.PLANNING_REQUEST_DEF == dt) {
			list = new PlanningRequestDefinitionDetailsList();
		} else if (DefinitionType.TASK_DEF == dt) {
			list = new TaskDefinitionDetailsList();
		}
		return (BaseDefinitionList<BaseDefinition>)list;
	}
	
	public BaseDefinitionList<BaseDefinition> getAll(DefinitionType dt, LongList ids) {
		BaseDefinitionList<BaseDefinition> list = createList(dt);
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			list.add(find(dt, id));
		}
		return list;
	}
	
	protected void updatePr(PlanningRequestDefinitionDetails def) {
		prDefs.put(def.getId(), def);
	}
	
	protected void updateTask(TaskDefinitionDetails def) {
		taskDefs.put(def.getId(), def);
	}
	
	/**
	 * Replaces single PR def by id.
	 * @param id
	 * @param prDef
	 */
	public void update(DefinitionType dt, BaseDefinition def) {
		if (DefinitionType.PLANNING_REQUEST_DEF == dt) {
			updatePr((PlanningRequestDefinitionDetails)def);
		} else if (DefinitionType.TASK_DEF == dt) {
			updateTask((TaskDefinitionDetails)def);
		}
	}
	
	/**
	 * Replaces several PR defs by id.
	 * @param ids
	 * @param defs
	 * @throws MALException
	 */
	public void updateAll(DefinitionType dt, BaseDefinitionList<? extends BaseDefinition> defs) {
		for (int i = 0; i < defs.size(); ++i) {
			BaseDefinition def = defs.get(i);
			update(dt, def);
		}
	}
	
	protected void removePr(Long id) {
		prDefs.remove(id);
	}
	
	protected void removeTask(Long id) {
		taskDefs.remove(id);
	}
	
	/**
	 * Removes single PR def by id.
	 * @param id
	 */
	public void remove(DefinitionType dt, Long id) {
		if (DefinitionType.PLANNING_REQUEST_DEF == dt) {
			removePr(id);
		} else if (DefinitionType.TASK_DEF == dt) {
			removeTask(id);
		}
	}
	
	/**
	 * Removes several PR defs by id.
	 * @param ids
	 * @throws MALException
	 */
	public void removeAll(DefinitionType dt, LongList ids) {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			remove(dt, id);
		}
	}
}
