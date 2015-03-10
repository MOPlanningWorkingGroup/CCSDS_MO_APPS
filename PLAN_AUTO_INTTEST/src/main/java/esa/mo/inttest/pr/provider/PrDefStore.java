package esa.mo.inttest.pr.provider;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;

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
	
	public Long add(PlanningRequestDefinitionDetails prDef) {
		Long id = new Long(++lastPrId);
		prDefs.put(id, prDef);
		return id;
	}
	
	public void update(Long id, PlanningRequestDefinitionDetails prDef) {
		prDefs.put(id, prDef);
	}
	
	public void remove(Long id) {
		prDefs.remove(id);
	}
}
