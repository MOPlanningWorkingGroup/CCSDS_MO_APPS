package org.ccsds.moims.mo.mal.automation.datamodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class ProcedureDefinitionArgument {

	private Long id;
	private ProcedureDefinition procedureDefinition;
	private String name;
	private Short dataType;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "procedureDefinition_id")
	public ProcedureDefinition getProcedureDefinition() {
		return procedureDefinition;
	}

	public void setProcedureDefinition(ProcedureDefinition procedureDefinition) {
		this.procedureDefinition = procedureDefinition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Short getDataType() {
		return dataType;
	}

	public void setDataType(Short dataType) {
		this.dataType = dataType;
	}

}
