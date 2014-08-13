package org.ccsds.moims.mo.mal.planning.test;

import java.util.Map;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.task.consumer.TaskAdapter;

public class TaskServiceTestAdapter extends TaskAdapter {

	public static final Logger LOGGER = Logger
			.getLogger(TaskServiceTestAdapter.class.getName());

	private String name;

	public TaskServiceTestAdapter(String name) {
		this.name = name;
	}

	@Override
	public void listDefinitionResponseReceived(MALMessageHeader msgHeader,
			LongList longList, Map qosProperties) {
		LOGGER.info(" *** listDefinitionResponseReceived " + name);
		super.listDefinitionResponseReceived(msgHeader, longList,
				qosProperties);
	}

	@Override
	public void listDefinitionErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** listDefinitionErrorReceived " + name);
		super.listDefinitionErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void addDefinitionResponseReceived(MALMessageHeader msgHeader,
			LongList _LongList0, Map qosProperties) {
		LOGGER.info(" *** addDefinitionResponseReceived " + name);
		super.addDefinitionResponseReceived(msgHeader, _LongList0,
				qosProperties);
	}

	@Override
	public void addDefinitionErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** addDefinitionErrorReceived " + name);
		super.addDefinitionErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void updateDefinitionAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** updateDefinitionAckReceived " + name);
		super.updateDefinitionAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void updateDefinitionErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** updateDefinitionErrorReceived " + name);
		super.updateDefinitionErrorReceived(msgHeader, error, qosProperties);
	}

	@Override
	public void removeDefinitionAckReceived(MALMessageHeader msgHeader,
			Map qosProperties) {
		LOGGER.info(" *** removeDefinitionAckReceived " + name);
		super.removeDefinitionAckReceived(msgHeader, qosProperties);
	}

	@Override
	public void removeDefinitionErrorReceived(MALMessageHeader msgHeader,
			MALStandardError error, Map qosProperties) {
		LOGGER.info(" *** removeDefinitionErrorReceived " + name);
		super.removeDefinitionErrorReceived(msgHeader, error, qosProperties);
	}

}
