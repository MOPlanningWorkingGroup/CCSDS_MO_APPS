package esa.mo.inttest.pr.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALService;
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
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorTasksPublisher;
import org.ccsds.moims.mo.planningdatatypes.PlanningDataTypesHelper;

/**
 * Planning request provider factory.
 */
public class PlanningRequestProviderFactory {

	private static final Logger LOG = Logger.getLogger(PlanningRequestProviderFactory.class.getName());
	
	private String propertyFile = null;
	private MALContext malCtx = null;
	private PlanningRequestProvider prov = null;
	private MonitorTasksPublisher taskPub = null;
	private MonitorPlanningRequestsPublisher prPub = null;
	private MALProviderManager malProvMgr = null;
	private MALProvider malProv = null;

	public void setPropertyFile(String fn) {
		propertyFile = fn;
		LOG.log(Level.CONFIG, "property file name set to {0}", propertyFile);
	}
	
	private void initProperties() throws IOException {
		LOG.entering(getClass().getName(), "initProperties");
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
		Properties props = new Properties();
		props.load(is);
		is.close();
		System.getProperties().putAll(props);
		LOG.log(Level.CONFIG, "property rmi transport: {0}", System.getProperty("org.ccsds.moims.mo.mal.transport.protocol.rmi"));
		LOG.log(Level.CONFIG, "property string encoder: {0}", System.getProperty("org.ccsds.moims.mo.mal.encoding.protocol.rmi"));
		LOG.log(Level.CONFIG, "property gen wrap: {0}", System.getProperty("org.ccsds.moims.mo.mal.transport.gen.wrap"));
		LOG.exiting(getClass().getName(), "initProperties");
	}
	
	private void initContext() throws MALException {
		LOG.entering(getClass().getName(), "initContext");
		malCtx = MALContextFactory.newFactory().createMALContext(System.getProperties());
		LOG.exiting(getClass().getName(), "initContext");
	}

	private void initHelpers() throws MALException {
		LOG.entering(getClass().getName(), "initHelpers");
		MALHelper.init(MALContextFactory.getElementFactoryRegistry());
		COMHelper.init(MALContextFactory.getElementFactoryRegistry()); // required for publishing
		PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
		PlanningDataTypesHelper.init(MALContextFactory.getElementFactoryRegistry());
		MALService tmp = PlanningHelper.PLANNING_AREA.getServiceByName(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
		if (tmp == null) { // re-init error workaround
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
		LOG.exiting(getClass().getName(), "initHelpers");
	}
	
	private void initProvider() throws MALException {
		LOG.entering(getClass().getName(), "initProvider");
		malProvMgr = malCtx.createProviderManager();
		prov = new PlanningRequestProvider();
		String provName = "testPrProv";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(1L);
		boolean isPublisher = true;
		URI brokerUri = null;
		
		malProv = malProvMgr.createProvider(provName, proto, PlanningRequestHelper.PLANNINGREQUEST_SERVICE,
				authId, prov, expQos, priority, System.getProperties(), isPublisher, brokerUri);
		LOG.exiting(getClass().getName(), "initProvider");
	}

	private void initTaskPublisher() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "initTaskPublicher");
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.BESTEFFORT;
		UInteger priority = new UInteger(0L);
		
		taskPub = prov.createMonitorTasksPublisher(domain, network, sessionType, sessionName, qos,
				System.getProperties(), priority);
		
		EntityKeyList keyList = new EntityKeyList();
		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		taskPub.register(keyList, prov);
		
		prov.setTaskPub(taskPub);
		LOG.exiting(getClass().getName(), "initTaskPublisher");
	}

	private void initPrPublisher() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "initPrPublisher");
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.BESTEFFORT;
		UInteger priority = new UInteger(0L);
		
		prPub = prov.createMonitorPlanningRequestsPublisher(domain, network, sessionType, sessionName, qos,
				System.getProperties(), priority);
		
		EntityKeyList keyList = new EntityKeyList();
		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		prPub.register(keyList, prov);
		
		prov.setPrPub(prPub);
		LOG.exiting(getClass().getName(), "initPrPublisher");
	}

	public void start() throws IOException, MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "start");
		initProperties();
		initContext();
		initHelpers();
		initProvider();
		initTaskPublisher();
		initPrPublisher();
		LOG.exiting(getClass().getName(), "start");
	}
	
	public URI getProviderUri() {
		URI uri = malProv.getURI();
		LOG.log(Level.CONFIG, "provider uri: {0}", uri.getValue());
		return uri;
	}
	
	public URI getBrokerUri() {
		URI uri = malProv.getBrokerURI();
		LOG.log(Level.CONFIG, "broker uri: {0}", uri.getValue());
		return uri;
	}
	
	public void stop() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "stop");
		if (taskPub != null) {
			try {
				taskPub.deregister();
			} catch (MALInteractionException e) { // ignore
				LOG.log(Level.WARNING, "task pub de-reg: {0}", e.getStandardError());
			}
			taskPub.close();
		}
		taskPub = null;
		if (prPub != null) {
			try {
				prPub.deregister();
			} catch (MALInteractionException e) { // ignore
				LOG.log(Level.WARNING, "pr pub de-reg: {0}", e.getStandardError());
			}
			prPub.close();
		}
		prPub = null;
		if (malProv != null) {
			malProv.close();
		}
		malProv = null;
		if (malProvMgr != null) {
			malProvMgr.close();
		}
		malProvMgr = null;
		prov = null;
		if (malCtx != null) {
			malCtx.close();
		}
		malCtx = null;
		LOG.exiting(getClass().getName(), "stop");
	}
}
