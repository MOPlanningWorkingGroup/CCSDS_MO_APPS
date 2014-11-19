package org.ccsds.moims.mo.mal.automation.datamodel;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "Schedule", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({ @NamedQuery(name = "Schedule.findAll", query = "SELECT c FROM Schedule c") })
public class Schedule extends Activity {
	
	private String source;
	private String destination;
	private ScheduleDefinition scheduleDefinition;
	private List<ScheduleArgumentValue> argumentValues;
	private ExecutionTimingConstraints executionTiming;
	private ExecutionRunningConstraints executionDetails;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@OneToOne
	@JoinColumn(name = "scheduleDefinition_id")
	public ScheduleDefinition getScheduleDefinition() {
		return scheduleDefinition;
	}

	public void setScheduleDefinition(ScheduleDefinition scheduleDefinition) {
		this.scheduleDefinition = scheduleDefinition;
	}

	@OneToMany(targetEntity = ScheduleArgumentValue.class, mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ScheduleArgumentValue> getArgumentValues() {
		return argumentValues;
	}

	public void setArgumentValues(List<ScheduleArgumentValue> arguments) {
		this.argumentValues = arguments;
	}

	public ExecutionTimingConstraints getExecutionTiming() {
		return executionTiming;
	}

	public void setExecutionTiming(ExecutionTimingConstraints executionTiming) {
		this.executionTiming = executionTiming;
	}

	public ExecutionRunningConstraints getExecutionDetails() {
		return executionDetails;
	}

	public void setExecutionDetails(ExecutionRunningConstraints executionDetails) {
		this.executionDetails = executionDetails;
	}

}
