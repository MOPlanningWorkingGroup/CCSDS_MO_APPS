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

public class GocePlanner {

	private PlanningRequestStub stub;

	public GocePlanner(PlanningRequestStub stub) {
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
	
	private ArgumentDefinitionDetailsList createPlanDefFields() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("EVRQ_Time", "plan time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("EVRQ_Type", "plan type", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EVRQ_Description", "plan description", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}
	
	private PlanningRequestDefinitionDetails createPlanDef(String desc, Identifier planDefName) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(planDefName);
		prDef.setDescription(desc);
		prDef.setFields(createPlanDefFields());
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
	
	private AttributeValueList createTaskFieldsValues(String src, String dest, String type) {
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
	
	private TriggerDetailsList createTaskTriggers(Time uplink, Time exec) {
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
	
	private AttributeValueList createPlanFieldsValues(Time time, String type, String desc) {
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
	
	public Long payloadPlan1() throws MALException, MALInteractionException, ParseException {
		Identifier planDefName = new Identifier("goce_plan_1");
		TaskDefinitionDetails taskDef = createTaskDef("task1", "payload task", planDefName,
				createPayloadTaskDefFields(), createTaskDefArgs());
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails planDef = createPlanDef("plan", planDefName);
		planDef.setTaskDefNames(getTaskNameList(taskDef));
		
		Long planDefId = submitPlanDef(planDef);
		
		Identifier planName = new Identifier("GOCE plan 1");
		TaskInstanceDetails taskInst = createTaskInst("MSDDIA_B", "Disable_SSTI-B_Diag v01", planName,
				createTaskFieldsValues("RPF", "MPS", "Time-tagged sequence"),
				createTaskArgsValues((short)1, "SID", "session id", "Raw", "Decimal", "", "32"),
				createTaskTriggers(null, parseTime("UTC=2007-08-31T19:53:23")));
		// TODO submit task inst to COM archive?
		PlanningRequestInstanceDetails planInst = createPlanInst(planName, "goce plan 1",
				createPlanFieldsValues(parseTime("UTC=2007-08-31T19:53:23"), "Request", ""));
		planInst.setTasks(getTaskInstList(taskInst));
		// TODO submit plan inst to COM archive?
		stub.submitPlanningRequest(planDefId, null, planInst);
		return planDefId;
	}
	
	public void payloadPlan2() throws MALException, MALInteractionException, ParseException {
		Long planDefId = payloadPlan1();
		// plan2 instances use same definitions as plan1
		Identifier plan2Name = new Identifier("GOCE plan 2");
		TaskInstanceDetails task2Inst = createTaskInst("MSEDIA_A", "Enable_SSTI-A_Diag v01", plan2Name,
				createTaskFieldsValues("RPF", "MPS", "Time-tagged sequence"),
				createTaskArgsValues((short)1, "SID", "SID", "Raw", "Decimal", "", "32"),
				createTaskTriggers(null, parseTime("UTC=2007-08-31T20:03:23")));
		//TODO submit task instance to COM archive?
		PlanningRequestInstanceDetails plan2Inst = createPlanInst(plan2Name, "goce plan 2",
				createPlanFieldsValues(parseTime("UTC=2007-08-31T20:02:23"), "Request", ""));
		// TODO submit plan instance to COM archive?
		stub.submitPlanningRequest(planDefId, null, plan2Inst);
	}
	
	private ArgumentDefinitionDetailsList createIncrementTaskDefFields() {
		ArgumentDefinitionDetailsList fields = createPayloadTaskDefFields();
		// these 3 fields come from payload task def
//		fields.add(createArgDef("RQ_Source", "task source", Attribute.STRING_TYPE_SHORT_FORM));
//		fields.add(createArgDef("RQ_Destination", "task destination", Attribute.STRING_TYPE_SHORT_FORM));
//		fields.add(createArgDef("RQ_Type", "task type", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("RQ_Status", "task status", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("RQ_Subsystem", "task sub-system", Attribute.STRING_TYPE_SHORT_FORM));
//		fields.add(createArgDef("RQ_Parent_Event", "task parent event", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EV_Name", "task parent event name", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EV_Source", "task parent event source", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EV_Time", "task parent event time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("EV_ID", "task parent event id", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}
	
	public void incrementalPlan() throws MALException, MALInteractionException {
		Identifier planDefName = new Identifier("goce_plan_2");
		TaskDefinitionDetails taskDef = createTaskDef("task2", "incremental task", planDefName, 
				createIncrementTaskDefFields(), createTaskDefArgs());
		Long taskDefId = submitTaskDef(taskDef);
	}
}
