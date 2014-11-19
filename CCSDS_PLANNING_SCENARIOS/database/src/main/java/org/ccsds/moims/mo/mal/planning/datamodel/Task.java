package org.ccsds.moims.mo.mal.planning.datamodel;

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
public class Task {

	private Long id;
	private Task parent;
	private String comment;
	private List<Task> subTasks;
	private List<TaskArgumentValue> argumentValues;
	private List<ExecutionTimingConstraints> executionTiming;
	private List<ExecutionRunningConstraints> executionDetails;

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
	public Task getParent() {
		return parent;
	}

	public void setParent(Task parent) {
		this.parent = parent;
	}

	@OneToMany
	@JoinColumn(name="task_id")
	public List<TaskArgumentValue> getArgumentValues() {
		return argumentValues;
	}

	public void setArgumentValues(List<TaskArgumentValue> argumentValues) {
		this.argumentValues = argumentValues;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@OneToMany(targetEntity = Task.class, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Task> getSubTasks() {
		return subTasks;
	}

	public void setSubTasks(List<Task> subTasks) {
		this.subTasks = subTasks;
	}

	@OneToMany(targetEntity = ExecutionTimingConstraints.class, mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ExecutionTimingConstraints> getExecutionTiming() {
		return executionTiming;
	}

	public void setExecutionTiming(List<ExecutionTimingConstraints> executionTiming) {
		this.executionTiming = executionTiming;
	}

	@OneToMany(targetEntity = ExecutionRunningConstraints.class, mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ExecutionRunningConstraints> getExecutionDetails() {
		return executionDetails;
	}

	public void setExecutionDetails(
			List<ExecutionRunningConstraints> executionDetails) {
		this.executionDetails = executionDetails;
	}

}
