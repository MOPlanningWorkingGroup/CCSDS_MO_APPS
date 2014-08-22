package org.ccsds.moims.mo.mal.automation.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.ccsds.moims.mo.mal.automation.dao.ProcedureStatusDao;
import org.ccsds.moims.mo.mal.automation.datamodel.Procedure;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureState;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatusMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProcedureStatusDaoImpl implements ProcedureStatusDao {
	
	@PersistenceUnit(unitName="automation-persistence")
    private EntityManagerFactory factory;
	
	@Autowired
	private ProcedureDaoImpl procedureDaoImpl;

	public void insert(Procedure procedure, ProcedureState state) {
		EntityManager entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		ProcedureStatus procedureStatus = new ProcedureStatus();
		procedureStatus.setProcedure(procedure);
		procedureStatus.setState(state);
		procedureStatus.setCreationDate(new Date());
		procedureStatus.setMessages(new ArrayList<ProcedureStatusMessage>());
		ProcedureStatusMessage msg1 = new ProcedureStatusMessage();
		msg1.setProcedureStatus(procedureStatus);
		msg1.setMessage("this is status message");
		procedureStatus.getMessages().add(msg1);
		entityManager.persist(procedureStatus);
		entityManager.getTransaction().commit();
	}

	public List<ProcedureStatus> getList(Procedure procedure) {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("ProcedureStatus.findProcedureStatuses");
		query.setParameter("procedure", procedure);
		return query.getResultList();
	}

	public void start(Long procedureId) {
		Procedure procedure = procedureDaoImpl.get(procedureId);
		if (procedure != null) {
			insert(procedure, ProcedureState.RUNNING);
		}
	}

	public void pause(Long procedureId) {
		Procedure procedure = procedureDaoImpl.get(procedureId);
		if (procedure != null) {
			insert(procedure, ProcedureState.PAUSED);
		}
	}

	public void resume(Long procedureId) {
		Procedure procedure = procedureDaoImpl.get(procedureId);
		if (procedure != null) {
			insert(procedure, ProcedureState.RUNNING);
		}
	}

	public void terminate(Long procedureId) {
		Procedure procedure = procedureDaoImpl.get(procedureId);
		if (procedure != null) {
			insert(procedure, ProcedureState.ABORTED);
		}
	}

	public ProcedureStatus getCurrentStatus(Procedure procedure) {
		EntityManager entityManager = factory.createEntityManager();
		Query query = entityManager.createNamedQuery("ProcedureStatus.findProcedureStatuses");
		query.setParameter("procedure", procedure);
		List<ProcedureStatus> list = query.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(list.size() - 1);
		} else {
			return null;
		}
	}

	

}
