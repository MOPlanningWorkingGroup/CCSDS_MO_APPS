package org.ccsds.moims.mo.mal.planning.consumer;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestFilterList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;

/**
 * Adapter to listen events.
 * @author krikse
 *
 */
public class PlanningRequestServiceConsumerAdapter extends PlanningRequestAdapter {
	
	public static final Logger LOGGER = Logger
			.getLogger(PlanningRequestServiceConsumerAdapter.class.getName());
	private String name;
	private Long monitorNotifyReceivedCounter = 0L;
	private Long monitorNotifyErrorReceived = 0L;
	
	public PlanningRequestServiceConsumerAdapter(String name) {
		this.name = name;
	}

	public Long getMonitorNotifyReceivedCounter() {
		return monitorNotifyReceivedCounter;
	}

	public Long getMonitorNotifyErrorReceived() {
		return monitorNotifyErrorReceived;
	}

	@Override
	public void addResponseReceived(
			MALMessageHeader msgHeader, Long _Long0, Map qosProperties) {
		LOGGER.info(" *** addResponseReceived " + name);
		super.addResponseReceived(msgHeader, _Long0, qosProperties);
	}

	@Override
	public void updateAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** updateAckReceived " + name);
		super.updateAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void removeAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** removeAckReceived " + name);
		super.removeAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void getResponseReceived(MALMessageHeader msgHeader,
			PlanningRequest _PlanningRequest0, Map qosProperties) {
		LOGGER.info(" *** getResponseReceived " + name);
		super.getResponseReceived(msgHeader, _PlanningRequest0,
				qosProperties);
	}

	@Override
	public void listResponseReceived(
			MALMessageHeader msgHeader,
			PlanningRequestList _PlanningRequestList0, Map qosProperties) {
		LOGGER.info(" *** listResponseReceived " + name);
		super.listResponseReceived(msgHeader, _PlanningRequestList0,
				qosProperties);
	}

	@Override
	public void notifyReceivedFromOtherService(MALMessageHeader msgHeader,
			MALNotifyBody body, Map qosProperties) throws MALException {
		LOGGER.info(" *** notifyReceivedFromOtherService " + name);
		super.notifyReceivedFromOtherService(msgHeader, body, qosProperties);
	}

	@Override
	public void monitorPlanningRequestsRegisterAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** monitorPlanningRequestsRegisterAckReceived " + name);
		super.monitorPlanningRequestsRegisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorPlanningRequestsRegisterErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** monitorPlanningRequestsRegisterErrorReceived " + name);
		super.monitorPlanningRequestsRegisterErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void monitorPlanningRequestsDeregisterAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** monitorPlanningRequestsDeregisterAckReceived " + name);
		super.monitorPlanningRequestsDeregisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader,
			Identifier _Identifier0, UpdateHeaderList _UpdateHeaderList1,
			PlanningRequestFilterList _PlanningRequestFilterList2,
			Map qosProperties) {
		LOGGER.info(" *** monitorNotifyReceived " + name);
		super.monitorPlanningRequestsNotifyReceived(msgHeader, _Identifier0, _UpdateHeaderList1, _PlanningRequestFilterList2, qosProperties);
		monitorNotifyReceivedCounter++;
	}

	@Override
	public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** monitorPlanningRequestsNotifyErrorReceived " + name);
		super.monitorPlanningRequestsNotifyErrorReceived(msgHeader, error, qosProperties);
		monitorNotifyErrorReceived++;
	}

	@Override
	public void addErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** addErrorReceived " + name);
		super.addErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void updateErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** updateErrorReceived " + name);
		super.updateErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void removeErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** removeErrorReceived " + name);
		super.removeErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void getErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** getErrorReceived " + name);
		super.getErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void listErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** listErrorReceived " + name);
		super.listErrorReceived(msgHeader, error, qosProperties);
	}

	

}
