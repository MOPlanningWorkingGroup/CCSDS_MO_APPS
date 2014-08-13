package org.ccsds.moims.mo.mal.planning.datamodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class PlanningRequestTaskDefinition {

	private Long id;
	private TaskDefinition taskDefinition;
	private PlanningRequestDefinition planningRequestDefinition;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "taskDefinition_id")
	public TaskDefinition getTaskDefinition() {
		return taskDefinition;
	}

	public void setTaskDefinition(TaskDefinition taskDefinition) {
		this.taskDefinition = taskDefinition;
	}

	@OneToOne
	@JoinColumn(name = "planningRequestDefinition_id")
	public PlanningRequestDefinition getPlanningRequestDefinition() {
		return planningRequestDefinition;
	}

	public void setPlanningRequestDefinition(
			PlanningRequestDefinition planningRequestDefinition) {
		this.planningRequestDefinition = planningRequestDefinition;
	}

}
