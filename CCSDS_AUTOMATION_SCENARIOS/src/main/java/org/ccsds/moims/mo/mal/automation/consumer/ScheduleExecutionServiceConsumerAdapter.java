package org.ccsds.moims.mo.mal.automation.consumer;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.scheduleexecutionservice.consumer.ScheduleExecutionServiceAdapter;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.structures.LevelOfDetailList;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.structures.ScheduleFilterList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * ScheduleExecution consumer listener.
 * @author krikse
 *
 */
public class ScheduleExecutionServiceConsumerAdapter extends ScheduleExecutionServiceAdapter {
	
	public static final Logger LOGGER = Logger
			.getLogger(ScheduleExecutionServiceConsumerAdapter.class.getName());

	@Override
	public void submitScheduleResponseReceived(MALMessageHeader msgHeader,
			Long _Long0, Map qosProperties) {
		LOGGER.info(" *** submitScheduleResponseReceived");
		super.submitScheduleResponseReceived(msgHeader, _Long0, qosProperties);
	}

	@Override
	public void updateScheduleAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** updateScheduleAckReceived");
		super.updateScheduleAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void removeScheduleAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** removeScheduleAckReceived");
		super.removeScheduleAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void startScheduleAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** startScheduleAckReceived");
		super.startScheduleAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void pauseScheduleAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** pauseScheduleAckReceived");
		super.pauseScheduleAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void resumeScheduleAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** resumeScheduleAckReceived");
		super.resumeScheduleAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void terminateScheduleAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** terminateScheduleAckReceived");
		super.terminateScheduleAckReceived(msgHeader, qosProperties);
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

}
