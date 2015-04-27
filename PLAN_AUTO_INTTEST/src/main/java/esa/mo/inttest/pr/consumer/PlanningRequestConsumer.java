package esa.mo.inttest.pr.consumer;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
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
	
	/**
	 * Implements PR status notification callback.
	 * @see org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter#monitorPlanningRequestsNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.structures.Identifier, org.ccsds.moims.mo.mal.structures.UpdateHeaderList, org.ccsds.moims.mo.com.structures.ObjectIdList, org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
			ObjectIdList objIds, PlanningRequestStatusDetailsList prStats, Map qosProps) {
		LOG.log(Level.INFO, "{4}.monitorPlanningRequestNotifyReceived(id={0}, List:updHdrs, List:objIds, List:schStats)\n  updHdrs[]={1}\n  objIds[]={2}\n  schStats[]={3}",
				new Object[] { id, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.prStats(prStats), Dumper.fromBroker("PrProvider", msgHdr) });
	}
	
	/**
	 * Implements PR status notification error callback.
	 * @see org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter#monitorPlanningRequestsNotifyErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.MALStandardError, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError err, Map qosProps) {
		LOG.log(Level.INFO, "{1}.monitorPlanningRequestNotifyErrorReceived(error)\n  error={0}",
				new Object[] { err, Dumper.fromBroker("PrProvider", msgHdr) });
	}
	
	/**
	 * Implements Task status notification callback.
	 * @see org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter#monitorTasksNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.structures.Identifier, org.ccsds.moims.mo.mal.structures.UpdateHeaderList, org.ccsds.moims.mo.com.structures.ObjectIdList, org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void monitorTasksNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
			ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
		LOG.log(Level.INFO, "{4}.monitorTasksNotifyReceived(id={0}, List:updHdrs, List:objIds, List:taskStats)\n  updHdrs[]={1}\n  objIds[]={2}\n  taskStats[]={3}",
				new Object[] { id, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.taskStats(taskStats), Dumper.fromBroker("PrProvider", msgHdr) });
	}

	/**
	 * Implements Task status notification error callback.
	 * @see org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter#monitorTasksNotifyErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.MALStandardError, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError err, Map qosProps) {
		LOG.log(Level.INFO, "{1}.monitorTasksNotifyErrorReceived(error)\n  error={0}",
				new Object[] { err, Dumper.fromBroker("PrProvider", msgHdr) });
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
	public static TaskInstanceDetails createTaskInst(String name, String comm, String prName) {
		TaskInstanceDetails inst = new TaskInstanceDetails();
		inst.setName(new Identifier(name)); // mandatory
		inst.setComment(comm);
		inst.setPrName(new Identifier(prName)); // mandatory
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
	public static PlanningRequestInstanceDetails createPrInst(String name, String comm) {
		PlanningRequestInstanceDetails inst = new PlanningRequestInstanceDetails();
		inst.setName(new Identifier(name)); // mandatory
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
		
		TaskInstanceDetailsList taskInsts = new TaskInstanceDetailsList();
		taskInsts.add(taskInst);
		
		LongList taskInstIds = new LongList();
		taskInstIds.add(new Long(1L));
		
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setTasks(taskInsts);
		
		Long prInstId = new Long(2L);
		
		stub.submitPlanningRequest(prDefIds.get(0), prInstId, prInst, taskDefIds, taskInstIds);
	}
}
