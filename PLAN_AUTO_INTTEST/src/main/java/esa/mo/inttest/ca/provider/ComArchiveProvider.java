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
	private IdentifierList domain = new IdentifierList();
	
	public ComArchiveProvider() {
		domain.add(new Identifier("desd"));
	}
	
	public void setDomain(IdentifierList domain) {
		this.domain = domain;
	}
	
	@Override
	public void retrieve(ObjectType objType, IdentifierList domain, LongList objIds, RetrieveInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received retrieve request: objType={0}, domain={1}, objIds={2}, interaction={3}",
				new Object[] { Dumper.objType(objType), Dumper.names(domain), objIds, interaction });
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void query(Boolean returnBody, ObjectType objType, ArchiveQueryList archiveQuery,
			QueryFilterList queryFilter, QueryInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received query request: returnBody={0}, objType={1}, arcQuery={2}, filter={3}, interaction={4}",
				new Object[] { returnBody, Dumper.objType(objType), archiveQuery, queryFilter, interaction });
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void count(ObjectType objType, ArchiveQueryList archiveQuery, QueryFilterList queryFilter,
			CountInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received count request: objType={0}, arcQuery={1}, filter={2}, interaction={3}",
				new Object[] { Dumper.objType(objType), archiveQuery, queryFilter, interaction });
	}

	@SuppressWarnings("rawtypes")
	@Override
	public LongList store(Boolean returnIds, ObjectType objType, IdentifierList domain,
			ArchiveDetailsList objDetails, ElementList objBodies, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received store request: returnIds={0}, objType={1}, domain={2}, details={3}, bodies={4}, interaction={5}",
				new Object[] { returnIds, Dumper.objType(objType), Dumper.names(domain), objDetails, objBodies, interaction });
		LongList ids = null;
		if (returnIds != null && returnIds.booleanValue()) {
			ids = new LongList();
			for (int i = 0; i < objDetails.size(); ++i) {
				ArchiveDetails arcDetail = objDetails.get(i);
				ids.add(arcDetail.getInstId()); // TODO store elements for retrieval - for now just reply id-s
			}
		}
		return ids;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void update(ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
			MALInteraction interaction) throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received update request: objType={0}, domain={1}, details={2}, bodies={3}, interaction={4}",
				new Object[] { Dumper.objType(objType), Dumper.names(domain), objDetails, objBodies, interaction });
	}

	@Override
	public LongList delete(ObjectType objType, IdentifierList domain, LongList objIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "received delete request: objType={0}, domain={1}, objIds={2}, interaction={3}",
				new Object[] { Dumper.objType(objType), Dumper.names(domain), objIds, interaction });
		return null;
	}

}
