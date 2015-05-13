package esa.mo.inttest.sch;

import static org.junit.Assert.*;

import org.ccsds.moims.mo.automation.AutomationHelper;
import org.ccsds.moims.mo.automation.schedule.ScheduleHelper;
import org.ccsds.moims.mo.automation.schedule.structures.*;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALService;
import org.junit.Test;

public class ScheduleHelperTest {

	@Test
	public void testShortFormUniqueness() throws MALException {
		MALService tmp = AutomationHelper.AUTOMATION_AREA.getServiceByName(ScheduleHelper.SCHEDULE_SERVICE_NAME);
		if (tmp == null) { // re-init error workaround
			ScheduleHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
		
		MALElementFactory f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleDefinitionDetails.SHORT_FORM);
		Object o = f.createElement();
		assertTrue(o instanceof ScheduleDefinitionDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleDefinitionDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleDefinitionDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleInstanceDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleInstanceDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleInstanceDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleInstanceDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleItemInstanceDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleItemInstanceDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleItemInstanceDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleItemInstanceDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleStatusDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleStatusDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleStatusDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleStatusDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleItemStatusDetails.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleItemStatusDetails);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleItemStatusDetailsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleItemStatusDetailsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(SchedulePatchOperations.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof SchedulePatchOperations);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(SchedulePatchOperationsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof SchedulePatchOperationsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleItemPatchOperations.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleItemPatchOperations);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(ScheduleItemPatchOperationsList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof ScheduleItemPatchOperationsList);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PatchOperation.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PatchOperation);
		
		f = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(PatchOperationList.SHORT_FORM);
		o = f.createElement();
		assertTrue(o instanceof PatchOperationList);
	}
}