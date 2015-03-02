package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;

/**
 * Payload planning file - PPF.
 */
public class PayloadPlanningFile extends CommonFile {

	protected static final String taskDefName = "goce ppf task def";
	protected static final String prDefName = "goce ppf pr def";
	protected static final String prName1 = "goce ppf pr inst 1";
	protected static final String prName2 = "goce ppf pr inst 2";
	
	public PayloadPlanningFile() {
	}
	
	public String getTaskDefName() {
		return taskDefName;
	}
	
	public String getPrDefName() {
		return prDefName;
	}
	
	public TaskDefinitionDetails createTaskDef() throws MALException, MALInteractionException {
		TaskDefinitionDetails taskDef = createTaskDef(taskDefName, "payload task", prDefName,
				createTaskDefFields(), createTaskDefArgs());
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = createPrDef(prDefName, "payload pr", createPrDefFields());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public TaskInstanceDetails createTaskInst1() throws ParseException {
		TaskInstanceDetails taskInst = createTaskInst("MSDDIA_B", "Disable_SSTI-B_Diag v01", prName1,
				createPpfTaskFieldsValues("RPF", "MPS", "Time-tagged sequence"),
				createTaskArgsValues((short)1, "SID", "session id", "Raw", "Decimal", "", "32"),
				createPpfTaskTriggers(null, parseTime("UTC=2007-08-31T19:53:23")));
		return taskInst;
	}
	
	public TaskInstanceDetails createTaskInst2() throws ParseException {
		TaskInstanceDetails task2Inst = createTaskInst("MSEDIA_A", "Enable_SSTI-A_Diag v01", prName2,
				createPpfTaskFieldsValues("RPF", "MPS", "Time-tagged sequence"),
				createTaskArgsValues((short)1, "SID", "SID", "Raw", "Decimal", "", "32"),
				createPpfTaskTriggers(null, parseTime("UTC=2007-08-31T20:03:23")));
		return task2Inst;
	}
	
	public PlanningRequestInstanceDetails createPrInst1(TaskInstanceDetailsList taskInsts)
			throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(prName1, "goce ppf pr 1",
				createPrFieldsValues(parseTime("UTC=2007-08-31T19:53:23"), "Request", ""));
		prInst.setTasks(taskInsts);
		return prInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst2(TaskInstanceDetailsList taskInsts)
			throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(prName2, "goce ppf pr 2",
				createPrFieldsValues(parseTime("UTC=2007-08-31T20:02:23"), "Request", ""));
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
