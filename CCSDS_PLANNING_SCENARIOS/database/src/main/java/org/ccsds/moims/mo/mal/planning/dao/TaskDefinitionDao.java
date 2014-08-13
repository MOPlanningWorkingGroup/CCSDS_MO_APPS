package org.ccsds.moims.mo.mal.planning.dao;

import java.util.List;

import org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition;


public interface TaskDefinitionDao {
	
	void insertUpdate(TaskDefinition def);
	
	void remove(long id);
	
	List<TaskDefinition> getList();
	
	TaskDefinition get(long id);

}
