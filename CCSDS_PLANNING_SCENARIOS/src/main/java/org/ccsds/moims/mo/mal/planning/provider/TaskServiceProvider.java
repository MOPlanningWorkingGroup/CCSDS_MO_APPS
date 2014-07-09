package org.ccsds.moims.mo.mal.planning.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.service.TaskServiceImpl;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.task.TaskHelper;
import org.ccsds.moims.mo.planning.task.provider.TaskInheritanceSkeleton;

public class TaskServiceProvider {
	
	public static final Logger LOGGER = Logger.getLogger(TaskServiceProvider.class.getName());
	
	private MALContextFactory malFactory;
	private MALContext mal;
	private MALProviderManager providerMgr;
	private MALProvider serviceProvider;
	private TaskServiceImpl taskService;
	private String propertyFile;
	
	public TaskInheritanceSkeleton getTestService() {
		return taskService;
	}

	public TaskServiceProvider(TaskServiceImpl testService) {
		this.taskService = testService;
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
		ProviderInitCenter.startTaskRegistry();
		ProviderInitCenter.startTaskRegistry();
		
		final IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("esa"));
		domain.add(new Identifier("mission"));
		// start transport
	    URI sharedBrokerURI = null;
	    if ((null != System.getProperty("demo.provider.useSharedBroker"))
	            && (null != System.getProperty("shared.broker.uri")))
	    {
	      sharedBrokerURI = new URI(System.getProperty("shared.broker.uri"));
	    }
		serviceProvider = providerMgr.createProvider("Task", null,
				TaskHelper.TASK_SERVICE, new Blob("".getBytes()),
				taskService, new QoSLevel[] { QoSLevel.ASSURED }, new UInteger(1),
				System.getProperties(), true, sharedBrokerURI);
		
		LOGGER.info("Task Provider started!");
		final EntityKeyList lst = new EntityKeyList();
		lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
	}
	
	public String getBrokerUri() {
		if (serviceProvider != null && serviceProvider.getBrokerURI() != null)
			return serviceProvider.getBrokerURI().getValue();
		else
			return null;
	}
	
	public String getUri() {
		if (serviceProvider != null && serviceProvider.getURI() != null)
			return serviceProvider.getURI().getValue();
		else
			return null;
	}

	public void stop() throws MALException, MALInteractionException {
		//publisher.deregister();
		if (null != serviceProvider) {
			serviceProvider.close();
		}
		if (null != providerMgr) {
			providerMgr.close();
		}
		if (null != mal) {
			mal.close();
		}
		LOGGER.info("Task Provider closed!");
	}

}
