package org.ccsds.moims.mo.mal.planning.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.planning.dao.impl.PlanningRequestDaoImpl;
import org.ccsds.moims.mo.mal.planning.dao.impl.PlanningRequestDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.planning.dao.impl.TaskDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestArgumentDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestArgumentValue;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilterList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestGroup;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatus;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestUpdate;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestUpdateList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskArgumentDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * PlanningRequest service implementation.
 * @author krikse
 *
 */
@Controller
public class PlanningRequestServiceImpl extends PlanningRequestInheritanceSkeleton {
	
	private MonitorPlanningRequestsPublisher publisher;
	
	@Autowired
	private PlanningRequestDaoImpl planningRequestDaoImpl;
	
	@Autowired
	private TaskDefinitionDaoImpl taskDefinitionDaoImpl;
	
	@Autowired
	private PlanningRequestDefinitionDaoImpl planningRequestDefinitionDaoImpl;

	@Override
	public MonitorPlanningRequestsPublisher createMonitorPlanningRequestsPublisher(IdentifierList domain,
			Identifier networkZone, SessionType sessionType,
			Identifier sessionName, QoSLevel qos, Map qosProps,
			UInteger priority) throws MALException {
		publisher = super.createMonitorPlanningRequestsPublisher(domain, networkZone, sessionType,
				sessionName, qos, qosProps, priority);
		return publisher;
	}

	public PlanningRequest get(Long id,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		PlanningRequest pr = null;
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest dbPr = planningRequestDaoImpl.get(id);
		if (dbPr != null) {
			pr = mapFromDb(dbPr);
		}
		return pr;
	}

	public void update(Long id,
			PlanningRequest planningRequest, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest dbPr = planningRequestDaoImpl.get(id);
		if (dbPr != null) {
			dbPr.setName(planningRequest.getName());
			dbPr.setDescription(planningRequest.getDescription());
			if (planningRequest.getPlanningRequestStatus() != null) {
				dbPr.setStatus(org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestStatus.valueOf(planningRequest.getPlanningRequestStatus().getState().toString()));
			}
			// TODO values
			planningRequestDaoImpl.insertUpdate(dbPr);
			publish(id, UpdateType.UPDATE);
		} else {
			throw new MALException("The Planning Request does not exist");
		}
	}

	public void remove(Long id, MALInteraction interaction)
			throws MALInteractionException, MALException {
		planningRequestDaoImpl.remove(id);
		publish(id, UpdateType.DELETION);
	}

	public PlanningRequestList list(
			PlanningRequestFilter _PlanningRequestFilter0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		PlanningRequestList list = new PlanningRequestList();
		List<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest> dbList = planningRequestDaoImpl.getList();
		if (dbList != null) {
			for (org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest dbPr : dbList) {
				list.add(mapFromDb(dbPr));
			}
		}
		return list;
	}

	private void publish(Long id, UpdateType updateType) throws IllegalArgumentException, MALInteractionException, MALException {
		UpdateHeaderList updateHeaderList = new UpdateHeaderList();
		final EntityKey ekey = new EntityKey(new Identifier(String.valueOf(id)), null, null, null);
	    final Time timestamp = new Time(System.currentTimeMillis());
	    updateHeaderList.add(new UpdateHeader(timestamp, new URI("SomeURI"), updateType, ekey));
	    PlanningRequestUpdateList list = new PlanningRequestUpdateList(); 
	    list.add(new PlanningRequestUpdate());
	    publisher.publish(updateHeaderList, new PlanningRequestFilterList());
	}

	public LongList listDefinition(IdentifierList identifierList,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList longList = null;
		if (identifierList != null) {
			longList = new LongList();
			List<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest> dbList = planningRequestDaoImpl.getList();
			if (dbList != null) {
				Iterator<Identifier> it = identifierList.iterator();
				while (it.hasNext()) {
					Identifier identifier = it.next();
					for (org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest dbPr : dbList) {
						if (dbPr.getId().equals(Long.parseLong(identifier.getValue()))) {
							longList.add(dbPr.getId());
						}
					}
				}
			}
		}
		return longList;
	}

	public Long addDefinition(
			PlanningRequestDefinition definition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition dbDefinition = new org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition();
		dbDefinition.setName(definition.getName());
		dbDefinition.setDescription(definition.getDescription());
		dbDefinition.setArguments(new ArrayList<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition>());
		if (definition.getArguments() != null) {
			Iterator<PlanningRequestArgumentDefinition> it = definition.getArguments().iterator();
			while (it.hasNext()) {
				PlanningRequestArgumentDefinition argDef = it.next();
				org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition dbArg = new org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition();
				dbArg.setName(argDef.getName());
				dbArg.setValueType(argDef.getValueType().getValue());
				dbArg.setPlanningRequestDefinition(dbDefinition);
				dbDefinition.getArguments().add(dbArg);
			}
		}
		planningRequestDefinitionDaoImpl.insertUpdate(dbDefinition);
		return dbDefinition.getId();
	}

	public void updateDefinition(Long planningRequestDefinitionId,
			PlanningRequestDefinition planningRequestDefinition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition def = planningRequestDefinitionDaoImpl.get(planningRequestDefinitionId);
		if (def == null) {
			throw new MALException("The Planning Request Definition does not exist");
		}
		def.setName(planningRequestDefinition.getName());
		def.setDescription(planningRequestDefinition.getDescription());
		// TODO arguments
		planningRequestDefinitionDaoImpl.insertUpdate(def);
	}

	public void removeDefinition(Long planningRequestDefinitionId,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition def = planningRequestDefinitionDaoImpl.get(planningRequestDefinitionId);
		if (def == null) {
			throw new MALException("The Planning Request Definition does not exist");
		}
		planningRequestDefinitionDaoImpl.remove(planningRequestDefinitionId);
	}

	public Long add(
			PlanningRequestGroup planningRequestGroup,
			Long planningRequestDefinitionId, PlanningRequest planningRequest,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition dbPrDef = planningRequestDefinitionDaoImpl.get(planningRequestDefinitionId);
		if (dbPrDef == null) {
			throw new MALException("The Planning Request Definition does not exist");
		}
		
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest dbReq = new org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest();
		dbReq.setCreationDate(new Date());
		dbReq.setCreator("kopernikus");
		dbReq.setName(dbPrDef.getName());
		dbReq.setDescription(dbPrDef.getDescription());
		dbReq.setStatus(org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestStatus.SUBMITTED);
		if (planningRequest.getArguments() != null) {
			dbReq.setValues(new ArrayList<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestValue>());
			Iterator<PlanningRequestArgumentValue> it = planningRequest.getArguments().iterator();
			while (it.hasNext()) {
				// TODO
			}
		}
		planningRequestDaoImpl.insertUpdate(dbReq);
		return dbReq.getId();
	}
	
	private PlanningRequest mapFromDb(org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest dbPr) {
		PlanningRequest pr = new PlanningRequest();
		pr.setName(dbPr.getName());
		pr.setDescription(dbPr.getDescription());
		if (dbPr.getStatus() != null) {
			PlanningRequestStatus status = new PlanningRequestStatus();
			status.setState(StateEnum.fromString(dbPr.getStatus().toString()));
			pr.setPlanningRequestStatus(status);
		}
		return pr;
	}
	
	public LongList listTaskDefinition(IdentifierList _IdentifierList0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		List<org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition> defList = taskDefinitionDaoImpl.getList();
		if (defList != null) {
			for (org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef : defList) {
				list.add(taskDef.getId());
			}
		}
		return list;
	}

	public LongList addTaskDefinition(TaskDefinitionList _TaskDefinitionList0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		if (_TaskDefinitionList0 != null) {
			for (TaskDefinition def : _TaskDefinitionList0) {
				org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef =
						new org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition();
				taskDef.setName(def.getName());
				taskDef.setDescription(def.getDescription());
				if (def.getArguments() != null) {
					taskDef.setTaskArgumentDefinitions(new ArrayList<org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition>());
					for (TaskArgumentDefinition argDef : def.getArguments()) {
						org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition dbArg =
								new org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition();
						dbArg.setName(argDef.getName());
						// TODO dbArg.setValueType(org.ccsds.moims.mo.mal.planning.datamodel.ValueType.valueOf(argDef.getValueType().toString()));
						dbArg.setTaskDefinition(taskDef);
						taskDef.getTaskArgumentDefinitions().add(dbArg);
					}
				}
				taskDefinitionDaoImpl.insertUpdate(taskDef);
				list.add(taskDef.getId());
			}
		}
		return list;
	}

	public void updateTaskDefinition(LongList _LongList0,
			TaskDefinitionList _TaskDefinitionList1, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (_LongList0 != null &&_TaskDefinitionList1 != null) {
			if (_LongList0.size() != _TaskDefinitionList1.size()) {
				MALStandardError error = new MALStandardError(null, "ID list size and task definition list size is different!");
				throw new MALInteractionException(error);
			}
			for (int i = 0; i < _LongList0.size(); i++) {
				Long id = _LongList0.get(i);
				org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef = taskDefinitionDaoImpl.get(id);
				if (taskDef == null) {
					MALStandardError error = new MALStandardError(null, "One of the supplied Task Definition object instance identifiers is unknown!");
					throw new MALInteractionException(error);
				}
				TaskDefinition def =_TaskDefinitionList1.get(i);
				taskDef.setName(def.getName());
				taskDef.setDescription(def.getDescription());
				if (def.getArguments() != null) {
					for (TaskArgumentDefinition argDef : def.getArguments()) {
						// TODO
						org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition dbArg =
								new org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition();
						dbArg.setName(argDef.getName());
						// TODO dbArg.setValueType(org.ccsds.moims.mo.mal.planning.datamodel.ValueType.valueOf(argDef.getValueType().toString()));
						dbArg.setTaskDefinition(taskDef);
						taskDef.getTaskArgumentDefinitions().add(dbArg);
					}
				}
				taskDefinitionDaoImpl.insertUpdate(taskDef);
			}
		}
		
	}

	public void removeTaskDefinition(LongList _LongList0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (_LongList0 != null) {
			for (int i = 0; i < _LongList0.size(); i++) {
				Long id = _LongList0.get(i);
				org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef = taskDefinitionDaoImpl.get(id);
				if (taskDef == null) {
					MALStandardError error = new MALStandardError(null, "One of the supplied Task Definition object instance identifiers is unknown!");
					throw new MALInteractionException(error);
				}
				taskDefinitionDaoImpl.remove(id);
			}
		}
	}


}
