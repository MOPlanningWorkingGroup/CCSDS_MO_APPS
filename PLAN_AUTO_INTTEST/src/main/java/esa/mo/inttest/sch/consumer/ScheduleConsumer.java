package esa.mo.inttest.sch.consumer;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleAdapter;
import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleStub;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;

import esa.mo.inttest.Dumper;
import esa.mo.inttest.Util;

/**
 * Schedule consumer for testing.
 */
public class ScheduleConsumer extends ScheduleAdapter {

	private static final Logger LOG = Logger.getLogger(ScheduleConsumer.class.getName());
	
	private ScheduleStub stub;
	
	/**
	 * Ctor.
	 * @param stub
	 */
	public ScheduleConsumer(ScheduleStub stub) {
		this.stub = stub;
	}
	
	/**
	 * Returns stub for provider access.
	 * @return
	 */
	public ScheduleStub getStub() {
		return this.stub;
	}
	
	/**
	 * Implements notification callback.
	 * @see org.ccsds.moims.mo.automation.schedule.consumer.ScheduleAdapter#monitorSchedulesNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.structures.Identifier, org.ccsds.moims.mo.mal.structures.UpdateHeaderList, org.ccsds.moims.mo.com.structures.ObjectIdList, org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void monitorSchedulesNotifyReceived(MALMessageHeader msgHdr, Identifier id, UpdateHeaderList updHdrs,
			ObjectIdList objIds, ScheduleStatusDetailsList schStats, Map qosProps) {
		LOG.log(Level.INFO, "{4}.monitorSchedulesNotifyReceived(id={0}, List:updHeaders, List:objIds, List:schStatuses)\n  updHeaders[]={1}\n  objIds[]={2}\n  schStatuses[]={3}",
				new Object[] { id, Dumper.updHdrs(updHdrs), Dumper.objIds(objIds), Dumper.schStats(schStats),
				Dumper.fromBroker("SchProvider", msgHdr) });
	}
	
	/**
	 * Implements notification error callback.
	 * @see org.ccsds.moims.mo.automation.schedule.consumer.ScheduleAdapter#monitorSchedulesNotifyErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader, org.ccsds.moims.mo.mal.MALStandardError, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void monitorSchedulesNotifyErrorReceived(MALMessageHeader msgHdr, MALStandardError err, Map qosProps) {
		LOG.log(Level.INFO, "{1}.monitorSchedulesNotifyErrorReceived(error)\n  error={0}",
				new Object[] { err, Dumper.fromBroker("SchProvider", msgHdr) });
	}
	
	/**
	 * Creates schedule deifinition.
	 * @param name
	 * @param desc
	 * @param args
	 * @param etypes
	 * @return
	 */
	public static ScheduleDefinitionDetails createDef(String name, String desc) {
		ScheduleDefinitionDetails schDef = new ScheduleDefinitionDetails();
		schDef.setName(new Identifier(name));
		schDef.setDescription(desc);
		return schDef;
	}
	
	/**
	 * Adds argument definition to list.
	 * @param defs
	 * @param name
	 * @param attrType
	 * @param area
	 * @return
	 */
	public static ArgumentDefinitionDetailsList addArgDef(ArgumentDefinitionDetailsList defs, String name, Byte attrType,
			UShort area) {
		ArgumentDefinitionDetailsList list = (null != defs) ? defs : new ArgumentDefinitionDetailsList();
		list.add(new ArgumentDefinitionDetails(new Identifier(name), null, attrType, null, null, null, null));
		return list;
	}
	
	/**
	 * Adds objType to list.
	 * @param types
	 * @param e
	 * @return
	 */
	public static ObjectTypeList addObjType(ObjectTypeList types, Element e) {
		ObjectTypeList list = (null != types) ? types : new ObjectTypeList();
		list.add(Util.createObjType(e));
		return list;
	}
	
	/**
	 * Creates schedule instance.
	 * @param name
	 * @param desc
	 * @param argNames
	 * @param argVals
	 * @param items
	 * @param trigs
	 * @return
	 */
	public static ScheduleInstanceDetails createInst(String name, String comm, IdentifierList argNames,
			AttributeValueList argVals, ScheduleItemInstanceDetailsList items, TriggerDetailsList trigs) {
		ScheduleInstanceDetails inst = new ScheduleInstanceDetails();
		inst.setName(new Identifier(name));
		inst.setComment(comm);
		inst.setArgumentDefNames(argNames);
		inst.setArgumentValues(argVals);
		inst.setScheduleItems(items);
		inst.setTimingConstraints(trigs);
		return inst;
	}
	
	/**
	 * Adds argment name to list.
	 * @param names
	 * @param name
	 * @return
	 */
	public static IdentifierList addArgName(IdentifierList names, String name) {
		IdentifierList list = (null != names) ? names : new IdentifierList();
		list.add(new Identifier(name));
		return list;
	}
	
	/**
	 * Adds argument value to list.
	 * @param vals
	 * @param val
	 * @return
	 */
	public static AttributeValueList addArgValue(AttributeValueList vals, Attribute val) {
		AttributeValueList list = (null != vals) ? vals : new AttributeValueList();
		list.add(new AttributeValue(val));
		return list;
	}
	
	/**
	 * Adds schedule item to schedule.
	 * @param items
	 * @param name
	 * @param schName
	 * @param argTypes
	 * @param argVals
	 * @param trigs
	 * @param del
	 * @return
	 */
	public static ScheduleItemInstanceDetailsList addItem(ScheduleItemInstanceDetailsList items, String name, String schName,
			ArgumentDefinitionDetailsList argTypes, AttributeValueList argVals, TriggerDetailsList trigs, ObjectId del) {
		ScheduleItemInstanceDetailsList list = (null != items) ? items : new ScheduleItemInstanceDetailsList();
		list.add(new ScheduleItemInstanceDetails(new Identifier(name), new Identifier(schName), argTypes, argVals,
				trigs, del)); // trigs - mandatory, delegate - mandatory
		return list;
	}
	
	/**
	 * Creates ObjectId for given element and id.
	 * @param e
	 * @param domain
	 * @param id
	 * @return
	 */
	public static ObjectId createObjId(Element e, IdentifierList domain, Long id) {
		ObjectKey objKey = new ObjectKey(domain, id);
		return new ObjectId(Util.createObjType(e), objKey);
	}
}
