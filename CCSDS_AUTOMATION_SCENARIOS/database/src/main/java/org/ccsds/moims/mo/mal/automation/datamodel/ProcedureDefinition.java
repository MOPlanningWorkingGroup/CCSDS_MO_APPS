package org.ccsds.moims.mo.mal.automation.datamodel;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "ProcedureDefinition.findAll", query = "SELECT c FROM ProcedureDefinition c") })
public class ProcedureDefinition {

	private Long id;
	private String name;
	private String description;
	private int version;
	private Date creationDate;
	private String creator;
	private String body;
	private List<ProcedureDefinitionArgument> arguments;
	

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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@OneToMany(targetEntity = ProcedureDefinitionArgument.class, mappedBy = "procedureDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ProcedureDefinitionArgument> getArguments() {
		return arguments;
	}

	public void setArguments(List<ProcedureDefinitionArgument> arguments) {
		this.arguments = arguments;
	}

}
