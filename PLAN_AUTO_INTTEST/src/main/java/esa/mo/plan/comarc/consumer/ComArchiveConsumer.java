package esa.mo.plan.comarc.consumer;

import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

public class ComArchiveConsumer {

	private ArchiveStub stub;

	public ComArchiveConsumer(ArchiveStub stub) {
		this.stub = stub;
	}

	public void submitPlanWithOneTask() throws MALException, MALInteractionException {
		
//		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
//		taskDef.setName(new Identifier("id"));
//		
//		TaskDefinitionDetailsList taskDefList = new TaskDefinitionDetailsList();
//		taskDefList.add(taskDef);
//		
//		LongList defIdList = stub.addTaskDefinition(taskDefList);
//		
//		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
//		
//		stub.submitPlanningRequest(defIdList.get(0), null, prInst);
		ObjectType objType = new ObjectType(new UShort(0), new UShort(0), new UOctet((short)0), new UShort(0));
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("test"));
		LongList objInstIds = new LongList();
		objInstIds.add(new Long(1L));
		ArchiveAdapter adapter = new ArchiveAdapter() {
		};
		stub.retrieve(objType, domain, objInstIds, adapter);
	}
}
