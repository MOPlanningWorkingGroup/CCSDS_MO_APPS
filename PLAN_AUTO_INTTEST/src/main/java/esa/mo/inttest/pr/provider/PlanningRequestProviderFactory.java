package esa.mo.inttest.pr.provider;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorTasksPublisher;
import org.ccsds.moims.mo.planningprototype.planningrequesttest.PlanningRequestTestHelper;

import esa.mo.inttest.pr.PlanningRequestFactory;

/**
 * Planning request provider factory. Produces single provider with Task and PR publishing.
 */
public class PlanningRequestProviderFactory extends PlanningRequestFactory {

	private static final Logger LOG = Logger.getLogger(PlanningRequestProviderFactory.class.getName());
	
	private PlanningRequestProvider prov = null;
	private URI brokerUri = null;
	private MonitorTasksPublisher taskPub = null;
	private MonitorPlanningRequestsPublisher prPub = null;
	private MALProviderManager malProvMgr = null;
	private MALProvider malProv = null;
	private PlanningRequestTestSupportProvider testProv = null;
	private MALProvider testMalProv = null;
	
	/**
	 * Set broker to use. If null, provider will create one itself.
	 * @param broker
	 */
	public void setBrokerUri(URI broker) {
		brokerUri = broker;
	}
	
	private void initProvider(String name) throws MALException {
		LOG.entering(getClass().getName(), "initProvider");
		
		prov = new PlanningRequestProvider();
		prov.setDomain(domain);
		
		String provName = (null != name && !name.isEmpty()) ? name : "PrProv";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(1L);
		boolean isPublisher = true;
		
		malProv = malProvMgr.createProvider(provName, proto, PlanningRequestHelper.PLANNINGREQUEST_SERVICE,
				authId, prov, expQos, priority, System.getProperties(), isPublisher, brokerUri);
		
		// testing support
		testProv = new PlanningRequestTestSupportProvider();
		testProv.setProvider(prov);
		
		String testProvName = provName + "TestSupport";
		
		testMalProv = malProvMgr.createProvider(testProvName, proto,
				PlanningRequestTestHelper.PLANNINGREQUESTTEST_SERVICE, authId, testProv, expQos, priority,
				System.getProperties(), false, (null==brokerUri) ? malProv.getBrokerURI() : brokerUri);
		
		LOG.exiting(getClass().getName(), "initProvider");
	}

	private void initTaskPublisher() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "initTaskPublicher");
		
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.BESTEFFORT;
		UInteger priority = new UInteger(0L);
		
		taskPub = prov.createMonitorTasksPublisher(domain, network, sessionType, sessionName, qos,
				System.getProperties(), priority);
		
		EntityKeyList keyList = new EntityKeyList();
		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		
		taskPub.register(keyList, null); // no async calls - no listener needed
		
		prov.setTaskPub(taskPub);
		
		LOG.exiting(getClass().getName(), "initTaskPublisher");
	}

	private void initPrPublisher() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "initPrPublisher");
		
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.BESTEFFORT;
		UInteger priority = new UInteger(0L);
		
		prPub = prov.createMonitorPlanningRequestsPublisher(domain, network, sessionType, sessionName, qos,
				System.getProperties(), priority);
		
		EntityKeyList keyList = new EntityKeyList();
		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		
		prPub.register(keyList, null); // no async calls - no listener needed
		
		prov.setPrPub(prPub);
		
		LOG.exiting(getClass().getName(), "initPrPublisher");
	}

	/**
	 * Creates provider and gets it up and running.
	 * @throws IOException
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void start(String name) throws IOException, MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "start");
		
		super.init();
		
		if (null == malProvMgr) {
			malProvMgr = malCtx.createProviderManager();
		}
		initProvider(name);
		initTaskPublisher();
		initPrPublisher();
		
		LOG.exiting(getClass().getName(), "start");
	}

	/**
	 * Returns provider URI for consumer to connect to.
	 * @return
	 */
	public URI getProviderUri() {
		return malProv.getURI();
	}

	/**
	 * Returns used broker URI.
	 * @return
	 */
	public URI getBrokerUri() {
		return malProv.getBrokerURI();
	}

	/**
	 * Returns testing support provider URI for consumer to connect to.
	 * @return
	 */
	public URI getTestProviderUri() {
		return testMalProv.getURI();
	}

	/**
	 * Stops provider.
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void stop() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "stop");
		
		if (null != prov) {
			prov.setTaskPub(null);
			prov.setPrPub(null);
		}
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
		if (testMalProv != null) {
			testMalProv.close();
		}
		testMalProv = null;
		if (malProv != null) {
			malProv.close();
		}
		malProv = null;
		if (malProvMgr != null) {
			malProvMgr.close();
		}
		malProvMgr = null;
		testProv = null; // test support
		prov = null;
		
		super.close();
		
		LOG.exiting(getClass().getName(), "stop");
	}
}
