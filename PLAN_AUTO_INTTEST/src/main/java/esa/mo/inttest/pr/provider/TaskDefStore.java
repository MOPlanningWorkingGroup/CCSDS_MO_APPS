package esa.mo.inttest.pr.provider;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;

/**
 * Task definitions storage.
 */
public class TaskDefStore {

	private long lastTaskId = 0L;
	private Map<Long, TaskDefinitionDetails> map = new HashMap<Long, TaskDefinitionDetails>();
	
	/**
	 * Looks up Task def by id.
	 * @param id
	 * @return
	 */
	public TaskDefinitionDetails find(Long id) {
		return map.get(id);
	}
	
	/**
	 * Looks up Task def by identifier.
	 * @param id
	 * @return
	 */
	public LongList list(Identifier id) {
		LongList list = new LongList();
		list.addAll(map.keySet()); // TODO filtering
		return list;
	}
	
	/**
	 * Looks up Task defs by identfiers.
	 * @param ids
	 * @return
	 * @throws MALException
	 */
	public LongList listAll(IdentifierList ids) throws MALException {
		LongList list = new LongList();
		for (int i = 0; i < ids.size(); ++i) {
			Identifier id = ids.get(i);
			if (null == id) {
				throw new MALException("name[" + i + "] is null");
			}
			list.addAll(list(id));
		}
		return list;
	}
	
	/**
	 * Adds single Task def and returns id.
	 * @param def
	 * @return
	 */
	public Long add(TaskDefinitionDetails def) {
		Long id = new Long(++lastTaskId);
		map.put(id, def);
		return id;
	}
	
	/**
	 * Adds several Task defs and returns list of ids.
	 * @param defs
	 * @return
	 * @throws MALException
	 */
	public LongList addAll(TaskDefinitionDetailsList defs) throws MALException {
		LongList ids = new LongList();
		for (int i = 0; i < defs.size(); ++i) {
			TaskDefinitionDetails def = defs.get(i);
			if (null == def) {
				throw new MALException("task definition[" + i + "] is null");
			}
			ids.add(add(def));
		}
		return ids;
	}
	
	/**
	 * Replaces single Task def by id.
	 * @param id
	 * @param def
	 */
	public void update(Long id, TaskDefinitionDetails def) {
		map.put(id, def);
	}
	
	/**
	 * Replaces several Task defs by id.
	 * @param ids
	 * @param defs
	 * @throws MALException
	 */
	public void updateAll(LongList ids, TaskDefinitionDetailsList defs) throws MALException {
		for (int i = 0; i < defs.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("task definition id[" + i + "] is null");
			}
			TaskDefinitionDetails def = defs.get(i);
			if (null == def) {
				throw new MALException("task definition[" + i + "] is null");
			}
			update(id, def);
		}
	}
	
	/**
	 * Removes single Task def by id.
	 * @param id
	 */
	public void remove(Long id) {
		map.remove(id);
	}
	
	/**
	 * Removes several Task defs by id.
	 * @param ids
	 * @throws MALException
	 */
	public void removeAll(LongList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("task def id[" + i + "] is null");
			}
			remove(id);
		}
	}
}
