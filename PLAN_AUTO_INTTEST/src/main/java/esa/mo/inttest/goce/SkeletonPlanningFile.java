package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

/**
 * SPF - Skeleton Planning File.
 */
public class SkeletonPlanningFile extends CommonFile {

	/**
	 * Creates Task def args.
	 * @return
	 */
	protected ArgumentDefinitionDetailsList createSpfTaskDefArgs() {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("EV_Parameters_count", null, Attribute.USHORT_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("EID", "Event ID", Attribute.INTEGER_TYPE_SHORT_FORM, null, "Raw", "Decimal", null));
		return argDefs;
	}
	
	/**
	 * Creates Task definition.
	 * @return
	 */
	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("NODE", "EV from SPF");
		taskDef.setArgumentDefs(createSpfTaskDefArgs());
		return taskDef;
	}
	
	/**
	 * Creates PR definition.
	 * @param taskDefIds
	 * @return
	 */
	public PlanningRequestDefinitionDetails createPrDef(LongList taskDefIds) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("EVRQ", "Ascending node crossing");
		prDef.setTaskDefIds(taskDefIds);
		return prDef;
	}
	
	/**
	 * Creates Task instance.
	 * @param idx
	 * @param id
	 * @param defId
	 * @param prId
	 * @return
	 */
	public TaskInstanceDetails createTaskInst(int idx, Long id, Long defId, Long prId) {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(id, defId, null);
		addTaskArg(taskInst, "EV_Parameters_count", new UShort(0));
		return taskInst;
	}
	
	/**
	 * Returns number of PR instances to create.
	 * @return
	 */
	public int getPrInstCount() {
		return 3;
	}
	
	/**
	 * Returns PR name for index.
	 * @param idx
	 * @return
	 */
	protected String getPrName(int idx) {
		return "EVRQ-"+ (idx+1);
	}
	
	/**
	 * Creates PR instance.
	 * @param idx
	 * @param id
	 * @param defId
	 * @param taskInsts
	 * @return
	 * @throws ParseException
	 */
	public PlanningRequestInstanceDetails createPrInst(int idx, Long id, Long defId,
			TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(id, defId, null);
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
