package org.ccsds.moims.mo.mal.automation.dao;

import java.util.List;

import org.ccsds.moims.mo.mal.automation.datamodel.Procedure;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureState;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus;

public interface ProcedureStatusDao {
	
	void insert(Procedure procedure, ProcedureState state);

	List<ProcedureStatus> getList(Procedure procedure);
	
	void start(Long procedureId);
	
	void pause(Long procedureId);
	
	void resume(Long procedureId);
	
	void terminate(Long procedureId);
	
	ProcedureStatus getCurrentStatus(Procedure procedure);

}
