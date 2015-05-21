package esa.mo.inttest.bepicolombo;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.EventTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.RelativeTime;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

public class CommandRequestFile {

	protected ArgumentDefinitionDetails createArgDef(String name, String desc, int iType, String unit, String repr,
			String radix, Attribute defVal) {
		byte bType = (byte)(iType & 0xff);
		return new ArgumentDefinitionDetails(new Identifier(name), desc, bType, unit, repr, radix, defVal);
	}
	
	protected ArgumentDefinitionDetails createArgDef(String name, String desc, int iType, String unit, String repr) {
		return createArgDef(name, desc, iType, unit, repr, null, null);
	}
	
	protected ArgumentDefinitionDetails createArgDef(String name, String desc, int iType, String repr) {
		return createArgDef(name, desc, iType, repr, null, null, null);
	}
	
	protected ArgumentDefinitionDetails createArgDef(String name, int iType, Attribute defVal) {
		return createArgDef(name, null, iType, null, null, null, defVal);
	}
	
	protected ArgumentDefinitionDetails createArgDef(String name, int iType) {
		return createArgDef(name, null, iType, null, null, null, null);
	}
	
	public TaskDefinitionDetails createPassTaskDef() {
		TaskDefinitionDetails def = PlanningRequestConsumer.createTaskDef("ZXC01060", "Definition of command ZXC01060");
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("passID", Attribute.ULONG_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("uniqueID", Attribute.IDENTIFIER_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("source", Attribute.STRING_TYPE_SHORT_FORM, new Union("SOC")));
		argDefs.add(createArgDef("destination", Attribute.STRING_TYPE_SHORT_FORM, new Union("MTL")));
		def.setArgumentDefs(argDefs);
		return def;
	}
	
	public TaskDefinitionDetails createExecTaskDef() {
		TaskDefinitionDetails def = PlanningRequestConsumer.createTaskDef("ZAC03340", "Definition of command ZAC03340");
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("uniqueID", Attribute.IDENTIFIER_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("source", Attribute.STRING_TYPE_SHORT_FORM, new Union("SOC")));
		argDefs.add(createArgDef("parameterCount", Attribute.USHORT_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("P123", Attribute.STRING_TYPE_SHORT_FORM));
		def.setArgumentDefs(argDefs);
		return def;
	}
	
	public TaskDefinitionDetails createMaesTaskDef() {
		TaskDefinitionDetails def = PlanningRequestConsumer.createTaskDef("MAESEvent", "Definition of event MAES");
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("uniqueID", Attribute.IDENTIFIER_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("parameterCount", Attribute.USHORT_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("Antenna", "G/S antenna mnemonic", Attribute.STRING_TYPE_SHORT_FORM, "Engineering"));
		argDefs.add(createArgDef("Elevation", "Elevation of horizon Mask", Attribute.FLOAT_TYPE_SHORT_FORM, "deg", "Engineering"));
		argDefs.add(createArgDef("RTLT", "Round Trip Light Time", Attribute.FLOAT_TYPE_SHORT_FORM, "sec", null, "Decimal", null));
		argDefs.add(createArgDef("X-Start", Attribute.TIME_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("Y-End_eventId", "Event Id of Y-End", Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("Y-End_eventCount", "Event count of Y-End", Attribute.ULONG_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("Y-End_delta", "Event delta of Y-End", Attribute.TIME_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("Y-End_deltaPositive", "Event delta sign of Y-End, since delta Time is absolute value",
				Attribute.BOOLEAN_TYPE_SHORT_FORM, null));
		def.setArgumentDefs(argDefs);
		return def;
	}
	
	public TaskDefinitionDetails createSeqTaskDef() {
		TaskDefinitionDetails def = PlanningRequestConsumer.createTaskDef("SQRA0030", "Definition of command sequence SQRA0030");
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("passID", Attribute.STRING_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("uniqueID", Attribute.IDENTIFIER_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("parameterCount", Attribute.USHORT_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("PZSR0001", "A long memory address", Attribute.STRING_TYPE_SHORT_FORM, "Hexadecimal"));
		argDefs.add(createArgDef("PFRE0004", null, Attribute.STRING_TYPE_SHORT_FORM, "Amps", "Raw", "Decimal", null));
		argDefs.add(createArgDef("profileCount", Attribute.USHORT_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("profile_type", "type of profile", Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("profile_timeOffset", "time offset of profile", Attribute.TIME_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("profile_timeOffsetPositive", "time offset sign of profile, since Time is absolute value",
				Attribute.BOOLEAN_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("profile_value", "value of profile", Attribute.STRING_TYPE_SHORT_FORM, null));
		def.setArgumentDefs(argDefs);
		return def;
	}
	
	public String getPrDefName() {
		return "CRF-PR-Def";
	}
	
	public PlanningRequestDefinitionDetails createPrDef() {
		PlanningRequestDefinitionDetails def = PlanningRequestConsumer.createPrDef(getPrDefName(), null);
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("type", Attribute.STRING_TYPE_SHORT_FORM, new Union("POR")));
		argDefs.add(createArgDef("formatVersion", Attribute.STRING_TYPE_SHORT_FORM, new Union("1")));
		argDefs.add(createArgDef("occurrenceCount", Attribute.USHORT_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("author", Attribute.STRING_TYPE_SHORT_FORM));
		def.setArgumentDefs(argDefs);
		return def;
	}
	
	public String getPrInstName() {
		return "CRF-PR-1";
	}
	
	protected void addTaskArg(TaskInstanceDetails inst, String name, Attribute val) {
		if (null == inst.getArgumentValues()) {
			inst.setArgumentValues(new ArgumentValueList());
		}
		inst.getArgumentValues().add(new ArgumentValue(new Identifier(name), val));
	}
	
	public TimingDetails createEventTrig(TriggerName name, Time t) {
		TimeTrigger tt = new TimeTrigger(t, null);
		return new TimingDetails(name, tt, null, null, null, null, null);
	}
	
	protected Time parseAbsTime(String t) throws ParseException {
		if (t.endsWith("Z")) {
			t = t.substring(0, t.length()-1);
		}
		String form = "yyyy-DDD'T'HH:mm:ss";
		int tp = t.indexOf(':');
		int dp = t.indexOf('.');
		if (-1 != dp && dp < tp) { // have dot before time
			form = "DDD.HH:mm:ss";
		}
		int ms = t.lastIndexOf('.');
		if (-1 != ms) { // have millis
			form += ".SSS";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(form);
		Date d = sdf.parse(t);
		return new Time(d.getTime());
	}
	
	public TaskInstanceDetails createPassTaskInst(Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails inst = PlanningRequestConsumer.createTaskInst(/*"ZXC01060"*/id, defId, "some useful comments"/*, getPrInstName()*/);
		TimingDetailsList tims = new TimingDetailsList();
		tims.add(createEventTrig(TriggerName.RELEASE, parseAbsTime("2009-300T12:00:00.000Z")));
		inst.setTimingConstraints(tims);
		addTaskArg(inst, "passID", new ULong(new BigInteger("23")));
		addTaskArg(inst, "uniqueID", new Identifier("DW00001"));
		return inst;
	}
	
	protected TimingDetails createEventTrig(TriggerName name, String id, String startCount, String endCount, RelativeTime delta) {
		EventTrigger et = new EventTrigger(new Identifier(id), null, null,
				new ULong(new BigInteger(startCount)), new ULong(new BigInteger(endCount)), null, delta, null);
		return new TimingDetails(name, null, et, null, null, null, null);
	}
	
	public TaskInstanceDetails createExecTaskInst(Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails inst = PlanningRequestConsumer.createTaskInst(/*"ZAC003340"*/id, defId, "some useful comments"/*, getPrInstName()*/);
		TimingDetailsList tims = new TimingDetailsList();
		tims.add(createEventTrig(TriggerName.START, "AOD", "1", "2", parseRelTime("12:33:44")));
		inst.setTimingConstraints(tims);
		addTaskArg(inst, "uniqueID", new Identifier("DW00001"));
		addTaskArg(inst, "parameterCount", new UShort(1));
		addTaskArg(inst, "P123", new Union("145"));
		return inst;
	}
	
	protected TimingDetails createEventTrig(TriggerName name, String id, String startCount, String endCount,
			RelativeTime delta, String repeat, Time separ, RelativeTime earliest, RelativeTime latest, int propFact) {
		EventTrigger et = new EventTrigger(new Identifier(id), null, null,
				new ULong(new BigInteger(startCount)), new ULong(new BigInteger(endCount)), null, delta, propFact);
		EventTrigger early = new EventTrigger(null, null, null, null, null, null, earliest, propFact);
		EventTrigger late = new EventTrigger(null, null, null, null, null, null, latest, propFact);
		return new TimingDetails(name, null, et, new ULong(new BigInteger(repeat)), separ, early, late);
	}

	public TaskInstanceDetails createMaesTaskInst(Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails inst = PlanningRequestConsumer.createTaskInst(/*"MAP3"*/id, defId, "mission specific comment"/*, getPrInstName()*/);
		TimingDetailsList tims = new TimingDetailsList();
		tims.add(createEventTrig(TriggerName.START, "MAP1", "1", "14", parseRelTime("01:00:00"), "9",
				parseRelTime("00:00:10").getRelativeTime(), parseRelTime("-10:59:00"), parseRelTime("001.23:59:59.000"), 2));
		inst.setTimingConstraints(tims);
		addTaskArg(inst, "uniqueID", new Identifier("JS0003"));
		addTaskArg(inst, "parameterCount", new UShort(5));
		addTaskArg(inst, "Antenna", new Union("MAD"));
		addTaskArg(inst, "Elevation", new Union(10.4f));
		addTaskArg(inst, "RTLT", new Union(1856f));
		addTaskArg(inst, "X-Start", parseAbsTime("2009-324T23:59:59.000Z"));
		addTaskArg(inst, "Y-End_eventId", new Union("VPER"));
		addTaskArg(inst, "Y-End_eventCount", new ULong(new BigInteger("4")));
		addTaskArg(inst, "Y-End_delta", parseAbsTime("324.23:59:59.000"));
		addTaskArg(inst, "Y-End_deltaPositive", new Union(true));
		return inst;
	}
	
	protected TimingDetails createEventTrig(TriggerName name, RelativeTime t) {
		TimeTrigger tt = new TimeTrigger(null, t);
		return new TimingDetails(name, tt, null, null, null, null, null);
	}
	
	public TaskInstanceDetails createSeqTaskInst(Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails inst = PlanningRequestConsumer.createTaskInst(/*"SQRA0030"*/id, defId, "mission specific comment"/*, getPrInstName()*/);
		TimingDetailsList tims = new TimingDetailsList();
		tims.add(createEventTrig(TriggerName.RELEASE, parseRelTime("10:00:00")));
		tims.add(createEventTrig(TriggerName.START, parseAbsTime("2009-301T18:43:22Z")));
		inst.setTimingConstraints(tims);
		addTaskArg(inst, "passID", new Union("MyPass123"));
		addTaskArg(inst, "uniqueID", new Identifier("JD-0001"));
		addTaskArg(inst, "parameterCount", new UShort(2));
		addTaskArg(inst, "PZSR0001", new Union("FFAA34FF55CC22FF"));
		addTaskArg(inst, "PFRE0004", new Union("1234"));
		addTaskArg(inst, "profileCount", new UShort(1));
		addTaskArg(inst, "profile_type", new Union("Data"));
		addTaskArg(inst, "profile_timeOffset", parseRelTime("-11:22:33").getRelativeTime());
		addTaskArg(inst, "profile_timeOffsetPositive", new Union(false));
		addTaskArg(inst, "profile_value", new Union("Seom vlaid vlaue"));
		return inst;
	}
	
	protected void addPrArg(PlanningRequestInstanceDetails inst, String name, Attribute val) {
		if (null == inst.getArgumentValues()) {
			inst.setArgumentValues(new ArgumentValueList());
		}
		inst.getArgumentValues().add(new ArgumentValue(new Identifier(name), val));
	}
	
	protected RelativeTime parseRelTime(String t) throws ParseException {
		boolean isPos = !t.startsWith("-");
		if (!isPos) {
			t = t.substring(1);
		}
		int tp = t.indexOf(':');
		int ms = t.lastIndexOf('.'); // have millis?
		String[] ds = t.substring(0, tp).split("\\."); // how many date parts?
		String form = "HH:mm:ss";
		if (-1 != ms) { // have millis
			form += ".SSS";
		}
		if (0 == ds.length) { // no date
		} else if (2 == ds.length) { // doy&time
			form = "DDD." + form;
		} else if (3 == ds.length) { // year&day&time
			form = "YYYY.DDD.";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(form);
		Date d = sdf.parse(t);
		Time time = new Time(d.getTime());
		return new RelativeTime(time, isPos);
	}
	
	protected TimingDetails createEventTrig(TriggerName name, String id, String evCount, RelativeTime delta, int propF) {
		EventTrigger et = new EventTrigger(new Identifier(id), null, null, null, null, new ULong(new BigInteger(evCount)), delta, propF);
		return new TimingDetails(name, null, et, null, null, null, null);
	}
	
	public PlanningRequestInstanceDetails createPrInst(Long id, Long defId) throws ParseException {
		PlanningRequestInstanceDetails inst = PlanningRequestConsumer.createPrInst(/*getPrInstName()*/id, defId, null);
		TimingDetailsList tims = new TimingDetailsList();
		tims.add(createEventTrig(TriggerName.VALIDITY_START, "AOS", "123", parseRelTime("-188.12:32:00.123"), 0));
		tims.add(createEventTrig(TriggerName.VALIDITY_END, "LOS", "1234", parseRelTime("12:32:00"), -2));
		inst.setTimingConstraints(tims);
		addPrArg(inst, "occurrenceCount", new UShort(12));
		return inst;
	}
}
