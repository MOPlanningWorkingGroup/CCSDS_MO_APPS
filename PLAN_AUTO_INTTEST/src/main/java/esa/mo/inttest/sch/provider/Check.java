package esa.mo.inttest.sch.provider;

import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

import esa.mo.inttest.Util;

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
	public static void patchList(ScheduleInstanceDetailsList changes) throws MALException {
		if (null == changes) {
			throw new MALException("changes list is null");
		}
		if (changes.isEmpty()) {
			throw new MALException("changes list is empty");
		}
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
	
	protected static void schArgs(int i, ArgumentValueList src, ArgumentValueList dst, String pre) throws MALException {
		for (int j = 0; (null != src) && (j < src.size()); ++j) {
			ArgumentValue val = src.get(j);
			schArg(i, j, val, pre);
			schArgName(i, j, val.getArgDefName(), "change ");
			ArgumentValue argVal = Util.findArg(val.getArgDefName(), dst);
			if (null == argVal) {
				throw new MALException("change schedule instance[" + i + "].argVal[" + j + "] not found by name: " + val.getArgDefName());
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

	protected static void schType(int i, ScheduleType ct, String pre) throws MALException {
		if (null == ct) {
			throw new MALException(pre+"schedule instance[" + i + "].schType is null");
		}
		boolean accepted = false;
		if (ScheduleType.INCREMENT_REMOVE == ct) {
			accepted = true;
		} else if (ScheduleType.INCREMENT_ADD == ct) {
			accepted = true;
		}
		if (!accepted) {
			throw new MALException(pre+"schedule instance[" + i + "].schType must be ADD/UPDATE/REMOVE");
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

	protected static void schTim(int i, int j, TimingDetails tim, String pre) throws MALException {
		if (null == tim) {
			throw new MALException(pre+"schedule instance[" + i + "].timing[" + j + "] is null");
		}
	}

	protected static void schTrigName(int i, int j, TriggerName tn, String pre) throws MALException {
		if (null == tn) {
			throw new MALException(pre+"schedule instance[" + i + "].trig[" + j + "].name is null");
		}
	}

	protected static void schTims(int i, TimingDetailsList src, TimingDetailsList dst, String pre)
			throws MALException {
		for (int j = 0; (null != src) && (j < src.size()); ++j) {
			TimingDetails tim = src.get(j);
			schTim(i, j, tim, pre);
			schTrigName(i, j, tim.getTriggerName(), pre);
			TimingDetails tim2 = Util.findTiming(tim.getTriggerName(), dst);
			if (null == tim2) {
				throw new MALException("change schedule instance[" + i + "].timing[" + j + "] not found by name: " + tim.getTriggerName());
			}
		}
	}

	protected static void schItem(int i, int j, ScheduleItemInstanceDetails item, String pre)
			throws MALException {
		if (null == item) {
			throw new MALException(pre+"schedule instance[" + i + "].item[" + j + "] is null");
		}
	}

	protected static void schItemId(int i, int j, Long id, String pre)
			throws MALException {
		if (null == id) {
			throw new MALException(pre+"schedule instance[" + i + "].item[" + j + "].id is null");
		}
	}

	protected static void schItems(int i, ScheduleItemInstanceDetailsList src,
			ScheduleItemInstanceDetailsList dst, InstStore store, String pre) throws MALException {
		for (int j = 0; (null != src) && (j < src.size()); ++j) {
			ScheduleItemInstanceDetails item = src.get(j);
			schItem(i, j, item, pre);
			schItemId(i, j, item.getId(), pre);
			ScheduleItemInstanceDetails it2 = Util.findItem(item.getId(), dst);
			if (null == it2) {
				throw new MALException(pre+"schedule instance[" + i + "].item[" + j + "] not found by id: " + item.getId());
			}
		}
	}

	protected static void addSchArgs(int i, ArgumentValueList src, ArgumentValueList dst,
			ScheduleDefinitionDetails def, String pre) throws MALException {
		for (int j = 0; (null != src) && (j < src.size()); ++j) {
			ArgumentValue arg = src.get(j);
			schArg(i, j, arg, pre);
			schArgName(i, j, arg.getArgDefName(), pre);
			ArgumentValue argVal = Util.findArg(arg.getArgDefName(), dst);
			if (null != argVal) {
				throw new MALException(pre+"schedule instance[" + i + "].arg[" + j + "] already has arg, name: " + arg.getArgDefName());
			}
			ArgumentDefinitionDetails argDef = schArgDefExists(i, j, arg.getArgDefName(), def.getArgumentDefs());
			schArgVal(i, j, arg.getValue(), argDef);
		}
	}
	
	protected static void addSchTims(int i, TimingDetailsList src, TimingDetailsList dst, String pre) throws MALException {
		for (int j = 0; (null != src) && (j < src.size()); ++j) {
			TimingDetails tim = src.get(j);
			schTim(i, j, tim, pre);
			schTrigName(i, j, tim.getTriggerName(), pre);
			TimingDetails tim2 = Util.findTiming(tim.getTriggerName(), dst);
			if (null != tim2) {
				throw new MALException(pre+"schedule instance[" + i + "].timing[" + j + "] already exists, name: " + tim.getTriggerName());
			}
		}
	}
	
	protected static void addSchItems(int i, ScheduleItemInstanceDetailsList src,
			ScheduleItemInstanceDetailsList dst, String pre) throws MALException {
		for (int j = 0; (null != src) && (j < src.size()); ++j) {
			ScheduleItemInstanceDetails item = src.get(j);
			schItem(i, j, item, pre);
			schItemId(i, j, item.getId(), pre);
//			schItemSchId(i, j, item.getSchInstId(), sch.getId()); // FIXME ignore, overwrite?
			// item doesn't have definition
			schItemArgs(i, j, item);
			ScheduleItemInstanceDetails item2 = Util.findItem(item.getId(), dst);
			if (null != item2) {
				throw new MALException(pre+"schedule instance[" + i + "].item[" + j + "] alread exists, id: "+item.getId());
			}
		}
	}
	
	/**
	 * Check patch remove list.
	 * @param change
	 * @param insts
	 * @throws MALException
	 */
	public static void patches(ScheduleInstanceDetailsList change, DefStore defs, InstStore insts) throws MALException {
		String pre = "change ";
		for (int i = 0; (null != change) && (i < change.size()); ++i) {
			ScheduleInstanceDetails sch = change.get(i);
			schInst(i, sch, pre);
			schType(i, sch.getScheduleType(), pre);
			schInstId(i, sch.getId(), pre);
			InstStore.SchItem it = insts.findSchItem(sch.getId());
			if (null == it) {
				throw new MALException("can't find schedule instance[" + i + "] to change, id: " + sch.getId());
			}
			if (ScheduleType.INCREMENT_REMOVE == sch.getScheduleType()) {
				schArgs(i, sch.getArgumentValues(), it.sch.getArgumentValues(), pre);
				schTims(i, sch.getTimingConstraints(), it.sch.getTimingConstraints(), pre);
				schItems(i, sch.getScheduleItems(), it.sch.getScheduleItems(), insts, pre);
			} else if (ScheduleType.INCREMENT_ADD == sch.getScheduleType()) {
				ScheduleDefinitionDetails def = schDefExists(i, it.sch.getSchDefId(), defs);
				addSchArgs(i, sch.getArgumentValues(), it.sch.getArgumentValues(), def, pre);
				addSchTims(i, sch.getTimingConstraints(), it.sch.getTimingConstraints(), pre);
				addSchItems(i, sch.getScheduleItems(), it.sch.getScheduleItems(), pre);
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
	protected static void schDefId(int i, Long id, String pre) throws MALException {
		if (null == id) {
			throw new MALException(pre+"schedule definition[" + i + "] id is null");
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
	
	protected static ArgumentDefinitionDetails schArgDefExists(int i, int j, Identifier name,
			ArgumentDefinitionDetailsList defs) throws MALException {
		ArgumentDefinitionDetails argDef = Util.findArgDef(name, defs);
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
	protected static void addSchItems(int i, ScheduleInstanceDetails sch, InstStore insts, String pre) throws MALException {
		ScheduleItemInstanceDetailsList items = sch.getScheduleItems();
		// items are optional, list may be null and empty
		for (int j = 0; (null != items) && (j < items.size()); ++j) {
			ScheduleItemInstanceDetails item = items.get(i);
			schItem(i, j, item, pre);
			schItemId(i, j, item.getId(), pre);
			schItemSchId(i, j, item.getSchInstId(), sch.getId());
			// item doesn't have definition
			schItemArgs(i, j, item);
			schItemNoExist(i, j, item.getId(), insts);
		}
	}

	public static void addSchInsts(ScheduleInstanceDetailsList scheds, DefStore defs, InstStore insts) throws MALException {
		String pre = "";
		for (int i = 0; i < scheds.size(); ++i) {
			ScheduleInstanceDetails sch = scheds.get(i);
			schInst(i, sch, pre);
			schInstId(i, sch.getId(), pre);
			schDefId(i, sch.getSchDefId(), pre);
			ScheduleDefinitionDetails def = schDefExists(i, sch.getSchDefId(), defs);
			schArgs(i, sch, def);
			schInstNoExist(i, sch.getId(), insts);
			addSchItems(i, sch, insts, pre);
		}
	}
	
	protected static InstStore.SchItem schInstExists(int i, Long id, InstStore insts) throws MALException {
		InstStore.SchItem item = insts.findSchItem(id);
		if (null == item) {
			throw new MALException("schedule instance[" + i + "] not found by id: " + id);
		}
		return item;
	}
	
	protected static void updateSchItems(int i, ScheduleInstanceDetails sch, String pre)
			throws MALException {
		ScheduleItemInstanceDetailsList items = sch.getScheduleItems();
		// items are optional, list may be null and empty
		for (int j = 0; (null != items) && (j < items.size()); ++j) {
			ScheduleItemInstanceDetails item = items.get(i);
			schItem(i, j, item, pre);
			schItemId(i, j, item.getId(), pre);
			schItemSchId(i, j, item.getSchInstId(), sch.getId());
			// item doesn't have definition
			schItemArgs(i, j, item);
			// item exists -> update; doesn exist -> add
		}
	}
	
	public static List<InstStore.SchItem> updateSchInsts(ScheduleInstanceDetailsList scheds,
			DefStore defs, InstStore insts) throws MALException {
		String pre = "";
		List<InstStore.SchItem> items = new ArrayList<InstStore.SchItem>();
		for (int i = 0; i < scheds.size(); ++i) {
			ScheduleInstanceDetails sch = scheds.get(i);
			schInst(i, sch, pre);
			schInstId(i, sch.getId(), pre);
			schDefId(i, sch.getSchDefId(), pre);
			ScheduleDefinitionDetails def = schDefExists(i, sch.getSchDefId(), defs);
			schArgs(i, sch, def);
			// schedule has to exist for update
			items.add(schInstExists(i, sch.getId(), insts));
			updateSchItems(i, sch, pre); // items don't have to exist
		}
		return items;
	}
}
