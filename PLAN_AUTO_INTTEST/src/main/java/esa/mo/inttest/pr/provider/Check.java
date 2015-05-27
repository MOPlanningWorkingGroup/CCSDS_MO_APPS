package esa.mo.inttest.pr.provider;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;

/**
 * Verification and validation of various PR fields.
 */
public class Check {

	/**
	 * Hidden ctor.
	 */
	private Check() {
	}
	
	/**
	 * Verify that PR def Id is not null.
	 * @param id
	 * @throws MALException
	 */
	public static void prDefId(Long id) throws MALException {
		if (null == id) {
			throw new MALException("pr definition id is null");
		}
	}
	
	/**
	 * Verify that PR def exists.
	 * @param id
	 * @param store
	 * @throws MALException
	 */
	public static void prDefExists(Long id, PrDefStore store) throws MALException {
		PlanningRequestDefinitionDetails def = store.find(id);
		if (null == def) {
			throw new MALException("pr definition not found, id: " + id);
		}
	}
	
	/**
	 * Verify that PR inst Id is not null.
	 * @param id
	 * @throws MALException
	 */
	public static void prInstId(Long id) throws MALException {
		if (null == id) {
			throw new MALException("pr instance id is null");
		}
	}
	
	/**
	 * Verify that PR inst does not exist.
	 * @param id
	 * @param store
	 * @throws MALException
	 */
	public static void prInstNoExist(Long id, PrInstStore store) throws MALException {
		PrInstStore.PrItem it = store.findPrItem(id);
		if (null != it) {
			throw new MALException("pr instance already exists, id: " + id);
		}
	}
	
	/**
	 * Verify that PR inst exists.
	 * @param id
	 * @param store
	 * @return
	 * @throws MALException
	 */
	public static PrInstStore.PrItem prInstExists(Long id, PrInstStore store) throws MALException {
		PrInstStore.PrItem it = store.findPrItem(id);
		if (null == it) {
			throw new MALException("pr instance not found, id: " + id);
		}
		return it;
	}
	
	/**
	 * Verify that PR inst is not null.
	 * @param inst
	 * @throws MALException
	 */
	public static void prInst(PlanningRequestInstanceDetails inst) throws MALException {
		if (null == inst) {
			throw new MALException("pr instance is null");
		}
	}
	
	/**
	 * Verify that both lists have same amount of elements.
	 * @param tasks
	 * @param defIds
	 * @param instIds
	 * @throws MALException
	 */
	public static void listSizes(TaskInstanceDetailsList tasks, LongList defIds, LongList instIds) throws MALException {
		int taskCount = (null != tasks) ? tasks.size() : 0;
		int defIdCount = (null != defIds) ? defIds.size() : 0;
		if (taskCount != defIdCount) {
			throw new MALException("pr tasks count does not match task definition id count");
		}
		int instIdCount = (null != instIds) ? instIds.size() : 0;
		if (defIdCount != instIdCount) {
			throw new MALException("task definition id count does not match task instance id count");
		}
	}
	
	/**
	 * Verify that list elements are not null.
	 * @param tasks
	 * @param defIds
	 * @param instIds
	 * @throws MALException
	 */
	public static void listElements(TaskInstanceDetailsList tasks, Long prId) throws MALException {
		for (int i = 0; (null != tasks) && (i < tasks.size()); ++i) {
			TaskInstanceDetails taskInst = tasks.get(i);
			if (null == taskInst) {
				throw new MALException("task instance[" + i + "] is null");
			}
			if (null == taskInst.getTaskDefId()) {
				throw new MALException("task definition id[" + i + "] is null");
			}
			if (null == taskInst.getId()) {
				throw new MALException("task instance id[" + i + "] is null");
			}
			if (prId != taskInst.getPrInstId()) {
				throw new MALException("task instance[" + i + "].prId doesn't match prInst.id");
			}
		}
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
	 * Check PR instance arguments for matching argument definition.
	 * @param defId
	 * @param inst
	 * @throws MALException
	 */
	public static void prArgs(PlanningRequestInstanceDetails inst, PrDefStore store) throws MALException {
		PlanningRequestDefinitionDetails def = store.find(/*defId*/inst.getPrDefId());
		for (int i = 0; (null != inst.getArgumentValues()) && (i < inst.getArgumentValues().size()); ++i) {
			ArgumentValue argVal = inst.getArgumentValues().get(i);
			ArgumentDefinitionDetails argDef = findArg(def.getArgumentDefs(), argVal.getArgDefName());
			if (null == argDef) {
				throw new MALException("pr argument[" + i + "] has no definition, argName: " + argVal.getArgDefName());
			}
			if (null != argVal && null != argVal.getValue()) {
				Byte type = (byte)(argVal.getValue().getTypeShortForm() & 0xff);
				if (type != argDef.getAttributeType()) {
					throw new MALException("pr argument[" + i + "] value (" + argVal.getArgDefName() +
							") type (" + argVal.getTypeShortForm() + ") does not match defined (" +
							argDef.getName() + ") type (" + argDef.getTypeShortForm() + ")");
				}
			} // else null - no value - no type
		}
	}
	
	/**
	 * Check Task instance arguments for matching argument definition.
	 * @param def
	 * @param inst
	 * @throws MALException
	 */
	protected static void taskArgs(TaskDefinitionDetails def, TaskInstanceDetails inst) throws MALException {
		for (int i = 0; (null != inst.getArgumentValues()) && (i < inst.getArgumentValues().size()); ++i) {
			ArgumentValue argVal = inst.getArgumentValues().get(i);
			ArgumentDefinitionDetails argDef = findArg(def.getArgumentDefs(), argVal.getArgDefName());
			if (null == argDef) {
				throw new MALException("task argument[" + i + "] has no definition: " + argVal.getArgDefName());
			}
			if (null != argVal && null != argVal.getValue()) {
				Byte type = (byte)(argVal.getValue().getTypeShortForm() & 0xff);
				if (type != argDef.getAttributeType()) {
					throw new MALException("task argument[" + i + "] value (" + argVal.getArgDefName() +
							") type (" + argVal.getTypeShortForm() + ") does not match defined (" +
							argDef.getName() + ") type (" + argDef.getTypeShortForm() + ")");
				}
			} // else null
		}
	}
	
	/**
	 * Check Task instances for matching definition.
	 * @param defIds
	 * @param insts
	 * @throws MALException
	 */
	public static void tasksArgs(TaskInstanceDetailsList insts, /*LongList defIds,*/ TaskDefStore store) throws MALException {
		for (int i = 0; (null != insts) && (i < insts.size()); ++i) {
			Long defId = /*defIds.get(i)*/insts.get(i).getTaskDefId();
			TaskDefinitionDetails def = store.find(defId);
			if (null == def) {
				throw new MALException("task def id does not exist: " + defId);
			}
			TaskInstanceDetails inst = insts.get(i);
			taskArgs(def, inst);
		}
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
	public static void idList(IdentifierList ids) throws MALException {
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
}
