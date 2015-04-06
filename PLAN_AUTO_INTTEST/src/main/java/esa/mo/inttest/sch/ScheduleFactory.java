package esa.mo.inttest.sch;

import org.ccsds.moims.mo.automation.AutomationHelper;
import org.ccsds.moims.mo.automation.schedule.ScheduleHelper;
import org.ccsds.moims.mo.automationprototype.AutomationPrototypeHelper;
import org.ccsds.moims.mo.automationprototype.scheduletest.ScheduleTestHelper;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;

import esa.mo.inttest.FactoryBase;

/**
 * Common factory for Schedule. Initializes same Helpers on consumer and provider side.
 */
public abstract class ScheduleFactory extends FactoryBase {

	protected void initHelpers() throws MALException {
		MALService tmp = AutomationHelper.AUTOMATION_AREA.getServiceByName(ScheduleHelper.SCHEDULE_SERVICE_NAME);
		if (tmp == null) { // re-init error workaround
			MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			COMHelper.init(MALContextFactory.getElementFactoryRegistry()); // required for publishing
			AutomationHelper.init(MALContextFactory.getElementFactoryRegistry());
			ScheduleHelper.init(MALContextFactory.getElementFactoryRegistry());
			AutomationPrototypeHelper.init(MALContextFactory.getElementFactoryRegistry()); // test support
			ScheduleTestHelper.init(MALContextFactory.getElementFactoryRegistry());
		} // else already initialized
	}
}
