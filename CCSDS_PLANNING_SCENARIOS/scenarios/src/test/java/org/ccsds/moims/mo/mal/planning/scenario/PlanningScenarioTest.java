package org.ccsds.moims.mo.mal.planning.scenario;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumer;
import org.ccsds.moims.mo.mal.planning.provider.PlanningRequestServiceProvider;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinition;
import org.ccsds.moims.mo.planningcom.structures.ArgumentDefinition;
import org.ccsds.moims.mo.planningcom.structures.ArgumentDefinitionList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/testPlanningScenariosContext.xml")
public class PlanningScenarioTest {
	
	public static final Logger LOGGER = Logger
			.getLogger(PlanningScenarioTest.class.getName());
	
	private Long prDefId;
	
	@Autowired
	private PlanningRequestServiceProvider planningRequestServiceProvider;
	
	@Autowired
	private PlanningRequestServiceConsumer planningRequestConsumer;
	
	private long getPrDefId() throws MALInteractionException, MALException {
		if (prDefId == null) {
			PlanningRequestDefinition prDef = new PlanningRequestDefinition();
			prDef.setName("test5");
			prDef.setDescription("description");
			prDefId = planningRequestConsumer.getPlanningRequestService()
					.addDefinition(prDef);
		}
		return prDefId;
	}
	
	@Test
	public void addPrDefinition() throws MALInteractionException,
			MALException {
		LOGGER.info(" *** addPrDefinition");
		PlanningRequestDefinition prDef = new PlanningRequestDefinition();
		prDef.setName("List of RQ parameters");
		prDef.setDescription("List of RQ parameters");
		ArgumentDefinitionList list0 = new ArgumentDefinitionList();
		ArgumentDefinition l0 = new ArgumentDefinition();
		l0.setName("List_of_RQ_Parameters");
		l0.setType(new Byte((byte) 15));
		ArgumentDefinitionList list1 = new ArgumentDefinitionList();
		ArgumentDefinition l1 = new ArgumentDefinition();
		l1.setName("RQ Parameter");
		l1.setType(new Byte((byte) 15));
		ArgumentDefinitionList list2 = new ArgumentDefinitionList();
		String[] l2Names = {"RQ_Parameter_Name", "RQ_Parameter_Description", "RQ_Parameter_Representation",
				"radix", "RQ_Parameter_Unit", "Value"};
		for (String name : l2Names) {
			ArgumentDefinition l2_1 = new ArgumentDefinition();
			l2_1.setName(name);
			l2_1.setType(new Byte((byte) 15));
			list2.add(l2_1);
		}
		l1.setChildArguments(list2);
		list1.add(l1);
		l0.setChildArguments(list1);
		list0.add(l0);
		prDef.setArguments(list0);
		prDefId = planningRequestConsumer.getPlanningRequestService().addDefinition(prDef);
		LOGGER.info(" *** addPrDefinition, defId=" + prDefId);
		prDef = planningRequestConsumer.getPlanningRequestService().getDefinition(prDefId);
		assertTrue(prDefId != null);
	}
	
	@Test
	public void addTaskDefinition() throws MALInteractionException,
			MALException {
		LOGGER.info(" *** addTaskDefinition");
		TaskDefinition taskDefinition = new TaskDefinition();
		taskDefinition.setPlanningRequestDefinitionId(getPrDefId());
		taskDefinition.setName("RQ_Destination");
		ArgumentDefinitionList list = new ArgumentDefinitionList();
		String[] names = {"EVRQ_Time", "EVRQ_Time", "EVRQ_Description", "RQ_Source", "RQ_Destination", "RQ_Type"};
		for (String name : names) {
			ArgumentDefinition a1 = new ArgumentDefinition();
			a1.setName(name);
			a1.setType(new Byte((byte)15));
			list.add(a1);
		}
		ArgumentDefinition a2 = new ArgumentDefinition();
		a2.setName("List_of_RQ_Parameters");
		a2.setType(new Byte((byte)15));
		String[] names2 = {"RQ_Parameter_Name", "RQ_Parameter_Value", "RQ_Parameter_Description", "RQ_Parameter_Representation",
				"RQ_Parameter_Radix", "RQ_Parameter_Unit"};
		ArgumentDefinitionList list2 = new ArgumentDefinitionList();
		for (String name : names2) {
			ArgumentDefinition a1 = new ArgumentDefinition();
			a1.setName(name);
			a1.setType(new Byte((byte)15));
			list2.add(a1);
		}
		a2.setChildArguments(list2);
		list.add(a2);
		taskDefinition.setArguments(list);
		Long taskDefId = planningRequestConsumer.getPlanningRequestService().addTaskDefinition(taskDefinition);
		
	}

}
