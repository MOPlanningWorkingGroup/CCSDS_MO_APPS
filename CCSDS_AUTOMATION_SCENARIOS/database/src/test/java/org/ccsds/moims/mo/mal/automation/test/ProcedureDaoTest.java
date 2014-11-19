package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.automation.datamodel.Procedure;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureArgumentValue;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinitionArgument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaAutomationContext.xml")
public class ProcedureDaoTest {
	
	@Autowired
	private ProcedureDefinitionDaoImpl procedureDefinitionDaoImpl;
	
	@Autowired
	private ProcedureDaoImpl procedureDaoImpl;
	
	@Test
	public void testProcedurDao() {
		ProcedureDefinition procedureDefinition = new ProcedureDefinition();
		procedureDefinition.setName("procedureDefinition");
		procedureDefinition.setDescription("description");
		procedureDefinition.setBody("select * from procedureDefinition");
		procedureDefinition.setCreationDate(new Date());
		procedureDefinition.setCreator("kopernikus");
		procedureDefinition.setVersion(1);
		procedureDefinition.setArguments(new ArrayList<ProcedureDefinitionArgument>());
		ProcedureDefinitionArgument arg1 = new ProcedureDefinitionArgument();
		arg1.setName("arg1");
		arg1.setDataType((short) 12);
		arg1.setProcedureDefinition(procedureDefinition);
		procedureDefinition.getArguments().add(arg1);
		procedureDefinitionDaoImpl.insertUpdate(procedureDefinition);
		Procedure procedure = new Procedure();
		procedure.setProcedureDefinition(procedureDefinition);
		procedure.setArguments(new ArrayList<ProcedureArgumentValue>());
		procedureDaoImpl.insertUpdate(procedure);
		procedure = procedureDaoImpl.get(procedure.getId());
		procedureDaoImpl.remove(procedure.getId());
		procedureDefinitionDaoImpl.remove(procedureDefinition.getId());
		List<Procedure> list = procedureDaoImpl.getList();
		assertTrue(list.size() == 0);
	}

}
