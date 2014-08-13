package org.ccsds.moims.mo.mal.planning.datamodel;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Entity
public class PlanningRequestValue {

	private Long id;
	private PlanningRequest planningRequest;
	private short type;
	private byte[] value;
	private String unit;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "planningRequest_id")
	public PlanningRequest getPlanningRequest() {
		return planningRequest;
	}

	public void setPlanningRequest(PlanningRequest planningRequest) {
		this.planningRequest = planningRequest;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
