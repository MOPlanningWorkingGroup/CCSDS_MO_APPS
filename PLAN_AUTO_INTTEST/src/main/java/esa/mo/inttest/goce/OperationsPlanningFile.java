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

	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("MCDD10HZ", "DIS_DFACS_10_HZ v01");
		ArgumentDefinitionDetailsList argDefs = createSrcDestTypeArgDefs("FCT");
		setParamsCountArgDef(argDefs);
		taskDef.setArgumentDefs(argDefs);
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("OPF", "EVRQ from OPF");
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public String getPrName() {
		return "OPF-1";
	}
	
	public TaskInstanceDetails createTaskInst(Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(/*"MCDD10HZ"*/id, defId, null/*, getPrName()*/);
		setTaskArg(taskInst, "RQ_Parameters_count", new AttributeValue(new UShort(0)));
		taskInst.setTimingConstraints(createPpfTaskTriggers(null, parseTime("UTC=2007-01-02T12:10:00")));
		return taskInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst(Long id, Long defId, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(/*getPrName()*/id, defId, null);
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
