package esa.mo.inttest.ca.provider;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

import esa.mo.inttest.ca.ComArchiveFactory;
import esa.mo.inttest.ev.publisher.EventPublisher;
import esa.mo.inttest.ev.subscriber.EventSubscriber;

/**
 * COM Archive provider factory.
 */
public class ComArchiveProviderFactory extends ComArchiveFactory {

	private static final Logger LOG = Logger.getLogger(ComArchiveProviderFactory.class.getName());
	
	private ComArchiveProvider prov = null;
	private URI brokerUri = null;
	private MALProviderManager malProvMgr = null;
	private MALProvider malProv = null;
	
	/**
	 * Set broker to use. If null, provider will create one itself.
	 * @param broker
	 */
	public void setBrokerUri(URI broker) {
		brokerUri = broker;
	}
	
	public void setEventSubscriber(EventSubscriber evSub) throws MALException, MALInteractionException {
		if (null != prov) {
			prov.setEventSubscriber(evSub);
		}
	}
	
	public EventSubscriber getEventSubscriber() {
		return (null != prov) ? prov.getEventSubscriber() : null;
	}
	
	public void setEventPublisher(EventPublisher evPub) throws MALException, MALInteractionException {
		if (null != prov) {
			prov.setEventPublisher(evPub);
		}
	}
	
	public EventPublisher getEventPublisher() {
		return (null != prov) ? prov.getEventPublisher() : null;
	}
	
	/**
	 * Creates a provider.
	 * @param name
	 * @throws MALException
	 */
	private void initProvider(String name) throws MALException {
		LOG.entering(getClass().getName(), "initProvider");
		
		prov = new ComArchiveProvider();
		prov.setDomain(domain);
		
		String provName = (null != name && !name.isEmpty()) ? name : "CaProv";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(1L);
		boolean isPublisher = true;
		
		malProv = malProvMgr.createProvider(provName, proto, ArchiveHelper.ARCHIVE_SERVICE,
				authId, prov, expQos, priority, System.getProperties(), isPublisher, brokerUri);
		
		LOG.exiting(getClass().getName(), "initProvider");
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
	 * Stops provider.
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void stop() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "stop");
		
		if (malProv != null) {
			malProv.close();
		}
		malProv = null;
		if (malProvMgr != null) {
			malProvMgr.close();
		}
		malProvMgr = null;
		prov = null;
		
		super.close();
		
		LOG.exiting(getClass().getName(), "stop");
	}
}
