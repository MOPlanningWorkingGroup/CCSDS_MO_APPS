package esa.mo.inttest.sch.provider;

import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;

/**
 * Schedule consistency checks and error messages.
 */
public class Check {

	/**
	 * Hidden ctor.
	 */
	private Check() {
	}
	
	/**
	 * Check schedule id list.
	 * @param ids
	 * @throws MALException
	 */
	public static void schInstIdList(LongList ids) throws MALException {
		if (null == ids) {
			throw new MALException("schedule instance id list is null");
		}
		if (ids.isEmpty()) {
			throw new MALException("schedule instance id list is empty");
		}
	}
	
	/**
	 * Check schedule id list elements.
	 * @param ids
	 * @throws MALException
	 */
	public static void schInstIds(LongList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule instance id[" + i + "] is null");
			}
		}
	}
	
	public static List<InstStore.SchItem> schInstsExist(LongList ids, InstStore insts) throws MALException {
		List<InstStore.SchItem> items = new ArrayList<InstStore.SchItem>();
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			InstStore.SchItem item = insts.findSchItem(id);
			if (null == item) {
				throw new MALException("schedule instance id[" + i + "] not found, id: " + id);
			}
			items.add(item);
		}
		return items;
	}
	
	/**
	 * Check schedule exists.
	 * @param id
	 * @param insts
	 * @return
	 * @throws MALException
	 */
	public static InstStore.SchItem schInstExists(Long id, InstStore insts) throws MALException {
		InstStore.SchItem item = insts.findSchItem(id);
		if (null == item) {
			throw new MALException("schedule instance not found, id: " + id);
		}
		return item;
	}
	
	/**
	 * Check patch framgents.
	 * @param remove
	 * @param update
	 * @param add
	 * @throws MALException
	 */
	public static void patchLists(ScheduleInstanceDetailsList remove,
			ScheduleInstanceDetailsList update, ScheduleInstanceDetailsList add) throws MALException {
		boolean noRemove = (null == remove) || (remove.isEmpty());
		boolean noUpdate = (null == update) || (update.isEmpty());
		boolean noAdd = (null == add) || (add.isEmpty());
		if (noRemove && noUpdate && noAdd) {
			throw new MALException("nothing to patch, lists are null/empty");
		}
	}
	
	protected static ArgumentValue findArg(Identifier name, ArgumentValueList args) {
		ArgumentValue val = null;
		for (int i = 0; (null == val) && (null != args) && (i < args.size()); ++i) {
			ArgumentValue argVal = args.get(i);
			if (name.equals(argVal.getArgDefName())) {
				val = argVal;
			}
		}
		return val;
	}

	protected static void schArg(int i, int j, ArgumentValue val, String pre) throws MALException {
		if (null == val) {
			throw new MALException(pre+"schedule instance[" + i + "].argVal[" + j + "] is null");
		}
	}
	
	protected static void schArgName(int i, int j, Identifier name, String pre) throws MALException {
		if (null == name || null == name.getValue()) {
			throw new MALException(pre+"schedule instance[" + i + "].argVal[" + j + "].name is null");
		}
		if (name.getValue().isEmpty()) {
			throw new MALException(pre+"schedule instance[" + i + "].argVal[" + j + "].name is empty");
		}
	}
	
	protected static void schArgs(int i, ArgumentValueList vals, ArgumentValueList args) throws MALException {
		for (int j = 0; (null != vals) && (j < vals.size()); ++j) {
			ArgumentValue val = vals.get(j);
			schArg(i, j, val, "remove ");
			schArgName(i, j, val.getArgDefName(), "remove ");
			ArgumentValue argVal = findArg(val.getArgDefName(), args);
			if (null == argVal) {
				throw new MALException("remove schedule instance[" + i + "].argVal[" + j + "] not found by name: " + val.getArgDefName());
			}
		}
	}

	/**
	 * Check schedule for nullpointer.
	 * @param schInst
	 * @throws MALException
	 */
	protected static void schInst(int i, ScheduleInstanceDetails sch, String pre) throws MALException {
		if (null == sch) {
			throw new MALException(pre+"schedule instance[" + i + "] is null");
		}
	}

	/**
	 * Check schedule for id.
	 * @param id
	 * @throws MALException
	 */
	protected static void schInstId(int i, Long id, String pre) throws MALException {
		if (null == id) {
			throw new MALException(pre+"schedule instance[" + i + "] id is null");
		}
	}

	/**
	 * Check patch remove list.
	 * @param remove
	 * @param store
	 * @throws MALException
	 */
	public static void patchRemove(ScheduleInstanceDetailsList remove, InstStore store) throws MALException {
		for (int i = 0; (null != remove) && (i < remove.size()); ++i) {
			ScheduleInstanceDetails sch = remove.get(i);
			schInst(i, sch, "remove ");
			schInstId(i, sch.getId(), "remove ");
			InstStore.SchItem it = store.findSchItem(sch.getId());
			if (null == it) {
				throw new MALException("can't find schedule instance[" + i + "] to remove from, id: " + sch.getId());
			}
			schArgs(i, sch.getArgumentValues(), it.sch.getArgumentValues());
		}
	}
	
	/**
	 * Check patch update list.
	 * @param update
	 * @param insts
	 * @throws MALException
	 */
	public static void patchUpdate(ScheduleInstanceDetailsList update, InstStore insts) throws MALException {
		for (int i = 0; (null != update) && (i < update.size()); ++i) {
			ScheduleInstanceDetails sch = update.get(i);
			schInst(i, sch, "update ");
			schInstId(i, sch.getId(), "update ");
			InstStore.SchItem it = insts.findSchItem(sch.getId());
			if (null == it) {
				throw new MALException("can't find schedule instance[" + i + "] to update, id: " + sch.getId());
			}
		}
	}

	/**
	 * Check patch add list.
	 * @param add
	 * @param insts
	 * @throws MALException
	 */
	public static void patchAdd(ScheduleInstanceDetailsList add, InstStore insts) throws MALException {
		for (int i = 0; (null != add) && (i < add.size()); ++i) {
			ScheduleInstanceDetails sch = add.get(i);
			schInst(i, sch, "add ");
			schInstId(i, sch.getId(), "add ");
			InstStore.SchItem it = insts.findSchItem(sch.getId());
			if (null == it) {
				throw new MALException("can't find schedule instance[" + i + "] to add to, id: " + sch.getId());
			}
		}
	}
	
	/**
	 * Check identifiers list.
	 * @param names
	 * @throws MALException
	 */
	public static void namesList(IdentifierList names) throws MALException {
		if (null == names) {
			throw new MALException("schedule names list is null");
		}
		if (names.isEmpty()) {
			throw new MALException("schedule names list is empty");
		}
	}
	
	/**
	 * Check definitions list.
	 * @param defs
	 * @throws MALException
	 */
	public static void defsList(ScheduleDefinitionDetailsList defs) throws MALException {
		if (null == defs) {
			throw new MALException("schedule definitions list is null");
		}
		if (defs.isEmpty()) {
			throw new MALException("schedule definitions list is empty");
		}
	}
	
	public static void schInstList(ScheduleInstanceDetailsList insts) throws MALException {
		if (null == insts) {
			throw new MALException("schedule instance list is null");
		}
		if (insts.isEmpty()) {
			throw new MALException("schedule instance list is empty");
		}
	}

	/**
	 * Check schedule for definition id.
	 * @param id
	 * @throws MALException
	 */
	protected static void schDefId(int i, Long id) throws MALException {
		if (null == id) {
			throw new MALException("schedule definition[" + i + "] id is null");
		}
	}

	/**
	 * Check schedule definition exists.
	 * @param id
	 * @param defs
	 * @throws MALException
	 */
	protected static ScheduleDefinitionDetails schDefExists(int i, Long id, DefStore defs) throws MALException {
		ScheduleDefinitionDetails def = defs.find(id);
		if (null == def) {
			throw new MALException("schedule instance[" + i + "] definition not found by id: " + id);
		}
		return def;
	}

	/**
	 * Check schedule doesn't exist.
	 * @param id
	 * @param insts
	 * @throws MALException
	 */
	protected static void schInstNoExist(int i, Long id, InstStore insts) throws MALException {
		InstStore.SchItem item = insts.findSchItem(id);
		if (null != item) {
			throw new MALException("schedule instance[" + i + "] already exists, id: " + id);
		}
	}

	protected static void schItem(int i, int j, ScheduleItemInstanceDetails item) throws MALException {
		if (null == item) {
			throw new MALException("schedule instance[" + i + "].item[" + j + "] is null");
		}
	}
	
	protected static void schItemId(int i, int j, Long id) throws MALException {
		if (null == id) {
			throw new MALException("schedule instance[" + i + "].item[" + j + "].id is null");
		}
	}
	
	protected static void schItemSchId(int i, int j, Long itemSchId, Long schId) throws MALException {
		if (itemSchId != schId) {
			throw new MALException("schedule instance[" + i + "].item[" + j + "].schId doesn't match schInst.id");
		}
	}
	
	protected static void schItemArg(int i, int j, int k, ArgumentValue arg) throws MALException {
		if (null == arg) {
			throw new MALException("schedule instance[" + i + "].item[" + j + "].arg[" + k + "] is null");
		}
	}
	
	protected static void schItemArgName(int i, int j, int k, Identifier name) throws MALException {
		if (null == name || null == name.getValue()) {
			throw new MALException("schedule instance[" + i + "].item[" + j + "].arg[" + k + "].name is null");
		}
		if (name.getValue().isEmpty()) {
			throw new MALException("schedule instance[" + i + "].item[" + j + "].arg[" + k + "].name is empty");
		}
	}
	
	protected static void schItemArgs(int i, int j, ScheduleItemInstanceDetails item) throws MALException {
		ArgumentValueList argVals = item.getArgumentValues();
		// arguments are optional
		for (int k = 0; (null != argVals) && (k < argVals.size()); ++k) {
			ArgumentValue argVal = argVals.get(k);
			schItemArg(i, j, k, argVal);
			schItemArgName(i, j, k, argVal.getArgDefName());
			// can't verify arg def because item has no def
		}
	}
	
	protected static ArgumentDefinitionDetails findArg(Identifier name, ArgumentDefinitionDetailsList args) {
		ArgumentDefinitionDetails def = null;
		for (int i = 0; (null == def) && (null != args) && (i < args.size()); ++i) {
			ArgumentDefinitionDetails argDef = args.get(i);
			if (name.equals(argDef.getName())) {
				def = argDef;
			}
		}
		return def;
	}
	
	protected static ArgumentDefinitionDetails schArgDefExists(int i, int j, Identifier name,
			ArgumentDefinitionDetailsList defs) throws MALException {
		ArgumentDefinitionDetails argDef = findArg(name, defs);
		if (null == argDef) {
			throw new MALException("schedule instance[" + i + "].arg[" + j + "] definition can't be found by name: " + name);
		}
		return argDef;
	}
	
	protected static void schArgVal(int i, int j, Attribute val, ArgumentDefinitionDetails def) throws MALException {
		if (null != val) {
			byte type = (byte)(val.getTypeShortForm() & 0xff);
			if (type != def.getAttributeType()) {
				throw new MALException("schedule instance[" + i + "].arg[" + j + "] value type (" +
						type + ") does not match defined type (" + def.getTypeShortForm() + ")");
			}
		}
	}
	
	protected static void schArgs(int i, ScheduleInstanceDetails sch, ScheduleDefinitionDetails def) throws MALException {
		ArgumentValueList args = sch.getArgumentValues();
		// args are optional
		for (int j = 0; (null != args) && (j < args.size()); ++j) {
			ArgumentValue arg = args.get(j);
			schArg(i, j, arg, "");
			schArgName(i, j, arg.getArgDefName(), "");
			ArgumentDefinitionDetails argDef = schArgDefExists(i, j, arg.getArgDefName(), def.getArgumentDefs());
			schArgVal(i, j, arg.getValue(), argDef);
		}
	}
	
	protected static void schItemNoExist(int i, int j, Long id, InstStore insts) throws MALException {
		InstStore.ItemItem item = insts.findItemItem(id);
		if (null != item) {
			throw new MALException("schedule instance[" + i + "].item[" + j + "] already exists with id: " + id);
		}
	}
	
	/**
	 * Check schedule items list elements.
	 * @param items
	 * @param schId
	 * @throws MALException
	 */
	protected static void addSchItems(int i, ScheduleInstanceDetails sch, InstStore insts) throws MALException {
		ScheduleItemInstanceDetailsList items = sch.getScheduleItems();
		// items are optional, list may be null and empty
		for (int j = 0; (null != items) && (j < items.size()); ++j) {
			ScheduleItemInstanceDetails item = items.get(i);
			schItem(i, j, item);
			schItemId(i, j, item.getId());
			schItemSchId(i, j, item.getSchInstId(), sch.getId());
			// item doesn't have definition
			schItemArgs(i, j, item);
			schItemNoExist(i, j, item.getId(), insts);
		}
	}

	public static void addSchInsts(ScheduleInstanceDetailsList scheds, DefStore defs, InstStore insts) throws MALException {
		for (int i = 0; i < scheds.size(); ++i) {
			ScheduleInstanceDetails sch = scheds.get(i);
			schInst(i, sch, "");
			schInstId(i, sch.getId(), "");
			schDefId(i, sch.getSchDefId());
			ScheduleDefinitionDetails def = schDefExists(i, sch.getSchDefId(), defs);
			schArgs(i, sch, def);
			schInstNoExist(i, sch.getId(), insts);
			addSchItems(i, sch, insts);
		}
	}
	
	protected static InstStore.SchItem schInstExists(int i, Long id, InstStore insts) throws MALException {
		InstStore.SchItem item = insts.findSchItem(id);
		if (null == item) {
			throw new MALException("schedule instance[" + i + "] not found by id: " + id);
		}
		return item;
	}
	
	protected static void updateSchItems(int i, ScheduleInstanceDetails sch) throws MALException {
		ScheduleItemInstanceDetailsList items = sch.getScheduleItems();
		// items are optional, list may be null and empty
		for (int j = 0; (null != items) && (j < items.size()); ++j) {
			ScheduleItemInstanceDetails item = items.get(i);
			schItem(i, j, item);
			schItemId(i, j, item.getId());
			schItemSchId(i, j, item.getSchInstId(), sch.getId());
			// item doesn't have definition
			schItemArgs(i, j, item);
			// item exists -> update; doesn exist -> add
		}
	}
	
	public static List<InstStore.SchItem> updateSchInsts(ScheduleInstanceDetailsList scheds, DefStore defs, InstStore insts) throws MALException {
		List<InstStore.SchItem> items = new ArrayList<InstStore.SchItem>();
		for (int i = 0; i < scheds.size(); ++i) {
			ScheduleInstanceDetails sch = scheds.get(i);
			schInst(i, sch, "");
			schInstId(i, sch.getId(), "");
			schDefId(i, sch.getSchDefId());
			ScheduleDefinitionDetails def = schDefExists(i, sch.getSchDefId(), defs);
			schArgs(i, sch, def);
			// schedule has to exist for update
			items.add(schInstExists(i, sch.getId(), insts));
			updateSchItems(i, sch); // items don't have to exist
		}
		return items;
	}
}
