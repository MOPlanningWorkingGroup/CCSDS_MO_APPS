/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.pr;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planningdatatypes.PlanningDataTypesHelper;

import esa.mo.inttest.FactoryBase;

/**
 * Common factory for PR. Initializes same Helpers on consumer and provider side.
 */
public abstract class PlanningRequestFactory extends FactoryBase {

	protected void initHelpers() throws MALException {
		MALHelper.init(MALContextFactory.getElementFactoryRegistry());
		COMHelper.init(MALContextFactory.getElementFactoryRegistry()); // required for publishing
		PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
		PlanningDataTypesHelper.init(MALContextFactory.getElementFactoryRegistry());
		try {
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
		} catch (MALException e) {
			// ignore "service already registered"
		}
	}
}
