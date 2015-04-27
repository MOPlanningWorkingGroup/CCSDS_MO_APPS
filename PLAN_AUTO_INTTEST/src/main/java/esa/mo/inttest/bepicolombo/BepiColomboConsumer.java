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
		TaskDefinitionDetailsList defs = new TaskDefinitionDetailsList();
		defs.add(def);
		LongList ids = cons.getStub().addDefinition(DefinitionType.TASK_DEF, defs);
		return ids.get(0);
	}
	
	protected IdentifierList taskDefNames(TaskDefinitionDetails... defs) {
		IdentifierList list = new IdentifierList();
		for (TaskDefinitionDetails def: defs) {
			list.add(def.getName());
		}
		return list;
	}
	
	protected Long submitPrDef(PlanningRequestDefinitionDetails def) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList defs = new PlanningRequestDefinitionDetailsList();
		defs.add(def);
		LongList ids = cons.getStub().addDefinition(DefinitionType.PLANNING_REQUEST_DEF, defs);
		return ids.get(0);
	}
	
	protected void submitPrInst(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst, LongList taskDefIds,
			LongList taskInstIds) throws MALException, MALInteractionException {
		cons.getStub().submitPlanningRequest(prDefId, prInstId, prInst, taskDefIds, taskInstIds);
	}
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	protected long generateId() {
		return lastId.incrementAndGet();
	}
	
	protected TaskInstanceDetailsList taskInsts(TaskInstanceDetails... tasks) {
		TaskInstanceDetailsList list = new TaskInstanceDetailsList();
		for (TaskInstanceDetails task: tasks) {
			list.add(task);
		}
		return list;
	}
	
	protected LongList ids(Long... ids) {
		LongList list = new LongList();
		for (Long id: ids) {
			list.add(id);
		}
		return list;
	}
	
	public void crf() throws MALException, MALInteractionException, ParseException {
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
		
		TaskInstanceDetails taskInst1 = crf.createPassTaskInst();
		Long taskInstId1 = generateId();
		
		TaskInstanceDetails taskInst2 = crf.createExecTaskInst();
		Long taskInstId2 = generateId();
		
		TaskInstanceDetails taskInst3 = crf.createMaesTaskInst();
		Long taskInstId3 = generateId();
		
		TaskInstanceDetails taskInst4 = crf.createSeqTaskInst();
		Long taskInstId4 = generateId();
		
		PlanningRequestInstanceDetails prInst = crf.createPrInst();
		prInst.setTasks(taskInsts(taskInst1, taskInst2, taskInst3, taskInst4));
		
		Long prInstId = generateId();
		LongList taskDefIds = ids(taskDefId1, taskDefId2, taskDefId3, taskDefId4);
		LongList taskInstIds = ids(taskInstId1, taskInstId2, taskInstId3, taskInstId4);
		
		submitPrInst(prDefId, prInstId, prInst, taskDefIds, taskInstIds);
	}
}
