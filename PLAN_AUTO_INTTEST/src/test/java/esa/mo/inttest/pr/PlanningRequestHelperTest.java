package esa.mo.inttest.pr;

import static org.junit.Assert.*;

import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.structures.*;
import org.junit.Test;

public class PlanningRequestHelperTest {

	@Test
	public void testShortFormUniqueness() throws MALException {
		MALService tmp = PlanningHelper.PLANNING_AREA.getServiceByName(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
		if (tmp == null) { // re-init error workaround
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
		
		MALElementFactory f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(DefinitionType.SHORT_FORM);
		Object o = f.createElement();
		assertTrue(o instanceof DefinitionType);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(DefinitionTypeList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof DefinitionTypeList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestDefinitionDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestDefinitionDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestDefinitionDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestDefinitionDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(TaskDefinitionDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof TaskDefinitionDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(TaskDefinitionDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof TaskDefinitionDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestInstanceDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestInstanceDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestInstanceDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestInstanceDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(TaskInstanceDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof TaskInstanceDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(TaskInstanceDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof TaskInstanceDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestStatusDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestStatusDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestStatusDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestStatusDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(TaskStatusDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof TaskStatusDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(TaskStatusDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof TaskStatusDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestResponseDefinitionDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestResponseDefinitionDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestResponseDefinitionDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestResponseDefinitionDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestResponseInstanceDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestResponseInstanceDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PlanningRequestResponseInstanceDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PlanningRequestResponseInstanceDetailsList);
	}

}
