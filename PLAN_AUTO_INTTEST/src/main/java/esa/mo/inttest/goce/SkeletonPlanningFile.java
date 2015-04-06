package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;

/**
 * SPF - Skeleton Planning File.
 */
public class SkeletonPlanningFile extends CommonFile {

	private static final String PR_DEF_NAME = "goce spf pr def";
	private static final String PR_NAME_PRFX = "goce spf pr ";
	private static final String[] PR_DATES = { "UTC=2008-06-02T00:56:57", "UTC=2008-06-02T00:56:58", "UTC=2008-06-02T00:56:59" };

	protected ArgumentDefinitionDetailsList createSpfTaskDefArgs() {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("EV_Parameters_count", Attribute.OCTET_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Parameter_Name", Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Parameter_Description", Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Parameter_Representation", Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Parameter_Radix", Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Parameter_Unit", Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Parameter_Value", Attribute.STRING_TYPE_SHORT_FORM, null));
		return argDefs;
	}
	
	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = createTaskDef("goce spf task def", "skeleton plan task", PR_DEF_NAME);
		taskDef.setArgumentDefs(createSpfTaskDefArgs());
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = createPrDef("plan3", PR_DEF_NAME);
		prDef.setArgumentDefs(createPrDefArgDefs());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public int getInstCount() {
		return PR_DATES.length;
	}
	
	public TaskInstanceDetails createTaskInst(int idx) {
		TaskInstanceDetails taskInst = createTaskInst("NODE", "goce_task_" + (3+idx), getPrName(idx), null);
		taskInst.setArgumentDefNames(new IdentifierList());
		taskInst.setArgumentValues(new AttributeValueList());
		setTaskParamsCount(taskInst, (short)0);
		return taskInst;
	}
	
	protected String getPrName(int idx) {
		return PR_NAME_PRFX + "goce spf pr "+ (idx+1);
	}
	
	public PlanningRequestInstanceDetails createPrInst(int idx, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(getPrName(idx), "GOCE plan " + (4+idx));
		setPrArgs(prInst, parseTime(PR_DATES[idx]), "Event", "Ascending node crossing");
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
