package esa.mo.inttest.pr.provider;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;

/**
 * PR definitions storage.
 */
public class PrDefStore {

	private long lastPrId = 0L;
	private Map<Long, PlanningRequestDefinitionDetails> prDefs = new HashMap<Long, PlanningRequestDefinitionDetails>();
	
	/**
	 * Look up a def by id.
	 * @param id
	 * @return
	 */
	public PlanningRequestDefinitionDetails find(Long id) {
		return prDefs.get(id);
	}
	
	/**
	 * Look up defs by identifier.
	 * @param id
	 * @return
	 */
	public LongList list(Identifier id) {
		LongList list = new LongList();
		list.addAll(prDefs.keySet()); // TODO filtering
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
	 * Adds single PR def and returns id.
	 * @param prDef
	 * @return
	 */
	public Long add(PlanningRequestDefinitionDetails prDef) {
		Long id = new Long(++lastPrId);
		prDefs.put(id, prDef);
		return id;
	}
	
	/**
	 * Adds several PR defs and returns list of ids.
	 * @param defs
	 * @return
	 * @throws MALException
	 */
	public LongList addAll(PlanningRequestDefinitionDetailsList defs) throws MALException {
		LongList ids = new LongList();
		for (int i = 0; i < defs.size(); ++i) {
			PlanningRequestDefinitionDetails def = defs.get(i);
			if (null == def) {
				throw new MALException("planning request definition[" + i + "] is null");
			}
			ids.add(add(def));
		}
		return ids;
	}
	
	/**
	 * Replaces single PR def by id.
	 * @param id
	 * @param prDef
	 */
	public void update(Long id, PlanningRequestDefinitionDetails prDef) {
		prDefs.put(id, prDef);
	}
	
	/**
	 * Replaces several PR defs by id.
	 * @param ids
	 * @param defs
	 * @throws MALException
	 */
	public void updateAll(LongList ids, PlanningRequestDefinitionDetailsList defs) throws MALException {
		for (int i = 0; i < defs.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("planning request id[" + i + "] is null");
			}
			PlanningRequestDefinitionDetails def = defs.get(i);
			if (null == def) {
				throw new MALException("planning request[" + i + "] is null");
			}
			update(id, def);
		}
	}
	
	/**
	 * Removes single PR def by id.
	 * @param id
	 */
	public void remove(Long id) {
		prDefs.remove(id);
	}
	
	/**
	 * Removes several PR defs by id.
	 * @param ids
	 * @throws MALException
	 */
	public void removeAll(LongList ids) throws MALException {
		for (int i = 0; i < ids.size(); ++i) {
			Long id = ids.get(i);
			if (null == id) {
				throw new MALException("planning request id[" + i + "] is null");
			}
			remove(id);
		}
	}
}
