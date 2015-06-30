/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
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

	/**
	 * Creates ArgumentDefinition.
	 * @param name
	 * @param desc
	 * @param iType
	 * @param unit
	 * @param repr
	 * @param radix
	 * @param defVal
	 * @return
	 */
	protected ArgumentDefinitionDetails createArgDef(String name, String desc, int iType, String unit, String repr,
			String radix, Attribute defVal) {
		byte bType = (byte)(iType & 0xff);
		return new ArgumentDefinitionDetails(new Identifier(name), desc, bType, unit, repr, radix, defVal);
	}
	
	/**
	 * Create ArgDef variation.
	 * @param name
	 * @param desc
	 * @param iType
	 * @param unit
	 * @param repr
	 * @return
	 */
	protected ArgumentDefinitionDetails createArgDef(String name, String desc, int iType, String unit, String repr) {
		return createArgDef(name, desc, iType, unit, repr, null, null);
	}
	
	/**
	 * Create ArgDef variation.
	 * @param name
	 * @param desc
	 * @param iType
	 * @param repr
	 * @return
	 */
	protected ArgumentDefinitionDetails createArgDef(String name, String desc, int iType, String repr) {
		return createArgDef(name, desc, iType, repr, null, null, null);
	}
	
	/**
	 * Create ArgDef variation.
	 * @param name
	 * @param iType
	 * @param defVal
	 * @return
	 */
	protected ArgumentDefinitionDetails createArgDef(String name, int iType, Attribute defVal) {
		return createArgDef(name, null, iType, null, null, null, defVal);
	}
	
	/**
	 * Create ArgDef variation.
	 * @param name
	 * @param iType
	 * @return
	 */
	protected ArgumentDefinitionDetails createArgDef(String name, int iType) {
		return createArgDef(name, null, iType, null, null, null, null);
	}
	
	/**
	 * Creates Task definition for command ZXC01060. <command> element in XML.
	 * @return
	 */
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
	
	/**
	 * Creates Task definition for command ZAC03340. <command> element in XML.
	 * @return
	 */
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
	
	/**
	 * Creates Task definition for sequence SQRA0030. <sequence> element in XML.
	 * @return
	 */
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
	
	/**
	 * Return pr definition name.
	 * @return
	 */
	public String getPrDefName() {
		return "CRF-PR-Def";
	}
	
	/**
	 * Creates PR definition. <commandRequests> element in XML.
	 * @return
	 */
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
	
	/**
	 * Returns PR instance name.
	 * @return
	 */
	public String getPrInstName() {
		return "CRF-PR-1";
	}
	
	/**
	 * Adds argument to Task.
	 * @param inst
	 * @param name
	 * @param val
	 */
	protected void addTaskArg(TaskInstanceDetails inst, String name, Attribute val) {
		if (null == inst.getArgumentValues()) {
			inst.setArgumentValues(new ArgumentValueList());
		}
		inst.getArgumentValues().add(new ArgumentValue(new Identifier(name), val));
	}
	
	/**
	 * Create Event Trigger.
	 * @param name
	 * @param t
	 * @return
	 */
	public TimingDetails createEventTrig(TriggerName name, Time t) {
		TimeTrigger tt = new TimeTrigger(t, null);
		return new TimingDetails(name, tt, null, null, null, null, null);
	}
	
	/**
	 * Parses absolute time-string to Time class.
	 * @param t
	 * @return
	 * @throws ParseException
	 */
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
	
	/**
	 * Creates Task instance for command ZXC01060. <command> element in XML.
	 * @param id
	 * @param defId
	 * @return
	 * @throws ParseException
	 */
	public TaskInstanceDetails createPassTaskInst(Long id, Long defId) throws ParseException {
		TaskInstanceDetails inst = PlanningRequestConsumer.createTaskInst(id, defId, "some useful comments");
		TimingDetailsList tims = new TimingDetailsList();
		tims.add(createEventTrig(TriggerName.RELEASE, parseAbsTime("2009-300T12:00:00.000Z")));
		inst.setTimingConstraints(tims);
		addTaskArg(inst, "passID", new ULong(new BigInteger("23")));
		addTaskArg(inst, "uniqueID", new Identifier("DW00001"));
		return inst;
	}
	
	/**
	 * Creates Event Trigger.
	 * @param name
	 * @param id
	 * @param startCount
	 * @param endCount
	 * @param delta
	 * @return
	 */
	protected TimingDetails createEventTrig(TriggerName name, String id, String startCount, String endCount, RelativeTime delta) {
		EventTrigger et = new EventTrigger(new Identifier(id), null, null,
				new ULong(new BigInteger(startCount)), new ULong(new BigInteger(endCount)), null, delta, null);
		return new TimingDetails(name, null, et, null, null, null, null);
	}
	
	/**
	 * Creates Task instance for command ZAC003340. <command> element in XML.
	 * @param id
	 * @param defId
	 * @return
	 * @throws ParseException
	 */
	public TaskInstanceDetails createExecTaskInst(Long id, Long defId) throws ParseException {
		TaskInstanceDetails inst = PlanningRequestConsumer.createTaskInst(id, defId, "some useful comments");
		TimingDetailsList tims = new TimingDetailsList();
		tims.add(createEventTrig(TriggerName.START, "AOD", "1", "2", parseRelTime("12:33:44")));
		inst.setTimingConstraints(tims);
		addTaskArg(inst, "uniqueID", new Identifier("DW00001"));
		addTaskArg(inst, "parameterCount", new UShort(1));
		addTaskArg(inst, "P123", new Union("145"));
		return inst;
	}
	
	/**
	 * Creates Event Trigger.
	 * @param name
	 * @param t
	 * @return
	 */
	protected TimingDetails createEventTrig(TriggerName name, RelativeTime t) {
		TimeTrigger tt = new TimeTrigger(null, t);
		return new TimingDetails(name, tt, null, null, null, null, null);
	}
	
	/**
	 * Creates Task instance for sequence SQRA0030. <sequence> element in XML.
	 * @param id
	 * @param defId
	 * @return
	 * @throws ParseException
	 */
	public TaskInstanceDetails createSeqTaskInst(Long id, Long defId) throws ParseException {
		TaskInstanceDetails inst = PlanningRequestConsumer.createTaskInst(/*"SQRA0030"*/id, defId, "mission specific comment");
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
	
	/**
	 * Adds arg to PR.
	 * @param inst
	 * @param name
	 * @param val
	 */
	protected void addPrArg(PlanningRequestInstanceDetails inst, String name, Attribute val) {
		if (null == inst.getArgumentValues()) {
			inst.setArgumentValues(new ArgumentValueList());
		}
		inst.getArgumentValues().add(new ArgumentValue(new Identifier(name), val));
	}
	
	/**
	 * Parses relative time-string to RelativeTime class.
	 * @param t
	 * @return
	 * @throws ParseException
	 */
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
	
	/**
	 * Creates Event Trigger.
	 * @param name
	 * @param id
	 * @param evCount
	 * @param delta
	 * @param propF
	 * @return
	 */
	protected TimingDetails createEventTrig(TriggerName name, String id, String evCount, RelativeTime delta, int propF) {
		EventTrigger et = new EventTrigger(new Identifier(id), null, null, null, null, new ULong(new BigInteger(evCount)), delta, propF);
		return new TimingDetails(name, null, et, null, null, null, null);
	}
	
	/**
	 * Creates PR instance. <commandRequests> element in XML.
	 * @param id
	 * @param defId
	 * @return
	 * @throws ParseException
	 */
	public PlanningRequestInstanceDetails createPrInst(Long id, Long defId) throws ParseException {
		PlanningRequestInstanceDetails inst = PlanningRequestConsumer.createPrInst(id, defId, null);
		TimingDetailsList tims = new TimingDetailsList();
		tims.add(createEventTrig(TriggerName.VALIDITY_START, "AOS", "123", parseRelTime("-188.12:32:00.123"), 0));
		tims.add(createEventTrig(TriggerName.VALIDITY_END, "LOS", "1234", parseRelTime("12:32:00"), -2));
		inst.setTimingConstraints(tims);
		addPrArg(inst, "occurrenceCount", new UShort(12));
		return inst;
	}
}
