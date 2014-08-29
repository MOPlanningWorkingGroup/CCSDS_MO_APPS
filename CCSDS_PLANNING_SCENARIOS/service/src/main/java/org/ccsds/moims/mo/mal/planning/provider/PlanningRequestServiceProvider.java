package org.ccsds.moims.mo.mal.planning.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorTasksPublisher;
import org.ccsds.moims.mo.mal.planning.service.PlanningRequestServiceImpl;

/**
 * PlanningRequest service provider.
 * @author krikse
 *
 */
public class PlanningRequestServiceProvider {
	
	public static final Logger LOGGER = Logger.getLogger(PlanningRequestServiceProvider.class.getName());

	private MALContextFactory malFactory;
	private MALContext mal;
	private MALProviderManager providerMgr;
	private MALProvider serviceProvider;
	private PlanningRequestServiceImpl planningRequestService;
	private String propertyFile;
	private MonitorPlanningRequestsPublisher publisher;
	private MonitorTasksPublisher taskPublisher;
	
	public PlanningRequestInheritanceSkeleton getTestService() {
		return planningRequestService;
	}

	public PlanningRequestServiceProvider(PlanningRequestServiceImpl testService) {
		this.planningRequestService = testService;
	}

	public void start() {
		try {
			initProperties();
			startProvider();
		} catch (Exception ex) {
			LOGGER.severe(ex.getMessage());
		}
	}

	public String getPropertyFile() {
		return propertyFile;
	}

	public void setPropertyFile(String propertyFile) {
		this.propertyFile = propertyFile;
	}
	
	private void initProperties() throws IOException {
		final java.util.Properties sysProps = System.getProperties();
		Properties prop = new Properties();
		InputStream in1 = getClass().getResourceAsStream(propertyFile);
		prop.load(in1);
		in1.close();
		sysProps.putAll(prop);
		System.setProperties(sysProps);
	}

	private void startProvider() throws MALException, IllegalArgumentException,
			MALInteractionException {
		malFactory = MALContextFactory.newFactory();
		mal = malFactory.createMALContext(System.getProperties());
		providerMgr = mal.createProviderManager();
		ProviderInitCenter.startPlanningRequestRegistry();
		
		final IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("esa"));
		domain.add(new Identifier("mission"));
		publisher = planningRequestService.createMonitorPlanningRequestsPublisher(domain, new Identifier("GROUND"),
				SessionType.LIVE, new Identifier("LIVE"), QoSLevel.BESTEFFORT,
				null, new UInteger(0));
		taskPublisher = planningRequestService.createMonitorTasksPublisher(domain, new Identifier("GROUND"),
				SessionType.LIVE, new Identifier("LIVE"), QoSLevel.BESTEFFORT,
				null, new UInteger(0));
		// start transport
	    URI sharedBrokerURI = null;
	    if ((null != System.getProperty("demo.provider.useSharedBroker"))
	            && (null != System.getProperty("shared.broker.uri")))
	    {
	      sharedBrokerURI = new URI(System.getProperty("shared.broker.uri"));
	    }
		serviceProvider = providerMgr.createProvider("RequestPlanning", null,
				PlanningRequestHelper.PLANNINGREQUEST_SERVICE, new Blob("".getBytes()),
				planningRequestService, new QoSLevel[] { QoSLevel.ASSURED }, new UInteger(1),
				System.getProperties(), true, sharedBrokerURI);
		
		LOGGER.info("Request Planning Provider started!");
		final EntityKeyList lst = new EntityKeyList();
		lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		publisher.register(lst, new PlanningRequestListener());
	}
	
	public String getBrokerUri() {
		return serviceProvider.getBrokerURI().getValue();
	}
	
	public String getUri() {
		return serviceProvider.getURI().getValue();
	}

	public void stop() throws MALException, MALInteractionException {
		publisher.deregister();
		if (null != serviceProvider) {
			serviceProvider.close();
		}
		if (null != providerMgr) {
			providerMgr.close();
		}
		if (null != mal) {
			mal.close();
		}
		LOGGER.info("Request Planning Provider closed!");
	}

}