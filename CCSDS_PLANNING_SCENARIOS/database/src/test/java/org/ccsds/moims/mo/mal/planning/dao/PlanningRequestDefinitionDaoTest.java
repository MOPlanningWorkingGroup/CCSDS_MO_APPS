package org.ccsds.moims.mo.mal.planning.dao;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.planning.dao.impl.PlanningRequestDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition;
import org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaPlanningContext.xml")
public class PlanningRequestDefinitionDaoTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PlanningRequestDefinitionDaoTest.class);
	
	@Autowired
	private PlanningRequestDefinitionDaoImpl daoImpl;
	
	@Test
	public void testPlanningRequestDefinitionDao() {
		PlanningRequestDefinition def = new PlanningRequestDefinition();
		def.setName("junit");
		def.setDescription("this is test");
		def.setArguments(new ArrayList<PlanningRequestArgumentDefinition>());
		PlanningRequestArgumentDefinition a1 = new PlanningRequestArgumentDefinition();
		a1.setName("a1");
		a1.setName("a1 name");
		a1.setPlanningRequestDefinition(def);
		def.getArguments().add(a1);
		PlanningRequestArgumentDefinition a2 = new PlanningRequestArgumentDefinition();
		a2.setName("a2");
		a2.setName("a2 name");
		a2.setPlanningRequestDefinition(def);
		def.getArguments().add(a2);
		daoImpl.insertUpdate(def);
		List<PlanningRequestDefinition> list = daoImpl.getList();
		assertTrue(list.size() == 1);
		def.setDescription("description");
		daoImpl.insertUpdate(def);
		daoImpl.remove(def.getId());
		list = daoImpl.getList();
		assertTrue(list.size() == 0);
	}

}
