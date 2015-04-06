package esa.mo.inttest.goce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerType;

/**
 * SIST - Station Schedule Increment File.
 */
public class StationScheduleIncrementFile extends CommonFile {

	private static final String SCH_NAME = "SIST";
	
	public ScheduleDefinitionDetails createDef(String name, String desc, ArgumentDefinitionDetailsList argDefs,
			ObjectTypeList eventTypes) {
		ScheduleDefinitionDetails schDef = new ScheduleDefinitionDetails();
		schDef.setName(new Identifier(name));
		schDef.setDescription(desc);
		schDef.setArgumentDefs(argDefs);
		schDef.setEventTypes(eventTypes);
		return schDef;
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
	
	public ScheduleInstanceDetails createInst(String name, String desc, AttributeValueList args,
			TriggerDetailsList triggers, ScheduleItemInstanceDetailsList items) {
		ScheduleInstanceDetails schInst = new ScheduleInstanceDetails();
		schInst.setName(new Identifier(name));
		schInst.setDescription(desc);
		schInst.setArgumentValues(args);
		schInst.setTimingConstraints(triggers);
		schInst.setScheduleItems(items);
		return schInst;
	}
	
	protected ArgumentDefinitionDetailsList createItemArgDefs() {
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(createArgDef("template", Attribute.STRING_TYPE_SHORT_FORM, null));
		return args;
	}
	
	protected AttributeValueList createItemArgVals(String template) {
		AttributeValueList args = new AttributeValueList();
		args.add(new AttributeValue(new Union(template)));
		return args;
	}
	
	protected TriggerDetailsList createItemTriggers(Time startTime, Time endTime) {
		TriggerDetailsList trigs = new TriggerDetailsList();
		trigs.add(new TriggerDetails(TriggerName.START, TriggerType.TIME, new TimeTrigger(startTime, true), null));
		trigs.add(new TriggerDetails(TriggerName.END, TriggerType.TIME, new TimeTrigger(endTime, false), null));
		return trigs;
	}
	
	protected Time parseTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.ddd.HH.mm.ss.SSS");
		Date d = sdf.parse(s);
		long l = d.getTime();
		return new Time(l);
	}
	
	protected Time parseDelta(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("'ST'HH.mm.ss");
		Date d = sdf.parse(s);
		long l = d.getTime();
		return new Time(l);
	}
	
	public ScheduleItemInstanceDetails createSchItem1() throws ParseException {
		ScheduleItemInstanceDetails item = createItemInst("346001", SCH_NAME, null, createItemArgDefs(),
				createItemArgVals("GOC_CONF"), createItemTriggers(
						parseTime("2008.346.00.31.02.000"), parseDelta("ST+00.00.00")));
		return item;
	}
	
	public ScheduleItemInstanceDetails createSchItem2() throws ParseException {
		ScheduleItemInstanceDetails item = createItemInst("346002", SCH_NAME, null, createItemArgDefs(),
				createItemArgVals("GOCDFTON"), createItemTriggers(
						parseTime("2008.346.00.36.02.000"), parseDelta("ST+00.00.00")));
		return item;
	}
	
	public ScheduleInstanceDetails createSchedule(ScheduleItemInstanceDetailsList items) {
		ScheduleInstanceDetails inst = createInst(SCH_NAME, "SIST example", null, null, items);
		return inst;
	}
}
