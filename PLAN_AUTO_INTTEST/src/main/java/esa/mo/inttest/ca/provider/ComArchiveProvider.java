package esa.mo.inttest.ca.provider;

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
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * COM Archive provider for testing. Implemented as little as necessary.
 */
public class ComArchiveProvider extends ArchiveInheritanceSkeleton {

	@Override
	public void retrieve(ObjectType objType, IdentifierList domain, LongList objInstIds, RetrieveInteraction interaction)
			throws MALInteractionException, MALException {
		// not implemented because not used
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void query(Boolean returnBody, ObjectType objType, ArchiveQueryList archiveQuery,
			QueryFilterList queryFilter, QueryInteraction interaction) throws MALInteractionException, MALException {
		// not implemented because not used
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void count(ObjectType objType, ArchiveQueryList archiveQuery, QueryFilterList queryFilter,
			CountInteraction interaction) throws MALInteractionException, MALException {
		// not implemented because not used
	}

	@SuppressWarnings("rawtypes")
	@Override
	public LongList store(Boolean returnObjInstIds, ObjectType objType, IdentifierList domain,
			ArchiveDetailsList objDetails, ElementList objBodies, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LongList ids = null;
		if (returnObjInstIds != null && returnObjInstIds.booleanValue()) {
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
		// not implemented because not used
	}

	@Override
	public LongList delete(ObjectType objType, IdentifierList domain, LongList objInstIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		// not implemented because not used
		return null;
	}

}
