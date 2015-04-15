package esa.mo.inttest.goce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.RelativeTime;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

/**
 * Frequently used methods that may be of use to any file.
 */
public class CommonFile {

	/**
	 * Creates single argDef.
	 * @param id
	 * @param desc
	 * @param type
	 * @param unit
	 * @param repr
	 * @param radix
	 * @param def
	 * @return
	 */
	protected ArgumentDefinitionDetails createArgDef(String id, String desc, int type, String unit, String repr,
			String radix, Attribute def) {
		byte attr = (byte)(type & 0xff);
		return new ArgumentDefinitionDetails(new Identifier(id), desc, new Byte(attr), unit, repr, radix, def);
	}
	
	/**
	 * Defines <RQ> fields.
	 * @return argDefs
	 */
	protected ArgumentDefinitionDetailsList createTaskDefArgDefs() {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("RQ_Source", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("RQ_Destination", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("RQ_Type", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		return argDefs;
	}
	
	/**
	 * Defines <RQ_Parameter> count fields.
	 * @param argDefs
	 */
	protected ArgumentDefinitionDetailsList setTaskDefParamDefs(ArgumentDefinitionDetailsList argDefs) {
		argDefs.add(createArgDef("RQ_Parameters_count", null, Attribute.USHORT_TYPE_SHORT_FORM, null, null, null, null));
		return argDefs;
	}
	
	/**
	 * Defines <EVRQ_Header> fields.
	 * @return argDefs
	 */
	protected ArgumentDefinitionDetailsList createPrDefArgDefs() {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("EVRQ_Time", null, Attribute.TIME_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("EVRQ_Type", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("EVRQ_Description", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		return argDefs;
	}
	
	/**
	 * Sets task <RQ> arguments.
	 */
	protected void setTaskArgs(TaskInstanceDetails taskInst, String src, String dest, String type) {
		taskInst.setArgumentDefNames(new IdentifierList());
		taskInst.setArgumentValues(new AttributeValueList());
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Source"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(src)));
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Destination"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(dest)));
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Type"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(type)));
	}
	
	/**
	 * Sets task <RQ_Parameter> fields.
	 * @param taskInst
	 * @param name
	 * @param desc
	 * @param repr
	 * @param radix
	 * @param unit
	 * @param value
	 */
	protected void setTaskParam(TaskInstanceDetails taskInst, String name, AttributeValue val) {
		taskInst.getArgumentDefNames().add(new Identifier(name));
		taskInst.getArgumentValues().add(val);
	}
	
	/**
	 * Creates TimeTrigger with absolute Time.
	 * @param time
	 * @return
	 */
	protected TimeTrigger createAbsTimeTrig(Time absTime) {
		return new TimeTrigger(absTime, null);
	}
	
	/**
	 * Creates TimeTrigger with relative Time.
	 * @param time
	 * @return
	 */
	protected TimeTrigger createRelTimeTrig(Time relTime, boolean forward) {
		return new TimeTrigger(null, new RelativeTime(relTime, forward));
	}
	
	/**
	 * Creates TIME-type trigger.
	 * @param name
	 * @param value
	 * @return
	 */
	protected TriggerDetails createTaskTrigger(TriggerName name, TimeTrigger tt) {
		TriggerDetails trig = new TriggerDetails();
		trig.setTriggerName(name);
		trig.setTimeTrigger(tt);
		return trig;
	}
	
	/**
	 * Parses datetime string to Time class.
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	protected Time parseTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("zzz=yyyy-MM-dd'T'HH:mm:ss");
		Date d = sdf.parse(s);
		long l = d.getTime();
		return new Time(l);
	}
	
	/**
	 * Sets pr <EVRQ> fields.
	 * @param prInst
	 * @param time
	 * @param type
	 * @param desc
	 */
	protected void setPrArgs(PlanningRequestInstanceDetails prInst, Time time, String type, String desc) {
		prInst.setArgumentDefNames(new IdentifierList());
		prInst.setArgumentValues(new AttributeValueList());
		
		prInst.getArgumentDefNames().add(new Identifier("EVRQ_Time"));
		prInst.getArgumentValues().add(new AttributeValue(time));
		
		prInst.getArgumentDefNames().add(new Identifier("EVRQ_Type"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(type)));
		
		prInst.getArgumentDefNames().add(new Identifier("EVRQ_Description"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(desc)));
	}
	
	/**
	 * Creates "UPLINK" & "START" TriggerDetailsList.
	 * @param uplink
	 * @param exec
	 * @return
	 */
	protected TriggerDetailsList createPpfTaskTriggers(Time uplink, Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.UPLINK, createAbsTimeTrig(uplink)));
		list.add(createTaskTrigger(TriggerName.START, createRelTimeTrig(exec, true)));
		return list;
	}
}
