package esa.mo.inttest.bepicolombo;

import java.text.ParseException;
import java.util.concurrent.atomic.AtomicLong;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

public class BepiColomboConsumer {

	private PlanningRequestConsumer cons;
	
	public BepiColomboConsumer(PlanningRequestConsumer cons) {
		this.cons = cons;
	}
	
	public PlanningRequestConsumer getConsumer() {
		return cons;
	}
	
	protected Long submitTaskDef(TaskDefinitionDetails def) throws MALException, MALInteractionException {
		def.setId(0L);
		TaskDefinitionDetailsList defs = new TaskDefinitionDetailsList();
		defs.add(def);
		LongList ids = cons.getStub().addDefinition(DefinitionType.TASK_DEF, defs);
		def.setId(ids.get(0));
		return def.getId();
	}
	
	protected IdentifierList taskDefNames(TaskDefinitionDetails... defs) {
		IdentifierList list = new IdentifierList();
		for (TaskDefinitionDetails def: defs) {
			list.add(def.getName());
		}
		return list;
	}
	
	protected Long submitPrDef(PlanningRequestDefinitionDetails def) throws MALException, MALInteractionException {
		def.setId(0L);
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		LongList ids = cons.getStub().addDefinition(DefinitionType.PLANNING_REQUEST_DEF, defs);
		def.setId(ids.get(0));
		return def.getId();
	}
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	protected long generateId() {
		return lastId.incrementAndGet();
	}
	
	protected void setTaskInsts(PlanningRequestInstanceDetails pr, TaskInstanceDetails... tasks) {
		TaskInstanceDetailsList list = new TaskInstanceDetailsList();
		for (TaskInstanceDetails task: tasks) {
			task.setPrInstId(pr.getId());
			list.add(task);
		}
		pr.setTasks(list);
	}
	
	public PlanningRequestStatusDetails crf() throws MALException, MALInteractionException, ParseException {
		CommandRequestFile crf = new CommandRequestFile();
		
		TaskDefinitionDetails taskDef1 = crf.createPassTaskDef();
		Long taskDefId1 = submitTaskDef(taskDef1);
		
		TaskDefinitionDetails taskDef2 = crf.createExecTaskDef();
		Long taskDefId2 = submitTaskDef(taskDef2);
		
		TaskDefinitionDetails taskDef3 = crf.createMaesTaskDef();
		Long taskDefId3 = submitTaskDef(taskDef3);
		
		TaskDefinitionDetails taskDef4 = crf.createSeqTaskDef();
		Long taskDefId4 = submitTaskDef(taskDef4);
		
		PlanningRequestDefinitionDetails prDef = crf.createPrDef();
		prDef.setTaskDefNames(taskDefNames(taskDef1, taskDef2, taskDef3, taskDef4));
		Long prDefId = submitPrDef(prDef);
		
		TaskInstanceDetails taskInst1 = crf.createPassTaskInst(generateId(), taskDefId1, null);
		
		TaskInstanceDetails taskInst2 = crf.createExecTaskInst(generateId(), taskDefId2, null);
		
		TaskInstanceDetails taskInst3 = crf.createMaesTaskInst(generateId(), taskDefId3, null);
		
		TaskInstanceDetails taskInst4 = crf.createSeqTaskInst(generateId(), taskDefId4, null);
		
		PlanningRequestInstanceDetails prInst = crf.createPrInst(generateId(), prDefId);
		setTaskInsts(prInst, taskInst1, taskInst2, taskInst3, taskInst4);
		
		return cons.getStub().submitPlanningRequest(prInst);
	}
	
	public PlanningRequestStatusDetails crrf() throws MALException, MALInteractionException, ParseException {
		CommandRequestFile crf = new CommandRequestFile();
		CommandRequestResponseFile crrf = new CommandRequestResponseFile();
		
		return crf();
	}
}
