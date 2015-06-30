/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.ev.subscriber;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.com.event.consumer.EventStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

import esa.mo.inttest.ev.EventFactory;

/**
 * COM Archive consumer factory.
 */
public class EventSubscriberFactory extends EventFactory {

	private static final Logger LOG = Logger.getLogger(EventSubscriberFactory.class.getName());
	
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
	
	private EventStub initSubscriber(String name) throws MALException {
		LOG.entering(getClass().getName(), "initSubscriber");
		
		String consName = (null != name && !name.isEmpty()) ? name : "CaCons";
		Blob authId = new Blob("".getBytes());
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		
		MALConsumer malCons = malConsMgr.createConsumer(consName, provUri, brokerUri,
				EventHelper.EVENT_SERVICE, authId, domain, network, sessionType, sessionName,
				qos, System.getProperties(), priority);
		
		EventStub stub = new EventStub(malCons);
		
		LOG.exiting(getClass().getName(), "initSubscriber");
		return stub;
	}
	
	/**
	 * Create (start) Subscriber.
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws MALException
	 */
	public EventStub start(String name) throws IOException, MALException {
		LOG.entering(getClass().getName(), "start");
		
		super.init();
		
		if (malConsMgr == null) {
			malConsMgr = malCtx.createConsumerManager();
		}
		EventStub stub = initSubscriber(name);
		
		LOG.exiting(getClass().getName(), "start");
		return stub;
	}
	
	/**
	 * Dispose (stop) Consumer.
	 * @param cons
	 * @throws MALException
	 */
	public void stop(EventStub cons) throws MALException {
		LOG.entering(getClass().getName(), "stop");
		
		if (cons != null) {
			if (cons.getConsumer() != null) {
				cons.getConsumer().close();
			}
		}
		if (malConsMgr != null) {
			malConsMgr.close();
		}
		malConsMgr = null;
		
		super.close();
		
		LOG.exiting(getClass().getName(), "stop");
	}
}
