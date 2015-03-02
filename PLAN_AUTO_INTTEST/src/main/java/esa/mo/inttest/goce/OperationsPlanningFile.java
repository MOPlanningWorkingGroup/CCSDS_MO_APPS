package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;

public class OperationsPlanningFile extends CommonFile {

	private static final String prDefName = "goce opf pr def";
	private static final String prName = "goce opf pr";
	
	public OperationsPlanningFile() {
	}

	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = createTaskDef("goce opf task", "operations task 4", prDefName,
				createTaskDefFields(), createTaskDefArgs());
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = createPrDef(prDefName, "plan4", createPrDefFields());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public TaskInstanceDetails createTaskInst() throws ParseException {
		TaskInstanceDetails taskInst = createTaskInst("MCDD10HZ", "DIS_DFACS_10_HZ v01", prName,
				createPpfTaskFieldsValues("FCT", "MPS", "Time-tagged Sequence"), createTaskArgsZeroValues(),
				createPpfTaskTriggers(null, parseTime("UTC=2007-01-02T12:10:00")));
		return taskInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst(TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(prName, "goce plan 7",
				createPrFieldsValues(parseTime("UTC=2007-01-01T12:10:00"), "Request", ""));
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
