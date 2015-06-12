package esa.mo.inttest.ev.subscriber;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.event.consumer.EventStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

import esa.mo.inttest.Util;

/**
 * Event Subscriber for testing.
 */
public class EventSubscriber {

	private static final Logger LOG = Logger.getLogger(EventSubscriber.class.getName());
	
	private EventStub stub;
	private String subId = null;
	
	/**
	 * Ctor.
	 * @param stub
	 */
	public EventSubscriber(EventStub stub) {
		this.stub = stub;
	}
	
	public EventStub getStub() {
		return stub;
	}
	
	/**
	 * Un-subscribe (de-register monitor).
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void unSubscribe() throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "EventSubscriber.unSubscribe(subId={0})", subId);
		IdentifierList subs = new IdentifierList();
		subs.add(new Identifier(subId));
		try {
			stub.monitorEventDeregister(subs);
		} catch (MALInteractionException e) {
			LOG.log(Level.INFO, "EventSubscriber.unSubScribe: malInt={0}", e); // ignore
		}
	}
	
	/**
	 * Subscribe (register monitor).
	 * @param subId
	 * @param events
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void subscribe(String subId, EventAdapter events) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "EventSubscriber.subscribe(subId={0})", subId);
		this.subId = subId;
		stub.monitorEventRegister(Util.createSub(subId), events);
	}
}
