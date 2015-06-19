package esa.mo.inttest.ev.publisher;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.event.provider.EventInheritanceSkeleton;
import org.ccsds.moims.mo.com.event.provider.MonitorEventPublisher;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import esa.mo.inttest.Util;

/**
 * Event publisher for testing.
 */
public class EventPublisher extends EventInheritanceSkeleton implements MALPublishInteractionListener {

	private static final Logger LOG = Logger.getLogger(EventPublisher.class.getName());
	
	private IdentifierList domain = new IdentifierList();
	private MALProvider prov = null;
	private MonitorEventPublisher evPub = null;
	
	/**
	 * Ctor.
	 */
	public EventPublisher() {
		domain.add(new Identifier("desd"));
	}
	
	/**
	 * Set domain to use.
	 * @param domain
	 */
	public void setDomain(IdentifierList domain) {
		this.domain = domain;
	}
	
	public IdentifierList getDomain() {
		return domain;
	}
	
	/**
	 * Set provider to use.
	 * @param prov
	 */
	public void setProvider(MALProvider prov) {
		this.prov = prov;
	}
	
	public MALProvider getProvider() {
		return prov;
	}
	
	/**
	 * Set publisher to use.
	 * @param evPub
	 */
	public void setEvPub(MonitorEventPublisher evPub) {
		this.evPub = evPub;
	}
	
	public MonitorEventPublisher getEvPub() {
		return evPub;
	}
	
	@SuppressWarnings("rawtypes")
	public void publishRegisterAckReceived(MALMessageHeader msgHdr, Map qosProps) throws MALException {
		LOG.log(Level.INFO, "EventPublisher.publishRegisterAckReceived()");
	}

	@SuppressWarnings("rawtypes")
	public void publishRegisterErrorReceived(MALMessageHeader msgHdr, MALErrorBody err, Map qosProp)
			throws MALException {
		LOG.log(Level.INFO, "EventPublisher.publishRegisterErrorReceoved={0}", err);
	}

	@SuppressWarnings("rawtypes")
	public void publishErrorReceived(MALMessageHeader mdgHdr, MALErrorBody err, Map qosProps)
			throws MALException {
		LOG.log(Level.INFO, "EventPublisher.publishErrorReceived={0}", err);
	}

	@SuppressWarnings("rawtypes")
	public void publishDeregisterAckReceived(MALMessageHeader msgHdr, Map qosProps) throws MALException {
		LOG.log(Level.INFO, "EventPublisher.publishDeRegisterAckReceived()");
	}
	
	/**
	 * Publish single event.
	 * @param el
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	@SuppressWarnings("rawtypes")
	public void publish(ElementList el) throws MALException, MALInteractionException {
		ElementList<Element> el2 = (ElementList<Element>)el;
		UpdateHeaderList uhl = new UpdateHeaderList();
		Time ts = Util.currentTime();
		URI uri = getProvider().getURI(); // mandatory, dummy
		UpdateType ut = UpdateType.CREATION;
		EntityKey ek = new EntityKey(new Identifier("event"), 0L, 0L, 0L);
		uhl.add(new UpdateHeader(ts, uri, ut, ek));
		
		ObjectDetailsList odl = new ObjectDetailsList();
		Long relId = null;
		ObjectType ot = Util.createObjType(el2.get(0));
		ObjectKey ok = new ObjectKey(domain, null);
		ObjectId srcId = new ObjectId(ot, ok);
		odl.add(new ObjectDetails(relId, srcId));
		
		evPub.publish(uhl, odl, el);
	}
}
