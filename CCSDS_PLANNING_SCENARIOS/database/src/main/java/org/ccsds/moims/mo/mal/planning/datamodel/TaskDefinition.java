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
@NamedQueries({ @NamedQuery(name = "TaskDefinition.findAll", query = "SELECT c FROM TaskDefinition c") })
public class TaskDefinition {

	private Long id;
	private String name;
	private String description;
	private List<TaskArgumentDefinition> taskArgumentDefinitions;

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

	@OneToMany(targetEntity = TaskArgumentDefinition.class, mappedBy = "taskDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<TaskArgumentDefinition> getTaskArgumentDefinitions() {
		return taskArgumentDefinitions;
	}

	public void setTaskArgumentDefinitions(
			List<TaskArgumentDefinition> taskArgumentDefinitions) {
		this.taskArgumentDefinitions = taskArgumentDefinitions;
	}

}
