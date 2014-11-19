package org.ccsds.moims.mo.mal.automation.datamodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class ScheduleItem {

	private Long id;
	private ExecutionStatus executionStatus;
	private ExecutionTimingConstraints executionTiming;
	private ExecutionRunningConstraints executionDetails;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(ExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}

	@OneToOne
	@JoinColumn(name = "executionTiming_id")
	public ExecutionTimingConstraints getExecutionTiming() {
		return executionTiming;
	}

	public void setExecutionTiming(ExecutionTimingConstraints executionTiming) {
		this.executionTiming = executionTiming;
	}

	@OneToOne
	@JoinColumn(name = "executionDetails_id")
	public ExecutionRunningConstraints getExecutionDetails() {
		return executionDetails;
	}

	public void setExecutionDetails(ExecutionRunningConstraints executionDetails) {
		this.executionDetails = executionDetails;
	}

}
