package org.ccsds.moims.mo.mal.planning.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.provider.SubscribePublisher;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestEventList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestEvent;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestGroup;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;

/**
 * PlanningRequest service implementation.
 * @author krikse
 *
 */
public class PlanningRequestServiceImpl extends PlanningRequestInheritanceSkeleton {
	
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

	public Long submitPlanningRequest(
			PlanningRequestGroup _PlanningRequestGroup0,
			PlanningRequest _PlanningRequest1, MALInteraction interaction)
			throws MALInteractionException, MALException {
		_PlanningRequest1.setStatus(StateEnum.SUBMITTED);
		UUID uuId = UUID.randomUUID();
		Long id = uuId.getMostSignificantBits();
		_PlanningRequest1Map.put(id, _PlanningRequest1);
		publish(id, UpdateType.CREATION);
		return id;
	}

	public PlanningRequest getPlanningRequest(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		return _PlanningRequest1Map.get(_Long0);
	}

	public void updatePlanningRequest(Long _Long0,
			PlanningRequest _PlanningRequest1, MALInteraction interaction)
			throws MALInteractionException, MALException {
		_PlanningRequest1Map.put(_Long0, _PlanningRequest1);
		publish(_Long0, UpdateType.UPDATE);
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
