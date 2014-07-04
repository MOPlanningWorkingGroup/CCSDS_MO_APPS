package org.ccsds.moims.mo.mal.automation.service;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.automation.proceduredefinitionservice.provider.ProcedureDefinitionServiceInheritanceSkeleton;
import org.ccsds.moims.mo.automation.proceduredefinitionservice.structures.ProcedureDefinition;
import org.ccsds.moims.mo.automation.proceduredefinitionservice.structures.ProcedureDefinitionFilter;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * ProcedureDefinition service implementation.
 * @author krikse
 *
 */
public class ProcedureDefinitionServiceImpl extends
		ProcedureDefinitionServiceInheritanceSkeleton {
	
	private static Map<Long, ProcedureDefinition> procedureDefinitions = new HashMap<Long, ProcedureDefinition>();
	private Long autoincrement = 1L;

	public Long addProcedureDefinition(ProcedureDefinition procedureDefinition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		procedureDefinition.setProcId(autoincrement++);
		procedureDefinitions.put(autoincrement, procedureDefinition);
		return autoincrement;
	}

	public void removeProcedureDefinition(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (!procedureDefinitions.containsKey(_Long0)) {
			throw new MALException("Procedure not found! Procedure id = " + _Long0);
		}
		procedureDefinitions.remove(_Long0);
	}

	public void updateProcedureDefinition(Long _Long0,
			ProcedureDefinition procedureDefinition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (!procedureDefinitions.containsKey(_Long0)) {
			throw new MALException("Procedure not found! Procedure id = " + _Long0);
		}
		procedureDefinitions.put(_Long0, procedureDefinition);
	}

	public ProcedureDefinition getProcedureDefinition(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (!procedureDefinitions.containsKey(_Long0)) {
			throw new MALException("Procedure not found! Procedure id = " + _Long0);
		}
		return procedureDefinitions.get(_Long0);
	}

	public LongList getProcedureDefinitionList(
			ProcedureDefinitionFilter procedureDefinitionFilter,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		for (ProcedureDefinition pd : procedureDefinitions.values()) {
			list.add(pd.getProcId());
		}
		return list;
	}
	

}
