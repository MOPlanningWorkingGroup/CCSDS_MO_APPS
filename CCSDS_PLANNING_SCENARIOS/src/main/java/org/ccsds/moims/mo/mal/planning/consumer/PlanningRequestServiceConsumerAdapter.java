package org.ccsds.moims.mo.mal.planning.consumer;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.ccsds.moims.mo.planning.planningrequestservice.consumer.PlanningRequestServiceAdapter;

/**
 * Adapter to listen events.
 * @author krikse
 *
 */
public class PlanningRequestServiceConsumerAdapter extends PlanningRequestServiceAdapter {
	
	public static final Logger LOGGER = Logger
			.getLogger(PlanningRequestServiceConsumerAdapter.class.getName());

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
	public void notifyReceivedFromOtherService(MALMessageHeader msgHeader,
			MALNotifyBody body, Map qosProperties) throws MALException {
		LOGGER.info(" *** notifyReceivedFromOtherService");
		super.notifyReceivedFromOtherService(msgHeader, body, qosProperties);
	}

	@Override
	public void subscribeRegisterAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** subscribeRegisterAckReceived");
		super.subscribeRegisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void subscribeDeregisterAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** subscribeDeregisterAckReceived");
		super.subscribeDeregisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void submitPlanningRequestAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** submitPlanningRequestAckReceived");
		super.submitPlanningRequestAckReceived(msgHeader, qosProperties);
	}

}
