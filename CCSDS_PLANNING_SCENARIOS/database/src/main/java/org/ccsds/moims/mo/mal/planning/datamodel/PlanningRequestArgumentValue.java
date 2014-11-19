package org.ccsds.moims.mo.mal.planning.datamodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class PlanningRequestArgumentValue {

	private Long id;
	private PlanningRequest planningRequest;
	private String name;
	private byte[] value;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "planningrequest_id")
	public PlanningRequest getPlanningRequest() {
		return planningRequest;
	}

	public void setPlanningRequest(PlanningRequest planningRequest) {
		this.planningRequest = planningRequest;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

}
