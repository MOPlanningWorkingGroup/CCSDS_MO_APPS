package org.ccsds.moims.mo.mal.automation.dao;

import java.util.List;

import org.ccsds.moims.mo.mal.automation.datamodel.Schedule;

public interface ScheduleDao {
	
	void insertUpdate(Schedule def);
	
	void remove(long id);
	
	List<Schedule> getList();
	
	Schedule get(long id);

}
