package esa.mo.inttest.ev.publisher;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.com.event.provider.MonitorEventPublisher;
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

import esa.mo.inttest.ev.EventFactory;

/**
 * Event publisher factory.
 */
public class EventPublisherFactory extends EventFactory {

	private static final Logger LOG = Logger.getLogger(EventPublisherFactory.class.getName());
	
	private URI brokerUri = null;
	private MALProviderManager malProvMgr = null;
	
	/**
	 * Set broker to use. If null, provider will create one itself.
	 * @param broker
	 */
	public void setBrokerUri(URI broker) {
		brokerUri = broker;
	}
	
	/**
	 * Creates a provider.
	 * @param name
	 * @return
	 * @throws MALException
	 */
	private EventPublisher initPublisher(String name) throws MALException {
		LOG.entering(getClass().getName(), "initPublisher");
		
		EventPublisher pub = new EventPublisher();
		pub.setDomain(domain);
		
		String provName = (null != name && !name.isEmpty()) ? name : "EvPub";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(1L);
		boolean isPublisher = true;
		
		MALProvider malProv = malProvMgr.createProvider(provName, proto, EventHelper.EVENT_SERVICE,
				authId, pub, expQos, priority, System.getProperties(), isPublisher, brokerUri);
		
		pub.setProvider(malProv);
		
		LOG.exiting(getClass().getName(), "initPublisher");
		return pub;
	}

	/**
	 * Creates Event publisher and registers it.
	 * @param evPub
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	private void initEvPublisher(EventPublisher evPub) throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "initPrPublisher");
		
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.BESTEFFORT;
		UInteger priority = new UInteger(0L);
		
		MonitorEventPublisher pub = evPub.createMonitorEventPublisher(domain, network,
				sessionType, sessionName, qos, System.getProperties(), priority);
		
		EntityKeyList keyList = new EntityKeyList();
		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		
		pub.register(keyList, evPub); // no async calls - no listener needed?
		
		evPub.setEvPub(pub);
		
		LOG.exiting(getClass().getName(), "initPrPublisher");
	}

	/**
	 * Creates provider and gets it up and running.
	 * @throws IOException
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public EventPublisher start(String name) throws IOException, MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "start");
		
		super.init();
		
		if (null == malProvMgr) {
			malProvMgr = malCtx.createProviderManager();
		}
		EventPublisher evPub = initPublisher(name);
		initEvPublisher(evPub);
		
		LOG.exiting(getClass().getName(), "start");
		return evPub;
	}
	
	/**
	 * Stops provider.
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void stop(EventPublisher pub) throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "stop");
		
		if ((null != pub) && (null != pub.getProvider())) {
			pub.getProvider().close();
			pub.setProvider(null);
		}
		
		if (malProvMgr != null) {
			malProvMgr.close();
		}
		malProvMgr = null;
		
		super.close();
		
		LOG.exiting(getClass().getName(), "stop");
	}
}
