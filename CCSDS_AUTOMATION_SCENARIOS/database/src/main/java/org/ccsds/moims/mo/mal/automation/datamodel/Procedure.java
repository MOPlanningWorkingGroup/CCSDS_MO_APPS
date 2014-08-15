package org.ccsds.moims.mo.mal.automation.datamodel;

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
@NamedQueries({ @NamedQuery(name = "Procedure.findAll", query = "SELECT c FROM Procedure c") })
public class Procedure {

	private Long id;
	private String name;
	private String description;
	private ProcedureDefinition procedureDefinition;
	private List<ProcedureArgument> arguments;
	private List<ProcedureKeyValue> keyValues;

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

	@OneToOne
	@JoinColumn(name = "procedureDefinition_id")
	public ProcedureDefinition getProcedureDefinition() {
		return procedureDefinition;
	}

	public void setProcedureDefinition(ProcedureDefinition procedureDefinition) {
		this.procedureDefinition = procedureDefinition;
	}

	@OneToMany(targetEntity = ProcedureArgument.class, mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ProcedureArgument> getArguments() {
		return arguments;
	}

	public void setArguments(List<ProcedureArgument> arguments) {
		this.arguments = arguments;
	}

	@OneToMany(targetEntity = ProcedureKeyValue.class, mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ProcedureKeyValue> getKeyValues() {
		return keyValues;
	}

	public void setKeyValues(List<ProcedureKeyValue> keyValues) {
		this.keyValues = keyValues;
	}

}
