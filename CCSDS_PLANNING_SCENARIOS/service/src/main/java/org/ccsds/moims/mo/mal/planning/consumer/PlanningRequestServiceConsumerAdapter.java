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
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestUpdateList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;

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
	public void submitPlanningRequestResponseReceived(
			MALMessageHeader msgHeader, Long _Long0, Map qosProperties) {
		LOGGER.info(" *** submitPlanningRequestResponseReceived " + name);
		super.submitPlanningRequestResponseReceived(msgHeader, _Long0, qosProperties);
	}

	@Override
	public void updatePlanningRequestAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** updatePlanningRequestAckReceived " + name);
		super.updatePlanningRequestAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void removePlanningRequestAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** removePlanningRequestAckReceived " + name);
		super.removePlanningRequestAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void getPlanningRequestResponseReceived(MALMessageHeader msgHeader,
			PlanningRequest _PlanningRequest0, Map qosProperties) {
		LOGGER.info(" *** getPlanningRequestResponseReceived " + name);
		super.getPlanningRequestResponseReceived(msgHeader, _PlanningRequest0,
				qosProperties);
	}

	@Override
	public void getPlanningRequestListResponseReceived(
			MALMessageHeader msgHeader,
			PlanningRequestList _PlanningRequestList0, Map qosProperties) {
		LOGGER.info(" *** getPlanningRequestListResponseReceived " + name);
		super.getPlanningRequestListResponseReceived(msgHeader, _PlanningRequestList0,
				qosProperties);
	}

	@Override
	public void getPlanningRequestStatusResponseReceived(
			MALMessageHeader msgHeader, StateEnum _StateEnum0, Map qosProperties) {
		LOGGER.info(" *** getPlanningRequestStatusResponseReceived " + name);
		super.getPlanningRequestStatusResponseReceived(msgHeader, _StateEnum0,
				qosProperties);
	}

	@Override
	public void notifyReceivedFromOtherService(MALMessageHeader msgHeader,
			MALNotifyBody body, Map qosProperties) throws MALException {
		LOGGER.info(" *** notifyReceivedFromOtherService " + name);
		super.notifyReceivedFromOtherService(msgHeader, body, qosProperties);
	}

	@Override
	public void monitorRegisterAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** monitorRegisterAckReceived " + name);
		super.monitorRegisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorRegisterErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** monitorRegisterErrorReceived " + name);
		super.monitorRegisterErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void monitorDeregisterAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** monitorDeregisterAckReceived " + name);
		super.monitorDeregisterAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorNotifyReceived(MALMessageHeader msgHeader,
			Identifier _Identifier0, UpdateHeaderList _UpdateHeaderList1,
			PlanningRequestUpdateList _PlanningRequestUpdateList2,
			Map qosProperties) {
		LOGGER.info(" *** monitorNotifyReceived " + name);
		super.monitorNotifyReceived(msgHeader, _Identifier0, _UpdateHeaderList1,
				_PlanningRequestUpdateList2, qosProperties);
		monitorNotifyReceivedCounter++;
	}

	@Override
	public void monitorNotifyErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** monitorNotifyErrorReceived " + name);
		super.monitorNotifyErrorReceived(msgHeader, error, qosProperties);
		monitorNotifyErrorReceived++;
	}

	@Override
	public void submitPlanningRequestErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** submitPlanningRequestErrorReceived " + name);
		super.submitPlanningRequestErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void updatePlanningRequestErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** updatePlanningRequestErrorReceived " + name);
		super.updatePlanningRequestErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void removePlanningRequestErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** removePlanningRequestErrorReceived " + name);
		super.removePlanningRequestErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void getPlanningRequestErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** getPlanningRequestErrorReceived " + name);
		super.getPlanningRequestErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void getPlanningRequestListErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** getPlanningRequestListErrorReceived " + name);
		super.getPlanningRequestListErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void getPlanningRequestStatusErrorReceived(
			MALMessageHeader msgHeader, MALStandardError error,
			Map qosProperties) {
		LOGGER.info(" ***  getPlanningRequestStatusErrorReceived " + name);
		super.getPlanningRequestStatusErrorReceived(msgHeader, error, qosProperties);
	}
	
	

}
