package esa.mo.inttest.pr.provider;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseDefinitionDetailsList;

/**
 * PR response definitions storage.
 */
public class RespDefStore {

	private long lastRespId = 0L;
	private Map<Long, PlanningRequestResponseDefinitionDetails> respDefs = new HashMap<Long, PlanningRequestResponseDefinitionDetails>();
	
	/**
	 * Look up a def by id.
	 * @param id
	 * @return
	 */
	public PlanningRequestResponseDefinitionDetails find(Long id) {
		return respDefs.get(id);
	}
	
	/**
	 * Look up defs by identifier.
	 * @param id
	 * @return
	 */
	public LongList list(Identifier id) {
		LongList list = new LongList();
		list.addAll(respDefs.keySet()); // TODO filtering
		return list;
	}
	
	/**
	 * Look up defs by identifiers.
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
	 * Adds single PR resp def and returns id.
	 * @param respDef
	 * @return
	 */
	public Long add(PlanningRequestResponseDefinitionDetails respDef) {
		Long id = new Long(++lastRespId);
		respDefs.put(id, respDef);
		return id;
	}
	
	/**
	 * Adds several PR resp defs and returns list of ids.
	 * @param defs
	 * @return
	 * @throws MALException
	 */
	public LongList addAll(PlanningRequestResponseDefinitionDetailsList defs) throws MALException {
		LongList ids = new LongList();
		for (int i = 0; i < defs.size(); ++i) {
			PlanningRequestResponseDefinitionDetails def = defs.get(i);
			if (null == def) {
				throw new MALException("pr response definition[" + i + "] is null");
			}
			ids.add(add(def));
		}
		return ids;
	}
	
	/**
	 * Replaces single PR resp def by id.
	 * @param id
	 * @param respDef
	 */
	public void update(Long id, PlanningRequestResponseDefinitionDetails respDef) {
		respDefs.put(id, respDef);
	}
	
	/**
	 * Replaces several PR resp defs by id.
	 * @param ids
	 * @param defs
	 * @throws MALException
	 */
	public void updateAll(LongList ids, PlanningRequestResponseDefinitionDetailsList defs) throws MALException {
		for (int i = 0; i < defs.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("pr response id[" + i + "] is null");
			}
			PlanningRequestResponseDefinitionDetails def = defs.get(i);
			if (null == def) {
				throw new MALException("pr response[" + i + "] is null");
			}
			update(id, def);
		}
	}
	
	/**
	 * Removes single PR resp def by id.
	 * @param id
	 */
	public void remove(Long id) {
		respDefs.remove(id);
	}
	
	/**
	 * Removes several PR resp defs by id.
	 * @param ids
	 * @throws MALException
	 */
	public void removeAll(LongList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("pr response id[" + i + "] is null");
			}
			remove(id);
		}
	}
}
