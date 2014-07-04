package org.ccsds.moims.mo.mal.automation.service;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.automation.scheduleexecutionservice.provider.MonitorExecutionPublisher;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.provider.ScheduleExecutionServiceInheritanceSkeleton;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.provider.SubscribePublisher;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.structures.Schedule;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.structures.ScheduleFilter;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.structures.ScheduleState;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.structures.ScheduleStatus;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;

/**
 * ScheduleExecution service implementation.
 * @author krikse
 *
 */
public class ScheduleExecutionServiceImpl extends
		ScheduleExecutionServiceInheritanceSkeleton {
	
	private SubscribePublisher subscribePublisher;
	private MonitorExecutionPublisher monitorExecutionPublisher;
	private Map<Long, Schedule> schedules = new HashMap<Long, Schedule>();
	private Map<Long, ScheduleStatus> scheduleStatuses = new HashMap<Long, ScheduleStatus>();

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

	public Long submitSchedule(Schedule schedule, MALInteraction interaction)
			throws MALInteractionException, MALException {
		schedules.put(schedule.getId(), schedule);
		ScheduleStatus status = new ScheduleStatus();
		status.setSchId(schedule.getId());
		scheduleStatuses.put(schedule.getId(), status);
		return schedule.getId();
	}

	public void updateSchedule(Long _Long0, Schedule schedule,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		schedules.put(_Long0, schedule);
	}

	public void removeSchedule(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		schedules.remove(_Long0);
	}

	public Schedule getSchedule(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		return schedules.get(_Long0);
	}

	public LongList getScheduleList(ScheduleFilter filter,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		for (Schedule schedule : schedules.values()) {
			list.add(schedule.getId());
		}
		return list;
	}

	public void startSchedule(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Schedule schedule = schedules.get(_Long0);
		if (schedule == null) {
			throw new MALException("Schedule not found!");
		}
		ScheduleStatus status = scheduleStatuses.get(_Long0);
		if (status != null && status.getState() == ScheduleState.RUNNING) {
			throw new MALException("Schedule is running already!");
		}
		status = new ScheduleStatus();
		status.setSchId(_Long0);
		status.setState(ScheduleState.RUNNING);
		scheduleStatuses.put(_Long0, status);
	}

	public void pauseSchedule(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Schedule schedule = schedules.get(_Long0);
		if (schedule == null) {
			throw new MALException("Schedule not found!");
		}
		ScheduleStatus status = scheduleStatuses.get(_Long0);
		if (status != null && status.getState() != ScheduleState.RUNNING) {
			throw new MALException("Schedule is not running!");
		}
		status.setSchId(_Long0);
		status.setState(ScheduleState.PAUSED);
		scheduleStatuses.put(_Long0, status);
	}

	public void resumeSchedule(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Schedule schedule = schedules.get(_Long0);
		if (schedule == null) {
			throw new MALException("Schedule not found!");
		}
		ScheduleStatus status = scheduleStatuses.get(_Long0);
		if (status != null && status.getState() != ScheduleState.PAUSED) {
			throw new MALException("Schedule is not paused!");
		}
		status.setSchId(_Long0);
		status.setState(ScheduleState.RUNNING);
		scheduleStatuses.put(_Long0, status);
	}

	public void terminateSchedule(Long _Long0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		Schedule schedule = schedules.get(_Long0);
		if (schedule == null) {
			throw new MALException("Schedule not found!");
		}
		ScheduleStatus status = scheduleStatuses.get(_Long0);
		if (status != null && status.getState() != ScheduleState.RUNNING) {
			throw new MALException("Schedule is not running!");
		}
		status.setSchId(_Long0);
		status.setState(ScheduleState.ABORTED);
		scheduleStatuses.put(_Long0, status);
	}

	public ScheduleStatus getScheduleStatus(Long _Long0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		Schedule schedule = schedules.get(_Long0);
		if (schedule == null) {
			throw new MALException("Schedule not found!");
		}
		return scheduleStatuses.get(_Long0);
	}

}
