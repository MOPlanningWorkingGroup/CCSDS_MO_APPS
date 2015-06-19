/*
 * Go to www.websequencediagrams.com and paste following text there for diagram.
 * 
note over cons, consEvPub, broker, arcEvSub, arcEvPub, arcProv, monConsEvSub, monCons: events pub/sub including comArc store

arcProv->arcProv: createEventSubscriber(arcEvSub)
arcProv->arcEvSub: register_ev_sub()
arcEvSub->broker: register_ev_sub()
broker->arcEvSub: ev_sub_registered
arcEvSub->arcProv: ev_sub_registered

arcProv->arcProv: createEventPublisher(arcEvPub)
arcProv->arcEvPub: register_ev_pub()
arcEvPub->broker: register_ev_pub()
broker->arcEvPub: ev_pub_registered
arcEvPub->arcProv: ev_pub_registered

monCons->monCons: createEventSubscriber(monConsEvSub)
monCons->monConsEvSub: register_ev_sub()
monConsEvSub->broker: register_ev_sub()
broker->monConsEvSub: ev_sub_registered
monConsEvSub->monCons: ev_sub_registered

cons->cons: createEventPublisher(consEvPub)
cons->consEvPub: register_ev_pub()
consEvPub->broker: register_ev_pub()
broker->consEvPub: ev_pub_registered
consEvPub->cons: ev_pub_registered

cons->consEvPub: pub_event("consumer started")
consEvPub->broker: pub_event("consumer started")

broker->arcEvSub: notif_event("consumer started")
arcEvSub->arcProv: notif_event("consumer started")
arcProv->arcProv: store(event)
arcProv->arcEvPub: pub_event("object stored")
arcEvPub->broker: pub_event("object stored")

broker->monConsEvSub: notif_event("consumer started")
monConsEvSub->monCons: notif_event("consumer started")

broker->arcEvSub: notif_event("object_stored")
arcEvSub->arcProv: notif_event("object_stored")

broker->monConsEvSub: notif_event("object_stored")
monConsEvSub->monCons: notif_event("object_stored")
 */
package esa.mo.inttest;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.event.consumer.EventStub;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import esa.mo.inttest.ca.provider.ComArchiveProviderFactory;
import esa.mo.inttest.ev.publisher.EventPublisher;
import esa.mo.inttest.ev.publisher.EventPublisherFactory;
import esa.mo.inttest.ev.subscriber.EventSubscriber;
import esa.mo.inttest.ev.subscriber.EventSubscriberFactory;

/**
 * Event test. Two consumers - one publisher, one subscriber - and COM Archive to monitor and store events.
 * Dummy event is published, CA receives it, stores it and publishes store event.
 */
public class EventTest {

	public static final class MyEventMonitor extends EventAdapter {
		
		@SuppressWarnings("rawtypes")
		public void monitorEventNotifyReceived(MALMessageHeader msgHdr, Identifier subId,
				UpdateHeaderList updHdrs, ObjectDetailsList objs, ElementList elements, Map qosProps)
		{
			LOG.log(Level.INFO, "monitorEventNotifyReceived(subId={0}, updHdrs={1}, objs={2}, els={3})",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objs(objs), Dumper.els(elements) });
		}
		
		@SuppressWarnings("rawtypes")
		public void monitorEventNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError err,
				Map qosProps)
		{
			LOG.log(Level.WARNING, "monitorEventNotifyErrorReceived={0}", err);
			assertTrue(false);
		}
	}
	
	private static final Logger LOG = Logger.getLogger(EventTest.class.getName());
	
	private static final String CA_PROV = "CaProv";
	private static final String CA_EV_SUB = "CaProvEvSub";
	private static final String MON_EV_SUB = "monConsEvSub";
	private static final String CA_EV_PUB = "CaProvEvPub";
	private static final String EV_PUB_1 = "evPub1";
	
	private ComArchiveProviderFactory caProvFct;
	
	private EventSubscriberFactory evSubFct;
	private EventStub evSub1;
	private EventStub evSub2;
	
	private EventPublisherFactory evPubFct;
	private EventPublisher evPub1;
	private EventPublisher evPub2;
	
	@Before
	public void setUp() throws Exception {
		String fn = "testInt.properties";
		
		caProvFct = new ComArchiveProviderFactory();
		caProvFct.setPropertyFile(fn);
		// no broker uri - create internally broker
		caProvFct.start(CA_PROV);
		
		URI broker = caProvFct.getBrokerUri();
		
		evSubFct = new EventSubscriberFactory();
		evSubFct.setPropertyFile(fn);
		// no provider uri needed because no provider calls
		evSubFct.setBrokerUri(broker);
		// CA subscribes to events in order to store them
		evSub1 = evSubFct.start(CA_EV_SUB);
		caProvFct.setEventSubscriber(new EventSubscriber(evSub1));
		// monitorConsumer just monitors events
		evSub2 = evSubFct.start(MON_EV_SUB);
		
		evPubFct = new EventPublisherFactory();
		evPubFct.setPropertyFile(fn);
		// no provider uri needed because no provider calls
		evPubFct.setBrokerUri(broker);
		// CA publishes "object stored" event
		evPub1 = evPubFct.start(CA_EV_PUB);
		caProvFct.setEventPublisher(evPub1);
		// event publishing consumer
		evPub2 = evPubFct.start(EV_PUB_1);
	}
	
	@After
	public void tearDown() throws Exception {
		evPubFct.stop(evPub2);
		caProvFct.setEventPublisher(null);
		evPubFct.stop(evPub1);
		evSubFct.stop(evSub2);
		caProvFct.setEventSubscriber(null); // that will unsubscribe
		evSubFct.stop(evSub1);
		caProvFct.stop();
	}
	
	protected void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	
	@Test
	public void testEventPublish() throws MALException, MALInteractionException {
		// CA is already registered for subscription and publishing
		// subscribe monitoring consumer
		EventSubscriber monCons = new EventSubscriber(evSub2);
		MyEventMonitor monEvents = new MyEventMonitor(); // just logging
		monCons.subscribe("monConsEvSubId", monEvents);
		
		// create dummy event
		ObjectIdList oil = new ObjectIdList();
		oil.add(new ObjectId(Util.createObjType(new EntityKey()), new ObjectKey(evPub2.getDomain(), 1L)));
		// publisher is already registered, go ahead and publish
		evPub2.publish(oil);
		
		sleep(200); // let events travel
		
		monCons.unSubscribe();
		
		sleep(100);
	}
}
