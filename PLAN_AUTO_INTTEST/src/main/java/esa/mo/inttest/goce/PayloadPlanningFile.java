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

	private static final String[] TASK_DEF_NAMES = { "MSDDIA_B", "MSEDIA_A" };
	private static final String[] TASK_DEF_DESCS = { "Disable_SSTI-B_Diag v01", "Enable_SSTI-A_Diag v01" };
	private static final String[] TASK_TIMES = { "UTC=2007-08-31T19:53:23", "UTC=2007-08-31T20:03:23" };
	
	protected String getTaskDefName(int idx) {
		return TASK_DEF_NAMES[idx];
	}
	
	protected String getTaskDefDesc(int idx) {
		return TASK_DEF_DESCS[idx];
	}
	
	public TaskDefinitionDetails createTaskDef(int idx) throws MALException, MALInteractionException {
		String taskDefName = getTaskDefName(idx);
		String taskDefDesc = getTaskDefDesc(idx);
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef(taskDefName, taskDefDesc);
		ArgumentDefinitionDetailsList argDefs = createSrcDestTypeArgDefs("RPF");
		setParamsCountArgDef(argDefs);
		argDefs.add(createArgDef("SID", "SID", Attribute.LONG_TYPE_SHORT_FORM, "", "Raw", "Decimal", null));
		taskDef.setArgumentDefs(argDefs);
		return taskDef;
	}
	
	protected String getPrDefName() {
		return "PPF-PR-Def";
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) throws MALException, MALInteractionException {
		String prDefName = getPrDefName();
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef(prDefName, "EVRQ from PPF");
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	protected String getTaskInstName(int idx) {
		return TASK_DEF_NAMES[idx] + "-1";
	}
	
	protected String getPrInstName(int idx) {
		return "PPF-PR-" + (idx+1);
	}
	
	protected String getTaskTime(int idx) {
		return TASK_TIMES[idx];
	}
	
	public TaskInstanceDetails createTaskInst(int idx, Long id, Long defId, Long prId) throws ParseException {
//		String taskName = getTaskInstName(idx);
//		String prName = getPrInstName(idx);
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(/*taskName*/id, defId, null/*, prName*/);
		String taskTime = getTaskTime(idx);
		taskInst.setTimingConstraints(createPpfTaskTriggers(null, parseTime(taskTime)));
		setTaskArg(taskInst, "RQ_Parameters_count", new AttributeValue(new UShort(1)));
		setTaskArg(taskInst, "SID", new AttributeValue(new Union(32L))); // long
		return taskInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst(int idx, Long id, Long defId, TaskInstanceDetailsList taskInsts) throws ParseException {
//		String prName = getPrInstName(idx);
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(/*prName*/id, defId, null);
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
