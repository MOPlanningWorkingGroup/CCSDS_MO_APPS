package esa.mo.inttest.pr.consumer;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;

import esa.mo.inttest.Dumper;

/**
 * Planning request consumer for testing.
 */
public class PlanningRequestConsumer extends PlanningRequestAdapter {

	private static final Logger LOG = Logger.getLogger(PlanningRequestConsumer.class.getName());
	
	private PlanningRequestStub stub;
	private String broker = "PrProvider";
	
	/**
	 * Ctor.
	 * @param stub
	 */
	public PlanningRequestConsumer(PlanningRequestStub stub) {
		this.stub = stub;
	}
	
	/**
	 * Returns used stub.
	 * @return
	 */
	public PlanningRequestStub getStub() {
		 return this.stub;
	}
	
	public void setBrokerName(String name) {
		broker = name;
	}
	
	/**
	 * Implements PR status notification callback.
	 * @see org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter#monitorPlanningRequestsNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.structures.Identifier, org.ccsds.moims.mo.mal.structures.UpdateHeaderList, org.ccsds.moims.mo.com.structures.ObjectIdList, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
			ObjectIdList objIds, PlanningRequestStatusDetailsList prStats, Map qosProps) {
		LOG.log(Level.INFO, "{4}.monitorPlanningRequestNotifyReceived(id={0}, List:updHeaders, List:objIds, List:schStatuses)\n  updHeaders[]={1}\n  objIds[]={2}\n  schStatuses[]={3}",
				new Object[] { id, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats),
				Dumper.fromBroker(broker, msgHdr) });
	}
	
	/**
	 * Implements Task status notification callback.
	 * @see org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter#monitorTasksNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.structures.Identifier, org.ccsds.moims.mo.mal.structures.UpdateHeaderList, org.ccsds.moims.mo.com.structures.ObjectIdList, org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void monitorTasksNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
			ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
		LOG.log(Level.INFO, "{4}.monitorTasksNotifyReceived(id={0}, List:updHeaders, List:objIds, List:taskStatuses)\n  updHeaders[]={1}\n  objIds[]={2}\n  taskStatuses[]={3}",
				new Object[] { id, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.taskStats(taskStats),
				Dumper.fromBroker(broker, msgHdr) });
	}

	/**
	 * Creates task definition base.
	 * @param name
	 * @param desc
	 * @return taskDef
	 */
	public static TaskDefinitionDetails createTaskDef(String name, String desc) {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier(name)); // mandatory
		taskDef.setDescription(desc);
		return taskDef;
	}
	
	/**
	 * Creates pr definition base.
	 * @param name
	 * @param desc
	 * @return
	 */
	public static PlanningRequestDefinitionDetails createPrDef(String name, String desc) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier(name)); // mandatory
		prDef.setDescription(desc);
		return prDef;
	}
	
	/**
	 * Creates task instance base.
	 * @param name
	 * @param comm
	 * @param prName
	 * @return
	 */
	public static TaskInstanceDetails createTaskInst(Long id, Long defId, String comm) {
		TaskInstanceDetails inst = new TaskInstanceDetails();
		inst.setId(id);
		inst.setTaskDefId(defId);
		inst.setComment(comm);
		return inst;
	}
	
	/**
	 * Creates List of given elements.
	 * @param inst
	 * @return
	 */
	public static TaskInstanceDetailsList createTasksList(TaskInstanceDetails... insts) {
		TaskInstanceDetailsList list = new TaskInstanceDetailsList();
		for (TaskInstanceDetails i: insts) {
			list.add(i);
		}
		return list;
	}
	
	/**
	 * Creates pr instance base.
	 * @param name
	 * @param comm
	 * @return
	 */
	public static PlanningRequestInstanceDetails createPrInst(/*String name*/Long id, Long defId, String comm) {
		PlanningRequestInstanceDetails inst = new PlanningRequestInstanceDetails();
		inst.setId(id); // mandatory
		inst.setPrDefId(defId); // mandatory
		inst.setComment(comm);
		return inst;
	}
	
	public void submitPlanWithOneTask() throws MALException, MALInteractionException {
		
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("id", null);
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		
		LongList taskDefIds = stub.addDefinition(DefinitionType.TASK_DEF, taskDefs);
		
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("id", null);
		
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		
		LongList prDefIds = stub.addDefinition(DefinitionType.PLANNING_REQUEST_DEF, prDefs);
		
		TaskInstanceDetails taskInst = new TaskInstanceDetails();
		taskInst.setId(1L);
		taskInst.setTaskDefId(taskDefIds.get(0));
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setId(2L);
		prInst.setPrDefId(prDefIds.get(0));
		prInst.setTasks(taskInsts);
		taskInst.setPrInstId(prInst.getId());
		
		submitPr(prInst);
	}
	
	public PlanningRequestStatusDetails submitPr(PlanningRequestInstanceDetails inst) throws MALException, MALInteractionException {
		PlanningRequestInstanceDetailsList insts = new PlanningRequestInstanceDetailsList();
		insts.add(inst);
		PlanningRequestStatusDetailsList stats = getStub().submitPlanningRequest(insts);
		return (null != stats && !stats.isEmpty()) ? stats.get(0) : null;
	}
}
