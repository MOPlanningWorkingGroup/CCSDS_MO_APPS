/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.sch.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * Schedule definitions storage.
 */
public class DefStore {

	private AtomicLong lastId = new AtomicLong(0L);
	private Map<Long, ScheduleDefinitionDetails> defs = new HashMap<Long, ScheduleDefinitionDetails>();
	
	/**
	 * Default ctor.
	 */
	public DefStore() {
	}
	
	protected LongList list(Identifier name) {
		LongList ids = new LongList();
		Iterator<ScheduleDefinitionDetails> it = defs.values().iterator();
		while (it.hasNext()) {
			ScheduleDefinitionDetails def = it.next();
			if ("*".equals(name.getValue()) || // "all" or "contains"
					def.getName().getValue().contains(name.getValue())) {
				ids.add(def.getId());
			}
		}
		return ids;
	}
	
	/**
	 * Returns schedule definition ids.
	 * @param schNames
	 * @return
	 * @throws MALException
	 */
	public LongList list(IdentifierList schNames) {
		LongList ids = new LongList();
		for (int i = 0; i < schNames.size(); ++i) {
			Identifier name = schNames.get(i);
			ids.addAll(list(name));
		}
		return ids;
	}
	
	/**
	 * Returns id for new schedule definition.
	 * @return
	 */
	protected long generateId() {
		return lastId.incrementAndGet();
	}
	
	/**
	 * Stores single schedule definition.
	 * @param sch
	 * @return Long id
	 */
	protected Long add(ScheduleDefinitionDetails sch) {
		Long id = generateId();
		sch.setId(id);
		defs.put(id, sch);
		return id;
	}
	
	/**
	 * Stores multiple schedules.
	 * @param schDefs
	 * @return
	 * @throws MALException
	 */
	public LongList addAll(ScheduleDefinitionDetailsList schDefs) throws MALException {
		LongList ids = new LongList();
		for (int i = 0; i < schDefs.size(); ++i) {
			ScheduleDefinitionDetails schDef = schDefs.get(i);
			if (null == schDef) {
				throw new MALException("schedule definition[" + i + "] is null");
			}
			ids.add(add(schDef));
		}
		return ids;
	}
	
	/**
	 * Updates single schedule.
	 * @param id
	 * @param def
	 * @throws MALException
	 */
	protected void update(ScheduleDefinitionDetails def) throws MALException {
		ScheduleDefinitionDetails def2 = defs.get(def.getId());
		if (null == def2) {
			throw new MALException("no schedule definition with id: " + def.getId());
		}
		defs.put(def.getId(), def);
	}
	
	/**
	 * Updates multiple schedules.
	 * @param ids
	 * @param defs
	 * @throws MALException
	 */
	public void updateAll(ScheduleDefinitionDetailsList defs) throws MALException {
		for (int i = 0; i < defs.size(); ++i) {
			ScheduleDefinitionDetails def = defs.get(i);
			if (null == def) {
				throw new MALException("schedule def[" + i + "] is null");
			}
			if (null == def.getId()) {
				throw new MALException("schedule def[" + i + "].id is null");
			}
			update(def);
		}
	}
	
	/**
	 * Removes multiple schedules.
	 * @param ids
	 * @throws MALException
	 */
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
	
	/**
	 * Looks up schedule definition by id.
	 * @param id
	 * @return
	 */
	public ScheduleDefinitionDetails find(Long id) {
		return defs.get(id);
	}
}
