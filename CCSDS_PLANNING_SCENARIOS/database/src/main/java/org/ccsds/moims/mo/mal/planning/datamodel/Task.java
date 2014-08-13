package org.ccsds.moims.mo.mal.planning.datamodel;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Task {

	private Long id;
	private String name;
	private String description;
	private TaskStatus status;
	private TaskDefinition taskDefinition;
	private List<TaskArgumentValue> argumentValues;

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

	@Enumerated(EnumType.STRING)
	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public TaskDefinition getTaskDefinition() {
		return taskDefinition;
	}

	public void setTaskDefinition(TaskDefinition taskDefinition) {
		this.taskDefinition = taskDefinition;
	}

	@OneToMany
	@JoinColumn(name="task_id")
	public List<TaskArgumentValue> getArgumentValues() {
		return argumentValues;
	}

	public void setArgumentValues(List<TaskArgumentValue> argumentValues) {
		this.argumentValues = argumentValues;
	}

}
