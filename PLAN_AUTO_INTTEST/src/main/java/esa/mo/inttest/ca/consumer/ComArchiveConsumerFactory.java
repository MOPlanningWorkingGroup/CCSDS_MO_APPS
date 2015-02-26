package esa.mo.inttest.ca.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
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
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;

/**
 * COM Archive consumer factory.
 */
public class ComArchiveConsumerFactory {

	private String propertyFile = null;
	private MALContext malCtx = null;
	private MALConsumerManager malConsMgr = null;
	private URI provUri = null;
	private URI brokerUri = null;
	private MALConsumer malCons = null;
	private ArchiveStub cons = null;

	public void setPropertyFile(String fn) {
		propertyFile = fn;
	}
	
	private void initProperties() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
		Properties props = new Properties(System.getProperties());
		props.load(is);
		is.close();
		System.setProperties(props);
	}
	
	private void initContext() throws MALException {
		malCtx = MALContextFactory.newFactory().createMALContext(System.getProperties());
	}
	
	private void initHelpers() throws MALException {
		MALHelper.init(MALContextFactory.getElementFactoryRegistry());
		COMHelper.init(MALContextFactory.getElementFactoryRegistry());
		MALService tmp = COMHelper.COM_AREA.getServiceByName(ArchiveHelper.ARCHIVE_SERVICE_NAME);
		if (tmp == null) {
			ArchiveHelper.init(MALContextFactory.getElementFactoryRegistry());
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
		
		String consName = "testCaCons";
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
		
		cons = new ArchiveStub(malCons);
	}
	
	public void start() throws IOException, MALException {
		initProperties();
		initContext();
		initHelpers();
		initConsumer();
	}
	
	public ArchiveStub getConsumer() {
		return cons;
	}
	
	public void stop() throws MALException {
		if (malCons != null) {
			malCons.close();
		}
		malCons = null;
		cons = null;
		if (malConsMgr != null) {
			malConsMgr.close();
		}
		malConsMgr = null;
		if (malCtx != null) {
			malCtx.close();
		}
		malCtx = null;
	}
}
