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
public class PlanningRequestArgumentDefinition {

	private Long id;
	private PlanningRequestDefinition planningRequestDefinition;
	private String name;
	private short valueType;
	private PlanningRequestArgumentDefinition parent;
	private List<PlanningRequestArgumentDefinition> arguments;

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
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	public PlanningRequestArgumentDefinition getParent() {
		return parent;
	}

	public void setParent(PlanningRequestArgumentDefinition parent) {
		this.parent = parent;
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

	@OneToMany(targetEntity = PlanningRequestArgumentDefinition.class, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PlanningRequestArgumentDefinition> getArguments() {
		return arguments;
	}

	public void setArguments(List<PlanningRequestArgumentDefinition> arguments) {
		this.arguments = arguments;
	}

}
