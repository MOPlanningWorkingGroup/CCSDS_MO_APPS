package esa.mo.inttest.goce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
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

public class CommonFile {

	protected ArgumentDefinitionDetails createArgDef(String id, String desc, int type) {
		byte attr = (byte)(0xff & type);
		return new ArgumentDefinitionDetails(new Identifier(id), desc, new Byte(attr));
	}

	protected TaskDefinitionDetails createTaskDef(String taskDefName, String desc, String prDefName,
			ArgumentDefinitionDetailsList fields, ArgumentDefinitionDetailsList args) {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier(taskDefName));
		taskDef.setDescription(desc);
		taskDef.setFields(fields);
		taskDef.setArguments(args);
		taskDef.setPrDefName(new Identifier(prDefName));
		return taskDef;
	}

	protected ArgumentDefinitionDetailsList createTaskDefFields() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("RQ_Source", "task source", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("RQ_Destination", "task destination", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("RQ_Type", "task type", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}

	protected ArgumentDefinitionDetailsList createTaskDefArgs() {
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(createArgDef("RQ_Parameters_count", "task parameters count", Attribute.OCTET_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Name", "task parameter name", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Description", "task parameter description",
				Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Representation", "task parameter representation",
				Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Radix", "task parameter radix", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Unit", "task parameter unit", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Value", "task parameter value", Attribute.STRING_TYPE_SHORT_FORM));
		return args;
	}

	protected PlanningRequestDefinitionDetails createPrDef(String prDefName, String desc,
			ArgumentDefinitionDetailsList fields) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier(prDefName));
		prDef.setDescription(desc);
		prDef.setFields(fields);
		return prDef;
	}

	protected ArgumentDefinitionDetailsList createPrDefFields() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("EVRQ_Time", "planning request time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("EVRQ_Type", "planning request type", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EVRQ_Description", "planning request description", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}

	protected TaskInstanceDetails createTaskInst(String taskName, String desc, String prName,
			AttributeValueList fields, AttributeValueList args, TriggerDetailsList triggers) {
		TaskInstanceDetails taskInst = new TaskInstanceDetails();
		taskInst.setName(new Identifier(taskName));
		taskInst.setDescription(desc);
		taskInst.setFieldValues(fields);
		taskInst.setArgumentValues(args);
		taskInst.setTimingConstraints(triggers);
		taskInst.setPrName(new Identifier(prName));
		return taskInst;
	}

	protected AttributeValueList createTaskArgsValues(short count, String name, String desc, String repr, String radix,
			String unit, String value) {
		AttributeValueList args = new AttributeValueList();
		args.add(new AttributeValue(new UOctet(count)));
		args.add(new AttributeValue(new Union(name)));
		args.add(new AttributeValue(new Union(desc)));
		args.add(new AttributeValue(new Union(repr)));
		args.add(new AttributeValue(new Union(radix)));
		args.add(new AttributeValue(new Union(unit)));
		args.add(new AttributeValue(new Union(value)));
		return args;
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

	protected AttributeValueList createPrFieldsValues(Time time, String type, String desc) {
		AttributeValueList fields = new AttributeValueList();
		fields.add(new AttributeValue(time));
		fields.add(new AttributeValue(new Union(type)));
		fields.add(new AttributeValue(new Union(desc)));
		return fields;
	}

	protected PlanningRequestInstanceDetails createPrInst(String prName, String desc, AttributeValueList fields) {
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setName(new Identifier(prName));
		prInst.setDescription(desc);
		prInst.setFieldValues(fields);
		return prInst;
	}

	protected AttributeValueList createPpfTaskFieldsValues(String src, String dest, String type) {
		AttributeValueList fields = new AttributeValueList();
		fields.add(new AttributeValue(new Union(src)));
		fields.add(new AttributeValue(new Union(dest)));
		fields.add(new AttributeValue(new Union(type)));
		return fields;
	}

	protected TriggerDetailsList createPpfTaskTriggers(Time uplink, Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.UPLINK, uplink));
		list.add(createTaskTrigger(TriggerName.START, exec));
		return list;
	}

	protected AttributeValueList createTaskArgsZeroValues() {
			AttributeValueList args = new AttributeValueList();
			args.add(new AttributeValue(new UOctet((short)0)));
			return args;
		}

}