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
	
	public TaskDefStore() {
	}
	
	public LongList list(Identifier id) {
		LongList list = new LongList();
		list.addAll(map.keySet()); // TODO filtering
		return list;
	}
	
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
	
	public Long add(TaskDefinitionDetails def) {
		Long id = new Long(++lastTaskId);
		map.put(id, def);
		return id;
	}
	
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
	
	public void update(Long id, TaskDefinitionDetails def) {
		map.put(id, def);
	}
	
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
	
	public void remove(Long id) {
		map.remove(id);
	}
	
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
