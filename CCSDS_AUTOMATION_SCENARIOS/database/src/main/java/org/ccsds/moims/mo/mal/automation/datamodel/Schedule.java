package org.ccsds.moims.mo.mal.automation.datamodel;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@NamedQueries({ @NamedQuery(name = "Schedule.findAll", query = "SELECT c FROM Schedule c") })
public class Schedule {
	
	private Long id;
	private String name;
	private String description;
	private ScheduleDefinition scheduleDefinition;
	private List<ScheduleArgumentValue> arguments;
	private List<ScheduleAttachment> attachments;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	public List<ScheduleArgumentValue> getArguments() {
		return arguments;
	}

	public void setArguments(List<ScheduleArgumentValue> arguments) {
		this.arguments = arguments;
	}

	@OneToMany(targetEntity = ScheduleAttachment.class, mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ScheduleAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<ScheduleAttachment> attachments) {
		this.attachments = attachments;
	}

}
