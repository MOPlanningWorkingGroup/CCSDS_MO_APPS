/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
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
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
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
	protected TimingDetails createTaskTiming(TriggerName name, TimeTrigger tt) {
		TimingDetails trig = new TimingDetails();
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
	protected TimingDetailsList createPpfTaskTriggers(Time uplink, Time exec) {
		TimingDetailsList list = new TimingDetailsList();
		list.add(createTaskTiming(TriggerName.UPLINK, createAbsTimeTrig(uplink)));
		list.add(createTaskTiming(TriggerName.START, createAbsTimeTrig(exec)));
		return list;
	}
}
