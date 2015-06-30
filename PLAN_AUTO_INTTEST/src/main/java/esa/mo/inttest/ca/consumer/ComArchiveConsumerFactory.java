/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.ca.consumer;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.ArchiveHelper;
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
		
		MALConsumer malCons = malConsMgr.createConsumer(consName, provUri, brokerUri,
				ArchiveHelper.ARCHIVE_SERVICE, authId, domain, network, sessionType, sessionName,
				qos, System.getProperties(), priority);
		
		ArchiveStub cons = new ArchiveStub(malCons);
		
		LOG.exiting(getClass().getName(), "initConsumer");
		return cons;
	}
	
	/**
	 * Create (start) Consumer.
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws MALException
	 */
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
	
	/**
	 * Dispose (stop) Consumer.
	 * @param cons
	 * @throws MALException
	 */
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
