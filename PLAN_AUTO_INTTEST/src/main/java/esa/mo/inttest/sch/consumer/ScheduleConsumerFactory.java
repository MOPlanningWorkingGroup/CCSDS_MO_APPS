package esa.mo.inttest.sch.consumer;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.ScheduleHelper;
import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleStub;
import org.ccsds.moims.mo.automationprototype.scheduletest.ScheduleTestHelper;
import org.ccsds.moims.mo.automationprototype.scheduletest.consumer.ScheduleTestStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

import esa.mo.inttest.sch.ScheduleFactory;

/**
 * Planning request consumer for testing. Can produce several consumers.
 */
public class ScheduleConsumerFactory extends ScheduleFactory {

	private static final Logger LOG = Logger.getLogger(ScheduleConsumerFactory.class.getName());
	
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
	}

	/**
	 * Set broker to use.
	 * @param uri
	 */
	public void setBrokerUri(URI uri) {
		brokerUri = uri;
	}

	/**
	 * Set testing support provider to use.
	 * @param uri
	 */
	public void setTestProviderUri(URI uri) {
		testProvUri = uri;
	}

	private ScheduleStub initConsumer(String name) throws MALException {
		LOG.entering(getClass().getName(), "initConsumer");
		
		String consName = (null != name && !name.isEmpty()) ? name : "SchCons";
		Blob authId = new Blob("".getBytes());
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		
		MALConsumer malCons = malConsMgr.createConsumer(consName, provUri, brokerUri,
				ScheduleHelper.SCHEDULE_SERVICE, authId, domain, network, sessionType, sessionName,
				qos, System.getProperties(), priority);
		
		ScheduleStub cons = new ScheduleStub(malCons);
		
		LOG.exiting(getClass().getName(), "initConsumer");
		return cons;
	}

	public ScheduleStub start(String name) throws IOException, MALException {
		LOG.entering(getClass().getName(), "start");
		
		super.init();
		
		if (malConsMgr == null) {
			malConsMgr = malCtx.createConsumerManager();
		}
		ScheduleStub stub = initConsumer(name);
		
		LOG.exiting(getClass().getName(), "start");
		return stub;
	}

	public void stop(ScheduleStub cons) throws MALException {
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
	
	private ScheduleTestStub initTestConsumer(String name) throws MALException {
		LOG.entering(getClass().getName(), "initTestConsumer");
		
		String consName = (null != name && !name.isEmpty()) ? name : "SchConsTestSupport";
		Blob authId = new Blob("".getBytes());
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		
		MALConsumer malCons = malConsMgr.createConsumer(consName, testProvUri, brokerUri,
				ScheduleTestHelper.SCHEDULETEST_SERVICE, authId, domain, network, sessionType,
				sessionName, qos, System.getProperties(), priority);
		
		ScheduleTestStub cons = new ScheduleTestStub(malCons);
		
		LOG.exiting(getClass().getName(), "initTestConsumer");
		return cons;
	}
	
	public ScheduleTestStub startTest(String name) throws IOException, MALException {
		LOG.entering(getClass().getName(), "startTest");
		
		super.init();
		
		if (malConsMgr == null) {
			malConsMgr = malCtx.createConsumerManager();
		}
		ScheduleTestStub stub = initTestConsumer(name);
		
		LOG.exiting(getClass().getName(), "startTest");
		return stub;
	}
	
	public void stopTest(ScheduleTestStub cons) throws MALException {
		LOG.entering(getClass().getName(), "stopTest");
		
		if (cons != null && cons.getConsumer() != null) {
			cons.getConsumer().close();
		}
		
		LOG.exiting(getClass().getName(), "stopTest");
	}

}
