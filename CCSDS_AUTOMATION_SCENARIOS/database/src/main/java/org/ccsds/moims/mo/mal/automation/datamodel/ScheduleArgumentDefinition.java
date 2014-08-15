package org.ccsds.moims.mo.mal.automation.datamodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class ScheduleArgumentDefinition {
	
	private Long id;
	private ScheduleDefinition scheduleDefinition;
	private String name;
	private Short valueType;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "scheduleDefinition_id")
	public ScheduleDefinition getScheduleDefinition() {
		return scheduleDefinition;
	}

	public void setScheduleDefinition(ScheduleDefinition scheduleDefinition) {
		this.scheduleDefinition = scheduleDefinition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Short getValueType() {
		return valueType;
	}

	public void setValueType(Short valueType) {
		this.valueType = valueType;
	}

}
