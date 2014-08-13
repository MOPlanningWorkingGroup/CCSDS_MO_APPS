package org.ccsds.moims.mo.mal.planning.provider;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * PlanningRequest service listener.
 * @author krikse
 *
 */
public class TaskListener implements MALPublishInteractionListener {
	
	public static final Logger LOGGER = Logger.getLogger(TaskListener.class.getName());
	
	public void publishRegisterAckReceived(MALMessageHeader header,
			Map qosProperties) throws MALException {
		LOGGER.info(" ***** TaskListener::publishRegisterAckReceived");
	}

	public void publishRegisterErrorReceived(MALMessageHeader header,
			MALErrorBody body, Map qosProperties) throws MALException {
		LOGGER.info(" ***** TaskListener::publishRegisterErrorReceived");
	}

	public void publishErrorReceived(MALMessageHeader header,
			MALErrorBody body, Map qosProperties) throws MALException {
		LOGGER.info(" ***** TaskListener::publishErrorReceived");
	}

	public void publishDeregisterAckReceived(MALMessageHeader header,
			Map qosProperties) throws MALException {
		LOGGER.info(" ***** TaskListener::publishDeregisterAckReceived");
	}
	
	

}
