package esa.mo.inttest.ca.provider;

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
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

import esa.mo.inttest.Dumper;

/**
 * COM Archive provider for testing. Implemented as little as necessary.
 */
public class ComArchiveProvider extends ArchiveInheritanceSkeleton {

	private static final Logger LOG = Logger.getLogger(ComArchiveProvider.class.getName());
	
	private ObjStore objStore = new ObjStore();
	private IdentifierList domain = new IdentifierList();
	
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
