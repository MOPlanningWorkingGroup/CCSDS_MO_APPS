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

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

/**
 * SPF - Skeleton Planning File.
 */
public class SkeletonPlanningFile extends CommonFile {

	protected ArgumentDefinitionDetailsList createSpfTaskDefArgs() {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("EV_Parameters_count", null, Attribute.USHORT_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("EID", "Event ID", Attribute.INTEGER_TYPE_SHORT_FORM, null, "Raw", "Decimal", null));
		return argDefs;
	}
	
	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("NODE", "EV from SPF");
		taskDef.setArgumentDefs(createSpfTaskDefArgs());
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("EVRQ", "Ascending node crossing");
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public TaskInstanceDetails createTaskInst(int idx, Long id, Long defId, Long prId) {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(/*"NODE-"+(idx+1)*/id, defId, null/*, getPrName(idx)*/);
		setTaskArg(taskInst, "EV_Parameters_count", new AttributeValue(new UShort(0)));
		return taskInst;
	}
	
	public int getPrInstCount() {
		return 3;
	}
	
	protected String getPrName(int idx) {
		return "EVRQ-"+ (idx+1);
	}
	
	public PlanningRequestInstanceDetails createPrInst(int idx, Long id, Long defId, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(/*getPrName(idx)*/id, defId, null);
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
