package esa.mo.plan.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

public class PlanningRequestConsumerFactory {

	private String propertyFile = null;
	private MALContext malCtx = null;
	private MALConsumerManager malConsMgr = null;
	private URI provUri = null;
	private URI brokerUri = null;
	private MALConsumer malCons = null;
	private PlanningRequestStub cons = null;

	public void setPropertyFile(String fn) {
		propertyFile = fn;
	}
	
	private void initProperties() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
		Properties props = new Properties(System.getProperties());
		props.load(is);
		is.close();
		System.setProperties(props);
//		System.out.println("PRCF:initProperties: rmi transport: " + System.getProperty("org.ccsds.moims.mo.mal.transport.protocol.rmi", ""));
//		System.out.println("PRCF:initProperties: string encoder: " + System.getProperty("org.ccsds.moims.mo.mal.encoding.protocol.rmi", ""));
//		System.out.println("PRCF:initProperties: gen wrap: " + System.getProperty("org.ccsds.moims.mo.mal.transport.gen.wrap", ""));
	}
	
	private void initContext() throws MALException {
		malCtx = MALContextFactory.newFactory().createMALContext(System.getProperties());
	}
	
	private void initHelpers() throws MALException {
		MALHelper.init(MALContextFactory.getElementFactoryRegistry());
		PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
		PlanningDataTypesHelper.init(MALContextFactory.getElementFactoryRegistry());
		MALService tmp = PlanningHelper.PLANNING_AREA.getServiceByName(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
		if (tmp == null) {
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}

	public void setProviderUri(URI uri) {
		provUri = uri; 
	}
	
	public void setBrokerUri(URI uri) {
		brokerUri = uri;
	}
	
	private void initConsumer() throws MALException {
		malConsMgr = malCtx.createConsumerManager();
		
		String consName = "testCons";
		Blob authId = new Blob("".getBytes());
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("desd"));
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		
		malCons = malConsMgr.createConsumer(consName, provUri, brokerUri, PlanningRequestHelper.PLANNINGREQUEST_SERVICE,
				authId, domain, network, sessionType, sessionName, qos, System.getProperties(), priority);
		
		cons = new PlanningRequestStub(malCons);
	}
	
	public void start() throws IOException, MALException {
		initProperties();
		initContext();
		initHelpers();
		initConsumer();
	}
	
	public PlanningRequestStub getConsumer() {
		return cons;
	}
	
	public void stop() throws MALException {
		malCons.close();
		malCons = null;
		cons = null;
		malConsMgr.close();
		malConsMgr = null;
		malCtx.close();
		malCtx = null;
	}
}
