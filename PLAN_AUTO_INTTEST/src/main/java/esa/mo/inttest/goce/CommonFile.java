package esa.mo.inttest.goce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValue;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentValueList;
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
	protected ArgumentDefinitionDetailsList createSrcDestTypeArgDefs(String srcDefVal) {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("RQ_Source", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union(srcDefVal)));
		argDefs.add(createArgDef("RQ_Destination", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("MPS")));
		argDefs.add(createArgDef("RQ_Type", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("Time-tagged Sequence")));
		return argDefs;
	}
	
	/**
	 * Defines <RQ_Parameter> count fields.
	 * @param argDefs
	 */
	protected ArgumentDefinitionDetailsList setParamsCountArgDef(ArgumentDefinitionDetailsList argDefs) {
		argDefs.add(createArgDef("RQ_Parameters_count", null, Attribute.USHORT_TYPE_SHORT_FORM, null, null, null, null));
		return argDefs;
	}
	
	/**
	 * Sets task <RQ_Parameter> fields.
	 * @param taskInst
	 * @param name
	 * @param val
	 */
	protected void addTaskArg(TaskInstanceDetails taskInst, String name, Attribute val) {
		if (null == taskInst.getArgumentValues()) {
			taskInst.setArgumentValues(new ArgumentValueList());
		}
		taskInst.getArgumentValues().add(new ArgumentValue(new Identifier(name), val));
	}
	
	/**
	 * Sets task <RQ_Parameter> fields.
	 * @param prInst
	 * @param name
	 * @param val
	 */
	protected void addPrArg(PlanningRequestInstanceDetails prInst, String name, Attribute val) {
		if (null == prInst.getArgumentValues()) {
			prInst.setArgumentValues(new ArgumentValueList());
		}
		prInst.getArgumentValues().add(new ArgumentValue(new Identifier(name), val));
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
		SimpleDateFormat sdf = new SimpleDateFormat("zzz'='yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d = sdf.parse(s);
		long l = d.getTime(); // GMT
		return new Time(l);
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
		list.add(createTaskTrigger(TriggerName.START, createAbsTimeTrig(exec)));
		return list;
	}
}
