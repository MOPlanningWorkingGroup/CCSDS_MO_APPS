/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.ca.provider;

import org.ccsds.moims.mo.com.archive.provider.QueryInteraction;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.ElementList;

/**
 * Verification and validation methods for Com Archive.
 */
public class Check {

	/**
	 * Hidden ctor.
	 */
	private Check() {
	}
	
	/**
	 * Verify that ObjectType is not null.
	 * @param ot
	 * @throws MALException
	 */
	public static void objType(ObjectType ot) throws MALException {
		if (null == ot) {
			throw new MALException("object type is null");
		}
	}
	
	/**
	 * Verify that both lists are not null and have equal number of elements.
	 * @param objDetails
	 * @param objBodies
	 * @throws MALException
	 */
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
	
	/**
	 * Verify that QueryInteraction is not null.
	 * @param qa
	 * @throws MALException
	 */
	public static void queryInteract(QueryInteraction qa) throws MALException {
		if (null == qa) {
			throw new MALException("query interaction is null");
		}
	}
}
