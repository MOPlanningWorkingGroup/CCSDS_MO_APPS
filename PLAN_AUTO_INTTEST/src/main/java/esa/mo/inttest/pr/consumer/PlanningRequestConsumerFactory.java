package esa.mo.inttest.pr.consumer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planningprototype.planningrequesttest.PlanningRequestTestHelper;
import org.ccsds.moims.mo.planningprototype.planningrequesttest.consumer.PlanningRequestTestStub;

import esa.mo.inttest.pr.PlanningRequestFactory;

/**
 * Planning request consumer for testing. Can produce several consumers.
 */
public class PlanningRequestConsumerFactory extends PlanningRequestFactory {

	private static final Logger LOG = Logger.getLogger(PlanningRequestConsumerFactory.class.getName());
	
	private MALConsumerManager malConsMgr = null;
	private URI provUri = null;
	private URI brokerUri = null;
	// testing support
	private URI testProvUri = null;
	
	/**
	 * Set provider to use.
	 * @param uri
	 */
	public void setProviderUri(URI uri) {
		provUri = uri;
		LOG.log(Level.CONFIG, "provider uri set: {0}", provUri);
	}

	/**
	 * Set broker to use.
	 * @param uri
	 */
	public void setBrokerUri(URI uri) {
		brokerUri = uri;
		LOG.log(Level.CONFIG, "broker uri set: {0}", brokerUri);
	}

	/**
	 * Set testing support provider to use.
	 * @param uri
	 */
	public void setTestProviderUri(URI uri) {
		testProvUri = uri;
		LOG.log(Level.CONFIG, "testProvider uri set: {0}", testProvUri);
	}

	private PlanningRequestStub initConsumer() throws MALException {
		LOG.entering(getClass().getName(), "initConsumer");
		
		String consName = "testPrCons";
		Blob authId = new Blob("".getBytes());
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		
		MALConsumer malCons = malConsMgr.createConsumer(consName, provUri, brokerUri, PlanningRequestHelper.PLANNINGREQUEST_SERVICE,
				authId, domain, network, sessionType, sessionName, qos, System.getProperties(), priority);
		
		PlanningRequestStub cons = new PlanningRequestStub(malCons);
		
		LOG.exiting(getClass().getName(), "initConsumer");
		return cons;
	}

	public PlanningRequestStub start() throws IOException, MALException {
		LOG.entering(getClass().getName(), "start");
		
		super.init();
		
		if (malConsMgr == null) {
			malConsMgr = malCtx.createConsumerManager();
		}
		PlanningRequestStub stub = initConsumer();
		
		LOG.exiting(getClass().getName(), "start");
		return stub;
	}

	public void stop(PlanningRequestStub cons) throws MALException {
		LOG.entering(getClass().getName(), "stop");
		
		if (cons != null && cons.getConsumer() != null) {
			cons.getConsumer().close();
		}
		if (malConsMgr != null) {
			malConsMgr.close();
		}
		malConsMgr = null;
		
		super.close();
		
		LOG.exiting(getClass().getName(), "stop");
	}
	
	private PlanningRequestTestStub initTestConsumer() throws MALException {
		LOG.entering(getClass().getName(), "initTestConsumer");
		
		String consName = "testPrTestCons";
		Blob authId = new Blob("".getBytes());
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		
		MALConsumer malCons = malConsMgr.createConsumer(consName, testProvUri, brokerUri, PlanningRequestTestHelper.PLANNINGREQUESTTEST_SERVICE,
				authId, domain, network, sessionType, sessionName, qos, System.getProperties(), priority);
		
		PlanningRequestTestStub cons = new PlanningRequestTestStub(malCons);
		
		LOG.exiting(getClass().getName(), "initTestConsumer");
		return cons;
	}
	
	public PlanningRequestTestStub startTest() throws IOException, MALException {
		LOG.entering(getClass().getName(), "startTest");
		
		super.init();
		
		if (malConsMgr == null) {
			malConsMgr = malCtx.createConsumerManager();
		}
		PlanningRequestTestStub stub = initTestConsumer();
		
		LOG.exiting(getClass().getName(), "startTest");
		return stub;
	}
	
	public void stopTest(PlanningRequestTestStub cons) throws MALException {
		LOG.entering(getClass().getName(), "stopTest");
		
		if (cons != null && cons.getConsumer() != null) {
			cons.getConsumer().close();
		}
		
		LOG.exiting(getClass().getName(), "stopTest");
	}
}
