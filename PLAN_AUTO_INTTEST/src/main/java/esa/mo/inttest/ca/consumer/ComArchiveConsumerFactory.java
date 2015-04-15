package esa.mo.inttest.ca.consumer;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;

import esa.mo.inttest.ca.ComArchiveFactory;

/**
 * COM Archive consumer factory.
 */
public class ComArchiveConsumerFactory extends ComArchiveFactory {

	private static final Logger LOG = Logger.getLogger(ComArchiveConsumerFactory.class.getName());
	
	private MALConsumerManager malConsMgr = null;
	private URI provUri = null;
	private URI brokerUri = null;
	
	/**
	 * Set provider URI to connect to.
	 * @param uri
	 */
	public void setProviderUri(URI uri) {
		provUri = uri; 
	}
	
	/**
	 * Set broker URI to connect to.
	 * @param uri
	 */
	public void setBrokerUri(URI uri) {
		brokerUri = uri;
	}
	
	private ArchiveStub initConsumer(String name) throws MALException {
		LOG.entering(getClass().getName(), "initConsumer");
		
		String consName = (null != name && !name.isEmpty()) ? name : "CaCons";
		Blob authId = new Blob("".getBytes());
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		
		MALConsumer malCons = malConsMgr.createConsumer(consName, provUri, brokerUri, PlanningRequestHelper.PLANNINGREQUEST_SERVICE,
				authId, domain, network, sessionType, sessionName, qos, System.getProperties(), priority);
		
		ArchiveStub cons = new ArchiveStub(malCons);
		
		LOG.exiting(getClass().getName(), "initConsumer");
		return cons;
	}
	
	public ArchiveStub start(String name) throws IOException, MALException {
		LOG.entering(getClass().getName(), "start");
		
		super.init();
		
		if (malConsMgr == null) {
			malConsMgr = malCtx.createConsumerManager();
		}
		ArchiveStub stub = initConsumer(name);
		
		LOG.exiting(getClass().getName(), "start");
		return stub;
	}
	
	public void stop(ArchiveStub cons) throws MALException {
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
}
