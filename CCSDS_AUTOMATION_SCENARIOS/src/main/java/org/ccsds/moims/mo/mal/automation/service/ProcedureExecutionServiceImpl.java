package org.ccsds.moims.mo.mal.automation.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.ccsds.moims.mo.automation.procedureexecution.provider.ProcedureExecutionInheritanceSkeleton;
import org.ccsds.moims.mo.automation.procedureexecution.structures.Procedure;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureDefinition;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureInvocationDetails;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureOccurrenceFilter;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureState;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureStatus;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * ProcedureExecution service implementation.
 * @author krikse
 *
 */
public class ProcedureExecutionServiceImpl extends
		ProcedureExecutionInheritanceSkeleton {
	
	private Map<Long, Procedure> procedures = new HashMap<Long, Procedure>();
	private Map<Long, ProcedureDefinition> procedureDefinitions = new HashMap<Long, ProcedureDefinition>();
	private Map<String, Long> procedureDefinitionIds = new HashMap<String, Long>();
	private Long autoincrementDefinition = 0L;

	public void startProcedure(Long _Long0,
			ProcedureInvocationDetails procedureInvocationDetails,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		Procedure pe = procedures.get(_Long0);
		if (pe != null && pe.getStatus() != null && pe.getStatus().getState() == ProcedureState.RUNNING) {
			throw new MALException("Procedure is running already!");
		}
		pe = new Procedure();
		ProcedureStatus status = new ProcedureStatus();
		status.setState(ProcedureState.RUNNING);
		pe.setStatus(status);
		procedures.put(_Long0, pe);
	}

	public void pauseProcedure(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Procedure pe = procedures.get(_Long0);
		if (pe != null && pe.getStatus() == null || pe.getStatus().getState() != ProcedureState.RUNNING) {
			throw new MALException("Procedure is not running!");
		}
		pe.getStatus().setState(ProcedureState.PAUSED);
		procedures.put(_Long0, pe);
	}

	public void resumeProcedure(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Procedure pe = procedures.get(_Long0);
		if (pe != null && pe.getStatus() == null || pe.getStatus().getState() != ProcedureState.PAUSED) {
			throw new MALException("Procedure is not paused!");
		}
		pe.getStatus().setState(ProcedureState.RUNNING);
		procedures.put(_Long0, pe);
	}

	public void terminateProcedure(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Procedure pe = procedures.get(_Long0);
		if (pe != null && pe.getStatus() == null || pe.getStatus().getState() != ProcedureState.RUNNING) {
			throw new MALException("Procedure is not running!");
		}
		pe.getStatus().setState(ProcedureState.ABORTED);
		procedures.put(_Long0, pe);
	}

	public Procedure getProcedure(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		return procedures.get(_Long0);
	}

	public LongList getProcedureList(
			ProcedureOccurrenceFilter procedureOccurrenceFilter,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		for (Long id : procedures.keySet()) {
			list.add(id);
		}
		return list;
	}

	public ProcedureStatus getStatus(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Procedure pe = procedures.get(_Long0);
		if (pe == null) {
			throw new MALException("Procedure status not found!");
		}
		return pe.getStatus();
	}

	public Long addDefinition(ProcedureDefinition procedureDefinition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		autoincrementDefinition++;
		Long id = autoincrementDefinition;
		procedureDefinitions.put(id, procedureDefinition);
		procedureDefinitionIds.put(procedureDefinition.getName(), id);
		return id;
	}

	public void updateDefinition(Long procedureDefinitionId,
			ProcedureDefinition procedureDefinition, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (!procedureDefinitions.containsKey(procedureDefinitionId)) {
			throw new MALException("ProcedureDefinition not found! ProcedureDefinition id = " + procedureDefinitionId);
		}
		String previousName = procedureDefinitions.get(procedureDefinitionId).getName();
		if (!previousName.equals(procedureDefinition.getName())) {
			procedureDefinitionIds.remove(previousName);
		}
		procedureDefinitions.put(procedureDefinitionId, procedureDefinition);
		procedureDefinitionIds.put(procedureDefinition.getName(), procedureDefinitionId);
	}

	public void removeDefinition(Long procedureDefinitionId,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (!procedureDefinitions.containsKey(procedureDefinitionId)) {
			throw new MALException("ProcedureDefinition not found! ProcedureDefinition id = " + procedureDefinitionId);
		}
		ProcedureDefinition procedureDefinition = procedureDefinitions.get(procedureDefinitionId);
		procedureDefinitionIds.remove(procedureDefinition.getName());
		procedureDefinitions.remove(procedureDefinitionId);
	}

	public LongList listDefinition(IdentifierList identifierList,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList longList = null;
		if (identifierList != null) {
			longList = new LongList();
			Iterator<Identifier> it = identifierList.iterator();
			while (it.hasNext()) {
				Identifier identifier = it.next();
				Long id = procedureDefinitionIds.get(identifier.getValue());
				if (id != null) {
					longList.add(id);
				}
			}
		}
		return longList;
	}

}
