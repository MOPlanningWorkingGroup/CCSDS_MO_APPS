package esa.mo.inttest.pr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.goce.GOCEHelper;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planningdatatypes.PlanningDataTypesHelper;
import org.ccsds.moims.mo.planningprototype.PlanningPrototypeHelper;
import org.ccsds.moims.mo.planningprototype.planningrequesttest.PlanningRequestTestHelper;

/**
 * Common factory for PR. Initializes same Helpers on consumer and provider side.
 */
public abstract class PlanningRequestFactory {

	protected String propertyFile = null;
	protected MALContext malCtx = null;

	public PlanningRequestFactory() {
	}

	public void setPropertyFile(String fn) {
		propertyFile = fn;
	}

	protected void initProperties() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
		Properties props = new Properties();
		props.load(is);
		is.close();
		System.getProperties().putAll(props);
	}

	protected void initContext() throws MALException {
		if (null == malCtx) {
			malCtx = MALContextFactory.newFactory().createMALContext(System.getProperties());
		}
	}

	protected void initHelpers() throws MALException {
		MALService tmp = PlanningHelper.PLANNING_AREA.getServiceByName(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
		if (tmp == null) { // re-init error workaround
			MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			COMHelper.init(MALContextFactory.getElementFactoryRegistry()); // required for publishing
			PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningDataTypesHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
			GOCEHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningPrototypeHelper.init(MALContextFactory.getElementFactoryRegistry()); // testing support
			PlanningRequestTestHelper.init(MALContextFactory.getElementFactoryRegistry()); // testing support
		} // else already initialized
	}

	protected void init() throws IOException, MALException {
		initProperties();
		initContext();
		initHelpers();
	}

	protected void close() throws MALException {
		if (malCtx != null) {
			malCtx.close();
		}
		malCtx = null;
	}
}