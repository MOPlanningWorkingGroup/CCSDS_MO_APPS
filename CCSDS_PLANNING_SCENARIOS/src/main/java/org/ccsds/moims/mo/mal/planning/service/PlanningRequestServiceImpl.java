package org.ccsds.moims.mo.mal.planning.service;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planning.planningrequestservice.provider.PlanningRequestServiceInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequestservice.provider.SubscribePublisher;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequestEventList;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequestEvent;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequestGroup;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequestservice.structures.StateEnum;

/**
 * PlanningRequest service implementation.
 * @author krikse
 *
 */
public class PlanningRequestServiceImpl extends PlanningRequestServiceInheritanceSkeleton {
	
	private Map<Long, PlanningRequest> _PlanningRequest1Map = new HashMap<Long, PlanningRequest>();
	private SubscribePublisher publisher;

	@Override
	public SubscribePublisher createSubscribePublisher(IdentifierList domain,
			Identifier networkZone, SessionType sessionType,
			Identifier sessionName, QoSLevel qos, Map qosProps,
			UInteger priority) throws MALException {
		publisher = super.createSubscribePublisher(domain, networkZone, sessionType,
				sessionName, qos, qosProps, priority);
		return publisher;
	}

	public void submitPlanningRequest(
			PlanningRequestGroup _PlanningRequestGroup0,
			PlanningRequest _PlanningRequest1, MALInteraction interaction)
			throws MALInteractionException, MALException {
		_PlanningRequest1.setStatus(StateEnum.SUBMITTED);
		_PlanningRequest1Map.put(_PlanningRequest1.getId(), _PlanningRequest1);
		publish(_PlanningRequest1.getId(), UpdateType.CREATION);
	}

	public PlanningRequest getPlanningRequest(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		return _PlanningRequest1Map.get(_Long0);
	}

	public void updatePlanningRequest(Long _Long0,
			PlanningRequest _PlanningRequest1, MALInteraction interaction)
			throws MALInteractionException, MALException {
		_PlanningRequest1Map.put(_PlanningRequest1.getId(), _PlanningRequest1);
		publish(_PlanningRequest1.getId(), UpdateType.UPDATE);
	}

	public void removePlanningRequest(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		_PlanningRequest1Map.remove(_Long0);
		publish(_Long0, UpdateType.DELETION);
	}

	public StateEnum getPlanningRequestStatus(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		PlanningRequest pr = _PlanningRequest1Map.get(_Long0);
		if (pr == null) {
			throw new MALException("The Planning Request does not exist");
		}
		return pr.getStatus();
	}

	public PlanningRequestList getPlanningRequestList(
			PlanningRequestFilter _PlanningRequestFilter0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		PlanningRequestList list = new PlanningRequestList();
		list.addAll(_PlanningRequest1Map.values());
		return list;
	}

	private void publish(Long id, UpdateType updateType) throws IllegalArgumentException, MALInteractionException, MALException {
		UpdateHeaderList updateHeaderList = new UpdateHeaderList();
		final EntityKey ekey = new EntityKey(new Identifier(String.valueOf(id)), null, null, null);
	    final Time timestamp = new Time(System.currentTimeMillis());
	    updateHeaderList.add(new UpdateHeader(timestamp, new URI("SomeURI"), updateType, ekey));
	    PlanningRequestEventList eventList = new PlanningRequestEventList();
	    eventList.add(new PlanningRequestEvent());
		publisher.publish(updateHeaderList, eventList);
	}
	

	


}
