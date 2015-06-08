package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

/**
 * PPF - Payload planning file.
 */
public class PayloadPlanningFile extends CommonFile {

	private static final String[] TASK_DEF_NAMES = { "MSDDIA_B", "MSEDIA_A" };
	private static final String[] TASK_DEF_DESCS = { "Disable_SSTI-B_Diag v01", "Enable_SSTI-A_Diag v01" };
	private static final String[] TASK_TIMES = { "UTC=2007-08-31T19:53:23", "UTC=2007-08-31T20:03:23" };
	
	/**
	 * Returns Task def name for index.
	 * @param idx
	 * @return
	 */
	protected String getTaskDefName(int idx) {
		return TASK_DEF_NAMES[idx];
	}
	
	/**
	 * Returns Task def desc for index.
	 * @param idx
	 * @return
	 */
	protected String getTaskDefDesc(int idx) {
		return TASK_DEF_DESCS[idx];
	}
	
	/**
	 * Create PPF Task definition. <RQ> element in XML.
	 * 
	 * @param idx
	 * @return
	 * @throws MALException
	 * @throws MALInteractionException
	 */
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
	
	/**
	 * Returns PR def name.
	 * @return
	 */
	protected String getPrDefName() {
		return "PPF-PR-Def";
	}
	
	/**
	 * Creates PFF PR definition. <EVRQ> element in XML.
	 * 
	 * @param taskDefIds
	 * @return
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public PlanningRequestDefinitionDetails createPrDef(LongList taskDefIds) throws MALException, MALInteractionException {
		String prDefName = getPrDefName();
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef(prDefName, "EVRQ from PPF");
		prDef.setTaskDefIds(taskDefIds);
		return prDef;
	}
	
	/**
	 * Returns Task instance name for index.
	 * @param idx
	 * @return
	 */
	protected String getTaskInstName(int idx) {
		return TASK_DEF_NAMES[idx] + "-1";
	}
	
	/**
	 * Returns Task instance name for index.
	 * @param idx
	 * @return
	 */
	protected String getPrInstName(int idx) {
		return "PPF-PR-" + (idx+1);
	}
	
	/**
	 * Returns Task time for index.
	 * @param idx
	 * @return
	 */
	protected String getTaskTime(int idx) {
		return TASK_TIMES[idx];
	}
	
	/**
	 * Creates PPF Task instance. <RQ> element in XML.
	 * 
	 * @param idx
	 * @param id
	 * @param defId
	 * @param prId
	 * @return
	 * @throws ParseException
	 */
	public TaskInstanceDetails createTaskInst(int idx, Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(id, defId, null);
		String taskTime = getTaskTime(idx);
		taskInst.setTimingConstraints(createPpfTaskTriggers(null, parseTime(taskTime)));
		addTaskArg(taskInst, "RQ_Parameters_count", new UShort(1));
		addTaskArg(taskInst, "SID", new Union(32L)); // long
		return taskInst;
	}
	
	/**
	 * Create PPF PR instance. <EVRQ> element in XML.
	 * 
	 * @param idx
	 * @param id
	 * @param defId
	 * @param taskInsts
	 * @return
	 * @throws ParseException
	 */
	public PlanningRequestInstanceDetails createPrInst(int idx, Long id, Long defId, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(id, defId, null);
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
