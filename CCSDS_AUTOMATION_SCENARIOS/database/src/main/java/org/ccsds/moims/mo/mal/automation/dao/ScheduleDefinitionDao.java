package org.ccsds.moims.mo.mal.automation.dao;

import java.util.List;

import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition;

public interface ScheduleDefinitionDao {
	
	void insertUpdate(ScheduleDefinition def);
	
	void remove(long id);
	
	List<ScheduleDefinition> getList();
	
	ScheduleDefinition get(long id);

}
