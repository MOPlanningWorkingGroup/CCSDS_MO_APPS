package org.ccsds.moims.mo.mal.automation.dao;

import java.util.List;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition;

public interface ProcedureDefinitionDao {
	
	void insertUpdate(ProcedureDefinition definition);

	void remove(long id);

	List<ProcedureDefinition> getList();

	ProcedureDefinition get(long id);

}
