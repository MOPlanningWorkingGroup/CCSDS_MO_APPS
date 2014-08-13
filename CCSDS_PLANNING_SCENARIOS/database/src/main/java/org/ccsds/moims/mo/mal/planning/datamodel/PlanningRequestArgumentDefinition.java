package org.ccsds.moims.mo.mal.planning.datamodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class PlanningRequestArgumentDefinition {

	private Long id;
	private PlanningRequestDefinition planningRequestDefinition;
	private String name;
	private short valueType;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getValueType() {
		return valueType;
	}

	public void setValueType(short valueType) {
		this.valueType = valueType;
	}
	
	

}
