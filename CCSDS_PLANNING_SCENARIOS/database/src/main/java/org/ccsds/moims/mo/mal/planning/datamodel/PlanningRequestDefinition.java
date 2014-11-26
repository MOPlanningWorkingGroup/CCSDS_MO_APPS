package org.ccsds.moims.mo.mal.planning.datamodel;

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
@NamedQueries({ @NamedQuery(name = "PlanningRequestDefinition.findAll", query = "SELECT c FROM PlanningRequestDefinition c") })
public class PlanningRequestDefinition {

	private Long id;
	private String name;
	private String description;
	private Boolean validateOnSubmit;
	private List<PlanningRequestArgumentDefinition> arguments;
	private List<TaskDefinition> taskDefinitions;

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

	public Boolean getValidateOnSubmit() {
		return validateOnSubmit;
	}

	public void setValidateOnSubmit(Boolean validateOnSubmit) {
		this.validateOnSubmit = validateOnSubmit;
	}

	@OneToMany(targetEntity = PlanningRequestArgumentDefinition.class, mappedBy = "planningRequestDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PlanningRequestArgumentDefinition> getArguments() {
		return arguments;
	}

	public void setArguments(List<PlanningRequestArgumentDefinition> arguments) {
		this.arguments = arguments;
	}

	@OneToMany(targetEntity = TaskDefinition.class, mappedBy = "planningRequestDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<TaskDefinition> getTaskDefinitions() {
		return taskDefinitions;
	}

	public void setTaskDefinitions(List<TaskDefinition> taskDefinitions) {
		this.taskDefinitions = taskDefinitions;
	}

}
