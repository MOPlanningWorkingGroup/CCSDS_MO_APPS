package org.ccsds.moims.mo.mal.planning.datamodel;

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
@NamedQueries({ @NamedQuery(name = "TaskDefinition.findAll", query = "SELECT c FROM TaskDefinition c") })
public class TaskDefinition {

	private Long id;
	private TaskDefinition parent;
	private String name;
	private String description;
	private List<TaskArgumentDefinition> arguments;
	private List<TaskDefinition> subTasks;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne
	@JoinColumn(name = "parent")
	public TaskDefinition getParent() {
		return parent;
	}

	public void setParent(TaskDefinition parent) {
		this.parent = parent;
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
	public List<TaskArgumentDefinition> getArguments() {
		return arguments;
	}

	public void setArguments(
			List<TaskArgumentDefinition> taskArgumentDefinitions) {
		this.arguments = taskArgumentDefinitions;
	}

	@OneToMany(targetEntity = TaskDefinition.class, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<TaskDefinition> getSubTasks() {
		return subTasks;
	}

	public void setSubTasks(List<TaskDefinition> subTasks) {
		this.subTasks = subTasks;
	}
	
	

}
