package esa.mo.inttest.goce;

import java.text.ParseException;
import java.util.concurrent.atomic.AtomicLong;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleStub;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;

/**
 * Planning request consumer for testing. Implemented by GOCE example files -
 * PPF, PIF, SPF, OPF.
 */
public class GoceConsumer {

	private PlanningRequestStub prStub;
	private ScheduleStub schStub;
	private AtomicLong lastId = new AtomicLong(0L);

	/**
	 * Ctor.
	 * 
	 * @param prStub
	 */
	public GoceConsumer(PlanningRequestStub prStub, ScheduleStub schStub) {
		this.prStub = prStub;
		this.schStub = schStub;
	}

	/**
	 * Returns stub for provider access.
	 * 
	 * @return
	 */
	public PlanningRequestStub getPrStub() {
		return prStub;
	}

	/**
	 * Returns stub for provider access.
	 * 
	 * @return
	 */
	public ScheduleStub getSChStub() {
		return schStub;
	}

	protected long generateId() {
		return lastId.incrementAndGet();
	}

	protected LongList getTaskDefIds(Long taskDefId) {
		LongList list = new LongList();
		list.add(taskDefId);
		return list;
	}

	protected TaskInstanceDetailsList getTaskInsts(TaskInstanceDetails taskInst) {
		TaskInstanceDetailsList list = new TaskInstanceDetailsList();
		list.add(taskInst);
		return list;
	}

	protected Long submitTaskDef(TaskDefinitionDetails taskDef) throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefsList = new TaskDefinitionDetailsList();
		taskDefsList.add(taskDef);
		taskDef.setId(0L);
		LongList taskIdsList = prStub.addDefinition(DefinitionType.TASK_DEF, taskDefsList);
		Long id = null;
		if (null != taskIdsList && !taskIdsList.isEmpty()) {
			taskDef.setId(taskIdsList.get(0));
			id = taskDef.getId();
		}
		return id;
	}

	protected Long submitPrDef(PlanningRequestDefinitionDetails prDef) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefsList = new PlanningRequestDefinitionDetailsList();
		prDefsList.add(prDef);
		prDef.setId(0L);
		LongList defIdsList = prStub.addDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefsList);
		Long id = null;
		if (null != defIdsList && !defIdsList.isEmpty()) {
			prDef.setId(defIdsList.get(0));
			id = prDef.getId();
		}
		return id;
	}

	protected void updatePrDef(PlanningRequestDefinitionDetails prDef)
			throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		prStub.updateDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs);
	}

	/**
	 * Creates Task and PR structures from PPF example file. Submits them to PR
	 * provider.
	 * 
	 * @throws MALException
	 * @throws MALInteractionException
	 * @throws ParseException
	 */
	public void ppf() throws MALException, MALInteractionException, ParseException {
		
		PayloadPlanningFile ppf = new PayloadPlanningFile();

		TaskDefinitionDetails taskDef = ppf.createTaskDef(0);
		Long taskDefId = submitTaskDef(taskDef);

		PlanningRequestDefinitionDetails prDef = ppf.createPrDef(getTaskDefIds(taskDef.getId()));
		Long prDefId = submitPrDef(prDef);

		TaskInstanceDetails taskInst = ppf.createTaskInst(0, generateId(), taskDefId, null);

		PlanningRequestInstanceDetails prInst = ppf.createPrInst(0, generateId(), prDefId, getTaskInsts(taskInst));
		taskInst.setPrInstId(prInst.getId());

		PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
		insts.add(prInst);
		
		prStub.submitPlanningRequest(insts);

		TaskDefinitionDetails taskDef2 = ppf.createTaskDef(1);
		Long taskDefId2 = submitTaskDef(taskDef2);

		prDef.getTaskDefIds().add(taskDef2.getId());
		updatePrDef(prDef);

		TaskInstanceDetails taskInst2 = ppf.createTaskInst(1, generateId(), taskDefId2, null);
		
		PlanningRequestInstanceDetails prInst2 = ppf.createPrInst(1, generateId(), prDefId, getTaskInsts(taskInst2));
		taskInst2.setPrInstId(prInst2.getId());

		PlanningRequestInstanceDetailsList insts2 = new PlanningRequestInstanceDetailsList();
		insts2.add(prInst2);
		
		prStub.submitPlanningRequest(insts2);
	}

	/**
	 * Creates TaskDef structures from PPF example and submits them to PR
	 * provider.
	 * 
	 * @return
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public boolean createPpfTaskDefIfMissing(LongList taskDefIds) throws MALException,
			MALInteractionException {
		PayloadPlanningFile ppf = new PayloadPlanningFile();
		boolean exists = false;
		boolean created = false;
		IdentifierList taskNames = new IdentifierList();
		taskNames.add(new Identifier(ppf.getTaskDefName(0)));

		LongList taskIds = prStub.listDefinition(DefinitionType.TASK_DEF, taskNames);
		if (taskIds.isEmpty()) {
			TaskDefinitionDetails taskDef = ppf.createTaskDef(0);
			Long taskDefId = submitTaskDef(taskDef);
			if (null != taskDefId) {
				taskDefIds.add(taskDefId);
			}
			created = (taskDef != null) && (taskDefId != null);
		} else if (taskIds.size() > 1) {
			throw new MALException("more than one goce ppf task def with same name found");
		} else {
			exists = true;
			taskDefIds.add(taskIds.get(0));
		}
		return exists || created;
	}

	/**
	 * Creates PrDef structures from PPF file and submits them to PR provider.
	 * 
	 * @return
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public boolean createPpfPrDefIfMissing(LongList taskDefIds) throws MALException,
			MALInteractionException {
		PayloadPlanningFile ppf = new PayloadPlanningFile();
		boolean exists = false;
		boolean created = false;
		IdentifierList prNames = new IdentifierList();
		prNames.add(new Identifier(ppf.getPrDefName()));

		LongList prIds = prStub.listDefinition(DefinitionType.PLANNING_REQUEST_DEF, prNames);

		if (prIds.isEmpty()) {
			PlanningRequestDefinitionDetails prDef = ppf.createPrDef(taskDefIds);
			Long prDefId = submitPrDef(prDef);
			created = (prDef != null) || (prDefId != null);
		} else if (prIds.size() > 1) {
			throw new MALException("more than one goce ppf pr def with same name found");
		} else {
			exists = true;
		}
		return exists || created;
	}

	/**
	 * Creates TaskInst and PrInst structures and submits them to PR provider.
	 * 
	 * @return
	 * @throws MALException
	 * @throws MALInteractionException
	 * @throws ParseException
	 */
	public boolean createPpfInstsIfMissingAndDefsExist() throws MALException,
			MALInteractionException, ParseException {
		PayloadPlanningFile ppf = new PayloadPlanningFile();
		boolean task1Created = false;
		boolean task2Created = false;
		IdentifierList taskNames = new IdentifierList();
		taskNames.add(new Identifier(ppf.getTaskDefName(0)));

		LongList taskDefIds = prStub.listDefinition(DefinitionType.TASK_DEF, taskNames);

		TaskInstanceDetails taskInst1 = null;
		TaskInstanceDetails taskInst2 = null;
		if (taskDefIds.isEmpty()) {
			// no task def - nothing to do
		} else if (taskDefIds.size() > 1) {
			throw new MALException("more than one goce ppf task def with same name found");
		} else {
			taskInst1 = ppf.createTaskInst(0, generateId(), taskDefIds.get(0), null);
			task1Created = (taskInst1 != null);
			taskInst2 = ppf.createTaskInst(1, generateId(), taskDefIds.get(0), null);
			task2Created = (taskInst2 != null);
		}
		// task instances ok, proceed with pr instances
		boolean pr1Created = false;
		boolean pr2Created = false;
		IdentifierList prNames = new IdentifierList();
		prNames.add(new Identifier(ppf.getPrDefName()));

		LongList prDefIds = prStub.listDefinition(DefinitionType.PLANNING_REQUEST_DEF, prNames);

		if (prDefIds.isEmpty()) {
			// no pr def - nothing to do
		} else if (prDefIds.size() > 1) {
			throw new MALException("more than one goce ppf pr def with same name found");
		} else {
			Long prDefId = prDefIds.get(0);
			PlanningRequestInstanceDetails prInst = ppf.createPrInst(0, generateId(), prDefId, getTaskInsts(taskInst1));
			taskInst1.setPrInstId(prInst.getId());
			PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
			insts.add(prInst);
			prStub.submitPlanningRequest(insts);
			pr1Created = true;

			PlanningRequestInstanceDetails prInst2 = ppf.createPrInst(1, generateId(), prDefId, getTaskInsts(taskInst2));
			taskInst2.setPrInstId(prInst2.getId());
			PlanningRequestInstanceDetailsList insts2 = new PlanningRequestInstanceDetailsList();
			insts2.add(prInst2);
			prStub.submitPlanningRequest(insts2);
			pr2Created = true;
		}
		return task1Created && pr1Created && task2Created && pr2Created;
	}

	/**
	 * Creates structures from PIF example and submits them to PR provider.
	 * 
	 * @throws MALException
	 * @throws MALInteractionException
	 * @throws ParseException
	 */
	public void pif() throws MALException, MALInteractionException, ParseException {
		
		PlanIncrementFile pif = new PlanIncrementFile();

		TaskDefinitionDetails taskDef = pif.createTaskDef();
		Long taskDefId = submitTaskDef(taskDef);

		PlanningRequestDefinitionDetails prDef = pif.createPrDef(getTaskDefIds(taskDef.getId()));
		Long prDefId = submitPrDef(prDef);

		TaskInstanceDetails taskInst = pif.createTaskInst(generateId(), taskDefId, null);

		PlanningRequestInstanceDetails prInst = pif.createPrInst(generateId(), prDefId, getTaskInsts(taskInst));
		taskInst.setPrInstId(prInst.getId());

		PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
		insts.add(prInst);
		
		prStub.submitPlanningRequest(insts);
	}

	/**
	 * Creates structures from SPF example file and submits them to PR provider.
	 * 
	 * @throws MALException
	 * @throws MALInteractionException
	 * @throws ParseException
	 */
	public void spf() throws MALException, MALInteractionException, ParseException {
		
		SkeletonPlanningFile spf = new SkeletonPlanningFile();

		TaskDefinitionDetails taskDef = spf.createTaskDef();
		Long taskDefId = submitTaskDef(taskDef);

		PlanningRequestDefinitionDetails prDef = spf.createPrDef(getTaskDefIds(taskDef.getId()));
		Long prDefId = submitPrDef(prDef);

		for (int i = 0; i < spf.getPrInstCount(); ++i) {
			TaskInstanceDetails taskInst = spf.createTaskInst(i, generateId(), taskDefId, null);

			PlanningRequestInstanceDetails prInst = spf.createPrInst(i, generateId(), prDefId, getTaskInsts(taskInst));
			taskInst.setPrInstId(prInst.getId());

			PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
			insts.add(prInst);
			
			prStub.submitPlanningRequest(insts);
		}
	}

	/**
	 * Creates structures from OPF example and submits them to PR provider.
	 * 
	 * @throws MALException
	 * @throws MALInteractionException
	 * @throws ParseException
	 */
	public void opf() throws MALException, MALInteractionException, ParseException {
		
		OperationsPlanningFile opf = new OperationsPlanningFile();

		TaskDefinitionDetails taskDef = opf.createTaskDef();
		Long taskDefId = submitTaskDef(taskDef);

		PlanningRequestDefinitionDetails prDef = opf.createPrDef(getTaskDefIds(taskDef.getId()));
		Long prDefId = submitPrDef(prDef);

		TaskInstanceDetails taskInst = opf.createTaskInst(generateId(), taskDefId, null);

		PlanningRequestInstanceDetails prInst = opf.createPrInst(generateId(), prDefId, getTaskInsts(taskInst));
		taskInst.setPrInstId(prInst.getId());

		PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
		insts.add(prInst);
		
		prStub.submitPlanningRequest(insts);
	}

	protected Long submitSchDef(ScheduleDefinitionDetails schDef)
			throws MALException, MALInteractionException {
		ScheduleDefinitionDetailsList schDefs = new ScheduleDefinitionDetailsList();
		schDefs.add(schDef);
		schDef.setId(0L);
		LongList schDefIds = schStub.addDefinition(schDefs);
		Long id = null;
		if (null != schDefIds && !schDefs.isEmpty()) {
			schDef.setId(schDefIds.get(0));
			id = schDef.getId();
		}
		return id;
	}

	public void sist() throws MALException, MALInteractionException, ParseException {
		
		StationScheduleIncrementFile sist = new StationScheduleIncrementFile();

		ScheduleDefinitionDetails schDef = sist.createSchDef();
		Long schDefId = submitSchDef(schDef);

		ScheduleInstanceDetails schInst = sist.createSchInst(0, generateId(), schDefId);

		ScheduleInstanceDetailsList insts = new ScheduleInstanceDetailsList();
		insts.add(schInst);
		
		schStub.submitSchedule(insts);

		ScheduleInstanceDetails schInst2 = sist.createSchInst(1, generateId(), schDefId);

		insts.clear();
		insts.add(schInst2);
		
		schStub.submitSchedule(insts);

		// alternative single schedule with two items
		ScheduleInstanceDetails schInst3 = sist.createSchInst2(generateId(), schDefId, generateId(), generateId());

		insts.clear();
		insts.add(schInst3);
		
		schStub.submitSchedule(insts);
	}
}
