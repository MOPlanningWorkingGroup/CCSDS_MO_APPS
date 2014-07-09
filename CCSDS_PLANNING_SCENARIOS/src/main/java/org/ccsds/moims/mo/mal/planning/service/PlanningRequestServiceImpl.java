package org.ccsds.moims.mo.mal.planning.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
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
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestGroup;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatus;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestUpdate;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestUpdateList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;

/**
 * PlanningRequest service implementation.
 * @author krikse
 *
 */
public class PlanningRequestServiceImpl extends PlanningRequestInheritanceSkeleton {
	
	private Map<Long, PlanningRequest> planningRequestMap = new HashMap<Long, PlanningRequest>();
	private MonitorPublisher publisher;
	private Long autoincrement = 0L;
	private Map<Long, PlanningRequestDefinition> planningRequestDefinitionMap = new HashMap<Long, PlanningRequestDefinition>();
	private Map<String, Long> identifierMap = new HashMap<String, Long>();
	private Long autoincrementDefinition = 0L;

	@Override
	public MonitorPublisher createMonitorPublisher(IdentifierList domain,
			Identifier networkZone, SessionType sessionType,
			Identifier sessionName, QoSLevel qos, Map qosProps,
			UInteger priority) throws MALException {
		// TODO Auto-generated method stub
		publisher = super.createMonitorPublisher(domain, networkZone, sessionType,
				sessionName, qos, qosProps, priority);
		return publisher;
	}

	public PlanningRequest getPlanningRequest(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		return planningRequestMap.get(_Long0);
	}

	public void updatePlanningRequest(Long _Long0,
			PlanningRequest _PlanningRequest1, MALInteraction interaction)
			throws MALInteractionException, MALException {
		planningRequestMap.put(_Long0, _PlanningRequest1);
		publish(_Long0, UpdateType.UPDATE);
	}

	public void removePlanningRequest(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		planningRequestMap.remove(_Long0);
		publish(_Long0, UpdateType.DELETION);
	}

	public StateEnum getPlanningRequestStatus(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		PlanningRequest pr = planningRequestMap.get(_Long0);
		if (pr == null) {
			throw new MALException("The Planning Request does not exist");
		}
		return pr.getPlanningRequestStatus().getState();
	}

	public PlanningRequestList getPlanningRequestList(
			PlanningRequestFilter _PlanningRequestFilter0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		PlanningRequestList list = new PlanningRequestList();
		list.addAll(planningRequestMap.values());
		return list;
	}

	private void publish(Long id, UpdateType updateType) throws IllegalArgumentException, MALInteractionException, MALException {
		UpdateHeaderList updateHeaderList = new UpdateHeaderList();
		final EntityKey ekey = new EntityKey(new Identifier(String.valueOf(id)), null, null, null);
	    final Time timestamp = new Time(System.currentTimeMillis());
	    updateHeaderList.add(new UpdateHeader(timestamp, new URI("SomeURI"), updateType, ekey));
	    PlanningRequestUpdateList list = new PlanningRequestUpdateList(); 
	    list.add(new PlanningRequestUpdate());
	    publisher.publish(updateHeaderList, list);
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
				if (identifierMap.containsKey(identifier.getValue())) {
					Long id = identifierMap.get(identifier.getValue());
					longList.add(id);
				}
			}
		}
		return longList;
	}

	public Long addDefinition(
			PlanningRequestDefinition planningRequestDefinition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (identifierMap.containsKey(planningRequestDefinition.getName())) {
			MALStandardError error = new MALStandardError(new UInteger(1), "Planning Request with this name exists already!");
			throw new MALInteractionException(error);
		}
		autoincrementDefinition++;
		Long id = autoincrementDefinition;
		planningRequestDefinitionMap.put(id, planningRequestDefinition);
		identifierMap.put(planningRequestDefinition.getName(), id);
		return id;
	}

	public void updateDefinition(Long planningRequestDefinitionId,
			PlanningRequestDefinition planningRequestDefinition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (!planningRequestDefinitionMap.containsKey(planningRequestDefinitionId)) {
			MALStandardError error = new MALStandardError(new UInteger(1), "Planning Request ID does'nt exists!");
			throw new MALInteractionException(error);
		}
		planningRequestDefinitionMap.put(planningRequestDefinitionId, planningRequestDefinition);
	}

	public void removeDefinition(Long planningRequestDefinitionId,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (!planningRequestDefinitionMap.containsKey(planningRequestDefinitionId)) {
			MALStandardError error = new MALStandardError(new UInteger(1), "Planning Request ID does'nt exists!");
			throw new MALInteractionException(error);
		}
		String name = planningRequestDefinitionMap.get(planningRequestDefinitionId).getName();
		planningRequestDefinitionMap.remove(planningRequestDefinitionId);
		identifierMap.remove(name);
	}

	public Long submitPlanningRequest(
			PlanningRequestGroup planningRequestGroup,
			Long planningRequestDefinitionId, PlanningRequest planningRequest,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		PlanningRequestStatus status = new PlanningRequestStatus();
		status.setPlanningRequestIdentifier(new Identifier("PR1"));
		status.setState(StateEnum.SUBMITTED);
		planningRequest.setPlanningRequestStatus(status);
		autoincrement++;
		Long id = autoincrement;
		planningRequestMap.put(id, planningRequest);
		publish(id, UpdateType.CREATION);
		return id;
	}


}
