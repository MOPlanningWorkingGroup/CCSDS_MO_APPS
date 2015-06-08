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
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.RelativeTime;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

import esa.mo.inttest.sch.consumer.ScheduleConsumer;

/**
 * SIST - Station Schedule Increment File.
 */
public class StationScheduleIncrementFile extends CommonFile {

	private static final String[] START_TIMES = { "2008.346.00.31.02.000", "2008.346.00.36.02.000" };
	private static final String[] TEMPLATES = { "GOC_CONF", "GOCDFTON" };
	
	/**
	 * Creates ArgumentDefinitions.
	 * @return
	 */
	protected ArgumentDefinitionDetailsList createArgDefs() {
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(createArgDef("TEMPLATE", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		return args;
	}
	
	/**
	 * Creates Schedule definition base. <SCHEDULE> element in file.
	 * @return
	 */
	public ScheduleDefinitionDetails createSchDef() {
		ScheduleDefinitionDetails schDef = ScheduleConsumer.createDef("SIST schedule", null);
		schDef.setArgumentDefs(createArgDefs());
		return schDef;
	}
	
	/**
	 * Creates Start and End Triggers.
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	protected TimingDetailsList createTriggers(Time startTime, Time endTime) {
		TimingDetailsList trigs = new TimingDetailsList();
		trigs.add(new TimingDetails(TriggerName.START, new TimeTrigger(startTime, null), null, null, null, null, null));
		trigs.add(new TimingDetails(TriggerName.END, new TimeTrigger(null, new RelativeTime(endTime, true)), null, null, null, null, null));
		return trigs;
	}
	
	/** Parses time-string to Time class.
	 * @see esa.mo.inttest.goce.CommonFile#parseTime(java.lang.String)
	 */
	protected Time parseTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.DDD.HH.mm.ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d = sdf.parse(s);
		long l = d.getTime();
		return new Time(l);
	}
	
	/**
	 * Parses relative time-string to Time class.
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	protected Time parseDelta(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("'ST+'HH.mm.ss"); // parsing negative probably fails
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d = sdf.parse(s);
		long l = d.getTime();
		return new Time(l);
	}
	
	/**
	 * Creates ArgumentValues.
	 * @param name
	 * @param val
	 * @return
	 */
	protected ArgumentValueList createArgValues(String name, String val) {
		ArgumentValueList args = new ArgumentValueList();
		args.add(new ArgumentValue(new Identifier(name), new Union(val)));
		return args;
	}
	
	/**
	 * Creates Schedule based on definition.
	 * @param idx
	 * @return
	 * @throws ParseException
	 */
	public ScheduleInstanceDetails createSchInst(int idx, Long id, Long defId) throws ParseException {
		TimingDetailsList tims = createTriggers(parseTime(START_TIMES[idx]), parseDelta("ST+00.00.00"));
		ArgumentValueList argValues = createArgValues("TEMPLATE", TEMPLATES[idx]);
		return ScheduleConsumer.createInst(id, defId, null, argValues, null, tims);
	}
	
	/**
	 * Creates ScheduleItem with args and triggers. Alternative mapping.
	 * @param id
	 * @param schId
	 * @param delegate
	 * @param argVals
	 * @param timings
	 * @return
	 */
	protected ScheduleItemInstanceDetails createItemInst(Long id, Long schId, ObjectId delegate,
			ArgumentValueList argVals, TimingDetailsList timings) {
		ScheduleItemInstanceDetails schItem = new ScheduleItemInstanceDetails();
		schItem.setId(id);
		schItem.setSchInstId(schId);
		schItem.setDelegateItem(delegate);
		schItem.setArgumentValues(argVals);
		schItem.setTimingConstraints(timings);
		return schItem;
	}
	
	/**
	 * Creates alternative Schedule without definition and with items.
	 * @param idx
	 * @return
	 * @throws ParseException
	 */
	public ScheduleInstanceDetails createSchInst2(Long id, Long defId, Long item1Id, Long item2Id) throws ParseException {
		TimingDetailsList tims1 = createTriggers(parseTime(START_TIMES[0]), parseDelta("ST+00.00.00"));
		ArgumentValueList argValues1 = createArgValues("TEMPLATE", TEMPLATES[0]);
		
		ScheduleItemInstanceDetails item1 = createItemInst(/*NAMES[0]*/item1Id, id, null, argValues1, tims1);
		
		TimingDetailsList tims2 = createTriggers(parseTime(START_TIMES[0]), parseDelta("ST+00.00.00"));
		ArgumentValueList argValues2 = createArgValues("TEMPLATE", TEMPLATES[0]);
		
		ScheduleItemInstanceDetails item2 = createItemInst(/*NAMES[0]*/item2Id, id, null, argValues2, tims2);
		
		ScheduleItemInstanceDetailsList schItems = new ScheduleItemInstanceDetailsList();
		schItems.add(item1);
		schItems.add(item2);
		
		return ScheduleConsumer.createInst(/*"SIST-1"*/id, defId, null, null, schItems, null);
	}
}
