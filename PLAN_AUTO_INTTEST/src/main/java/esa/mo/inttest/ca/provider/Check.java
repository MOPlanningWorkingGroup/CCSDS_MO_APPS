package esa.mo.inttest.ca.provider;

import org.ccsds.moims.mo.com.archive.provider.QueryInteraction;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.ElementList;

public class Check {

	/**
	 * Hidden ctor.
	 */
	private Check() {
	}
	
	public static void objType(ObjectType ot) throws MALException {
		if (null == ot) {
			throw new MALException("object type is null");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void objects(ArchiveDetailsList objDetails, ElementList objBodies) throws MALException {
		if (null == objDetails) {
			throw new MALException("object details list is null");
		}
		if (objDetails.isEmpty()) {
			throw new MALException("object details list is empty");
		}
		if (null == objBodies) {
			throw new MALException("object bodies list is null");
		}
		if (objDetails.size() != objBodies.size()) {
			throw new MALException("object details count does not match object bodies count");
		}
	}
	
	public static void queryInteract(QueryInteraction qa) throws MALException {
		if (null == qa) {
			throw new MALException("query interaction is null");
		}
	}
}
