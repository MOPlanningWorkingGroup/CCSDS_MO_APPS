/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest;

import java.util.concurrent.Callable;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.InstanceState;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

public class Util {

	/**
	 * Hidden ctor.
	 */
	private Util() {
	}
	
	/**
	 * Creates subscription with given id and sub-domain.
	 * @param subId
	 * @return
	 */
	public static Subscription createSub(String subId, IdentifierList subDom) {
		EntityKeyList entKeys = new EntityKeyList();
		entKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		EntityRequestList entReqs = new EntityRequestList();
		entReqs.add(new EntityRequest(subDom, true, true, true, false, entKeys));
		return new Subscription(new Identifier(subId), entReqs);
	}
	
	/**
	 * Creates subscription with given id.
	 * @param subId
	 * @return
	 */
	public static Subscription createSub(String subId) {
		return createSub(subId, null);
	}
	
	/**
	 * Finds given status from list.
	 * @param srl
	 * @param is
	 * @return
	 */
	public static StatusRecord findStatus(StatusRecordList srl, InstanceState is) {
		StatusRecord sr = null;
		if (null != srl) {
			for (int i = 0; (null == sr) && (i < srl.size()); ++i) {
				StatusRecord r = srl.get(i);
				if ((null != r) && (is == r.getState())) {
					sr = r;
				}
			}
		}
		return sr;
	}
	
	/**
	 * Returns current system Time.
	 * @return
	 */
	public static Time currentTime() {
		return new Time(System.currentTimeMillis());
	}
	
	/**
	 * Returns current system FineTime.
	 * @return
	 */
	public static FineTime currentFineTime() {
		return new FineTime(System.currentTimeMillis());
	}
	
	/**
	 * Add or update existing (PR) state in list.
	 * @param stat
	 * @param is
	 * @param t
	 * @param c
	 * @return
	 */
	public static StatusRecord addOrUpdateStatus(PlanningRequestStatusDetails stat, InstanceState is, Time t, String c) {
		StatusRecord sr = (null != stat.getStatus()) ? findStatus(stat.getStatus(), is) : null;
		if (null == stat.getStatus()) {
			stat.setStatus(new StatusRecordList());
		}
		if (null == sr) {
			sr = new StatusRecord(is, t, c);
			stat.getStatus().add(sr);
		} else {
			sr.setTimeStamp(t);
			sr.setComment(c);
		}
		return sr;
	}
	
	/**
	 * Add or update existing (Task) state in list.
	 * @param stat
	 * @param is
	 * @param t
	 * @param c
	 * @return
	 */
	public static StatusRecord addOrUpdateStatus(TaskStatusDetails stat, InstanceState is, Time t, String c) {
		StatusRecord sr = (null != stat.getStatus()) ? findStatus(stat.getStatus(), is) : null;
		if (null == stat.getStatus()) {
			stat.setStatus(new StatusRecordList());
		}
		if (null == sr) {
			sr = new StatusRecord(is, t, c);
			stat.getStatus().add(sr);
		} else {
			sr.setTimeStamp(t);
			sr.setComment(c);
		}
		return sr;
	}
	
	/**
	 * Add or update existing (Schedule) state in list.
	 * @param stat
	 * @param is
	 * @param t
	 * @param c
	 * @return
	 */
	public static StatusRecord addOrUpdateStatus(ScheduleStatusDetails stat, InstanceState is, Time t, String c) {
		StatusRecord sr = (null != stat.getStatus()) ? findStatus(stat.getStatus(), is) : null;
		if (null == stat.getStatus()) {
			stat.setStatus(new StatusRecordList());
		}
		if (null == sr) {
			sr = new StatusRecord(is, t, c);
			stat.getStatus().add(sr);
		} else {
			sr.setTimeStamp(t);
			sr.setComment(c);
		}
		return sr;
	}
	
	/**
	 * Add or update existing (ScheduleItem) state in list.
	 * @param stat
	 * @param is
	 * @param t
	 * @param c
	 * @return
	 */
	public static StatusRecord addOrUpdateStatus(ScheduleItemStatusDetails stat, InstanceState is, Time t, String c) {
		StatusRecord sr = (null != stat.getStatus()) ? findStatus(stat.getStatus(), is) : null;
		if (null == stat.getStatus()) {
			stat.setStatus(new StatusRecordList());
		}
		if (null == sr) {
			sr = new StatusRecord(is, t, c);
			stat.getStatus().add(sr);
		} else {
			sr.setTimeStamp(t);
			sr.setComment(c);
		}
		return sr;
	}
	
	/**
	 * Converts Attribute int-type to ArgDef byte-type.
	 * @param type
	 * @return
	 */
	public static byte attrType(int type) {
		return (byte)(type & 0xff);
	}
	
	/**
	 * Create ObjectType instance from given Element.
	 * @param e
	 * @return
	 */
	public static ObjectType createObjType(Element e) {
		return new ObjectType(e.getAreaNumber(), e.getServiceNumber(), e.getAreaVersion(),
				new UShort(e.getTypeShortForm()));
	}
	
	/**
	 * Wait for async work to finish. Return when condition is true or given time has passed.
	 * @param o
	 * @param ms
	 * @param c
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public static void waitFor(Object o, long ms, Callable<Boolean> c) throws InterruptedException, Exception {
		synchronized (o) {
			final long before = System.currentTimeMillis();
			final long step = ms / 10;
			long d = ms;
			while (!c.call() && (0 < d)) {
				o.wait(0 < step ? step : 1);
				d = ms - (System.currentTimeMillis() - before);
			}
		}
	}
	
	/**
	 * Creates UpdateHeader structure for publishing.
	 * @param ut
	 * @param uri
	 * @return
	 */
	public static UpdateHeader createUpdateHeader(UpdateType ut, URI uri) {
		UpdateHeader uh = new UpdateHeader();
		uh.setKey(new EntityKey(new Identifier("*"),  0L, 0L, 0L));
		uh.setSourceURI(uri);
		uh.setTimestamp(currentTime());
		uh.setUpdateType(ut);
		return uh;
	}
	
	/**
	 * Find ArgumentDefinition.
	 * @param name
	 * @param args
	 * @return
	 */
	public static ArgumentDefinitionDetails findArgDef(Identifier name, ArgumentDefinitionDetailsList args) {
		ArgumentDefinitionDetails def = null;
		for (int i = 0; (null == def) && (null != args) && (i < args.size()); ++i) {
			ArgumentDefinitionDetails argDef = args.get(i);
			if (name.equals(argDef.getName())) {
				def = argDef;
			}
		}
		return def;
	}

	/**
	 * Find ArgumentValue.
	 * @param name
	 * @param args
	 * @return
	 */
	public static ArgumentValue findArg(Identifier name, ArgumentValueList args) {
		ArgumentValue val = null;
		for (int i = 0; (null == val) && (null != args) && (i < args.size()); ++i) {
			ArgumentValue argVal = args.get(i);
			if (name.equals(argVal.getArgDefName())) {
				val = argVal;
			}
		}
		return val;
	}

	/**
	 * Find Timing.
	 * @param tn
	 * @param tdl
	 * @return
	 */
	public static TimingDetails findTiming(TriggerName tn, TimingDetailsList tdl) {
		TimingDetails td = null;
		for (int i = 0; (null != tdl) && (null == td) && (i < tdl.size()); ++i) {
			TimingDetails td2 = tdl.get(i);
			if (tn.equals(td2.getTriggerName())) {
				td = td2;
			}
		}
		return td;
	}
	
	/**
	 * Find ScheduleItem.
	 * @param id
	 * @param sil
	 * @return
	 */
	public static ScheduleItemInstanceDetails findItem(Long id, ScheduleItemInstanceDetailsList sil) {
		ScheduleItemInstanceDetails it = null;
		for (int i = 0; (null == it) && (null != sil) && (i < sil.size()); ++i) {
			ScheduleItemInstanceDetails it2 = sil.get(i);
			if (id.equals(it2.getId())) {
				it = it2;
			}
		}
		return it;
	}
}
