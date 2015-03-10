package esa.mo.inttest.pr.provider;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;

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
	
	public Long add(TaskDefinitionDetails def) {
		Long id = new Long(++lastTaskId);
		map.put(id, def);
		return id;
	}
	
	public void update(Long id, TaskDefinitionDetails def) {
		map.put(id, def);
	}
	
	public void remove(Long id) {
		map.remove(id);
	}
}
