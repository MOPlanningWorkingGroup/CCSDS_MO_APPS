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
 * Station schedule increment file.
 */
public class StationScheduleIncrementFile /*extends CommonFile*/ {

	private static final String schName = "SIST";
	
	public StationScheduleIncrementFile() {
	}
	
	public ScheduleDefinitionDetails createDef(String name, String desc, ArgumentDefinitionDetailsList args,
			ObjectTypeList eventTypes) {
		ScheduleDefinitionDetails schDef = new ScheduleDefinitionDetails();
		schDef.setName(new Identifier(name));
		schDef.setDescription(desc);
		schDef.setArguments(args);
		schDef.setEventTypes(eventTypes);
		return schDef;
	}
	
	protected ScheduleItemInstanceDetails createItemInst(String itemName, String name, ObjectId delegate,
			AttributeValueList args, TriggerDetails trigger) {
		ScheduleItemInstanceDetails schItem = new ScheduleItemInstanceDetails();
		schItem.setScheduleInstName(new Identifier(name));
		schItem.setScheduleItemInstName(new Identifier(itemName));
		schItem.setDelegateItem(delegate);
		schItem.setArgumentValues(args);
		schItem.setTimingConstraints(trigger);
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
	
	protected AttributeValueList createItemArgs(String template) {
		AttributeValueList args = new AttributeValueList();
		args.add(new AttributeValue(new Union(template)));
		return args;
	}
	
	protected TriggerDetails createItemTriggers(Time time) {
		TriggerDetails trig = new TriggerDetails();
		trig.setTriggerName(TriggerName.START);
		trig.setTriggerType(TriggerType.TIME);
		trig.setTimeTrigger(new TimeTrigger(time, true));
		return trig;
	}
	
	protected Time parseTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.ddd.HH.mm.ss.SSS");
		Date d = sdf.parse(s);
		long l = d.getTime();
		Time t = new Time(l);
		return t;
	}
	
	public ScheduleItemInstanceDetails createSchItem1() throws ParseException {
		ScheduleItemInstanceDetails item = createItemInst("346001", schName, null, createItemArgs("GOC_CONF"),
				createItemTriggers(parseTime("2008.346.00.31.02.000"))); // FIXME item has only one trigger
		return item;
	}

	public ScheduleItemInstanceDetails createSchItem2() throws ParseException {
		ScheduleItemInstanceDetails item = createItemInst("346002", schName, null, createItemArgs("GOCDFTON"),
				createItemTriggers(parseTime("2008.346.00.36.02.000"))); // FIXME item has only one trigger
		return item;
	}
	
	public ScheduleInstanceDetails createSchedule(ScheduleItemInstanceDetailsList items) {
		ScheduleInstanceDetails inst = createInst(schName, "SIST example", null, null, items);
		return inst;
	}
}
