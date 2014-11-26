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
@NamedQueries({ @NamedQuery(name = "PlanningRequest.findAll", query = "SELECT c FROM PlanningRequest c") })
public class PlanningRequest {

	private Long id;
	private PlanningRequestDefinition planningRequestDefinition;
	private String name;
	private String description;
	private String source;
	private String destination;
	private List<PlanningRequestArgumentValue> argumentValues;
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "planningrequestdefinition_id")
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@OneToMany(targetEntity = PlanningRequestArgumentValue.class, mappedBy = "planningRequest", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PlanningRequestArgumentValue> getArgumentValues() {
		return argumentValues;
	}

	public void setArgumentValues(List<PlanningRequestArgumentValue> argumentValues) {
		this.argumentValues = argumentValues;
	}


}
