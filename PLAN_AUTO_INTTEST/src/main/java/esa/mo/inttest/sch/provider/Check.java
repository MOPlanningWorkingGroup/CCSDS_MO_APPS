package esa.mo.inttest.sch.provider;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * Schedule consistency checks and error messages.
 */
public class Check {

	/**
	 * Check schedule for nullpointer.
	 * @param schInst
	 * @throws MALException
	 */
	public static void schInst(ScheduleInstanceDetails schInst) throws MALException {
		if (null == schInst) {
			throw new MALException("schedule instance is null");
		}
	}
	
	/**
	 * Check schedule for id.
	 * @param id
	 * @throws MALException
	 */
	public static void schInstId(Long id) throws MALException {
		if (null == id) {
			throw new MALException("schedule instance id is null");
		}
	}
	
	/**
	 * Check schedule for definition id.
	 * @param id
	 * @throws MALException
	 */
	public static void schDefId(Long id) throws MALException {
		if (null == id) {
			throw new MALException("schedule definition id is null");
		}
	}
	
	/**
	 * Check schedule definition exists.
	 * @param id
	 * @param defs
	 * @throws MALException
	 */
	public static void schDefExists(Long id, DefStore defs) throws MALException {
		ScheduleDefinitionDetails def = defs.find(id);
		if (null == def) {
			throw new MALException("schedule definition not found, id: " + id);
		}
	}
	
	/**
	 * Check schedule doesn't exist.
	 * @param id
	 * @param insts
	 * @throws MALException
	 */
	public static void schInstNoExist(Long id, InstStore insts) throws MALException {
		InstStore.Item item = insts.findItem(id);
		if (null != item) {
			throw new MALException("schedule instance already exists, id: " + id);
		}
	}
	
	/**
	 * Check schedule items list elements.
	 * @param items
	 * @param schId
	 * @throws MALException
	 */
	public static void listElements(ScheduleItemInstanceDetailsList items, Long schId) throws MALException {
		for (int i = 0; (null != items) && (i < items.size()); ++i) {
			ScheduleItemInstanceDetails item = items.get(i);
			if (null == item) {
				throw new MALException("schedule item[" + i + "] is null");
			}
			if (null == item.getId()) {
				throw new MALException("schedule item[" + i + "].id is null");
			}
			if (schId != item.getSchInstId()) {
				throw new MALException("schedule item[" + i + "].schId doesn't match schInst.id");
			}
		}
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
	
	/**
	 * Check schedule exists.
	 * @param id
	 * @param insts
	 * @return
	 * @throws MALException
	 */
	public static InstStore.Item schInstExists(Long id, InstStore insts) throws MALException {
		InstStore.Item item = insts.findItem(id);
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
	
	/**
	 * Check patch remove list.
	 * @param remove
	 * @param insts
	 * @throws MALException
	 */
	public static void patchRemove(ScheduleInstanceDetailsList remove, InstStore insts) throws MALException {
		for (int i = 0; (null != remove) && (i < remove.size()); ++i) {
			ScheduleInstanceDetails sch = remove.get(i);
			if (null == sch) {
				throw new MALException("remove schedule instance[" + i + "] is null");
			}
			if (null == sch.getId()) {
				throw new MALException("remove schedule instance[" + i + "].id is null");
			}
			InstStore.Item it = insts.findItem(sch.getId());
			if (null == it) {
				throw new MALException("can't find schedule to remove from[" + i + "] by id: " + sch.getId());
			}
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
			if (null == sch) {
				throw new MALException("update schedule instance[" + i + "] is null");
			}
			if (null == sch.getId()) {
				throw new MALException("update schedule instance[" + i + "].id is null");
			}
			InstStore.Item it = insts.findItem(sch.getId());
			if (null == it) {
				throw new MALException("can't find schedule to update from[" + i + "] by id: " + sch.getId());
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
			if (null == sch) {
				throw new MALException("add schedule instance[" + i + "] is null");
			}
			if (null == sch.getId()) {
				throw new MALException("add schedule instance[" + i + "].id is null");
			}
			InstStore.Item it = insts.findItem(sch.getId());
			if (null == it) {
				throw new MALException("can't find schedule to add to[" + i + "] by id: " + sch.getId());
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
}
