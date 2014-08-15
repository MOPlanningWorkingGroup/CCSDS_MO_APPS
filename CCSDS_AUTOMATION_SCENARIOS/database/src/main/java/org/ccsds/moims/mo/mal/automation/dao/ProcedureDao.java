package org.ccsds.moims.mo.mal.automation.dao;

import java.util.List;
import org.ccsds.moims.mo.mal.automation.datamodel.Procedure;

public interface ProcedureDao {

	void insertUpdate(Procedure procedure);

	void remove(long id);

	List<Procedure> getList();

	Procedure get(long id);

}
