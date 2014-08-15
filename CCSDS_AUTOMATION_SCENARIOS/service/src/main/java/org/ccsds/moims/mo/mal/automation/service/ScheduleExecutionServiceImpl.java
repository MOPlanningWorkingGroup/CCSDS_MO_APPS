package org.ccsds.moims.mo.mal.automation.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.ccsds.moims.mo.automation.scheduleexecution.provider.MonitorExecutionPublisher;
import org.ccsds.moims.mo.automation.scheduleexecution.provider.ScheduleExecutionInheritanceSkeleton;
import org.ccsds.moims.mo.automation.scheduleexecution.provider.SubscribePublisher;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.Schedule;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleDefinition;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleFilter;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleState;
import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleStatus;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.automation.dao.impl.ScheduleDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ScheduleDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.automation.dao.impl.ScheduleStatusDaoImpl;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * ScheduleExecution service implementation.
 * @author krikse
 *
 */
@Controller
public class ScheduleExecutionServiceImpl extends
		ScheduleExecutionInheritanceSkeleton {
	
	private SubscribePublisher subscribePublisher;
	private MonitorExecutionPublisher monitorExecutionPublisher;
	
	@Autowired
	private ScheduleDaoImpl scheduleDaoImpl;
	
	@Autowired
	private ScheduleDefinitionDaoImpl scheduleDefinitionDaoImpl;
	
	@Autowired
	private ScheduleStatusDaoImpl scheduleStatusDaoImpl;

	@Override
	public SubscribePublisher createSubscribePublisher(IdentifierList domain,
			Identifier networkZone, SessionType sessionType,
			Identifier sessionName, QoSLevel qos, Map qosProps,
			UInteger priority) throws MALException {
		subscribePublisher = super.createSubscribePublisher(domain, networkZone, sessionType,
				sessionName, qos, qosProps, priority);
		return subscribePublisher;
	}

	@Override
	public MonitorExecutionPublisher createMonitorExecutionPublisher(
			IdentifierList domain, Identifier networkZone,
			SessionType sessionType, Identifier sessionName, QoSLevel qos,
			Map qosProps, UInteger priority) throws MALException {
		monitorExecutionPublisher = super.createMonitorExecutionPublisher(domain, networkZone, sessionType,
				sessionName, qos, qosProps, priority);
		return monitorExecutionPublisher;
	}

	public Long submitSchedule(Long scheduleDefinitionId, Schedule schedule, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition def = scheduleDefinitionDaoImpl.get(scheduleDefinitionId);
		if (def == null) {
			throw new MALException("ScheduleDefinition not found! ScheduleDefinition id = " + scheduleDefinitionId);
		}
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = new org.ccsds.moims.mo.mal.automation.datamodel.Schedule();
		dbSchedule.setName(schedule.getName());
		dbSchedule.setDescription(schedule.getDescription());
		scheduleDaoImpl.insertUpdate(dbSchedule);
		return dbSchedule.getId();
	}

	public void updateSchedule(Long scheduleId, Schedule schedule,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = scheduleDaoImpl.get(scheduleId);
		if (dbSchedule == null) {
			throw new MALException("Schedule not found! Schedule id = " + scheduleId);
		}
		dbSchedule.setName(schedule.getName());
		dbSchedule.setDescription(schedule.getDescription());
		scheduleDaoImpl.insertUpdate(dbSchedule);
	}

	public void removeSchedule(Long scheduleId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = scheduleDaoImpl.get(scheduleId);
		if (dbSchedule == null) {
			throw new MALException("Schedule not found! Schedule id = " + scheduleId);
		}
		scheduleDaoImpl.remove(scheduleId);
	}

	public Schedule getSchedule(Long scheduleId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Schedule schedule = null;
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = scheduleDaoImpl.get(scheduleId);
		if (dbSchedule != null) {
			schedule = new Schedule();
			schedule.setName(dbSchedule.getName());
			schedule.setDescription(dbSchedule.getDescription());
			// TODO other properties
		}
		return schedule;
	}

	public LongList getScheduleList(ScheduleFilter filter,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		List<org.ccsds.moims.mo.mal.automation.datamodel.Schedule> schedules = scheduleDaoImpl.getList();
		if (schedules != null) {
			for (org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule : schedules) {
				list.add(dbSchedule.getId());
			}
		}
		return list;
	}

	public void startSchedule(Long scheduleId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = scheduleDaoImpl.get(scheduleId);
		if (dbSchedule == null) {
			throw new MALException("Schedule not found! Schedule id = " + scheduleId);
		}
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatus status = scheduleStatusDaoImpl.getCurrentStatus(dbSchedule);
		if (status != null && status.getState() == org.ccsds.moims.mo.mal.automation.datamodel.ScheduleState.RUNNING) {
			throw new MALException("Schedule is running already!");
		}
		scheduleStatusDaoImpl.start(scheduleId);
	}

	public void pauseSchedule(Long scheduleId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = scheduleDaoImpl.get(scheduleId);
		if (dbSchedule == null) {
			throw new MALException("Schedule not found! Schedule id = " + scheduleId);
		}
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatus status = scheduleStatusDaoImpl.getCurrentStatus(dbSchedule);
		if (status == null || status.getState() != org.ccsds.moims.mo.mal.automation.datamodel.ScheduleState.RUNNING) {
			throw new MALException("Schedule is not running!");
		}
		scheduleStatusDaoImpl.pause(scheduleId);
	}

	public void resumeSchedule(Long scheduleId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = scheduleDaoImpl.get(scheduleId);
		if (dbSchedule == null) {
			throw new MALException("Schedule not found! Schedule id = " + scheduleId);
		}
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatus status = scheduleStatusDaoImpl.getCurrentStatus(dbSchedule);
		if (status == null || status.getState() != org.ccsds.moims.mo.mal.automation.datamodel.ScheduleState.PAUSED) {
			throw new MALException("Schedule is not paused!");
		}
		scheduleStatusDaoImpl.resume(scheduleId);
	}

	public void terminateSchedule(Long scheduleId, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = scheduleDaoImpl.get(scheduleId);
		if (dbSchedule == null) {
			throw new MALException("Schedule not found! Schedule id = " + scheduleId);
		}
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatus status = scheduleStatusDaoImpl.getCurrentStatus(dbSchedule);
		if (status == null || status.getState() != org.ccsds.moims.mo.mal.automation.datamodel.ScheduleState.RUNNING) {
			throw new MALException("Schedule is not running!");
		}
		scheduleStatusDaoImpl.terminate(scheduleId);
	}

	public ScheduleStatus getScheduleStatus(Long scheduleId,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.Schedule dbSchedule = scheduleDaoImpl.get(scheduleId);
		if (dbSchedule == null) {
			throw new MALException("Schedule not found! Schedule id = " + scheduleId);
		}
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatus dbStatus = scheduleStatusDaoImpl.getCurrentStatus(dbSchedule);
		ScheduleStatus status = new ScheduleStatus();
		status.setState(ScheduleState.fromString(dbStatus.getState().toString()));
		if (dbStatus.getMessages() != null) {
			StringList messages = new StringList();
			for (org.ccsds.moims.mo.mal.automation.datamodel.ScheduleStatusMessage message : dbStatus.getMessages()) {
				messages.add(message.getMessage());
			}
			status.setMessage(messages);
		}
		return status;
	}

	public LongList listDefinition(IdentifierList identifierList,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList longList = null;
		if (identifierList != null) {
			longList = new LongList();
			Iterator<Identifier> it = identifierList.iterator();
			while (it.hasNext()) {
				Identifier identifier = it.next();
				org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition def = scheduleDefinitionDaoImpl.get(Long.parseLong(identifier.getValue()));
				if (def != null) {
					longList.add(def.getId());
				}
			}
		}
		return longList;
	}

	public Long addDefinition(ScheduleDefinition scheduleDefinition,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition def = new org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition();
		def.setName(scheduleDefinition.getName());
		def.setDescription(scheduleDefinition.getDescription());
		// TODO arguments
		scheduleDefinitionDaoImpl.insertUpdate(def);
		return def.getId();
	}

	public void updateDefinition(Long scheduleDefinitionId,
			ScheduleDefinition scheduleDefinition, MALInteraction interaction)
			throws MALInteractionException, MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition def =scheduleDefinitionDaoImpl.get(scheduleDefinitionId);
		if (def == null) {
			throw new MALException("ScheduleDefinition not found! ScheduleDefinition id = " + scheduleDefinitionId);
		}
		def.setName(scheduleDefinition.getName());
		def.setDescription(scheduleDefinition.getDescription());
		// TODO arguments
		scheduleDefinitionDaoImpl.insertUpdate(def);
	}

	public void removeDefinition(Long scheduleDefinitionId,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		org.ccsds.moims.mo.mal.automation.datamodel.ScheduleDefinition def =scheduleDefinitionDaoImpl.get(scheduleDefinitionId);
		if (def == null) {
			throw new MALException("ScheduleDefinition not found! ScheduleDefinition id = " + scheduleDefinitionId);
		}
		scheduleDefinitionDaoImpl.remove(scheduleDefinitionId);
	}

}
