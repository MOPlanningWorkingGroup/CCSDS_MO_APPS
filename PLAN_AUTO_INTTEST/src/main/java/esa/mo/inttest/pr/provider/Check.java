package esa.mo.inttest.pr.provider;

import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;

/**
 * Verification and validation of various PR fields. All error messages in one place.
 */
public class Check {

	/**
	 * Hidden ctor.
	 */
	private Check() {
	}

	/**
	 * Searches for argument def from list.
	 * @param args
	 * @param name
	 * @return
	 */
	protected static ArgumentDefinitionDetails findArg(ArgumentDefinitionDetailsList args, Identifier name) {
		ArgumentDefinitionDetails argDef = null;
		for (int j = 0; (null != args) && (null == argDef) && (j < args.size()); ++j) {
			ArgumentDefinitionDetails aDef2 = args.get(j);
			if (name.equals(aDef2.getName())) { // Identifier.equals() is case sensitive
				argDef = aDef2;
			}
		}
		return argDef;
	}

	/**
	 * Verify that PR inst Id list is not null.
	 * @param ids
	 * @throws MALException
	 */
	public static void prInstIdList(LongList ids) throws MALException {
		if (ids == null) {
			throw new MALException("pr instance id list is null");
		}
		if (ids.isEmpty()) {
			throw new MALException("pr instance id list is empty");
		}
	}
	
	/**
	 * Verify that PR inst Id list elements are not null.
	 * @param ids
	 * @throws MALException
	 */
	public static void prInstIds(LongList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (id == null) {
				throw new MALException("pr instance id[" + i + "] is null");
			}
		}
	}

	/**
	 * Verify that def type is the supported one.
	 * @param type
	 * @throws MALException
	 */
	public static void defType(DefinitionType type) throws MALException {
		if (null == type) {
			throw new MALException("no definition type given");
		}
		if (DefinitionType.TASK_DEF == type) {
			// correct
		} else if (DefinitionType.PLANNING_REQUEST_DEF == type) {
			// correct
		} else {
			throw new MALException("definition type is not supported: " + type);
		}
	}
	
	/**
	 * Verify that identifier list is not null.
	 * @param ids
	 * @throws MALException
	 */
	public static void nameList(IdentifierList ids) throws MALException {
		if (ids == null) {
			throw new MALException("identifier list is null");
		}
		if (ids.isEmpty()) {
			throw new MALException("identifier list is empty");
		}
	}
	
	/**
	 * Verify that baseDef list is not null.
	 * @param defs
	 * @throws MALException
	 */
	@SuppressWarnings("rawtypes")
	public static void baseDefList(BaseDefinitionList defs) throws MALException {
		if (null == defs) {
			throw new MALException("no definition list given");
		}
		if (defs.isEmpty()) {
			throw new MALException("no definitions in list");
		}
	}
	
	protected static boolean extendsBaseDef(BaseDefinition bd, DefinitionType dt) throws MALException {
		boolean rval = false;
		if ((DefinitionType.TASK_DEF == dt) && (bd instanceof TaskDefinitionDetails)) {
			rval = true;
		} else if ((DefinitionType.PLANNING_REQUEST_DEF == dt) && (bd instanceof PlanningRequestDefinitionDetails)) {
			rval = true;
		}
		return rval;
	}
	
	/**
	 * Verify that baseDef list has valid elements.
	 * @param defs
	 * @param type
	 * @throws MALException
	 */
	@SuppressWarnings("rawtypes")
	public static void defTypes(BaseDefinitionList defs, DefinitionType type) throws MALException {
		for (int i = 0; (null != defs) && (i < defs.size()); ++i) {
			Object obj = defs.get(i);
			if (null == obj) {
				throw new MALException("baseDefinition[" + i + "] is null");
			}
			if (obj instanceof BaseDefinition) {
				BaseDefinition def = (BaseDefinition)obj;
				if (!extendsBaseDef(def, type)) {
					throw new MALException("baseDefinition[" + i + "] does not match definitionType (" + type + ")");
				}
			} else {
				throw new MALException("object[" + i + "] is not baseDefinition");
			}
		}
	}
	
	/**
	 * Verify that def Id list is not null.
	 * @param ids
	 * @throws MALException
	 */
	public static void defIdList(LongList ids) throws MALException {
		if (null == ids) {
			throw new MALException("definition id list is null");
		}
		if (ids.isEmpty()) {
			throw new MALException("definition id list is empty");
		}
	}
	
	/**
	 * Verify that defs of given type do exists.
	 * @param ids
	 * @param type
	 * @param prStore
	 * @param taskStore
	 * @param respStore
	 * @throws MALException
	 */
	public static void defsExist(LongList ids, DefinitionType type, PrDefStore prStore,
			TaskDefStore taskStore) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("definition id[" + i + "] is null");
			}
			if (DefinitionType.PLANNING_REQUEST_DEF == type) {
				PlanningRequestDefinitionDetails def = prStore.find(id);
				if (null == def) {
					throw new MALException("pr definition[" + i + "] not found, id: " + id);
				}
			} else if (DefinitionType.TASK_DEF == type) {
				TaskDefinitionDetails def = taskStore.find(id);
				if (null == def) {
					throw new MALException("task definition[" + i + "] not found, id: " + id);
				}
			}
		}
	}
	
	/**
	 * Verify that lists have same amount of elements.
	 * @param ids
	 * @param baseDefs
	 * @throws MALException
	 */
	@SuppressWarnings("rawtypes")
	public static void defLists(LongList ids, BaseDefinitionList baseDefs) throws MALException {
		if (ids.size() != baseDefs.size()) {
			throw new MALException("definition ids count does not match definitions count");
		}
	}
	
	/**
	 * Verify that Task inst Id list is not null.
	 * @param ids
	 * @throws MALException
	 */
	public static void taskInstIdList(LongList ids) throws MALException {
		if (ids == null) {
			throw new MALException("task id list is null");
		}
		if (ids.isEmpty()) {
			throw new MALException("task id list is empty");
		}
	}
	
	/**
	 * Verify that Task inst Id in list is not null.
	 * @param ids
	 * @throws MALException
	 */
	public static void taskInstIds(LongList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (id == null) {
				throw new MALException("task id[" + i + "] is null");
			}
		}
	}
	
	public static void prInstList(PlanningRequestInstanceDetailsList insts) throws MALException {
		if (null == insts) {
			throw new MALException("pr instances list is null");
		}
		if (insts.isEmpty()) {
			throw new MALException("pr instances list is empty");
		}
	}
	
	/**
	 * Verify that PR inst is not null.
	 * @param inst
	 * @throws MALException
	 */
	protected static void prInst(int i, PlanningRequestInstanceDetails inst) throws MALException {
		if (null == inst) {
			throw new MALException("pr instance[" + i + "] is null");
		}
	}

	/**
	 * Verify that PR inst Id is not null.
	 * @param id
	 * @throws MALException
	 */
	protected static void prInstId(int i, Long id) throws MALException {
		if (null == id) {
			throw new MALException("pr instance[" + i + "].id is null");
		}
	}

	/**
	 * Verify that PR def Id is not null.
	 * @param id
	 * @throws MALException
	 */
	protected static void prDefId(int i, Long id) throws MALException {
		if (null == id) {
			throw new MALException("pr instance[" + i + "].defId is null");
		}
	}

	/**
	 * Verify that PR def exists.
	 * @param id
	 * @param store
	 * @throws MALException
	 */
	protected static PlanningRequestDefinitionDetails prDefExists(int i, Long id, PrDefStore store) throws MALException {
		PlanningRequestDefinitionDetails def = store.find(id);
		if (null == def) {
			throw new MALException("pr instance[" + i + "] definition not found by id: " + id);
		}
		return def;
	}

	protected static void prArg(int i, int j, ArgumentValue argVal) throws MALException {
		if (null == argVal) {
			throw new MALException(" pr instance[" + i + "].arg[" + j + "] is null");
		}
	}
	
	protected static void prArgName(int i, int j, Identifier name) throws MALException {
		if (null == name || null == name.getValue()) {
			throw new MALException("pr instance[" + i + "].arg[" + j + "].name is null");
		}
		if (name.getValue().isEmpty()) {
			throw new MALException("pr instance[" + i + "].arg[" + j + "].name is empty");
		}
	}
	
	protected static ArgumentDefinitionDetails prArgDefExists(int i, int j, Identifier name,
			ArgumentDefinitionDetailsList argDefs) throws MALException {
		ArgumentDefinitionDetails argDef = findArg(argDefs, name);
		if (null == argDef) {
			throw new MALException("pr instance[" + i + "].arg[" + j + "] definition not found by name: " + name);
		}
		return argDef;
	}
	
	protected static void prArgVal(int i, int j, Attribute val, ArgumentDefinitionDetails def) throws MALException {
		// argVal.value can be null
		if (null != val) {
			byte type = (byte)(val.getTypeShortForm() & 0xff);
			if (type != def.getAttributeType()) {
				throw new MALException("pr instance[" + i + "].arg[" + j + "] value type (" +
						type + ") does not match defined type (" + def.getTypeShortForm() + ")");
			}
		} // else null - no value - no type - no problems
	}
	
	/**
	 * Check PR instance arguments for matching argument definition.
	 * @param defId
	 * @param inst
	 * @throws MALException
	 */
	protected static void prArgs(int i, PlanningRequestInstanceDetails inst,
			PlanningRequestDefinitionDetails def) throws MALException {
		ArgumentValueList argVals = inst.getArgumentValues();
		// arguments are optional, args list can be null and empty
		for (int j = 0; (null != argVals) && (j < argVals.size()); ++j) {
			ArgumentValue argVal = argVals.get(j);
			prArg(i, j, argVal);
			prArgName(i, j, argVal.getArgDefName());
			ArgumentDefinitionDetails argDef = prArgDefExists(i, j, argVal.getArgDefName(), def.getArgumentDefs());
			prArgVal(i, j, argVal.getValue(), argDef);
		}
	}

	/**
	 * Verify that PR inst does not exist.
	 * @param id
	 * @param store
	 * @throws MALException
	 */
	protected static void prInstNoExist(int i, Long id, InstStore store) throws MALException {
		InstStore.PrItem it = store.findPrItem(id);
		if (null != it) {
			throw new MALException("pr instance[" + i + "] already exists with id: " + id);
		}
	}

	protected static void prTask(int i, int j, TaskInstanceDetails task) throws MALException {
		if (null == task) {
			throw new MALException("pr instance[" + i + "].task[" + j + "] is null");
		}
	}
	
	protected static void prTaskId(int i, int j, Long id) throws MALException {
		if (null == id) {
			throw new MALException("pr instance[" + i + "].task[" + j + "].id is null");
		}
	}
	
	protected static void prTaskDefId(int i, int j, Long id) throws MALException {
		if (null == id) {
			throw new MALException("pr instance[" + i + "].task[" + j + "].defId is null");
		}
	}
	
	protected static void prTaskPrId(int i, int j, Long taskPrId, Long prId) throws MALException {
		if (taskPrId != prId) {
			throw new MALException("pr instance[" + i + "].task[" + j + "].prId does not match prId");
		}
	}
	
	protected static TaskDefinitionDetails prTaskDefExists(int i, int j, Long id, TaskDefStore tasks) throws MALException {
		TaskDefinitionDetails def = tasks.find(id);
		if (null == def) {
			throw new MALException("pr instance[" + i + "].task[" + j + "] definition not found by id: " + id);
		}
		return def;
	}
	
	protected static void prTaskArg(int i, int j, int k, ArgumentValue argVal) throws MALException {
		if (null == argVal) {
			throw new MALException("pr instance[" + i + "].task[" + j + "].arg[" + k + "] is null");
		}
	}
	
	protected static void prTaskArgName(int i, int j, int k, Identifier name) throws MALException {
		if (null == name || null == name.getValue()) {
			throw new MALException("pr instance[" + i + "].task[" + j + "].arg[" + k + "].name is null");
		}
		if (name.getValue().isEmpty()) {
			throw new MALException("pr instance[" + i + "].task[" + j + "].arg[" + k + "].name is empty");
		}
	}

	protected static ArgumentDefinitionDetails prTaskArgDefExists(int i, int j, int k, Identifier name,
			ArgumentDefinitionDetailsList argDefs) throws MALException {
		ArgumentDefinitionDetails argDef = findArg(argDefs, name);
		if (null == argDef) {
			throw new MALException("pr instance[" + i + "].task[" + j + "].arg[" + k + "] definition not found by name: " + name);
		}
		return argDef;
	}
	
	protected static void prTaskArgVal(int i, int j, int k, Attribute val, ArgumentDefinitionDetails def) throws MALException {
		// argVal.value can be null
		if (null != val) {
			byte type = (byte)(val.getTypeShortForm() & 0xff);
			if (type != def.getAttributeType()) {
				throw new MALException("pr instance[" + i + "].task[" + j + "].arg[" + k + "] value type (" +
						type + ") does not match defined type (" + def.getTypeShortForm() + ")");
			}
		} // else null - no value - no type - no problems
	}

	protected static void prTaskArgs(int i, int j, TaskInstanceDetails task, TaskDefinitionDetails def) throws MALException {
		ArgumentValueList argVals = task.getArgumentValues();
		// args are optional, list can be null and empty
		for (int k = 0; (null != argVals) && (k < argVals.size()); ++k) {
			ArgumentValue argVal = argVals.get(k);
			prTaskArg(i, j, k, argVal);
			prTaskArgName(i, j, k, argVal.getArgDefName());
			ArgumentDefinitionDetails argDef = prTaskArgDefExists(i, j, k, argVal.getArgDefName(), def.getArgumentDefs());
			prTaskArgVal(i, j, k, argVal.getValue(), argDef);
		}
	}
	
	protected static void prTaskInstNoExist(int i, int j, Long id, InstStore store) throws MALException {
		InstStore.TaskItem item = store.findTaskItem(id);
		if (null != item) {
			throw new MALException("pr instance[" + i + "].task[" + j + "] already exists with id: " + id);
		}
	}
	
	/**
	 * Verify tasks list elements.
	 * @param i
	 * @param pr
	 * @param taskDefs
	 * @param store
	 * @throws MALException
	 */
	protected static void addPrTasks(int i, PlanningRequestInstanceDetails pr, TaskDefStore taskDefs,
			InstStore store) throws MALException {
		TaskInstanceDetailsList tasks = pr.getTasks();
		// tasks are optional, tasks list can be null and empty
		for (int j = 0; (null != tasks) && (j < tasks.size()); ++j) {
			TaskInstanceDetails task = tasks.get(i);
			prTask(i, j, task);
			prTaskId(i, j, task.getId());
			prTaskDefId(i, j, task.getTaskDefId());
			prTaskPrId(i, j, task.getPrInstId(), pr.getId());
			TaskDefinitionDetails def = prTaskDefExists(i, j, task.getTaskDefId(), taskDefs);
			prTaskArgs(i, j, task, def);
			prTaskInstNoExist(i, j, task.getId(), store);
		}
	}
	
	public static void addPrInsts(PlanningRequestInstanceDetailsList insts, PrDefStore prDefs,
			InstStore store, TaskDefStore taskDefs) throws MALException {
		for (int i = 0; i < insts.size(); ++i) {
			PlanningRequestInstanceDetails pr = insts.get(i);
			prInst(i, pr);
			prInstId(i, pr.getId());
			prDefId(i, pr.getPrDefId());
			PlanningRequestDefinitionDetails def = prDefExists(i, pr.getPrDefId(), prDefs);
			prArgs(i, pr, def);
			prInstNoExist(i, pr.getId(), store);
			addPrTasks(i, pr, taskDefs, store);
		}
	}
	
	/**
	 * Verify that PR inst exists.
	 * @param id
	 * @param store
	 * @return
	 * @throws MALException
	 */
	protected static InstStore.PrItem prInstExists(int i, Long id, InstStore store) throws MALException {
		InstStore.PrItem it = store.findPrItem(id);
		if (null == it) {
			throw new MALException("pr instance[" + i + "] not found by id: " + id);
		}
		return it;
	}

	protected static void updatePrTasks(int i, PlanningRequestInstanceDetails pr, TaskDefStore taskDefs,
			InstStore store) throws MALException {
		TaskInstanceDetailsList tasks = pr.getTasks();
		// tasks are optional, tasks list can be null and empty
		for (int j = 0; (null != tasks) && (j < tasks.size()); ++j) {
			TaskInstanceDetails task = tasks.get(i);
			prTask(i, j, task);
			prTaskId(i, j, task.getId());
			prTaskDefId(i, j, task.getTaskDefId());
			prTaskPrId(i, j, task.getPrInstId(), pr.getId());
			TaskDefinitionDetails def = prTaskDefExists(i, j, task.getTaskDefId(), taskDefs);
			prTaskArgs(i, j, task, def);
			// task may exist or not -> depends if it's update or add
//			prTaskInstExists(i, j, task.getId(), store);
		}
	}

	public static List<InstStore.PrItem> updatePrInsts(PlanningRequestInstanceDetailsList insts, PrDefStore prDefs,
			InstStore store, TaskDefStore taskDefs) throws MALException {
		List<InstStore.PrItem> items = new ArrayList<InstStore.PrItem>();
		for (int i = 0; i < insts.size(); ++i) {
			PlanningRequestInstanceDetails pr = insts.get(i);
			prInst(i, pr);
			prInstId(i, pr.getId());
			prDefId(i, pr.getPrDefId());
			PlanningRequestDefinitionDetails def = prDefExists(i, pr.getPrDefId(), prDefs);
			prArgs(i, pr, def);
			// pr must exist in order to update it
			items.add(prInstExists(i, pr.getId(), store));
			updatePrTasks(i, pr, taskDefs, store);
		}
		return items;
	}

	public static List<InstStore.PrItem> prInstsExist(LongList ids, InstStore store) throws MALException {
		List<InstStore.PrItem> items = new ArrayList<InstStore.PrItem>();
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			InstStore.PrItem item = store.findPrItem(id);
			if (null == item) {
				throw new MALException("pr instance id[" + i +"] not found by id: " + id);
			}
			items.add(item);
		}
		return items;
	}
}
