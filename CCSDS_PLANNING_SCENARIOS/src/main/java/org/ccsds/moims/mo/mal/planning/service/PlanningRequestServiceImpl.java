package org.ccsds.moims.mo.mal.planning.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.provider.PublishInteractionListener;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALInvoke;
import org.ccsds.moims.mo.mal.provider.MALProgress;
import org.ccsds.moims.mo.mal.provider.MALRequest;
import org.ccsds.moims.mo.mal.provider.MALSubmit;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
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
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestGroup;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestUpdate;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestUpdateList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;

/**
 * PlanningRequest service implementation.
 * @author krikse
 *
 */
public class PlanningRequestServiceImpl extends PlanningRequestInheritanceSkeleton {
	
	private Map<Long, PlanningRequest> _PlanningRequest1Map = new HashMap<Long, PlanningRequest>();
	private MonitorPublisher publisher;
	private Long autoincrement = 0L;



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

	@Override
	public void handleSend(MALInteraction interaction, MALMessageBody body)
			throws MALInteractionException, MALException {
		// TODO Auto-generated method stub
		super.handleSend(interaction, body);
	}

	@Override
	public void handleSubmit(MALSubmit interaction, MALMessageBody body)
			throws MALInteractionException, MALException {
		// TODO Auto-generated method stub
		super.handleSubmit(interaction, body);
	}

	@Override
	public void handleRequest(MALRequest interaction, MALMessageBody body)
			throws MALInteractionException, MALException {
		// TODO Auto-generated method stub
		super.handleRequest(interaction, body);
	}

	@Override
	public void handleInvoke(MALInvoke interaction, MALMessageBody body)
			throws MALInteractionException, MALException {
		// TODO Auto-generated method stub
		super.handleInvoke(interaction, body);
	}

	@Override
	public void handleProgress(MALProgress interaction, MALMessageBody body)
			throws MALInteractionException, MALException {
		// TODO Auto-generated method stub
		super.handleProgress(interaction, body);
	}

	public Long submitPlanningRequest(
			PlanningRequestGroup _PlanningRequestGroup0,
			PlanningRequest _PlanningRequest1, MALInteraction interaction)
			throws MALInteractionException, MALException {
		_PlanningRequest1.setStatus(StateEnum.SUBMITTED);
		autoincrement++;
		Long id = autoincrement;
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
	    PlanningRequestUpdateList list = new PlanningRequestUpdateList(); 
	    list.add(new PlanningRequestUpdate());
	    publisher.publish(updateHeaderList, list);
	}
	

	


}
