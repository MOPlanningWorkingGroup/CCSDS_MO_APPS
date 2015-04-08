package esa.mo.inttest.pr;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planningdatatypes.PlanningDataTypesHelper;
import org.ccsds.moims.mo.planningprototype.PlanningPrototypeHelper;
import org.ccsds.moims.mo.planningprototype.planningrequesttest.PlanningRequestTestHelper;

import esa.mo.inttest.FactoryBase;

/**
 * Common factory for PR. Initializes same Helpers on consumer and provider side.
 */
public abstract class PlanningRequestFactory extends FactoryBase {

	protected void initHelpers() throws MALException {
		MALService tmp = PlanningHelper.PLANNING_AREA.getServiceByName(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
		if (tmp == null) { // re-init error workaround
			MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			COMHelper.init(MALContextFactory.getElementFactoryRegistry()); // required for publishing
			PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningDataTypesHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningPrototypeHelper.init(MALContextFactory.getElementFactoryRegistry()); // testing support
			PlanningRequestTestHelper.init(MALContextFactory.getElementFactoryRegistry()); // testing support
		} // else already initialized
	}
}
