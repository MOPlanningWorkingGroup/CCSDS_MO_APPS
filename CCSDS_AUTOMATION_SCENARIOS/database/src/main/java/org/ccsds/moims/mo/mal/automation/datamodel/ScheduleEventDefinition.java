package org.ccsds.moims.mo.mal.automation.datamodel;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class ScheduleEventDefinition {

	private Long id;
	private ScheduleDefinition scheduleDefinition;
	private String name;
	private String description;
	private List<ArgumentEventDefinition> arguments;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne
	@JoinColumn(name = "scheduleDefinition_id")
	public ScheduleDefinition getScheduleDefinition() {
		return scheduleDefinition;
	}

	public void setScheduleDefinition(ScheduleDefinition scheduleDefinition) {
		this.scheduleDefinition = scheduleDefinition;
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

	@OneToMany(targetEntity = ArgumentEventDefinition.class, mappedBy = "scheduleEventDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ArgumentEventDefinition> getArguments() {
		return arguments;
	}

	public void setArguments(List<ArgumentEventDefinition> arguments) {
		this.arguments = arguments;
	}

}
