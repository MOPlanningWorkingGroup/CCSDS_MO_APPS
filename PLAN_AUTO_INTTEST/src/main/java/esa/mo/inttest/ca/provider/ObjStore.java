/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.ca.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.provider.QueryInteraction;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.archive.structures.QueryFilterList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

/**
 * COM objects storage for testing.
 */
public class ObjStore {

	/**
	 * Mapping object to object Id. Plus ElementList factory for that Element.
	 */
	public static final class ObjPairs {
		protected Map<ArchiveDetails, Element> idMap = new HashMap<ArchiveDetails, Element>();
		@SuppressWarnings("rawtypes")
		protected ElementList factory = null;
	}
	
	/**
	 * Mapping objects to domain.
	 */
	public static final class TypeDomains {
		protected Map<IdentifierList, ObjPairs> domMap = new HashMap<IdentifierList, ObjPairs>();
	}
	
	private static final Logger LOG = null;//disabled for now: Logger.getLogger(ObjStore.class.getName());
	
	private Map<ObjectType, TypeDomains> typeMap = new HashMap<ObjectType, TypeDomains>();
	
	private void log(Level l, String msg, Object arg) {
		if (null != LOG) {
			LOG.log(l, msg, arg);
		}
	}
	
	/**
	 * Adds all objects to storage.
	 * @param ot
	 * @param dom
	 * @param objInfo
	 * @param objs
	 */
	@SuppressWarnings("rawtypes")
	public void addAll(ObjectType ot, IdentifierList dom, ArchiveDetailsList objInfo, ElementList objs) {
		log(Level.INFO, "objType={0}, domain={1}, objInfo={2}, objects={3}", new Object[] { ot, dom, objInfo, objs });
		TypeDomains d = typeMap.get(ot);
		if (null == d) {
			log(Level.INFO, "new object type: {0}", ot);
			d = new TypeDomains();
			typeMap.put(ot, d);
		}
		ObjPairs p = d.domMap.get(dom);
		if (null == p) {
			log(Level.INFO, "new domain: {0}", dom);
			p = new ObjPairs();
			d.domMap.put(dom, p);
		}
		for (int i = 0; i < objInfo.size(); ++i) {
			ArchiveDetails arcDet = objInfo.get(i);
			log(Level.INFO, "new object id: {0}", arcDet.getInstId());
			Element el = (Element)objs.get(i);
			p.idMap.put(arcDet, el);
		}
		if (null == p.factory) {
			p.factory = (ElementList)objs.createElement();
		}
	}
	
	/**
	 * Finds objects.
	 * @param doRetBod
	 * @param ot
	 * @param arcQs
	 * @param queryFilters
	 * @param qa
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void query(boolean doRetBod, ObjectType ot, ArchiveQueryList arcQs, QueryFilterList queryFilters,
			QueryInteraction qa) throws MALException, MALInteractionException {
		log(Level.INFO, "doReturnBodies={0}, objType={1}, arcQs={2}, queryFilters={3}, qInt={4}",
				new Object[] { doRetBod, ot, arcQs, queryFilters, qa });
		qa.sendAcknowledgement();
		TypeDomains d = typeMap.get(ot);
		if (null != d) {
			log(Level.INFO, "found {0} domains of objType {1}", new Object[] { d.domMap.size(), ot });
			Iterator<Map.Entry<IdentifierList, ObjPairs>> it = d.domMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<IdentifierList, ObjPairs> e = it.next();
				ArchiveDetailsList adl = new ArchiveDetailsList();
				adl.addAll(e.getValue().idMap.keySet());
				ElementList els = (ElementList)e.getValue().factory.createElement();
				els.addAll(e.getValue().idMap.values());
				log(Level.INFO, "returning {0} objects", e.getValue().idMap.size());
				qa.sendUpdate(ot, e.getKey(), adl, els);
			}
		} else {
			log(Level.INFO, "found no domains for type {0}", ot);
		}
		qa.sendResponse(null, null, null, null);
	}
}
