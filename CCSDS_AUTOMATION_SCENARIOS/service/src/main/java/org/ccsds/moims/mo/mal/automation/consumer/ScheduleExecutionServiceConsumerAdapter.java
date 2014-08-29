package org.ccsds.moims.mo.mal.automation.consumer;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.scheduleexecution.consumer.ScheduleExecutionAdapter;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * ScheduleExecution consumer listener.
 * @author krikse
 *
 */
public class ScheduleExecutionServiceConsumerAdapter extends ScheduleExecutionAdapter {
	
	public static final Logger LOGGER = Logger
			.getLogger(ScheduleExecutionServiceConsumerAdapter.class.getName());

	@Override
	public void addResponseReceived(MALMessageHeader msgHeader,
			Long _Long0, Map qosProperties) {
		LOGGER.info(" *** addResponseReceived");
		super.addResponseReceived(msgHeader, _Long0, qosProperties);
	}

	@Override
	public void updateAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** updateAckReceived");
		super.updateAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void removeAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** removeAckReceived");
		super.removeAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void startAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** startAckReceived");
		super.startAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void pauseAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** pauseAckReceived");
		super.pauseAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void resumeAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** resumeAckReceived");
		super.resumeAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void terminateAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** terminateAckReceived");
		super.terminateAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorExecutionRegisterAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** monitorExecutionRegisterAckReceived");
		super.monitorExecutionRegisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorExecutionDeregisterAckReceived(
			MALMessageHeader msgHeader, Map qosProperties) {
		LOGGER.info(" *** monitorExecutionDeregisterAckReceived");
		super.monitorExecutionDeregisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorSchedulesRegisterAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** monitorSchedulesRegisterAckReceived");
		super.monitorSchedulesRegisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorSchedulesDeregisterAckReceived(
			MALMessageHeader msgHeader, Map qosProperties) {
		LOGGER.info(" *** monitorSchedulesDeregisterAckReceived");
		super.monitorSchedulesDeregisterAckReceived(msgHeader, qosProperties);
	}	
	
	

}
