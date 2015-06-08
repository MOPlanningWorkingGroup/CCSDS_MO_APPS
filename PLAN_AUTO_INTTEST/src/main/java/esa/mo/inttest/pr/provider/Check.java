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

import esa.mo.inttest.Util;

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
			throw new MALException("definition type is null");
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
	
	protected static void name(int i, Identifier name) throws MALException {
		if (null == name || null == name.getValue()) {
			throw new MALException("identifier[" + i + "] is null");
		}
		if (name.getValue().isEmpty()) {
			throw new MALException("identifier[" + i + "] is empty");
		}
	}
	
	public static void names(IdentifierList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Identifier id = ids.get(i);
			name(i, id);
		}
	}
	
	/**
	 * Verify that baseDef list is not null.
	 * @param defs
	 * @throws MALException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BaseDefinitionList<BaseDefinition> defList(BaseDefinitionList defs) throws MALException {
		if (null == defs) {
			throw new MALException("definitions list is null");
		}
		if (defs.isEmpty()) {
			throw new MALException("definitions list is empty");
		}
		return (BaseDefinitionList<BaseDefinition>)defs;
	}
	
	protected static void extendsBaseDef(int i, DefinitionType dt, BaseDefinition bd) throws MALException {
		if ((DefinitionType.TASK_DEF == dt) &&
				!(bd instanceof TaskDefinitionDetails)) {
			throw new MALException("definition[" + i + "] is not task definition");
		} else if ((DefinitionType.PLANNING_REQUEST_DEF == dt) &&
				!(bd instanceof PlanningRequestDefinitionDetails)) {
			throw new MALException("definition[" + i + "] is not pr definition");
		}
	}
	
	protected static void def(int i, Object d) throws MALException {
		if (null == d) {
			throw new MALException("definition[" + i + "] is null");
		}
	}
	
	protected static BaseDefinition baseDef(int i, Object d) throws MALException {
		BaseDefinition bd = null;
		if (d instanceof BaseDefinition) {
			bd = (BaseDefinition)d;
		} else {
			throw new MALException("definition[" + i + "] is not baseDefinition");
		}
		return bd;
	}
	
	/**
	 * Verify that baseDef list has valid elements.
	 * @param defs
	 * @param type
	 * @throws MALException
	 */
	public static BaseDefinitionList<BaseDefinition> defs(DefinitionType type,
			BaseDefinitionList<BaseDefinition> defs) throws MALException {
		for (int i = 0; (null != defs) && (i < defs.size()); ++i) {
			Object obj = defs.get(i);
			def(i, obj);
			BaseDefinition bd = baseDef(i, obj);
			extendsBaseDef(i, type, bd);
		}
		return defs;
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

	protected static void defId(int i, Long id) throws MALException {
		if (null == id) {
			throw new MALException("definition id[" + i + "] is null");
		}
	}
	
	public static void defIds(LongList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			defId(i, id);
		}
	}
	
	protected static Long defId(int i, DefinitionType dt, BaseDefinition bd) throws MALException {
		Long id = null;
		if (DefinitionType.PLANNING_REQUEST_DEF == dt) {
			PlanningRequestDefinitionDetails prDef = (PlanningRequestDefinitionDetails)bd;
			id = prDef.getId();
		} else if (DefinitionType.TASK_DEF == dt) {
			TaskDefinitionDetails taskDef = (TaskDefinitionDetails)bd;
			id = taskDef.getId();
		}
		if (null == id) {
			throw new MALException("definition[" + i + "].id is null");
		}
		return id;
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
	public static void defsExist(DefinitionType type, BaseDefinitionList<? extends BaseDefinition> defs,
			DefStore prStore) throws MALException {
		for (int i = 0; i < defs.size(); ++i) {
			BaseDefinition def = defs.get(i);
			Long id = defId(i, type, def);
			BaseDefinition bd = prStore.find(type, id);
			if (null == bd) {
				throw new MALException("definition[" + i + "] not found by id: " + id);
			}
		}
	}

	public static void defsExist(DefinitionType type, LongList ids, DefStore prStore) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			BaseDefinition bd = prStore.find(type, id);
			if (null == bd) {
				throw new MALException("definition not found by id[" + i + "], id: " + id);
			}
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
	protected static PlanningRequestDefinitionDetails prDefExists(int i, Long id, DefStore store) throws MALException {
		PlanningRequestDefinitionDetails def = (PlanningRequestDefinitionDetails)store.find(DefinitionType.PLANNING_REQUEST_DEF, id);
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
		ArgumentDefinitionDetails argDef = Util.findArgDef(name, argDefs);
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
	
	protected static TaskDefinitionDetails prTaskDefExists(int i, int j, Long id, DefStore tasks) throws MALException {
		TaskDefinitionDetails def = (TaskDefinitionDetails)tasks.find(DefinitionType.TASK_DEF, id);
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
		ArgumentDefinitionDetails argDef = Util.findArgDef(name, argDefs);
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
	protected static void addPrTasks(int i, PlanningRequestInstanceDetails pr, DefStore taskDefs,
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
	
	/**
	 * Verify that PR instances are valid for addition.
	 * @param insts
	 * @param prDefs
	 * @param store
	 * @throws MALException
	 */
	public static void addPrInsts(PlanningRequestInstanceDetailsList insts, DefStore prDefs,
			InstStore store) throws MALException {
		for (int i = 0; i < insts.size(); ++i) {
			PlanningRequestInstanceDetails pr = insts.get(i);
			prInst(i, pr);
			prInstId(i, pr.getId());
			prDefId(i, pr.getPrDefId());
			PlanningRequestDefinitionDetails def = prDefExists(i, pr.getPrDefId(), prDefs);
			prArgs(i, pr, def);
			prInstNoExist(i, pr.getId(), store);
			addPrTasks(i, pr, prDefs, store);
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

	protected static void updatePrTasks(int i, PlanningRequestInstanceDetails pr,
			DefStore taskDefs) throws MALException {
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
		}
	}

	/**
	 * Verify that PR instances are valid for update.
	 * @param insts
	 * @param prDefs
	 * @param store
	 * @return
	 * @throws MALException
	 */
	public static List<InstStore.PrItem> updatePrInsts(PlanningRequestInstanceDetailsList insts,
			DefStore prDefs, InstStore store) throws MALException {
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
			updatePrTasks(i, pr, prDefs); // tasks don't have to exist
		}
		return items;
	}

	/**
	 * Verify that PR instances exist.
	 * @param ids
	 * @param store
	 * @return
	 * @throws MALException
	 */
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
