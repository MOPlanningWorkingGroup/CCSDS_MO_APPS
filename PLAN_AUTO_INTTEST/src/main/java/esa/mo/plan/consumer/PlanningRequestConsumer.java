package esa.mo.plan.consumer;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
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
		
		TaskDefinitionDetailsList taskDefs = new TaskDefinitionDetailsList();
		taskDefs.add(taskDef);
		
		LongList taskDefIds = stub.addTaskDefinition(taskDefs);
		
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier("id"));
		
		PlanningRequestDefinitionDetailsList prDefs = new PlanningRequestDefinitionDetailsList();
		prDefs.add(prDef);
		
		LongList prDefIds = stub.addDefinition(prDefs);
		
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		
		stub.submitPlanningRequest(prDefIds.get(0), null, prInst);
	}
}
