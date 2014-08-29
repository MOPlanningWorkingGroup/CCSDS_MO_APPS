package org.ccsds.moims.mo.mal.planning.test;

import java.util.Map;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequest;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestList;
import org.ccsds.moims.mo.planning.planningrequest.structures.StateEnum;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskFilterList;

/**
 * Adapter to listen events.
 * @author krikse
 *
 */
public class PlanningRequestServiceTestAdapter extends PlanningRequestAdapter {
	
	public static final Logger LOGGER = Logger
			.getLogger(PlanningRequestServiceTestAdapter.class.getName());
	
	public int counter = 0;
	private Long planningRequestId;
	private String name;
	
	public Long getPlanningRequestId() {
		return planningRequestId;
	}
	
	public PlanningRequestServiceTestAdapter(String name) {
		this.name = name;
	}
	
	

	@Override
	public void addResponseReceived(MALMessageHeader msgHeader,
			Long planningRequestIdentifier, Map qosProperties) {
		planningRequestId = planningRequestIdentifier;
		counter++;
		super.addResponseReceived(msgHeader, planningRequestIdentifier, qosProperties);
	}

	@Override
	public void listDefinitionResponseReceived(MALMessageHeader msgHeader,
			LongList planningRequestDefinitionIdList, Map qosProperties) {
		LOGGER.info(" *** listDefinitionResponseReceived " + name);
		counter++;
		super.listDefinitionResponseReceived(msgHeader,
				planningRequestDefinitionIdList, qosProperties);
	}

	@Override
	public void removeAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
		LOGGER.info(" *** removeAckReceived " + name);
		counter++;
		super.removeAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void listResponseReceived(MALMessageHeader msgHeader,
			PlanningRequestList _PlanningRequestList0, Map qosProperties) {
		LOGGER.info(" *** listResponseReceived " + name);
		counter++;
		super.listResponseReceived(msgHeader, _PlanningRequestList0, qosProperties);
	}

	@Override
	public void listTaskDefinitionResponseReceived(MALMessageHeader msgHeader,
			LongList taskDefinitionIds, Map qosProperties) {
		LOGGER.info(" *** listTaskDefinitionResponseReceived " + name);
		counter++;
		super.listTaskDefinitionResponseReceived(msgHeader, taskDefinitionIds,
				qosProperties);
	}

	@Override
	public void updateTaskDefinitionAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** updateTaskDefinitionAckReceived " + name);
		counter++;
		super.updateTaskDefinitionAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void monitorTasksNotifyReceived(MALMessageHeader msgHeader,
			Identifier _Identifier0, UpdateHeaderList _UpdateHeaderList1,
			TaskFilterList _TaskFilterList2, Map qosProperties) {
		LOGGER.info(" *** monitorTasksNotifyReceived " + name);
		counter++;
		super.monitorTasksNotifyReceived(msgHeader, _Identifier0, _UpdateHeaderList1,
				_TaskFilterList2, qosProperties);
	}

	

}
