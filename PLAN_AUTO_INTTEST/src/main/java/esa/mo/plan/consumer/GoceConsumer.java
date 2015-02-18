package esa.mo.plan.consumer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerType;

public class GoceConsumer {

	private PlanningRequestStub stub;

	public GoceConsumer(PlanningRequestStub stub) {
		this.stub = stub;
	}

	private ArgumentDefinitionDetails createArgDef(String id, String desc, int type) {
		byte attr = (byte)(0xff & type);
		return new ArgumentDefinitionDetails(new Identifier(id), desc, new Byte(attr));
	}
	
	private ArgumentDefinitionDetailsList createPayloadTaskDefFields() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("RQ_Source", "task source", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("RQ_Destination", "task destination", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("RQ_Type", "task type", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}
	
	private ArgumentDefinitionDetailsList createTaskDefArgs() {
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(createArgDef("RQ_Parameters_count", "task parameters count", Attribute.OCTET_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Name", "task parameter name", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Description", "task parameter description", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Representation", "task parameter representation", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Radix", "task parameter radix", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Unit", "task parameter unit", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Value", "task parameter value", Attribute.STRING_TYPE_SHORT_FORM));
		return args;
	}
	
	private TaskDefinitionDetails createTaskDef(String taskDefName, String desc, Identifier planDefName,
			ArgumentDefinitionDetailsList fields, ArgumentDefinitionDetailsList args) {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier(taskDefName));
		taskDef.setDescription(desc);
		taskDef.setFields(fields);
		taskDef.setArguments(args);
		taskDef.setPrDefName(planDefName);
		return taskDef;
	}
	
	private Long submitTaskDef(TaskDefinitionDetails taskDef) throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefsList = new TaskDefinitionDetailsList();
		taskDefsList.add(taskDef);
		LongList taskIdsList = null;
		taskIdsList = stub.addTaskDefinition(taskDefsList);
		return (taskIdsList != null && !taskIdsList.isEmpty()) ? taskIdsList.get(0) : null;
	}
	
	private ArgumentDefinitionDetailsList createPayloadPlanDefFields() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("EVRQ_Time", "plan time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("EVRQ_Type", "plan type", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EVRQ_Description", "plan description", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}
	
	private PlanningRequestDefinitionDetails createPlanDef(String desc, Identifier planDefName,
			ArgumentDefinitionDetailsList args) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(planDefName);
		prDef.setDescription(desc);
		prDef.setFields(args);
		return prDef;
	}
	
	private IdentifierList getTaskNameList(TaskDefinitionDetails taskDef) {
		IdentifierList list = new IdentifierList();
		list.add(taskDef.getName());
		return list;
	}
	
	private Long submitPlanDef(PlanningRequestDefinitionDetails prDef) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefsList = new PlanningRequestDefinitionDetailsList();
		prDefsList.add(prDef);
		LongList defIdsList = null;
		defIdsList = stub.addDefinition(prDefsList);
		return (defIdsList != null && !defIdsList.isEmpty()) ? defIdsList.get(0) : null;
	}
	
	private AttributeValueList createPayloadTaskFieldsValues(String src, String dest, String type) {
		AttributeValueList fields = new AttributeValueList();
		fields.add(new AttributeValue(new Union(src)));
		fields.add(new AttributeValue(new Union(dest)));
		fields.add(new AttributeValue(new Union(type)));
		return fields;
	}
	
	private AttributeValueList createTaskArgsValues(short count, String name, String desc, String repr, String radix,
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
	
	private TimeTrigger createTaskTime(Time time) {
		TimeTrigger tt = new TimeTrigger();
		tt.setTimeValue(time);
		tt.setAbsoluteTime(new Boolean(true));
		return tt;
	}
	
	private TriggerDetails createTaskTrigger(TriggerName name, Time value) {
		TriggerDetails trig = new TriggerDetails();
		trig.setTriggerName(name);
		trig.setTriggerType(TriggerType.TIME);
		trig.setTimeTrigger(createTaskTime(value));
		trig.setEventTrigger(null);
		return trig;
	}
	
	private TriggerDetailsList createPayloadTaskTriggers(Time uplink, Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.UPLINK, uplink));
		list.add(createTaskTrigger(TriggerName.START, exec));
		return list;
	}
	
	private TaskInstanceDetails createTaskInst(String taskName, String desc, Identifier planName,
			AttributeValueList fields, AttributeValueList args, TriggerDetailsList triggers) {
		TaskInstanceDetails taskInst = new TaskInstanceDetails();
		taskInst.setName(new Identifier(taskName));
		taskInst.setDescription(desc);
		taskInst.setFieldValues(fields);
		taskInst.setArgumentValues(args);
		taskInst.setTimingConstraints(triggers);
		taskInst.setPrName(planName);
		return taskInst;
	}
	
	private Time parseTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("zzz=yyyy-MM-dd'T'HH:mm:ss");
		Date d = sdf.parse(s);
		long l = d.getTime();
		Time t = new Time(l);
		return t;
	}
	
	private AttributeValueList createPayloadPlanFieldsValues(Time time, String type, String desc) {
		AttributeValueList fields = new AttributeValueList();
		fields.add(new AttributeValue(time));
		fields.add(new AttributeValue(new Union(type)));
		fields.add(new AttributeValue(new Union(desc)));
		return fields;
	}
	
	private PlanningRequestInstanceDetails createPlanInst(Identifier planName, String desc, AttributeValueList fields) {
		PlanningRequestInstanceDetails planInst = new PlanningRequestInstanceDetails();
		planInst.setName(planName);
		planInst.setDescription(desc);
		planInst.setFieldValues(fields);
		return planInst;
	}
	
	private TaskInstanceDetailsList getTaskInstList(TaskInstanceDetails taskInst) {
		TaskInstanceDetailsList list = new TaskInstanceDetailsList();
		list.add(taskInst);
		return list;
	}
	
	// PPF - playload plan file part 1
	public Long payloadPlan1() throws MALException, MALInteractionException, ParseException {
		Identifier prDefName = new Identifier("goce_plan_1");
		TaskDefinitionDetails taskDef = createTaskDef("task1", "payload task", prDefName,
				createPayloadTaskDefFields(), createTaskDefArgs());
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails planDef = createPlanDef("plan1", prDefName, createPayloadPlanDefFields());
		planDef.setTaskDefNames(getTaskNameList(taskDef));
		
		Long prDefId = submitPlanDef(planDef);
		
		Identifier prName = new Identifier("GOCE plan 1");
		TaskInstanceDetails taskInst = createTaskInst("MSDDIA_B", "Disable_SSTI-B_Diag v01", prName,
				createPayloadTaskFieldsValues("RPF", "MPS", "Time-tagged sequence"),
				createTaskArgsValues((short)1, "SID", "session id", "Raw", "Decimal", "", "32"),
				createPayloadTaskTriggers(null, parseTime("UTC=2007-08-31T19:53:23")));
		// TODO submit task inst to COM archive?
		PlanningRequestInstanceDetails prInst = createPlanInst(prName, "goce plan 1",
				createPayloadPlanFieldsValues(parseTime("UTC=2007-08-31T19:53:23"), "Request", ""));
		prInst.setTasks(getTaskInstList(taskInst));
		// TODO submit plan inst to COM archive?
		stub.submitPlanningRequest(prDefId, null, prInst);
		return prDefId;
	}
	
	// PPF - payload plan file part 2
	public void payloadPlan2() throws MALException, MALInteractionException, ParseException {
		Long prDefId = payloadPlan1();
		// plan2 instances use same definitions as plan1
		Identifier pr2Name = new Identifier("GOCE plan 2");
		TaskInstanceDetails task2Inst = createTaskInst("MSEDIA_A", "Enable_SSTI-A_Diag v01", pr2Name,
				createPayloadTaskFieldsValues("RPF", "MPS", "Time-tagged sequence"),
				createTaskArgsValues((short)1, "SID", "SID", "Raw", "Decimal", "", "32"),
				createPayloadTaskTriggers(null, parseTime("UTC=2007-08-31T20:03:23")));
		//TODO submit task instance to COM archive?
		PlanningRequestInstanceDetails pr2Inst = createPlanInst(pr2Name, "goce plan 2",
				createPayloadPlanFieldsValues(parseTime("UTC=2007-08-31T20:02:23"), "Request", ""));
		// TODO submit plan instance to COM archive?
		stub.submitPlanningRequest(prDefId, null, pr2Inst);
	}
	
	private ArgumentDefinitionDetailsList createIncrementTaskDefFields() {
		ArgumentDefinitionDetailsList fields = createPayloadTaskDefFields();
		// these 3 fields come from payload task def
//		fields.add(createArgDef("RQ_Source", "task source", Attribute.STRING_TYPE_SHORT_FORM));
//		fields.add(createArgDef("RQ_Destination", "task destination", Attribute.STRING_TYPE_SHORT_FORM));
//		fields.add(createArgDef("RQ_Type", "task type", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("RQ_Status", "task status", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("RQ_Subsystem", "task sub-system", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EV_Name", "task parent event name", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EV_Source", "task parent event source", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EV_Time", "task parent event time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("EV_ID", "task parent event id", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}
	
	private ArgumentDefinitionDetailsList createIncrementPlanDefFields() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("PIF_File_Type", "plan file type", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_Start", "plan start time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_File_Version", "plan file version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_Status", "plan status", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_Replan_Time", "plan replan time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_SPF_Version", "plan spf version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_PPF_Version", "plan ppf version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_OPF_Version", "plan opf version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_MTF_Version", "plan mtf version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_WODB_Version", "plan wodb version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_RC_Version", "plan rc version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_KUP_Version", "plan kup version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_SI_Version", "plan si version", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}
	
	private AttributeValueList createIncrementTaskFieldsValues(String src, String dest, String type, String stat,
			String subSys, String evName, String evSrc, Time evTime, String evId) {
		AttributeValueList fields = createPayloadTaskFieldsValues(src, dest, type);
		fields.add(new AttributeValue(new Union(stat)));
		fields.add(new AttributeValue(new Union(subSys)));
		fields.add(new AttributeValue(new Union(evName)));
		fields.add(new AttributeValue(new Union(evSrc)));
		fields.add(new AttributeValue(evTime));
		fields.add(new AttributeValue(new Union(evId)));
		return fields;
	}

	private TriggerDetailsList createIncrementTaskTrigger(Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.START, exec));
		return list;
	}

	private AttributeValueList createIncrementPlanFieldsValues(String fType, Time start, String fVer, String stat,
			Time replan, String spf, String ppf, String opf, String mtf, String wodb, String rc, String kup, String si) {
		AttributeValueList fields = new AttributeValueList();
		fields.add(new AttributeValue(new Union(fType)));
		fields.add(new AttributeValue(start));
		fields.add(new AttributeValue(new Union(fVer)));
		fields.add(new AttributeValue(new Union(stat)));
		fields.add((replan != null) ? new AttributeValue(replan) : null);
		fields.add(new AttributeValue(new Union(spf)));
		fields.add(new AttributeValue(new Union(ppf)));
		fields.add(new AttributeValue(new Union(opf)));
		fields.add(new AttributeValue(new Union(mtf)));
		fields.add(new AttributeValue(new Union(wodb)));
		fields.add(new AttributeValue(new Union(rc)));
		fields.add(new AttributeValue(new Union(kup)));
		fields.add(new AttributeValue(new Union(si)));
		return fields;
	}

	// PIF - plan increment file
	public void incrementPlan() throws MALException, MALInteractionException, ParseException {
		Identifier prDefName = new Identifier("goce_plan_2");
		TaskDefinitionDetails taskDef = createTaskDef("task2", "incremental task", prDefName,
				createIncrementTaskDefFields(), createTaskDefArgs());
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = createPlanDef("plan2", prDefName, createIncrementPlanDefFields());
		prDef.setTaskDefNames(getTaskNameList(taskDef));
		Long prDefId = submitPlanDef(prDef);
		
		Identifier prName = new Identifier("GOCE plan 3");
		TaskInstanceDetails taskInst = createTaskInst("MCEMON", "ENA_MON_ID_PASW v01", prName,
				createIncrementTaskFieldsValues("FDS", "MPS", "Time-tagged sequence", "Enabled", "CDMU_CTR", "MCEMON",
						"SPF", parseTime("UTC=2008-04-09T15:00:00.000"), ""),
				createTaskArgsValues((short)1, "MON_ID", "Monitoring id", "Raw", "Decimal", "", "60000"),
				createIncrementTaskTrigger(parseTime("UTC=2007-08-31T20:03:23")));
		// TODO store task instance in COM archive?
		PlanningRequestInstanceDetails prInst = createPlanInst(prName, "goce plan 3",
				createIncrementPlanFieldsValues("FOS plan increment file", parseTime("UTC=2008-04-07T00:00:00"), "2",
						"Generated", null, "1", "1", "1", "0", "GODB_013", "3", "1", "1"));
		// TODO store pr instance in COM archive?
		stub.submitPlanningRequest(prDefId, null, prInst);
	}
}
