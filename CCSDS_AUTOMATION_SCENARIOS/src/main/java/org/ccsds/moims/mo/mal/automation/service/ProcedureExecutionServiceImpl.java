package org.ccsds.moims.mo.mal.automation.service;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.automation.procedureexecution.provider.ProcedureExecutionInheritanceSkeleton;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureInvocationDetails;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureOccurrence;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureOccurrenceFilter;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureState;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureStatus;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UOctet;

/**
 * ProcedureExecution service implementation.
 * @author krikse
 *
 */
public class ProcedureExecutionServiceImpl extends
		ProcedureExecutionInheritanceSkeleton {
	
	private Map<Long, ProcedureOccurrence> procedureOccurrences = new HashMap<Long, ProcedureOccurrence>();

	public void startProcedure(Long _Long0,
			ProcedureInvocationDetails procedureInvocationDetails,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		//if (!ProcedureDefinitionServiceImpl.procedureDefinitions.containsKey(_Long0)) {
			//throw new MALException("Procedure not found! Procedure definition id = " + _Long0);
		//}

		MALOperation operation = MALContextFactory.lookupArea(new Identifier("Automation"), new UOctet())
				.getServiceByName(new Identifier("ProcedureDefinition"))
				.getOperationByName(new Identifier("getProcedureDefinition"));
		
		ProcedureOccurrence pe = procedureOccurrences.get(_Long0);
		if (pe != null && pe.getStatus() != null && pe.getStatus().getState() == ProcedureState.RUNNING) {
			throw new MALException("Procedure is running already!");
		}
		pe = new ProcedureOccurrence();
		ProcedureStatus status = new ProcedureStatus();
		status.setState(ProcedureState.RUNNING);
		pe.setStatus(status);
		procedureOccurrences.put(_Long0, pe);
	}

	public void pauseProcedure(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		ProcedureOccurrence pe = procedureOccurrences.get(_Long0);
		if (pe != null && pe.getStatus() == null || pe.getStatus().getState() != ProcedureState.RUNNING) {
			throw new MALException("Procedure is not running!");
		}
		pe.getStatus().setState(ProcedureState.PAUSED);
		procedureOccurrences.put(_Long0, pe);
	}

	public void resumeProcedure(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		ProcedureOccurrence pe = procedureOccurrences.get(_Long0);
		if (pe != null && pe.getStatus() == null || pe.getStatus().getState() != ProcedureState.PAUSED) {
			throw new MALException("Procedure is not paused!");
		}
		pe.getStatus().setState(ProcedureState.RUNNING);
		procedureOccurrences.put(_Long0, pe);
	}

	public void terminateProcedure(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		ProcedureOccurrence pe = procedureOccurrences.get(_Long0);
		if (pe != null && pe.getStatus() == null || pe.getStatus().getState() != ProcedureState.RUNNING) {
			throw new MALException("Procedure is not running!");
		}
		pe.getStatus().setState(ProcedureState.ABORTED);
		procedureOccurrences.put(_Long0, pe);
	}

	public ProcedureOccurrence getProcedure(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		return procedureOccurrences.get(_Long0);
	}

	public LongList getProcedureList(
			ProcedureOccurrenceFilter procedureOccurrenceFilter,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		for (Long id : procedureOccurrences.keySet()) {
			list.add(id);
		}
		return list;
	}

	public ProcedureStatus getStatus(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		ProcedureOccurrence pe = procedureOccurrences.get(_Long0);
		if (pe == null) {
			throw new MALException("Procedure status not found!");
		}
		return pe.getStatus();
	}
	
}
