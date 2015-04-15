package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

/**
 * OPF - Operations Planning File.
 */
public class OperationsPlanningFile extends CommonFile {

	private static final String PR_NAME = "goce opf pr";
	
	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("goce opf task", "operations task 4");
		ArgumentDefinitionDetailsList argDefs = createTaskDefArgDefs();
		setTaskDefParamDefs(argDefs);
		taskDef.setArgumentDefs(argDefs);
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("goce opf pr def", "plan4");
		prDef.setArgumentDefs(createPrDefArgDefs());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public TaskInstanceDetails createTaskInst() throws ParseException {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst("MCDD10HZ", "DIS_DFACS_10_HZ v01", PR_NAME);
		taskInst.setTimingConstraints(createPpfTaskTriggers(null, parseTime("UTC=2007-01-02T12:10:00")));
		setTaskArgs(taskInst, "FCT", "MPS", "Time-tagged sequence");
		setTaskParam(taskInst, "RQ_Parameters_count", new AttributeValue(new UShort(0)));
		return taskInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst(TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(PR_NAME, "goce plan 7");
		setPrArgs(prInst, parseTime("UTC=2007-01-01T12:10:00"), "Request", "");
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
