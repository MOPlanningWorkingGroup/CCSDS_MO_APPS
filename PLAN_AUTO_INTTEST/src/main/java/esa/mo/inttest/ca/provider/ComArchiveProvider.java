package esa.mo.inttest.ca.provider;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.archive.provider.CountInteraction;
import org.ccsds.moims.mo.com.archive.provider.QueryInteraction;
import org.ccsds.moims.mo.com.archive.provider.RetrieveInteraction;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.archive.structures.QueryFilterList;
import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;
import esa.mo.inttest.ev.publisher.EventPublisher;
import esa.mo.inttest.ev.subscriber.EventSubscriber;

/**
 * COM Archive provider for testing. Implemented as little as necessary.
 */
public class ComArchiveProvider extends ArchiveInheritanceSkeleton {

	public static final class MyEventsAdapter extends EventAdapter {
		
		private ComArchiveProvider ca;
		
		public MyEventsAdapter(ComArchiveProvider ca) {
			this.ca = ca;
		}
		
		/**
		 * Implements Event notify reception.
		 * Stores all received events in COM Archvie.
		 * @see org.ccsds.moims.mo.com.event.consumer.EventAdapter#monitorEventNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.structures.Identifier, org.ccsds.moims.mo.mal.structures.UpdateHeaderList, org.ccsds.moims.mo.com.structures.ObjectDetailsList, org.ccsds.moims.mo.mal.structures.ElementList, java.util.Map)
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void monitorEventNotifyReceived(MALMessageHeader msgHdr, Identifier subId,
				UpdateHeaderList updHdrs, ObjectDetailsList objs, ElementList elements, Map qosProps)
		{
			LOG.log(Level.INFO, "monitorEventNotifyReceived(subId={0}, updHdrs={1}, objs={2}, els={3})",
					new Object[] { subId, Dumper.updHdrs(updHdrs), Dumper.objs(objs), Dumper.els(elements) });
			
			Boolean retIds = new Boolean(false);
			ElementList<Element> els = elements;
			ObjectType ot = null;
			if ((null != els) && !els.isEmpty()) {
				Element el = els.get(0);
				ot = Util.createObjType(el);
			}
			ArchiveDetailsList adl = new ArchiveDetailsList();
			for (int i = 0; (null != objs) && (i < objs.size()); ++i) {
				UpdateHeader uh = updHdrs.get(i);
				// UH: KEY: first, second, third, fourth
				// UH: URI
				// UH: TIMESTAMP
				// UH: UPDATETYPE
				ObjectDetails od = objs.get(i);
				// OD: RELATED: long
				// OD: SOURCE: KEY, TYPE
				Long instId = 1L; // TODO where to get instance id from
				Identifier network = new Identifier("junit"); // TODO get from factory
				FineTime ts = Util.currentFineTime();
				URI provider = uh.getSourceURI(); // FIXME is that correct uri?
				adl.add(new ArchiveDetails(instId, od, network, ts, provider));
			}
			try {
				ca.store(retIds, ot, ca.domain, adl, elements, null);
			} catch (MALException e) {
				LOG.log(Level.WARNING, "CA.store: mal={0}", e);
			} catch (MALInteractionException e) {
				LOG.log(Level.WARNING, "CA.store: malInt={0}", e);
			}
		}
		
		@SuppressWarnings("rawtypes")
		public void monitorEventNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError err,
				Map qosProps)
		{
			LOG.log(Level.WARNING, "monitorEventNotifyErrorReceived={0}", err);
		}
	}
	
	private static final Logger LOG = Logger.getLogger(ComArchiveProvider.class.getName());
	
	private ObjStore objStore = new ObjStore();
	private IdentifierList domain = new IdentifierList();
	private EventSubscriber evSub = null;
	private String evSubId = "CaProvEvSubId";
	private EventPublisher evPub = null;
	
	
	/**
	 * Ctor.
	 */
	public ComArchiveProvider() {
		domain.add(new Identifier("desd"));
	}
	
	/**
	 * Set domain to use.
	 * @param domain
	 */
	public void setDomain(IdentifierList domain) {
		this.domain = domain;
	}
	
	/**
	 * Use given Subscriber to sign up for events.
	 * @param evSub
	 */
	public void setEventSubscriber(EventSubscriber evSub) throws MALException, MALInteractionException {
		if (null != this.evSub) {
			this.evSub.unSubscribe();
		}
		if (null != evSub) {
			evSub.subscribe(evSubId, new MyEventsAdapter(this));
		}
		this.evSub = evSub;
	}
	
	public EventSubscriber getEventSubscriber() {
		return evSub;
	}
	
	/**
	 * Use given Publisher to notify about CA events.
	 * @param evPub
	 */
	public void setEventPublisher(EventPublisher evPub) {
		this.evPub = evPub;
	}
	
	public EventPublisher getEventPublisher() {
		return evPub;
	}
	
	/**
	 * Publishes event if has publisher. Doesn't throw exceptions, logs 'em.
	 */
	@SuppressWarnings("rawtypes")
	protected void publishEv(ElementList els) {
		if (null != evPub) {
			// dummy "object stored" event
			ObjectIdList oil = new ObjectIdList();
			oil.add(new ObjectId(Util.createObjType(new ObjectId()), new ObjectKey(domain, 0L)));
			// avoid infinite loop
			if (!oil.equals(els)) {
				LOG.log(Level.INFO, "publishEvent: {0}", Dumper.els(oil));
				try {
					evPub.publish(oil);
				} catch (MALException e) {
					LOG.log(Level.WARNING, "publishEvent: mal: {0}", e);
				} catch (MALInteractionException e) {
					LOG.log(Level.WARNING, "publishEvent: malInt: {0}", e);
				}
			}
		}
	}
	/**
	 * Implements retrieval.
	 * @see org.ccsds.moims.mo.com.archive.provider.ArchiveHandler#retrieve(org.ccsds.moims.mo.com.structures.ObjectType, org.ccsds.moims.mo.mal.structures.IdentifierList, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.com.archive.provider.RetrieveInteraction)
	 */
	@Override
	public void retrieve(ObjectType objType, IdentifierList domain, LongList objIds, RetrieveInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received retrieve request: objType={0}, domain={1}, objIds={2}, interaction={3}",
				new Object[] { Dumper.objType(objType), Dumper.names(domain), objIds, interaction });
	}

	/**
	 * Implements query.
	 * @see org.ccsds.moims.mo.com.archive.provider.ArchiveHandler#query(java.lang.Boolean, org.ccsds.moims.mo.com.structures.ObjectType, org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList, org.ccsds.moims.mo.com.archive.structures.QueryFilterList, org.ccsds.moims.mo.com.archive.provider.QueryInteraction)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void query(Boolean returnBody, ObjectType objType, ArchiveQueryList arcQs,
			QueryFilterList queryFilters, QueryInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "CA.query(returnBody={0}, objType={1}, arcQuery={2}, filter={3}, interaction={4})",
				new Object[] { returnBody, Dumper.objType(objType), arcQs, queryFilters, interaction });
		Check.objType(objType);
		Check.queryInteract(interaction);
		boolean doRetBod = (null != returnBody) && returnBody.booleanValue();
		objStore.query(doRetBod, objType, arcQs, queryFilters, interaction);
		LOG.log(Level.INFO, "CA.query() response in interaction");
	}

	/** Implements counting.
	 * @see org.ccsds.moims.mo.com.archive.provider.ArchiveHandler#count(org.ccsds.moims.mo.com.structures.ObjectType, org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList, org.ccsds.moims.mo.com.archive.structures.QueryFilterList, org.ccsds.moims.mo.com.archive.provider.CountInteraction)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void count(ObjectType objType, ArchiveQueryList archiveQuery, QueryFilterList queryFilter,
			CountInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received count request: objType={0}, arcQuery={1}, filter={2}, interaction={3}",
				new Object[] { Dumper.objType(objType), archiveQuery, queryFilter, interaction });
	}

	/** Implements store.
	 * @see org.ccsds.moims.mo.com.archive.provider.ArchiveHandler#store(java.lang.Boolean, org.ccsds.moims.mo.com.structures.ObjectType, org.ccsds.moims.mo.mal.structures.IdentifierList, org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList, org.ccsds.moims.mo.mal.structures.ElementList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public LongList store(Boolean returnIds, ObjectType objType, IdentifierList domain,
			ArchiveDetailsList objDetails, ElementList objBodies, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "CA.store(returnIds={0}, objType={1}, domain={2}, details={3}, bodies={4})",
				new Object[] { returnIds, Dumper.objType(objType), Dumper.names(domain), Dumper.arcDets(objDetails), Dumper.els(objBodies) });
		Check.objType(objType);
		Check.objects(objDetails, objBodies);
		final boolean doRetIds = (null != returnIds) && returnIds.booleanValue();
		LongList ids = doRetIds ? new LongList() : null;
		objStore.addAll(objType, domain, objDetails, objBodies); 
		for (int i = 0; doRetIds && (i < objDetails.size()); ++i) {
			ArchiveDetails arcDetail = objDetails.get(i);
			ids.add(arcDetail.getInstId());
		}
		publishEv(objBodies);
		LOG.log(Level.INFO, "CA.store() response: ids={0}", ids);
		return ids;
	}

	/** Implements update.
	 * @see org.ccsds.moims.mo.com.archive.provider.ArchiveHandler#update(org.ccsds.moims.mo.com.structures.ObjectType, org.ccsds.moims.mo.mal.structures.IdentifierList, org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList, org.ccsds.moims.mo.mal.structures.ElementList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void update(ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received update request: objType={0}, domain={1}, details={2}, bodies={3}, interaction={4}",
				new Object[] { Dumper.objType(objType), Dumper.names(domain), objDetails, objBodies, interaction });
	}

	/**
	 * Implements removal.
	 * @see org.ccsds.moims.mo.com.archive.provider.ArchiveHandler#delete(org.ccsds.moims.mo.com.structures.ObjectType, org.ccsds.moims.mo.mal.structures.IdentifierList, org.ccsds.moims.mo.mal.structures.LongList, org.ccsds.moims.mo.mal.provider.MALInteraction)
	 */
	@Override
	public LongList delete(ObjectType objType, IdentifierList domain, LongList objIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received delete request: objType={0}, domain={1}, objIds={2}, interaction={3}",
				new Object[] { Dumper.objType(objType), Dumper.names(domain), objIds, interaction });
		return null;
	}
}
