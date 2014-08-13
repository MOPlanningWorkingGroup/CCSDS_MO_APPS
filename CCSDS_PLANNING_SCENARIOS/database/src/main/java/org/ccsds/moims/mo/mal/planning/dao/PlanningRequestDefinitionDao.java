package org.ccsds.moims.mo.mal.planning.dao;

import java.util.List;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition;

public interface PlanningRequestDefinitionDao {
	
void insertUpdate(PlanningRequestDefinition definition);
	
	void remove(long id);
	
	List<PlanningRequestDefinition> getList();
	
	PlanningRequestDefinition get(long id);

}
