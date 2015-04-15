package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

/**
 * PPF - Payload planning file.
 */
public class PayloadPlanningFile extends CommonFile {

	protected static final String TASK_DEF_NAME = "goce ppf task def";
	protected static final String PR_DEF_NAME = "goce ppf pr def";
	protected static final String PR_NAME_1 = "goce ppf pr inst 1";
	protected static final String PR_NAME_2 = "goce ppf pr inst 2";
	
	public String getTaskDefName() {
		return TASK_DEF_NAME;
	}
	
	public String getPrDefName() {
		return PR_DEF_NAME;
	}
	
	public TaskDefinitionDetails createTaskDef() throws MALException, MALInteractionException {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef(TASK_DEF_NAME, "payload task");
		ArgumentDefinitionDetailsList argDefs = createTaskDefArgDefs();
		setTaskDefParamDefs(argDefs);
		argDefs.add(createArgDef("SID", "SID", Attribute.INTEGER_TYPE_SHORT_FORM, null, "Raw", "Decimal", null));
		taskDef.setArgumentDefs(argDefs);
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef(PR_DEF_NAME, "payload pr");
		prDef.setArgumentDefs(createPrDefArgDefs());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public TaskInstanceDetails createTaskInst1() throws ParseException {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst("MSDDIA_B", "Disable_SSTI-B_Diag v01", PR_NAME_1);
		taskInst.setTimingConstraints(createPpfTaskTriggers(null, parseTime("UTC=2007-08-31T19:53:23")));
		setTaskArgs(taskInst, "RPF", "MPS", "Time-tagged sequence");
		setTaskParam(taskInst, "RQ_Parameters_count", new AttributeValue(new UShort(1)));
		setTaskParam(taskInst, "SID", new AttributeValue(new Union(32)));
		return taskInst;
	}
	
	public TaskInstanceDetails createTaskInst2() throws ParseException {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst("MSEDIA_A", "Enable_SSTI-A_Diag v01", PR_NAME_2);
		taskInst.setTimingConstraints(createPpfTaskTriggers(null, parseTime("UTC=2007-08-31T20:03:23")));
		setTaskArgs(taskInst, "RPF", "MPS", "Time-tagged sequence");
		setTaskParam(taskInst, "RQ_Parameters_count", new AttributeValue(new UShort(1)));
		setTaskParam(taskInst, "SID", new AttributeValue(new Union(32)));
		return taskInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst1(TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(PR_NAME_1, "goce ppf pr 1");
		setPrArgs(prInst, parseTime("UTC=2007-08-31T19:53:23"), "Request", "");
		prInst.setTasks(taskInsts);
		return prInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst2(TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(PR_NAME_2, "goce ppf pr 2");
		setPrArgs(prInst, parseTime("UTC=2007-08-31T20:02:23"), "Request", "");
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
