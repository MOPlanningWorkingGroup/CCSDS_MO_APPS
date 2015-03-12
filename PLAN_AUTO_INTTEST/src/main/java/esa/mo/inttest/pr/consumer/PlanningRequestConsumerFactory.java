package esa.mo.inttest.pr.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.goce.GOCEHelper;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planningdatatypes.PlanningDataTypesHelper;

/**
 * Planning request consumer for testing.
 */
public class PlanningRequestConsumerFactory {

	private static final Logger LOG = Logger.getLogger(PlanningRequestConsumerFactory.class.getName());
	
	private String propertyFile = null;
	private MALContext malCtx = null;
	private MALConsumerManager malConsMgr = null;
	private URI provUri = null;
	private URI brokerUri = null;

	public void setPropertyFile(String fn) {
		propertyFile = fn;
		LOG.log(Level.CONFIG, "propert file set to {0}", propertyFile);
	}
	
	private void initProperties() throws IOException {
		LOG.entering(getClass().getName(), "initProperties");
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
		Properties props = new Properties(System.getProperties());
		props.load(is);
		is.close();
		System.setProperties(props);
		LOG.log(Level.CONFIG, "property rmi transport: {0}", System.getProperty("org.ccsds.moims.mo.mal.transport.protocol.rmi", ""));
		LOG.log(Level.CONFIG, "property string encoder: {0}", System.getProperty("org.ccsds.moims.mo.mal.encoding.protocol.rmi", ""));
		LOG.log(Level.CONFIG, "property gen wrap: {0}", System.getProperty("org.ccsds.moims.mo.mal.transport.gen.wrap", ""));
		LOG.exiting(getClass().getName(), "initProperties");
	}
	
	private void initHelpers() throws MALException {
		LOG.entering(getClass().getName(), "initHelpers");
		MALService tmp = PlanningHelper.PLANNING_AREA.getServiceByName(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
		if (tmp == null) {
			MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			COMHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningDataTypesHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
			GOCEHelper.init(MALContextFactory.getElementFactoryRegistry());
		} // else already initialized
		LOG.exiting(getClass().getName(), "initHelpers");
	}

	public void setProviderUri(URI uri) {
		provUri = uri;
		LOG.log(Level.CONFIG, "provider uri set to {0}", provUri.getValue());
	}
	
	public void setBrokerUri(URI uri) {
		brokerUri = uri;
		LOG.log(Level.CONFIG, "broker uri set to {0}", brokerUri.getValue());
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
		initProperties();
		if (malCtx == null) {
			malCtx = MALContextFactory.newFactory().createMALContext(System.getProperties());
		}
		initHelpers();
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
		if (malCtx != null) {
			malCtx.close();
		}
		malCtx = null;
		LOG.exiting(getClass().getName(), "stop");
	}
}
