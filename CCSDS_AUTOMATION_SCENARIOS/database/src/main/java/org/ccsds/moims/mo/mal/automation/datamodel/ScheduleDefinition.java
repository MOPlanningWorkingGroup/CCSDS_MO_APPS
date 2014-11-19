package org.ccsds.moims.mo.mal.automation.datamodel;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({ @NamedQuery(name = "ScheduleDefinition.findAll", query = "SELECT c FROM ScheduleDefinition c") })
public class ScheduleDefinition {
	
	private Long id;
	private String name;
	private String description;
	private List<ScheduleArgumentDefinition> arguments;
	private String typeOfSchedule;
	private Boolean standaloneOrIncrement;
	private List<ScheduleEventDefinition> eventDefinitions;
	
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

	@OneToMany(targetEntity = ScheduleArgumentDefinition.class, mappedBy = "scheduleDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ScheduleArgumentDefinition> getArguments() {
		return arguments;
	}

	public void setArguments(List<ScheduleArgumentDefinition> arguments) {
		this.arguments = arguments;
	}

	public String getTypeOfSchedule() {
		return typeOfSchedule;
	}

	public void setTypeOfSchedule(String typeOfSchedule) {
		this.typeOfSchedule = typeOfSchedule;
	}

	public Boolean getStandaloneOrIncrement() {
		return standaloneOrIncrement;
	}

	public void setStandaloneOrIncrement(Boolean standaloneOrIncrement) {
		this.standaloneOrIncrement = standaloneOrIncrement;
	}

	@OneToMany(targetEntity = ScheduleEventDefinition.class, mappedBy = "scheduleDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ScheduleEventDefinition> getEventDefinitions() {
		return eventDefinitions;
	}

	public void setEventDefinitions(List<ScheduleEventDefinition> eventDefinitions) {
		this.eventDefinitions = eventDefinitions;
	}
	
	

}
