package org.ccsds.moims.mo.mal.planning.service;

import java.math.BigInteger;
import java.util.ArrayList;
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
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilterList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinition;
import org.ccsds.moims.mo.planningcom.structures.ArgumentDefinition;
import org.ccsds.moims.mo.planningcom.structures.ArgumentDefinitionList;
import org.ccsds.moims.mo.planningcom.structures.ArgumentValue;
import org.ccsds.moims.mo.planningcom.structures.ArgumentValueList;
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
			dbPr.setId(planningRequest.getId());
			dbPr.setName(planningRequest.getName());
			dbPr.setDescription(planningRequest.getDescription());
			dbPr.setSource(planningRequest.getSource());
			dbPr.setDestination(planningRequest.getDestination());
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
	    PlanningRequestFilterList filterList = new PlanningRequestFilterList();
	    PlanningRequestFilter filter = new PlanningRequestFilter();
	    filter.setStatus(StateEnum.EXECUTED);
	    filterList.add(filter);
	    publisher.publish(updateHeaderList, filterList);
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
		if (definition.getArguments() != null) {
			dbDefinition.setArguments(mapArgumentList(definition.getArguments()));
		}
		planningRequestDefinitionDaoImpl.insertUpdate(dbDefinition);
		return dbDefinition.getId();
	}
	
	private List<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition> mapArgumentList(ArgumentDefinitionList list) {
		List<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition> dbList = null;
		if (list != null) {
			dbList = new ArrayList<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition>();
			Iterator<ArgumentDefinition> it = list.iterator();
			while (it.hasNext()) {
				ArgumentDefinition argDef = it.next();
				org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition dbArg = new org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition();
				dbArg.setName(argDef.getName());
				dbArg.setValueType(argDef.getType());
				if (argDef.getChildArguments() != null) {
					dbArg.setArguments(mapArgumentList(argDef.getChildArguments()));
				}
				dbList.add(dbArg);
			}
		}
		return dbList;
	}
	
	public PlanningRequestDefinition getDefinition(
			Long planningRequestDefinitionId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		PlanningRequestDefinition definition = null;
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition dbDefinition = planningRequestDefinitionDaoImpl.get(planningRequestDefinitionId);
		if (dbDefinition != null) {
			definition = new PlanningRequestDefinition();
			definition.setId(dbDefinition.getId());
			definition.setName(dbDefinition.getName());
			definition.setDescription(dbDefinition.getDescription());
			definition.setArguments(mapDbArgumentList(dbDefinition.getArguments()));
		}
		return definition;
	}
	
	private ArgumentDefinitionList mapDbArgumentList(List<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition> dbList) {
		ArgumentDefinitionList list = null;
		if (dbList != null) {
			list = new ArgumentDefinitionList();
			Iterator<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition> it = dbList.iterator();
			while (it.hasNext()) {
				org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentDefinition dbArg = it.next();
				ArgumentDefinition argDef = new ArgumentDefinition();
				argDef.setName(dbArg.getName());
				argDef.setType(new Byte((byte) dbArg.getValueType()));
				if (dbArg.getArguments() != null) {
					argDef.setChildArguments(mapDbArgumentList(dbArg.getArguments()));
				}
				list.add(argDef);
			}
		}
		return list;
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
	
	private PlanningRequest mapFromDb(org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest dbPr) {
		PlanningRequest pr = new PlanningRequest();
		pr.setId(dbPr.getId());
		pr.setName(dbPr.getName());
		pr.setDescription(dbPr.getDescription());
		pr.setDestination(dbPr.getDestination());
		pr.setSource(dbPr.getSource());
		if (dbPr.getPlanningRequestDefinition() != null) {
			pr.setPlanningRequestDefinitionId(dbPr.getPlanningRequestDefinition().getId());
		}
		ArgumentValueList list = new ArgumentValueList();
		for (org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentValue prav : dbPr.getArgumentValues()) {
			ArgumentValue av = new ArgumentValue();
			av.setValue(new ULong(BigInteger.valueOf(prav.getId())));
			list.add(av);
		}
		pr.setArgumentValues(list);
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

	public Long addTaskDefinition(TaskDefinition def,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (def != null) {
			org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef = new org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition();
			taskDef.setName(def.getName());
			taskDef.setDescription(def.getDescription());
			org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition planningRequestDefinition = planningRequestDefinitionDaoImpl.get(def.getPlanningRequestDefinitionId());
			if (planningRequestDefinition != null) {
				taskDef.setPlanningRequestDefinition(planningRequestDefinition);
			}
			if (def.getArguments() != null) {
				taskDef.setArguments(new ArrayList<org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition>());
				for (ArgumentDefinition argDef : def.getArguments()) {
					org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition dbArg = new org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition();
					dbArg.setName(argDef.getName());
					dbArg.setValueType(org.ccsds.moims.mo.mal.planning.datamodel.ValueType.STRING); //TODO
					dbArg.setTaskDefinition(taskDef);
					taskDef.getArguments().add(dbArg);
				}
			}
			taskDefinitionDaoImpl.insertUpdate(taskDef);
			return taskDef.getId();
		}
		return null;
	}

	public void updateTaskDefinition(Long id,
			TaskDefinition def, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (id != null && def != null) {
				org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef = taskDefinitionDaoImpl.get(id);
				if (taskDef == null) {
					MALStandardError error = new MALStandardError(null, "One of the supplied Task Definition object instance identifiers is unknown!");
					throw new MALInteractionException(error);
				}
				taskDef.setName(def.getName());
				taskDef.setDescription(def.getDescription());
				if (def.getArguments() != null) {
					for (ArgumentDefinition argDef : def.getArguments()) {
						// TODO
						org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition dbArg =
								new org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition();
						dbArg.setName(argDef.getName());
						// TODO dbArg.setValueType(org.ccsds.moims.mo.mal.planning.datamodel.ValueType.valueOf(argDef.getValueType().toString()));
						dbArg.setTaskDefinition(taskDef);
						taskDef.getArguments().add(dbArg);
					}
				}
				taskDefinitionDaoImpl.insertUpdate(taskDef);
		}
	}

	public void removeTaskDefinition(Long id, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (id != null) {
			org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef = taskDefinitionDaoImpl
					.get(id);
			if (taskDef == null) {
				MALStandardError error = new MALStandardError(null,
						"One of the supplied Task Definition object instance identifiers is unknown!");
				throw new MALInteractionException(error);
			}
			taskDefinitionDaoImpl.remove(id);
		}
	}

	public Long add(Long planningRequestDefinitionId,
			PlanningRequest planningRequest, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestDefinition dbPrDef = planningRequestDefinitionDaoImpl.get(planningRequestDefinitionId);
		if (dbPrDef == null) {
			throw new MALException("The Planning Request Definition does not exist");
		}
		
		org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest dbReq = new org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequest();
		dbReq.setName("kopernikus");
		dbReq.setDescription("esa");
		dbReq.setDestination("rosetta");
		dbReq.setSource("philae");
		if (planningRequest.getArgumentValues() != null) {
			dbReq.setArgumentValues(new ArrayList<org.ccsds.moims.mo.mal.planning.datamodel.PlanningRequestArgumentValue>());
			Iterator<ArgumentValue> it = planningRequest.getArgumentValues().iterator();
			while (it.hasNext()) {
				// TODO
			}
		}
		planningRequestDaoImpl.insertUpdate(dbReq);
		return dbReq.getId();
	}


}
