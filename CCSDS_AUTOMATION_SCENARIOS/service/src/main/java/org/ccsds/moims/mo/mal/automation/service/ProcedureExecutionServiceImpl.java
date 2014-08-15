package org.ccsds.moims.mo.mal.automation.service;

import java.util.Iterator;
import java.util.List;
import org.ccsds.moims.mo.automation.procedureexecution.provider.ProcedureExecutionInheritanceSkeleton;
import org.ccsds.moims.mo.automation.procedureexecution.structures.Procedure;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureArgumentValue;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureArgumentValueList;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureDefinition;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureInvocationDetails;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureOccurrenceFilter;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureState;
import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureStatus;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ProcedureStatusDaoImpl;
import org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatusMessage;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * ProcedureExecution service implementation.
 * 
 * @author krikse
 *
 */
@Controller
public class ProcedureExecutionServiceImpl extends
		ProcedureExecutionInheritanceSkeleton {

	@Autowired
	private ProcedureDaoImpl procedureDaoImpl;

	@Autowired
	private ProcedureDefinitionDaoImpl procedureDefinitionDaoImpl;

	@Autowired
	private ProcedureStatusDaoImpl procedureStatusDaoImpl;

	public Long startProcedure(Long procedureDefId,
			ProcedureInvocationDetails procedureInvocationDetails,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition dbProcedureDef = procedureDefinitionDaoImpl
				.get(procedureDefId);
		if (procedureDefId == null) {
			throw new MALException("Unknown procedure definition!");
		}
		org.ccsds.moims.mo.mal.automation.datamodel.Procedure procedure = new org.ccsds.moims.mo.mal.automation.datamodel.Procedure();
		procedure.setProcedureDefinition(dbProcedureDef);
		procedure.setName(dbProcedureDef.getName());
		procedure.setDescription(dbProcedureDef.getDescription());
		procedureDaoImpl.insertUpdate(procedure);
		procedureStatusDaoImpl.start(procedure.getId());
		return procedure.getId();
	}

	public void pauseProcedure(Long procedureId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Procedure dbProcedure = procedureDaoImpl
				.get(procedureId);
		if (dbProcedure == null) {
			throw new MALException("Unknown procedure!");
		}
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus dbStatus = procedureStatusDaoImpl
				.getCurrentStatus(dbProcedure);
		if (dbStatus.getState() != org.ccsds.moims.mo.mal.automation.datamodel.ProcedureState.RUNNING) {
			throw new MALException(
					"Cannot pause procedure! Procedure is not in running state.");
		}
		procedureStatusDaoImpl.pause(procedureId);
	}

	public void resumeProcedure(Long procedureId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Procedure dbProcedure = procedureDaoImpl
				.get(procedureId);
		if (dbProcedure == null) {
			throw new MALException("Unknown procedure!");
		}
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus dbStatus = procedureStatusDaoImpl
				.getCurrentStatus(dbProcedure);
		if (dbStatus.getState() != org.ccsds.moims.mo.mal.automation.datamodel.ProcedureState.PAUSED) {
			throw new MALException(
					"Cannot resume procedure! Procedure is not in paused state.");
		}
		procedureStatusDaoImpl.resume(procedureId);
	}

	public void terminateProcedure(Long procedureId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Procedure dbProcedure = procedureDaoImpl
				.get(procedureId);
		if (dbProcedure == null) {
			throw new MALException("Unknown procedure!");
		}
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus dbStatus = procedureStatusDaoImpl
				.getCurrentStatus(dbProcedure);
		if (dbStatus.getState() != org.ccsds.moims.mo.mal.automation.datamodel.ProcedureState.RUNNING) {
			throw new MALException(
					"Cannot terminate procedure! Procedure is not in running state.");
		}
		procedureStatusDaoImpl.terminate(procedureId);
	}

	public Procedure getProcedure(Long procedureId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Procedure dbProcedure = procedureDaoImpl
				.get(procedureId);
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus dbProcedureStatus = procedureStatusDaoImpl
				.getCurrentStatus(dbProcedure);
		return cast(dbProcedure, dbProcedureStatus);
	}

	public LongList getProcedureList(
			ProcedureOccurrenceFilter procedureOccurrenceFilter,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		List<org.ccsds.moims.mo.mal.automation.datamodel.Procedure> dbList = procedureDaoImpl
				.getList();
		if (dbList != null) {
			for (org.ccsds.moims.mo.mal.automation.datamodel.Procedure dbProcedure : dbList) {
				list.add(dbProcedure.getId());
			}
		}
		return list;
	}

	public ProcedureStatus getStatus(Long procedureId,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Procedure dbProcedure = procedureDaoImpl
				.get(procedureId);
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus dbProcedureStatus = procedureStatusDaoImpl
				.getCurrentStatus(dbProcedure);
		return cast(dbProcedureStatus);
	}

	public Long addDefinition(ProcedureDefinition procedureDefinition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition dbProcedureDefinition = cast(procedureDefinition);
		procedureDefinitionDaoImpl.insertUpdate(dbProcedureDefinition);
		return dbProcedureDefinition.getId();
	}

	public void updateDefinition(Long procedureDefinitionId,
			ProcedureDefinition procedureDefinition, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition dbProcedureDefinition = cast(procedureDefinition);
		dbProcedureDefinition.setId(procedureDefinitionId);
		procedureDefinitionDaoImpl.insertUpdate(dbProcedureDefinition);
	}

	public void removeDefinition(Long procedureDefinitionId,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		procedureDefinitionDaoImpl.remove(procedureDefinitionId);
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
				org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition dbDef = procedureDefinitionDaoImpl
						.get(Long.parseLong(identifier.getValue()));
				if (dbDef != null) {
					Long id = dbDef.getId();
					if (id != null) {
						longList.add(id);
					}
				}
			}
		}
		return longList;
	}

	private static Procedure cast(
			org.ccsds.moims.mo.mal.automation.datamodel.Procedure dbProcedure,
			org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus dbProcedureStatus) {
		Procedure procedure = null;
		if (dbProcedure != null) {
			procedure = new Procedure();
			procedure.setName(dbProcedure.getName());
			procedure.setDescription(dbProcedure.getDescription());
			procedure.setStatus(cast(dbProcedureStatus));
			if (dbProcedure.getArguments() != null) {
				ProcedureArgumentValueList arguments = new ProcedureArgumentValueList();
				for (org.ccsds.moims.mo.mal.automation.datamodel.ProcedureArgument arg : dbProcedure
						.getArguments()) {
					ProcedureArgumentValue a = new ProcedureArgumentValue();
					a.setName(arg.getName());
					// TODO attribute
					arguments.add(a);
				}
				procedure.setArguments(arguments);
			}
			// TODO attachemnt
		}
		return procedure;
	}

	private static ProcedureStatus cast(
			org.ccsds.moims.mo.mal.automation.datamodel.ProcedureStatus dbProcedureStatus) {
		ProcedureStatus procedureStatus = null;
		if (dbProcedureStatus != null) {
			procedureStatus = new ProcedureStatus();
			procedureStatus.setState(ProcedureState
					.fromString(dbProcedureStatus.getState().toString()));
			if (dbProcedureStatus.getMessages() != null) {
				StringList messages = new StringList();
				for (ProcedureStatusMessage msg : dbProcedureStatus
						.getMessages()) {
					messages.add(msg.getMessage());
				}
				procedureStatus.setMessage(messages);
			}
		}
		return procedureStatus;
	}

	private static org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition cast(
			ProcedureDefinition def) {
		org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition dbDef = null;
		if (def != null) {
			dbDef = new org.ccsds.moims.mo.mal.automation.datamodel.ProcedureDefinition();
			dbDef.setName(def.getName());
			dbDef.setDescription(def.getDescription());
			// TODO other paramas.
		}
		return dbDef;
	}

}
