package esa.mo.inttest.pr.consumer;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;

/**
 * Planning request consumer for testing.
 */
public class PlanningRequestConsumer {

	private PlanningRequestStub stub;

	public PlanningRequestConsumer(PlanningRequestStub stub) {
		this.stub = stub;
	}

	public void submitPlanWithOneTask() throws MALException, MALInteractionException {
		
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier("id"));
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		
		LongList taskDefIds = stub.addDefinition(DefinitionType.TASK_DEF, taskDefs);
		
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier("id"));
		
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
