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

public class ObjStore {

	public static class ObjPairs {
		public Map<ArchiveDetails, Element> idMap = new HashMap<ArchiveDetails, Element>();
		@SuppressWarnings("rawtypes")
		public ElementList factory = null;
	}
	
	public static class TypeDomains {
		public Map<IdentifierList, ObjPairs> domMap = new HashMap<IdentifierList, ObjPairs>();
	}
	
	private static Logger LOG = Logger.getLogger(ObjStore.class.getName());
	
	private Map<ObjectType, TypeDomains> typeMap = new HashMap<ObjectType, TypeDomains>();
	
	@SuppressWarnings("rawtypes")
	public void addAll(ObjectType ot, IdentifierList dom, ArchiveDetailsList objInfo, ElementList objs) {
		LOG.log(Level.INFO, "objType={0}, domain={1}, objInfo={2}, objects={3}", new Object[] { ot, dom, objInfo, objs });
		TypeDomains d = typeMap.get(ot);
		if (null == d) {
			LOG.log(Level.INFO, "new object type: {0}", ot);
			d = new TypeDomains();
			typeMap.put(ot, d);
		}
		ObjPairs p = d.domMap.get(dom);
		if (null == p) {
			LOG.log(Level.INFO, "new domain: {0}", dom);
			p = new ObjPairs();
			d.domMap.put(dom, p);
		}
		for (int i = 0; i < objInfo.size(); ++i) {
			ArchiveDetails arcDet = objInfo.get(i);
			LOG.log(Level.INFO, "new object id: {0}", arcDet.getInstId());
			Element el = (Element)objs.get(i);
			p.idMap.put(arcDet, el);
		}
		if (null == p.factory) {
			p.factory = (ElementList)objs.createElement();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void query(boolean doRetBod, ObjectType ot, ArchiveQueryList arcQs, QueryFilterList queryFilters,
			QueryInteraction qa) throws MALException, MALInteractionException {
		LOG.log(Level.INFO, "doReturnBodies={0}, objType={1}, arcQs={2}, queryFilters={3}, qInt={4}",
				new Object[] { doRetBod, ot, arcQs, queryFilters, qa });
		qa.sendAcknowledgement();
		TypeDomains d = typeMap.get(ot);
		if (null != d) {
			LOG.log(Level.INFO, "found {0} domains of objType {1}", new Object[] { d.domMap.size(), ot });
			Iterator<Map.Entry<IdentifierList, ObjPairs>> it = d.domMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<IdentifierList, ObjPairs> e = it.next();
				ArchiveDetailsList adl = new ArchiveDetailsList();
				adl.addAll(e.getValue().idMap.keySet());
				ElementList el = (ElementList)e.getValue().factory.createElement();
				el.addAll(e.getValue().idMap.values());
				LOG.log(Level.INFO, "returning {0} objects", e.getValue().idMap.size());
				qa.sendUpdate(ot, e.getKey(), adl, el);
			}
		} else {
			LOG.log(Level.INFO, "found no domains for type {0}", ot);
		}
		qa.sendResponse(null, null, null, null);
	}
}
