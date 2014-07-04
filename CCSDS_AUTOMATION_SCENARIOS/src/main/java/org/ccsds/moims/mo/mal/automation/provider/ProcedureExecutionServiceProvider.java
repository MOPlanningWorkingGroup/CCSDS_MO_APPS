package org.ccsds.moims.mo.mal.automation.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.proceduredefinitionservice.ProcedureDefinitionServiceHelper;
import org.ccsds.moims.mo.automation.procedureexecutionservice.ProcedureExecutionServiceHelper;
import org.ccsds.moims.mo.automation.procedureexecutionservice.provider.MonitorPublisher;
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
import org.ccsds.moims.mo.mal.automation.service.ProcedureDefinitionServiceImpl;
import org.ccsds.moims.mo.mal.automation.service.ProcedureExecutionServiceImpl;

/**
 * ProcedureExecution service provider.
 * @author krikse
 *
 */
public class ProcedureExecutionServiceProvider {
	
public static final Logger LOGGER = Logger.getLogger(ProcedureDefinitionServiceProvider.class.getName());
	
	private MALContextFactory malFactory;
	private MALContext mal;
	private MALProviderManager providerMgr;
	private MALProvider serviceProvider;
	private ProcedureExecutionServiceImpl procedureExecutionService;
	private ProcedureDefinitionServiceImpl procedureDefinitionService;
	private MALProvider procedureDefinitionServiceProvider;
	private String propertyFile;
	private MonitorPublisher monitorPublisher;
	
	public ProcedureExecutionServiceProvider (
			ProcedureExecutionServiceImpl procedureExecutionService, ProcedureDefinitionServiceImpl procedureDefinitionService) {
		this.procedureExecutionService = procedureExecutionService;
		this.procedureDefinitionService = procedureDefinitionService;
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

		ProviderInitCenter.startProcedureDefinition();
		ProviderInitCenter.startProcedureExecution();
		
		final IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("esa"));
		domain.add(new Identifier("mission"));
		monitorPublisher = procedureExecutionService.createMonitorPublisher(domain, new Identifier("GROUND"),
				SessionType.LIVE, new Identifier("LIVE"), QoSLevel.BESTEFFORT,
				null, new UInteger(0));
		Properties props = System.getProperties();
		serviceProvider = providerMgr.createProvider("ProcedureExecution", null,
				ProcedureExecutionServiceHelper.PROCEDUREEXECUTIONSERVICE_SERVICE, new Blob("".getBytes()),
				procedureExecutionService, new QoSLevel[] { QoSLevel.ASSURED }, new UInteger(1),
				props, true, null);
		procedureDefinitionServiceProvider = providerMgr.createProvider("ProcedureDefinition", null,
				ProcedureDefinitionServiceHelper.PROCEDUREDEFINITIONSERVICE_SERVICE, new Blob("".getBytes()),
				procedureDefinitionService, new QoSLevel[] { QoSLevel.ASSURED }, new UInteger(1),
				props, true, null);
		LOGGER.info("Procedure Execution Service Provider started!");
		final EntityKeyList lst = new EntityKeyList();
		lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		monitorPublisher.register(lst, new ProcedureExecutionServiceListener());
	}
	
	public String getBrokerUri() {
		return serviceProvider.getBrokerURI().getValue();
	}
	
	public String getUri() {
		return serviceProvider.getURI().getValue();
	}

	public void stop() throws MALException, MALInteractionException {
		monitorPublisher.deregister();
		if (null != serviceProvider) {
			serviceProvider.close();
		}
		if (null != procedureDefinitionServiceProvider) {
			procedureDefinitionServiceProvider.close();
		}
		if (null != providerMgr) {
			providerMgr.close();
		}
		if (null != mal) {
			mal.close();
		}
		LOGGER.info("Procedure Execution Service Provider closed!");
	}

}
