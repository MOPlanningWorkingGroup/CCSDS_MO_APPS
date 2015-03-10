package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;

/**
 * OPF - Operations Planning File.
 */
public class OperationsPlanningFile extends CommonFile {

	private static final String PR_DEF_NAME = "goce opf pr def";
	private static final String PR_NAME = "goce opf pr";
	
	public OperationsPlanningFile() {
	}

	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = createTaskDef("goce opf task", "operations task 4", PR_DEF_NAME/*,
				createTaskDefFields(), createTaskDefArgs()*/);
		ArgumentDefinitionDetailsList argDefs = createTaskDefArgDefs();
		setTaskDefParamDefs(argDefs);
		taskDef.setArgumentDefs(argDefs);
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = createPrDef(PR_DEF_NAME, "plan4"/*, createPrDefFields()*/);
		prDef.setArgumentDefs(createPrDefArgDefs());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public TaskInstanceDetails createTaskInst() throws ParseException {
		TaskInstanceDetails taskInst = createTaskInst("MCDD10HZ", "DIS_DFACS_10_HZ v01", PR_NAME/*,
				createPpfTaskFieldsValues("FCT", "MPS", "Time-tagged Sequence"), createTaskArgsZeroValues()*/,
				createPpfTaskTriggers(null, parseTime("UTC=2007-01-02T12:10:00")));
		setTaskArgs(taskInst, "FCT", "MPS", "Time-tagged sequence");
		setTaskParamsCount(taskInst, (short)0);
		return taskInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst(TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(PR_NAME, "goce plan 7"/*,
				createPrFieldsValues(parseTime("UTC=2007-01-01T12:10:00"), "Request", "")*/);
		setPrArgs(prInst, parseTime("UTC=2007-01-01T12:10:00"), "Request", "");
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
