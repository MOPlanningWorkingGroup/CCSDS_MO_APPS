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
@NamedQueries({ @NamedQuery(name = "ScheduleStatus.findScheduleStatuses", query = "SELECT c FROM ScheduleStatus c where c.schedule = :schedule order by c.creationDate") })
public class ScheduleStatus {

	private Long id;
	private Date creationDate;
	private Schedule schedule;
	private ScheduleState state;
	private List<ScheduleStatusMessage> messages;

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
	@JoinColumn(name = "schedule_id")
	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	@Enumerated(EnumType.STRING)
	public ScheduleState getState() {
		return state;
	}

	public void setState(ScheduleState state) {
		this.state = state;
	}

	@OneToMany(targetEntity = ScheduleStatusMessage.class, mappedBy = "scheduleStatus", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ScheduleStatusMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ScheduleStatusMessage> messages) {
		this.messages = messages;
	}

}
