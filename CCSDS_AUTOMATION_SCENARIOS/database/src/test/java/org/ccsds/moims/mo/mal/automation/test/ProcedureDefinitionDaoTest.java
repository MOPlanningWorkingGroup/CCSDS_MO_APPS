package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinitionArgument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaAutomationContext.xml")
public class ProcedureDefinitionDaoTest {
	
	@Autowired
	private ProcedureDefinitionDaoImpl procedureDefinitionDaoImpl;
	
	@Test
	public void testProcedureDefinitionStatusDao() {
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
		procedureDefinition = procedureDefinitionDaoImpl.get(procedureDefinition.getId());
		procedureDefinitionDaoImpl.remove(procedureDefinition.getId());
		List<ProcedureDefinition> list = procedureDefinitionDaoImpl.getList();
		assertTrue(list.size() == 0);
	}

}
