package org.ccsds.moims.mo.mal.planning.datamodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PlanningRequestDomain {

	private Long id;
	private PlanningRequest planningRequest;
	private String domain;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PlanningRequest getPlanningRequest() {
		return planningRequest;
	}

	public void setPlanningRequest(PlanningRequest planningRequest) {
		this.planningRequest = planningRequest;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
