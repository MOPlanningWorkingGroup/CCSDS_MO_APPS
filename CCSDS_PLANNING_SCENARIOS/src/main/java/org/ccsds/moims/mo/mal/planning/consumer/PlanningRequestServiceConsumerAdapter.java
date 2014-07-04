package org.ccsds.moims.mo.mal.planning.consumer;

import java.util.Map;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;

/**
 * Adapter to listen events.
 * @author krikse
 *
 */
public class PlanningRequestServiceConsumerAdapter extends PlanningRequestAdapter {
	
	public static final Logger LOGGER = Logger
			.getLogger(PlanningRequestServiceConsumerAdapter.class.getName());

	@Override
	public void submitPlanningRequestResponseReceived(
			MALMessageHeader msgHeader, Long _Long0, Map qosProperties) {
		LOGGER.info(" *** submitPlanningRequestResponseReceived");
		super.submitPlanningRequestResponseReceived(msgHeader, _Long0, qosProperties);
	}

	@Override
	public void updatePlanningRequestAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** updatePlanningRequestAckReceived");
		super.updatePlanningRequestAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void removePlanningRequestAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** removePlanningRequestAckReceived");
		super.removePlanningRequestAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void getPlanningRequestResponseReceived(MALMessageHeader msgHeader,
			PlanningRequest _PlanningRequest0, Map qosProperties) {
		LOGGER.info(" *** getPlanningRequestResponseReceived");
		super.getPlanningRequestResponseReceived(msgHeader, _PlanningRequest0,
				qosProperties);
	}

	@Override
	public void getPlanningRequestListResponseReceived(
			MALMessageHeader msgHeader,
			PlanningRequestList _PlanningRequestList0, Map qosProperties) {
		LOGGER.info(" *** getPlanningRequestListResponseReceived");
		super.getPlanningRequestListResponseReceived(msgHeader, _PlanningRequestList0,
				qosProperties);
	}

	@Override
	public void getPlanningRequestStatusResponseReceived(
			MALMessageHeader msgHeader, StateEnum _StateEnum0, Map qosProperties) {
		LOGGER.info(" *** getPlanningRequestStatusResponseReceived");
		super.getPlanningRequestStatusResponseReceived(msgHeader, _StateEnum0,
				qosProperties);
	}

}
