package esa.mo.inttest.sch.provider;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

public class DefStore {

	private long lastId = 0;
	private Map<Long, ScheduleDefinitionDetails> defs = new HashMap<Long, ScheduleDefinitionDetails>();
	
	public DefStore() {
	}
	
	public LongList list(IdentifierList schNames) throws MALException {
		LongList ids = new LongList();
//		for (int i = 0; i < schNames.size(); ++i) {
			ids.addAll(defs.keySet()); // TODO filtering
//		}
		return ids;
	}
	
	public LongList addAll(ScheduleDefinitionDetailsList schDefs) throws MALException {
		LongList ids = new LongList();
		for (int i = 0; i < schDefs.size(); ++i) {
			ScheduleDefinitionDetails schDef = schDefs.get(i);
			if (null == schDef) {
				throw new MALException("schedule definition[" + i + "] is null");
			}
			Long id = new Long(++lastId);
			defs.put(id, schDef);
			ids.add(id);
		}
		return ids;
	}
	
	protected void update(Long id, ScheduleDefinitionDetails def) throws MALException {
		ScheduleDefinitionDetails def2 = defs.get(id);
		if (null == def2) {
			throw new MALException("no schedule definition with id: " + id);
		}
		defs.put(id, def);
	}
	
	public void updateAll(LongList ids, ScheduleDefinitionDetailsList defs) throws MALException {
		for (int i = 0; i < defs.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule def id[" + i + "] is null");
			}
			ScheduleDefinitionDetails def = defs.get(i);
			if (null == def) {
				throw new MALException("schedule def[" + i + "] is null");
			}
			update(id, def);
		}
	}
	
	public void removeAll(LongList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("schedule def id[" + i + "] is null");
			}
			ScheduleDefinitionDetails def = defs.get(id);
			if (null == def) {
				throw new MALException("no schedule definition with id: " + id);
			}
			defs.remove(id);
		}
	}
}
