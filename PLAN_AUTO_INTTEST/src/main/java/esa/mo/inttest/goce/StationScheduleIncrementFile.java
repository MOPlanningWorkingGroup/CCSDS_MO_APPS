package esa.mo.inttest.goce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.RelativeTime;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

import esa.mo.inttest.Util;
import esa.mo.inttest.sch.consumer.ScheduleConsumer;

/**
 * SIST - Station Schedule Increment File.
 */
public class StationScheduleIncrementFile extends CommonFile {

	private static final String[] START_TIMES = { "2008.346.00.31.02.000", "2008.346.00.36.02.000" };
	private static final String[] TEMPLATES = { "GOC_CONF", "GOCDFTON" };
	private static final String[] NAMES = { "SIST-346001", "SIST-346002" };
	
	protected ArgumentDefinitionDetailsList createArgDefs() {
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(createArgDef("TEMPLATE", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		return args;
	}
	
	/**
	 * Creates Schedule definition base.
	 * @return
	 */
	public ScheduleDefinitionDetails createSchDef() {
		ScheduleDefinitionDetails schDef = ScheduleConsumer.createDef("SIST schedule", null);
		schDef.setArgumentDefs(createArgDefs());
		return schDef;
	}
	
	protected TriggerDetailsList createTriggers(Time startTime, Time endTime) {
		TriggerDetailsList trigs = new TriggerDetailsList();
		trigs.add(new TriggerDetails(TriggerName.START, new TimeTrigger(startTime, null), null, null, null, null, null));
		trigs.add(new TriggerDetails(TriggerName.END, new TimeTrigger(null, new RelativeTime(endTime, true)), null, null, null, null, null));
		return trigs;
	}
	
	protected Time parseTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.DDD.HH.mm.ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d = sdf.parse(s);
		long l = d.getTime();
		return new Time(l);
	}
	
	protected Time parseDelta(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("'ST+'HH.mm.ss"); // parsing negative probably fails
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d = sdf.parse(s);
		long l = d.getTime();
		return new Time(l);
	}
	
	protected AttributeValueList createArgValues(String template) {
		AttributeValueList args = new AttributeValueList();
		args.add(new AttributeValue(new Union(template)));
		return args;
	}
	
	/**
	 * Creates Schedule based on definition.
	 * @param idx
	 * @return
	 * @throws ParseException
	 */
	public ScheduleInstanceDetails createSchInst(int idx) throws ParseException {
		TriggerDetailsList trigs = createTriggers(parseTime(START_TIMES[idx]), parseDelta("ST+00.00.00"));
		IdentifierList argNames = new IdentifierList();
		argNames.add(new Identifier("TEMPLATE"));
		AttributeValueList argValues = createArgValues(TEMPLATES[idx]);
		return ScheduleConsumer.createInst(NAMES[idx], null, argNames, argValues, null, trigs);
	}
	
	protected ScheduleItemInstanceDetails createItemInst(String itemName, String name, ObjectId delegate,
			ArgumentDefinitionDetailsList argDefs, AttributeValueList argVals, TriggerDetailsList triggers) {
		ScheduleItemInstanceDetails schItem = new ScheduleItemInstanceDetails();
		schItem.setScheduleInstName(new Identifier(name));
		schItem.setScheduleItemInstName(new Identifier(itemName));
		schItem.setDelegateItem(delegate);
		schItem.setArgumentTypes(argDefs);
		schItem.setArgumentValues(argVals);
		schItem.setTimingConstraints(triggers);
		return schItem;
	}
	
	/**
	 * Creates alternative Schedule without definition and with items.
	 * @param idx
	 * @return
	 * @throws ParseException
	 */
	public ScheduleInstanceDetails createSchInst2() throws ParseException {
		TriggerDetailsList trigs1 = createTriggers(parseTime(START_TIMES[0]), parseDelta("ST+00.00.00"));
		ArgumentDefinitionDetailsList argDefs = createArgDefs();
		AttributeValueList argValues1 = createArgValues(TEMPLATES[0]);
		
		ObjectId dummy = new ObjectId(Util.createObjType(new ScheduleItemInstanceDetails()), new ObjectKey(new IdentifierList(), 1L));
		
		ScheduleItemInstanceDetails item1 = createItemInst(NAMES[0], null, dummy, argDefs, argValues1, trigs1);
		
		TriggerDetailsList trigs2 = createTriggers(parseTime(START_TIMES[0]), parseDelta("ST+00.00.00"));
		AttributeValueList argValues2 = createArgValues(TEMPLATES[0]);
		ScheduleItemInstanceDetails item2 = createItemInst(NAMES[0], null, dummy, argDefs, argValues2, trigs2);
		
		ScheduleItemInstanceDetailsList schItems = new ScheduleItemInstanceDetailsList();
		schItems.add(item1);
		schItems.add(item2);
		
		return ScheduleConsumer.createInst("SIST-1", null, null, null, schItems, null);
	}
}
