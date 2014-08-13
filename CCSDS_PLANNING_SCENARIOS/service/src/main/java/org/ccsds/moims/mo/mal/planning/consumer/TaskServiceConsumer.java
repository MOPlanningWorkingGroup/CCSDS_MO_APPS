package org.ccsds.moims.mo.mal.planning.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.task.TaskHelper;
import org.ccsds.moims.mo.planning.task.consumer.TaskStub;

public class TaskServiceConsumer {
	
	public static final Logger LOGGER = Logger
			.getLogger(TaskServiceConsumer.class.getName());
	
	private MALContextFactory malFactory;
	private MALContext mal;
	private MALConsumerManager consumerMgr;
	private MALConsumer tmConsumer = null;
	private TaskStub taskService;
	private String propertyFile;
	private final IdentifierList domain = new IdentifierList();
	private final Identifier network = new Identifier("GROUND");
	private final SessionType session = SessionType.LIVE;
	private final Identifier sessionName = new Identifier("LIVE");
	private Subscription subRequestWildcard;
	private String uri; // System.getProperty("uri");
	private String broker; // System.getProperty("broker");
	private String consumerName;
	
	public TaskServiceConsumer(String consumerName) {
		this.consumerName = consumerName;
	}
	
	public void start() throws IOException {
		try {
			init();
			initConsumer();
			startConsumerService();
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	private void init() throws IOException {
		final java.util.Properties sysProps = System.getProperties();
		Properties prop = new Properties();
		InputStream in1 = getClass().getResourceAsStream(propertyFile);
		prop.load(in1);
		in1.close();
		sysProps.putAll(prop);
		System.setProperties(sysProps);
	}

	private void initConsumer() throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier(consumerName);
	    // set up the wildcard subscription
	    {
	      final EntityKey entitykey = new EntityKey(new Identifier("*"), 0L, 0L, 0L);

	      final EntityKeyList entityKeys = new EntityKeyList();
	      entityKeys.add(entitykey);

	      final EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);

	      final EntityRequestList entities = new EntityRequestList();
	      entities.add(entity);

	      subRequestWildcard = new Subscription(subscriptionId, entities);
	    }

	}

	private void startConsumerService() throws MALException,
			MalformedURLException, MALInteractionException {
		domain.add(new Identifier("esa"));
		domain.add(new Identifier("mission"));
		malFactory = MALContextFactory.newFactory();
		mal = malFactory.createMALContext(System.getProperties());
		consumerMgr = mal.createConsumerManager();
		tmConsumer = consumerMgr.createConsumer((String) null, new URI(uri),
				new URI(broker),
				TaskHelper.TASK_SERVICE,
				new Blob("".getBytes()), domain, network, session, sessionName,
				QoSLevel.ASSURED, System.getProperties(), new UInteger(0));
		taskService = new TaskStub(tmConsumer);
	}

	public TaskStub getTaskService() {
		return taskService;
	}

	public void stop() throws MALException, MALInteractionException {
		if (taskService != null) {
			Identifier subscriptionId = new Identifier(consumerName);
			IdentifierList subLst = new IdentifierList();
			subLst.add(subscriptionId);
		}
		if (tmConsumer != null)
			tmConsumer.close();
		if (consumerMgr != null)
			consumerMgr.close();
	}

}
