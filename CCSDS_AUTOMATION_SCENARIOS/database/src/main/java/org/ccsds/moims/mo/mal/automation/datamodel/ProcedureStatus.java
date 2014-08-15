package org.ccsds.moims.mo.mal.automation.datamodel;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "ProcedureStatus.findProcedureStatuses", query = "SELECT c FROM ProcedureStatus c where c.procedure = :procedure order by c.creationDate") })
public class ProcedureStatus {

	private Long id;
	private Date creationDate;
	private Procedure procedure;
	private ProcedureState state;
	private List<ProcedureStatusMessage> messages;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@OneToOne
	@JoinColumn(name = "procedure_id")
	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	@Enumerated(EnumType.STRING)
	public ProcedureState getState() {
		return state;
	}

	public void setState(ProcedureState state) {
		this.state = state;
	}

	@OneToMany(targetEntity = ProcedureStatusMessage.class, mappedBy = "procedureStatus", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ProcedureStatusMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ProcedureStatusMessage> messages) {
		this.messages = messages;
	}

}
