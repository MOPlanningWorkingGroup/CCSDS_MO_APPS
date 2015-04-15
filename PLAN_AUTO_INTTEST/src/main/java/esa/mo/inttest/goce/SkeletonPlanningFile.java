package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

/**
 * SPF - Skeleton Planning File.
 */
public class SkeletonPlanningFile extends CommonFile {

	private static final String PR_NAME_PRFX = "goce spf pr ";
	private static final String[] PR_DATES = { "UTC=2008-06-02T00:56:57", "UTC=2008-06-02T00:56:58", "UTC=2008-06-02T00:56:59" };

	protected ArgumentDefinitionDetailsList createSpfTaskDefArgs() {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("EV_Parameters_count", null, Attribute.USHORT_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("EID", "Event ID", Attribute.INTEGER_TYPE_SHORT_FORM, null, "Raw", "Decimal", null));
		return argDefs;
	}
	
	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("goce spf task def", "skeleton plan task");
		taskDef.setArgumentDefs(createSpfTaskDefArgs());
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("goce spf pr def", "plan3");
		prDef.setArgumentDefs(createPrDefArgDefs());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public int getInstCount() {
		return PR_DATES.length;
	}
	
	public TaskInstanceDetails createTaskInst(int idx) {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst("NODE", "goce_task_" + (3+idx), getPrName(idx));
		taskInst.setArgumentDefNames(new IdentifierList()); // needed for param below
		taskInst.setArgumentValues(new AttributeValueList());
		setTaskParam(taskInst, "EV_Parameters_count", new AttributeValue(new UShort(0)));
		return taskInst;
	}
	
	protected String getPrName(int idx) {
		return PR_NAME_PRFX + "goce spf pr "+ (idx+1);
	}
	
	public PlanningRequestInstanceDetails createPrInst(int idx, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(getPrName(idx), "GOCE plan " + (4+idx));
		setPrArgs(prInst, parseTime(PR_DATES[idx]), "Event", "Ascending node crossing");
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
