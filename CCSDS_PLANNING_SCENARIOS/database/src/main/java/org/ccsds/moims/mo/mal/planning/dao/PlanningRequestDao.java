package org.ccsds.moims.mo.mal.planning.dao;

import java.util.List;

import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest;

public interface PlanningRequestDao {
	
void insertUpdate(PlanningRequest request);
	
	void remove(long id);
	
	List<PlanningRequest> getList();
	
	PlanningRequest get(long id);

}
