package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;

/**
 * Planning request consumer for testing. Implemented by GOCE example files - PPF, PIF, SPF, OPF.
 */
public class GoceConsumer {

	private PlanningRequestStub stub;

	public GoceConsumer(PlanningRequestStub stub) {
		this.stub = stub;
	}

	public PlanningRequestStub getStub() {
		return this.stub;
	}
	
	protected IdentifierList getTaskNameList(String taskDefName) {
		IdentifierList list = new IdentifierList();
		list.add(new Identifier(taskDefName));
		return list;
	}
	
	protected TaskInstanceDetailsList getTaskInstList(TaskInstanceDetails taskInst) {
		TaskInstanceDetailsList list = new TaskInstanceDetailsList();
		list.add(taskInst);
		return list;
	}
	
	private Long submitTaskDef(TaskDefinitionDetails taskDef) throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefsList = new TaskDefinitionDetailsList();
		taskDefsList.add(taskDef);
		LongList taskIdsList = null;
		taskIdsList = stub.addTaskDefinition(taskDefsList);
		return (taskIdsList != null && !taskIdsList.isEmpty()) ? taskIdsList.get(0) : null;
	}
	
	private Long submitPrDef(PlanningRequestDefinitionDetails prDef) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefsList = new PlanningRequestDefinitionDetailsList();
		prDefsList.add(prDef);
		LongList defIdsList = null;
		defIdsList = stub.addDefinition(prDefsList);
		return (defIdsList != null && !defIdsList.isEmpty()) ? defIdsList.get(0) : null;
	}
	
	// PPF - payload plan file
	public void ppf() throws MALException, MALInteractionException, ParseException {
		PayloadPlanningFile ppf = new PayloadPlanningFile();
		
		TaskDefinitionDetails taskDef = ppf.createTaskDef();
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = ppf.createPrDef(getTaskNameList(taskDef.getName().getValue()));
		Long prDefId = submitPrDef(prDef);
		
		TaskInstanceDetails taskInst = ppf.createTaskInst1();
		Long taskInstId = 1L; // FIXME store task inst in COM archive?
		PlanningRequestInstanceDetails prInst = ppf.createPrInst1(getTaskInstList(taskInst));
		Long prInstId = 2L; // FIXME store pr inst in COM archive?
		stub.submitPlanningRequest(prDefId, prInstId, prInst);
		
		TaskInstanceDetails taskInst2 = ppf.createTaskInst2();
		Long taskInstId2 = 3L; // FIXME store task instance in COM archive?
		PlanningRequestInstanceDetails prInst2 = ppf.createPrInst2(getTaskInstList(taskInst2));
		Long prInstId2 = 4L; // FIXME store pr instance in COM archive?
		stub.submitPlanningRequest(prDefId, prInstId2, prInst2);
	}
	
	public void createPpfDefsIfMissing() throws MALException, MALInteractionException {
		PayloadPlanningFile ppf = new PayloadPlanningFile();
		
		IdentifierList taskNames = new IdentifierList();
		taskNames.add(new Identifier(ppf.getTaskDefName()));
		
		LongList taskIds = stub.listTaskDefinition(taskNames);
		
		if (taskIds.isEmpty()) {
			TaskDefinitionDetails taskDef = ppf.createTaskDef();
			Long taskDefId = submitTaskDef(taskDef);
		} else if (taskIds.size() > 1) {
			throw new MALException("more than one goce ppf task def with same name found");
		}
		// task def ok, proceed with pr def
		IdentifierList prNames = new IdentifierList();
		prNames.add(new Identifier(ppf.getPrDefName()));
		
		LongList prIds = stub.listDefinition(prNames);
		
		if (prIds.isEmpty()) {
			PlanningRequestDefinitionDetails prDef = ppf.createPrDef(getTaskNameList(ppf.getTaskDefName()));
			Long prDefId = submitPrDef(prDef);
		} else if (prIds.size() > 1) {
			throw new MALException("more than one goce ppf pr def with same name found");
		}
	}
	
	public void createPpfInstsIfMissingAndDefsExist() throws MALException, MALInteractionException, ParseException {
		PayloadPlanningFile ppf = new PayloadPlanningFile();
		
		IdentifierList taskNames = new IdentifierList();
		taskNames.add(new Identifier(ppf.getTaskDefName()));
		
		LongList taskIds = stub.listTaskDefinition(taskNames);
		
		TaskInstanceDetails taskInst1 = null;
		TaskInstanceDetails taskInst2 = null;
		if (taskIds.isEmpty()) {
			// no task def - nothing to do
		} else if (taskIds.size() > 1) {
			throw new MALException("more than one goce ppf task def with same name found");
		} else {
			// TODO check if task instances already exist
			taskInst1 = ppf.createTaskInst1(); // FIXME store task instance in COM archive?
			taskInst2 = ppf.createTaskInst2(); // FIXME store task instance in COM archive?
		}
		// task instances ok, proceed with pr instances
		IdentifierList prNames = new IdentifierList();
		prNames.add(new Identifier(ppf.getPrDefName()));
		
		LongList prIds = stub.listDefinition(prNames);
		
		if (prIds.isEmpty()) {
			// no pr def - nothing to do
		} else if (prIds.size() > 1) {
			throw new MALException("more than one goce ppf pr def with same name found");
		} else {
			// TODO check if pr instances already exist
			Long prDefId = 1L; // TODO acquire pr def id
			PlanningRequestInstanceDetails prInst = ppf.createPrInst1(getTaskInstList(taskInst1));
			Long prInstId = 2L; // FIXME store pr instance in COM archive?
			stub.submitPlanningRequest(prDefId, prInstId, prInst);
			
			PlanningRequestInstanceDetails prInst2 = ppf.createPrInst2(getTaskInstList(taskInst2));
			Long prInstId2 = 3L; // FIXME store pr instance in COM archive?
			stub.submitPlanningRequest(prDefId, prInstId2, prInst2);
		}
	}
	

	// PIF - plan increment file
	public void pif() throws MALException, MALInteractionException, ParseException {
		PlanIncrementFile pif = new PlanIncrementFile();
		
		TaskDefinitionDetails taskDef = pif.createTaskDef();
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = pif.createPrDef(getTaskNameList(taskDef.getName().getValue()));
		Long prDefId = submitPrDef(prDef);
		
		TaskInstanceDetails taskInst = pif.createTaskInst();
		Long taskInstId = 1L; // FIXME store task instance in COM archive?
		
		PlanningRequestInstanceDetails prInst = pif.createPrInst(getTaskInstList(taskInst));
		Long prInstId = 2L; // FIXME store pr instance in COM archive?
		
		stub.submitPlanningRequest(prDefId, prInstId, prInst);
	}
	
	// SPF - skeleton planning file
	public void spf() throws MALException, MALInteractionException, ParseException {
		SkeletonPlanningFile spf = new SkeletonPlanningFile();
		
		TaskDefinitionDetails taskDef = spf.createTaskDef();
		Long taskDefId = submitTaskDef(taskDef);
	
		PlanningRequestDefinitionDetails prDef = spf.createPrDef(getTaskNameList(taskDef.getName().getValue()));
		Long prDefId = submitPrDef(prDef);
		
		for (int i = 0; i < spf.getInstCount(); ++i) {
			TaskInstanceDetails taskInst = spf.createTaskInst(i);
			Long taskInstId = 1L; // FIXME store task instance in com arc
			
			PlanningRequestInstanceDetails prInst = spf.createPrInst(i, getTaskInstList(taskInst));
			Long prInstId = 2L; // FIXME store pr instance in com arc
			
			stub.submitPlanningRequest(prDefId, prInstId, prInst);
		}
	}
	
	// OPF - operations planning file
	public void opf() throws MALException, MALInteractionException, ParseException {
		OperationsPlanningFile opf = new OperationsPlanningFile();
		
		TaskDefinitionDetails taskDef = opf.createTaskDef();
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = opf.createPrDef(getTaskNameList(taskDef.getName().getValue()));
		Long prDefId = submitPrDef(prDef);
		
		TaskInstanceDetails taskInst = opf.createTaskInst();
		Long taskInstId = 1L; // FIXME store task inst in com arc
		
		PlanningRequestInstanceDetails prInst = opf.createPrInst(getTaskInstList(taskInst));
		Long prInstId = 2L; // FIXME store pr inst in com arc
		stub.submitPlanningRequest(prDefId, null, prInst);
	}
}
