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
	private String comment;
	private String source;
	private String destination;
	private List<PlanningRequestDomain> domains;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	@OneToMany(targetEntity = PlanningRequestDomain.class, mappedBy = "planningRequest", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PlanningRequestDomain> getDomains() {
		return domains;
	}

	public void setDomains(List<PlanningRequestDomain> domains) {
		this.domains = domains;
	}

	@OneToMany(targetEntity = PlanningRequestArgumentValue.class, mappedBy = "planningRequest", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PlanningRequestArgumentValue> getArgumentValues() {
		return argumentValues;
	}

	public void setArgumentValues(List<PlanningRequestArgumentValue> argumentValues) {
		this.argumentValues = argumentValues;
	}


}
