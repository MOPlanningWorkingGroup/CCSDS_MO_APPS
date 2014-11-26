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
	private String name;
	private String description;
	private List<Task> subTasks;
	private List<TaskArgumentValue> argumentValues;
	private List<TimeTrigger> timeTriggers;
	private List<EventTrigger> eventTriggers;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany
	@JoinColumn(name="task_id")
	public List<TaskArgumentValue> getArgumentValues() {
		return argumentValues;
	}

	public void setArgumentValues(List<TaskArgumentValue> argumentValues) {
		this.argumentValues = argumentValues;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(targetEntity = Task.class, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Task> getSubTasks() {
		return subTasks;
	}

	public void setSubTasks(List<Task> subTasks) {
		this.subTasks = subTasks;
	}

	public List<TimeTrigger> getTimeTriggers() {
		return timeTriggers;
	}

	public void setTimeTriggers(List<TimeTrigger> timeTriggers) {
		this.timeTriggers = timeTriggers;
	}

	public List<EventTrigger> getEventTriggers() {
		return eventTriggers;
	}

	public void setEventTriggers(List<EventTrigger> eventTriggers) {
		this.eventTriggers = eventTriggers;
	}

}
