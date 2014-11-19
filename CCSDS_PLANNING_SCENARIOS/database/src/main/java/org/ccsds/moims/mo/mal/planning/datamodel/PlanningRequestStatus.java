package org.ccsds.moims.mo.mal.planning.datamodel;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "PlanningRequestStatus.findAll", query = "SELECT c FROM PlanningRequestStatus c where c.planningRequest = :planningRequest") })
public class PlanningRequestStatus {

	private Long id;
	private PlanningRequest planningRequest;
	private Date date;
	private String comment;
	private StatusEnum statusEnum;

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

	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Enumerated(EnumType.STRING)
	public StatusEnum getStatusEnum() {
		return statusEnum;
	}

	public void setStatusEnum(StatusEnum statusEnum) {
		this.statusEnum = statusEnum;
	}

}
