package esa.mo.inttest.goce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerType;

/**
 * Frequently used methods that may be of use to any file.
 */
public class CommonFile {

	/**
	 * Creates single argDef.
	 * @param id
	 * @param desc
	 * @param type
	 * @return argDef
	 */
	protected ArgumentDefinitionDetails createArgDef(String id/*, String desc*/, int type, Short area) {
		byte attr = (byte)(0xff & type);
		return new ArgumentDefinitionDetails(new Identifier(id), new Byte(attr), area);
	}
	
	/**
	 * Creates TaskDef base.
	 * @param taskDefName
	 * @param desc
	 * @param prDefName
	 * @return taskDef
	 */
	protected TaskDefinitionDetails createTaskDef(String taskDefName, String desc, String prDefName/*,
			ArgumentDefinitionDetailsList fields, ArgumentDefinitionDetailsList args*/) {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier(taskDefName));
		taskDef.setDescription(desc);
//		taskDef.setFields(fields);
//		taskDef.setArguments(args);
		taskDef.setPrDefName(new Identifier(prDefName));
		return taskDef;
	}
	
	/**
	 * Defines <RQ> fields.
	 * @return argDefs
	 */
	protected ArgumentDefinitionDetailsList createTaskDefArgDefs() {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("RQ_Source", /*"task source",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Destination", /*"task destination",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Type", /*"task type",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		return argDefs;
	}
	
	/**
	 * Defines <RQ_Parameter> fields.
	 * @param argDefs
	 */
	protected ArgumentDefinitionDetailsList setTaskDefParamDefs(ArgumentDefinitionDetailsList argDefs) {
		argDefs.add(createArgDef("RQ_Parameters_count", /*"task parameters count",*/ Attribute.OCTET_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Parameter_Name", /*"task parameter name",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Parameter_Description", /*"task parameter description",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Parameter_Representation", /*"task parameter representation",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Parameter_Radix", /*"task parameter radix",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Parameter_Unit", /*"task parameter unit",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Parameter_Value", /*"task parameter value",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		return argDefs;
	}
	
	/**
	 * Creates prDef base.
	 * @param prDefName
	 * @param desc
	 * @return
	 */
	protected PlanningRequestDefinitionDetails createPrDef(String prDefName, String desc/*,
			ArgumentDefinitionDetailsList fields*/) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier(prDefName));
		prDef.setDescription(desc);
//		prDef.setFields(fields);
		return prDef;
	}
	
	/**
	 * Defines <EVRQ_Header> fields.
	 * @return argDefs
	 */
	protected ArgumentDefinitionDetailsList createPrDefArgDefs() {
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("EVRQ_Time", /*"planning request time",*/ Attribute.TIME_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EVRQ_Type", /*"planning request type",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EVRQ_Description", /*"planning request description",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		return argDefs;
	}
	
	/**
	 * Creates taskInst base.
	 * @param taskName
	 * @param desc
	 * @param prName
	 * @param triggers
	 * @return taskInst
	 */
	protected TaskInstanceDetails createTaskInst(String taskName, String desc, String prName/*,
			AttributeValueList fields, AttributeValueList args*/, TriggerDetailsList triggers) {
		TaskInstanceDetails taskInst = new TaskInstanceDetails();
		taskInst.setName(new Identifier(taskName));
		taskInst.setDescription(desc);
//		taskInst.setFieldValues(fields);
//		taskInst.setArgumentValues(args);
		taskInst.setTimingConstraints(triggers);
		taskInst.setPrName(new Identifier(prName));
		return taskInst;
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
	 * Sets task parameters count.
	 * @param taskInst
	 * @param count
	 */
	protected void setTaskParamsCount(TaskInstanceDetails taskInst, short count) {
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Parameters_count"));
		taskInst.getArgumentValues().add(new AttributeValue(new UOctet(count)));
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
	protected void setTaskParam(TaskInstanceDetails taskInst, String name, String desc, String repr, String radix,
			String unit, String value) {
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Parameter_Name"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(name)));
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Parameter_Description"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(desc)));
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Parameter_Representation"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(repr)));
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Parameter_Radix"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(radix)));
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Parameter_Unit"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(unit)));
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Parameter_Value"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(value)));
	}

	protected TimeTrigger createTaskTime(Time time) {
		TimeTrigger tt = new TimeTrigger();
		tt.setTimeValue(time);
		tt.setAbsoluteTime(new Boolean(true));
		return tt;
	}

	protected TriggerDetails createTaskTrigger(TriggerName name, Time value) {
		TriggerDetails trig = new TriggerDetails();
		trig.setTriggerName(name);
		trig.setTriggerType(TriggerType.TIME);
		trig.setTimeTrigger(createTaskTime(value));
		trig.setEventTrigger(null);
		return trig;
	}

	protected Time parseTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("zzz=yyyy-MM-dd'T'HH:mm:ss");
		Date d = sdf.parse(s);
		long l = d.getTime();
		Time t = new Time(l);
		return t;
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
	 * Sets prInst base.
	 * @param prName
	 * @param desc
	 * @return
	 */
	protected PlanningRequestInstanceDetails createPrInst(String prName, String desc/*, AttributeValueList fields*/) {
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setName(new Identifier(prName));
		prInst.setDescription(desc);
//		prInst.setFieldValues(fields);
		return prInst;
	}

	protected TriggerDetailsList createPpfTaskTriggers(Time uplink, Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.UPLINK, uplink));
		list.add(createTaskTrigger(TriggerName.START, exec));
		return list;
	}
}
