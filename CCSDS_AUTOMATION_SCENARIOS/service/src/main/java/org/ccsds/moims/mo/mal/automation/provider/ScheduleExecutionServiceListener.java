package org.ccsds.moims.mo.mal.automation.provider;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Provider listener.
 * @author krikse
 *
 */
public class ScheduleExecutionServiceListener implements MALPublishInteractionListener {
	
	public static final Logger LOGGER = Logger.getLogger(ScheduleExecutionServiceListener.class.getName());
	
	public void publishRegisterAckReceived(MALMessageHeader header,
			Map qosProperties) throws MALException {
		LOGGER.info(" ***** PublishInteractionListener::publishRegisterAckReceived");
	}

	public void publishRegisterErrorReceived(MALMessageHeader header,
			MALErrorBody body, Map qosProperties) throws MALException {
		LOGGER.info(" ***** PublishInteractionListener::publishRegisterErrorReceived");
	}

	public void publishErrorReceived(MALMessageHeader header,
			MALErrorBody body, Map qosProperties) throws MALException {
		LOGGER.info(" ***** PublishInteractionListener::publishErrorReceived");
	}

	public void publishDeregisterAckReceived(MALMessageHeader header,
			Map qosProperties) throws MALException {
		LOGGER.info(" ***** PublishInteractionListener::publishDeregisterAckReceived");
	}

}
