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

public class SkeletonPlanningFile extends CommonFile {

	private static final String prDefName = "goce spf pr def";
	private static final String prNamePrfx = "goce spf pr ";
	private static final String[] prDates = { "UTC=2008-06-02T00:56:57", "UTC=2008-06-02T00:56:58", "UTC=2008-06-02T00:56:59" };

	public SkeletonPlanningFile() {
	}

	protected ArgumentDefinitionDetailsList createSpfTaskDefArgs() {
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(createArgDef("EV_Parameters_count", "task event parameters count", Attribute.OCTET_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Name", "task parameter name", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Description", "task parameter description",
				Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Representation", "task parameter representation",
				Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Radix", "task parameter radix", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Unit", "task parameter unit", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Value", "task parameter value", Attribute.STRING_TYPE_SHORT_FORM));
		return args;
	}
	
	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = createTaskDef("goce spf task def", "skeleton plan task", prDefName, null,
				createSpfTaskDefArgs());
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = createPrDef("plan3", prDefName, createPrDefFields());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public int getInstCount() {
		return prDates.length;
	}
	
	public TaskInstanceDetails createTaskInst(int idx) {
		TaskInstanceDetails taskInst = createTaskInst("NODE", "goce_task_" + (3+idx), getPrName(idx), null,
				createTaskArgsZeroValues(), null);
		return taskInst;
	}
	
	protected String getPrName(int idx) {
		return prNamePrfx + "goce spf pr "+ (idx+1);
	}
	
	public PlanningRequestInstanceDetails createPrInst(int idx, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(getPrName(idx), "GOCE plan " + (4+idx),
				createPrFieldsValues(parseTime(prDates[idx]), "Event", "Ascending node crossing"));
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
