package org.ccsds.moims.mo.mal.automation.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureStatusDaoImpl;
import org.ccsds.moims.mo.mal.automation.datamodel.Procedure;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaContext.xml")
public class ProcedureStatusDaoTest {
	
	@Autowired
	private ProcedureStatusDaoImpl procedureStatusDaoImpl;
	
	@Autowired
	private ProcedureDaoImpl procedureDaoImpl;
	
	@Autowired
	private ProcedureDefinitionDaoImpl procedureDefinitionDaoImpl;
	
	@PersistenceUnit(unitName="planning-persistence")
    private EntityManagerFactory factory;
	
	@Test
	public void testProcedureStatusDao() {
		Procedure procedure = new Procedure();
		ProcedureDefinition procedureDefinition = new ProcedureDefinition();
		procedureDefinition.setName("procedureDefinition");
		procedureDefinitionDaoImpl.insertUpdate(procedureDefinition);
		procedure.setProcedureDefinition(procedureDefinition);
		procedureDaoImpl.insertUpdate(procedure);
		procedureStatusDaoImpl.start(procedure.getId());
		procedureStatusDaoImpl.pause(procedure.getId());
		procedureStatusDaoImpl.resume(procedure.getId());
		procedureStatusDaoImpl.terminate(procedure.getId());
		List<ProcedureStatus> list = procedureStatusDaoImpl.getList(procedure);
		assertTrue(list.size() == 4);
		cleanProcedureStatusTable(procedure);
		list = procedureStatusDaoImpl.getList(procedure);
		procedureDaoImpl.remove(procedure.getId());
		procedureDefinitionDaoImpl.remove(procedureDefinition.getId());
		assertTrue(list.size() == 0);
	}
	
	private void cleanProcedureStatusTable(Procedure procedure) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		List<ProcedureStatus> list = procedureStatusDaoImpl.getList(procedure);
		for (ProcedureStatus ps : list) {
			ProcedureStatus procedureStatus = entityManager.find(ProcedureStatus.class, ps.getId());
			entityManager.remove(procedureStatus);
		}
		entityManager.getTransaction().commit();
	}

}
