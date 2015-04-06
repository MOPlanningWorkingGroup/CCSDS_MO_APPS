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
	
	public PrDefStore() {
	}
	
	public LongList list(Identifier id) {
		LongList list = new LongList();
		list.addAll(prDefs.keySet()); // TODO filtering
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
	
	public Long add(PlanningRequestDefinitionDetails prDef) {
		Long id = new Long(++lastPrId);
		prDefs.put(id, prDef);
		return id;
	}
	
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
	
	public void update(Long id, PlanningRequestDefinitionDetails prDef) {
		prDefs.put(id, prDef);
	}
	
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
	
	public void remove(Long id) {
		prDefs.remove(id);
	}
	
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
