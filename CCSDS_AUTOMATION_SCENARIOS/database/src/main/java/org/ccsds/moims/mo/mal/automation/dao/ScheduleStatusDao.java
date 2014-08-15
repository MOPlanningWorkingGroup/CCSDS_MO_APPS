package org.ccsds.moims.mo.mal.automation.dao;

import java.util.List;

import org.ccsds.moims.mo.mal.automation.datamodel.Schedule;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleState;
import org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatus;

public interface ScheduleStatusDao {
	
	void insert(Schedule procedure, ScheduleState state);

	List<ScheduleStatus> getList(Schedule procedure);
	
	void start(Long procedureId);
	
	void pause(Long procedureId);
	
	void resume(Long procedureId);
	
	void terminate(Long procedureId);
	
	ScheduleStatus getCurrentStatus(Schedule procedure);

}
