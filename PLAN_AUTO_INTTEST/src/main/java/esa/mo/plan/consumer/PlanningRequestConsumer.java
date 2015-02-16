package esa.mo.plan.consumer;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;

public class PlanningRequestConsumer {

	private PlanningRequestStub stub;

	public PlanningRequestConsumer(PlanningRequestStub stub) {
		this.stub = stub;
	}

	public void submitPlanWithOneTask() throws MALException, MALInteractionException {
		
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier("id"));
		
		TaskDefinitionDetailsList taskDefList = new TaskDefinitionDetailsList();
		taskDefList.add(taskDef);
		
		LongList defIdList = stub.addTaskDefinition(taskDefList);
		
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		
		stub.submitPlanningRequest(defIdList.get(0), null, prInst);
	}
}
