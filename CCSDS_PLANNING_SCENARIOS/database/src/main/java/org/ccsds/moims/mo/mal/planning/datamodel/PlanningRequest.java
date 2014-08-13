package org.ccsds.moims.mo.mal.planning.datamodel;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "PlanningRequest.findAll", query = "SELECT c FROM PlanningRequest c") })
public class PlanningRequest {

	private Long id;
	private String name;
	private String description;
	private int version;
	private Date creationDate;
	private String creator;
	private PlanningRequestStatus status;
	private List<PlanningRequestValue> values;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Temporal(TemporalType.DATE)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Enumerated(EnumType.STRING)
	public PlanningRequestStatus getStatus() {
		return status;
	}

	public void setStatus(PlanningRequestStatus status) {
		this.status = status;
	}

	@OneToMany(targetEntity = PlanningRequestValue.class, mappedBy = "planningRequest", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PlanningRequestValue> getValues() {
		return values;
	}

	public void setValues(List<PlanningRequestValue> values) {
		this.values = values;
	}

}
