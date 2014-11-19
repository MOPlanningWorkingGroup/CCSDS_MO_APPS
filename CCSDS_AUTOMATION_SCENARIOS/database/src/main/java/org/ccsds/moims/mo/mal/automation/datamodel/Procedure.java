package org.ccsds.moims.mo.mal.automation.datamodel;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "Procedure", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({ @NamedQuery(name = "Procedure.findAll", query = "SELECT c FROM Procedure c") })
public class Procedure extends Activity {

	private ProcedureDefinition procedureDefinition;
	private List<ProcedureArgumentValue> arguments;

	@OneToOne
	@JoinColumn(name = "procedureDefinition_id")
	public ProcedureDefinition getProcedureDefinition() {
		return procedureDefinition;
	}

	public void setProcedureDefinition(ProcedureDefinition procedureDefinition) {
		this.procedureDefinition = procedureDefinition;
	}

	@OneToMany(targetEntity = ProcedureArgumentValue.class, mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ProcedureArgumentValue> getArguments() {
		return arguments;
	}

	public void setArguments(List<ProcedureArgumentValue> arguments) {
		this.arguments = arguments;
	}

}
