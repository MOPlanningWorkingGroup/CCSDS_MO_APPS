package esa.mo.inttest.goce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

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

/**
 * Planning request consumer for testing. Implemented by GOCE example files - PPF, PIF, SPF, OPF.
 */
public class GoceConsumer {

	private PlanningRequestStub stub;

	public GoceConsumer(PlanningRequestStub stub) {
		this.stub = stub;
	}

	public PlanningRequestStub getStub() {
		return this.stub;
	}
	
	private ArgumentDefinitionDetails createArgDef(String id, String desc, int type) {
		byte attr = (byte)(0xff & type);
		return new ArgumentDefinitionDetails(new Identifier(id), desc, new Byte(attr));
	}
	
	private ArgumentDefinitionDetailsList createTaskDefFields() {
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
		args.add(createArgDef("RQ_Parameter_Description", "task parameter description",
				Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Representation", "task parameter representation",
				Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Radix", "task parameter radix", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Unit", "task parameter unit", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("RQ_Parameter_Value", "task parameter value", Attribute.STRING_TYPE_SHORT_FORM));
		return args;
	}
	
	private TaskDefinitionDetails createTaskDef(String taskDefName, String desc, String prDefName,
			ArgumentDefinitionDetailsList fields, ArgumentDefinitionDetailsList args) {
		TaskDefinitionDetails taskDef = new TaskDefinitionDetails();
		taskDef.setName(new Identifier(taskDefName));
		taskDef.setDescription(desc);
		taskDef.setFields(fields);
		taskDef.setArguments(args);
		taskDef.setPrDefName(new Identifier(prDefName));
		return taskDef;
	}
	
	private Long submitTaskDef(TaskDefinitionDetails taskDef) throws MALException, MALInteractionException {
		TaskDefinitionDetailsList taskDefsList = new TaskDefinitionDetailsList();
		taskDefsList.add(taskDef);
		LongList taskIdsList = null;
		taskIdsList = stub.addTaskDefinition(taskDefsList);
		return (taskIdsList != null && !taskIdsList.isEmpty()) ? taskIdsList.get(0) : null;
	}
	
	private ArgumentDefinitionDetailsList createPrDefFields() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("EVRQ_Time", "planning request time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("EVRQ_Type", "planning request type", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("EVRQ_Description", "planning request description", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}
	
	private PlanningRequestDefinitionDetails createPrDef(String prDefName, String desc,
			ArgumentDefinitionDetailsList fields) {
		PlanningRequestDefinitionDetails prDef = new PlanningRequestDefinitionDetails();
		prDef.setName(new Identifier(prDefName));
		prDef.setDescription(desc);
		prDef.setFields(fields);
		return prDef;
	}
	
	private IdentifierList getTaskNameList(String taskDefName) {
		IdentifierList list = new IdentifierList();
		list.add(new Identifier(taskDefName));
		return list;
	}
	
	private Long submitPrDef(PlanningRequestDefinitionDetails prDef) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetailsList prDefsList = new PlanningRequestDefinitionDetailsList();
		prDefsList.add(prDef);
		LongList defIdsList = null;
		defIdsList = stub.addDefinition(prDefsList);
		return (defIdsList != null && !defIdsList.isEmpty()) ? defIdsList.get(0) : null;
	}
	
	private AttributeValueList createPpfTaskFieldsValues(String src, String dest, String type) {
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
	
	private TriggerDetailsList createPpfTaskTriggers(Time uplink, Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.UPLINK, uplink));
		list.add(createTaskTrigger(TriggerName.START, exec));
		return list;
	}
	
	private TaskInstanceDetails createTaskInst(String taskName, String desc, String prName,
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
	
	private Time parseTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("zzz=yyyy-MM-dd'T'HH:mm:ss");
		Date d = sdf.parse(s);
		long l = d.getTime();
		Time t = new Time(l);
		return t;
	}
	
	private AttributeValueList createPrFieldsValues(Time time, String type, String desc) {
		AttributeValueList fields = new AttributeValueList();
		fields.add(new AttributeValue(time));
		fields.add(new AttributeValue(new Union(type)));
		fields.add(new AttributeValue(new Union(desc)));
		return fields;
	}
	
	private PlanningRequestInstanceDetails createPrInst(String prName, String desc, AttributeValueList fields) {
		PlanningRequestInstanceDetails prInst = new PlanningRequestInstanceDetails();
		prInst.setName(new Identifier(prName));
		prInst.setDescription(desc);
		prInst.setFieldValues(fields);
		return prInst;
	}
	
	private TaskInstanceDetailsList getTaskInstList(TaskInstanceDetails taskInst) {
		TaskInstanceDetailsList list = new TaskInstanceDetailsList();
		list.add(taskInst);
		return list;
	}
	
	protected Map.Entry<Long, String> createPpfTaskDef(String prDefName) throws MALException, MALInteractionException {
		String taskDefName = "goce ppf task def";
		TaskDefinitionDetails taskDef = createTaskDef(taskDefName, "payload task", prDefName,
				createTaskDefFields(), createTaskDefArgs());
		Long taskDefId = submitTaskDef(taskDef);
		return new AbstractMap.SimpleEntry<Long, String>(taskDefId, taskDefName);
	}
	
	protected Long createPpfPrDef(String prDefName, IdentifierList taskDefNames) throws MALException, MALInteractionException {
		PlanningRequestDefinitionDetails prDef = createPrDef("plan1", prDefName, createPrDefFields());
		prDef.setTaskDefNames(taskDefNames);
		Long prDefId = submitPrDef(prDef);
		return prDefId;
	}
	
	protected TaskInstanceDetails createPpfTaskInst1(String prName) throws ParseException {
		TaskInstanceDetails taskInst = createTaskInst("MSDDIA_B", "Disable_SSTI-B_Diag v01", prName,
				createPpfTaskFieldsValues("RPF", "MPS", "Time-tagged sequence"),
				createTaskArgsValues((short)1, "SID", "session id", "Raw", "Decimal", "", "32"),
				createPpfTaskTriggers(null, parseTime("UTC=2007-08-31T19:53:23")));
		// TODO store task inst in COM archive?
		return taskInst;
	}
	
	protected PlanningRequestInstanceDetails createPpfPrInst1(String prName, TaskInstanceDetailsList taskInsts)
			throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(prName, "goce ppf pr 1",
				createPrFieldsValues(parseTime("UTC=2007-08-31T19:53:23"), "Request", ""));
		prInst.setTasks(taskInsts);
		// TODO store pr inst in COM archive?
		return prInst;
	}
	
	protected TaskInstanceDetails createPpfTaskInst2(String prName) throws ParseException {
		TaskInstanceDetails task2Inst = createTaskInst("MSEDIA_A", "Enable_SSTI-A_Diag v01", prName,
				createPpfTaskFieldsValues("RPF", "MPS", "Time-tagged sequence"),
				createTaskArgsValues((short)1, "SID", "SID", "Raw", "Decimal", "", "32"),
				createPpfTaskTriggers(null, parseTime("UTC=2007-08-31T20:03:23")));
		//TODO store task instance in COM archive?
		return task2Inst;
	}
	
	protected PlanningRequestInstanceDetails createPpfPrInst2(String prName, TaskInstanceDetailsList taskInsts)
			throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(prName, "goce ppf pr 2",
				createPrFieldsValues(parseTime("UTC=2007-08-31T20:02:23"), "Request", ""));
		prInst.setTasks(taskInsts);
		// TODO store pr instance in COM archive?
		return prInst;
	}
	
	// PPF - payload plan file
	public void ppf() throws MALException, MALInteractionException, ParseException {
		String prDefName = "goce ppf pr def";
		Map.Entry<Long, String> taskDefId = createPpfTaskDef(prDefName);
		Long prDefId = createPpfPrDef(prDefName, getTaskNameList(taskDefId.getValue()));
		
		String prName = "goce ppf pr 1";
		TaskInstanceDetails taskInst = createPpfTaskInst1(prName);
		PlanningRequestInstanceDetails prInst = createPpfPrInst1(prName, getTaskInstList(taskInst));
		stub.submitPlanningRequest(prDefId, null, prInst);
		
		String pr2Name = "goce ppf pr 2";
		TaskInstanceDetails taskInst2 = createPpfTaskInst2(pr2Name);
		PlanningRequestInstanceDetails prInst2 = createPpfPrInst2(pr2Name, getTaskInstList(taskInst2));
		stub.submitPlanningRequest(prDefId, null, prInst2);
	}
	
	public void createPpfDefsIfMissing() throws MALException, MALInteractionException {
		IdentifierList taskNames = new IdentifierList();
		String taskDefName = "goce ppf task def";
		taskNames.add(new Identifier(taskDefName));
		LongList taskIds = stub.listTaskDefinition(taskNames);
		
		String prDefName = "goce ppf pr def";
		if (taskIds.isEmpty()) {
			Map.Entry<Long, String> taskDefEntry = createPpfTaskDef(prDefName);
		} else if (taskIds.size() > 1) {
			throw new MALException("more than one goce ppf task def with same name found");
		}
		// task def ok, proceed with pr def
		IdentifierList prNames = new IdentifierList();
		prNames.add(new Identifier(prDefName));
		LongList prIds = stub.listDefinition(prNames);
		
		if (prIds.isEmpty()) {
			Long prDefId = createPpfPrDef(prDefName, getTaskNameList(taskDefName));
		} else if (prIds.size() > 1) {
			throw new MALException("more than one goce ppf pr def with same name found");
		}
	}
	
	public void createPpfInstsIfMissingAndDefsExist() throws MALException, MALInteractionException, ParseException {
		IdentifierList taskNames = new IdentifierList();
		String taskDefName = "goce ppf task def";
		taskNames.add(new Identifier(taskDefName));
		LongList taskIds = stub.listTaskDefinition(taskNames);
		
		TaskInstanceDetails taskInst1 = null;
		TaskInstanceDetails taskInst2 = null;
		if (taskIds.isEmpty()) {
			// no task def - nothing to do
		} else if (taskIds.size() > 1) {
			throw new MALException("more than one goce ppf task def with same name found");
		} else {
			// TODO check if task instances already exist
			String pr1Name = "goce ppf pr 1";
			taskInst1 = createPpfTaskInst1(pr1Name);
			String pr2Name = "goce ppf pr 2";
			taskInst2 = createPpfTaskInst2(pr2Name);
		}
		// task instances ok, proceed with pr instances
		IdentifierList prNames = new IdentifierList();
		String prDefName = "goce ppf pr def";
		prNames.add(new Identifier(prDefName));
		LongList prIds = stub.listDefinition(prNames);
		
		if (prIds.isEmpty()) {
			// no pr def - nothing to do
		} else if (prIds.size() > 1) {
			throw new MALException("more than one goce ppf pr def with same name found");
		} else {
			// TODO check if pr instances already exist
			String prName = "goce ppf pr 1";
			createPpfPrInst1(prName, getTaskInstList(taskInst1));
			String pr2Name = "goce ppf pr 2";
			createPpfPrInst1(pr2Name, getTaskInstList(taskInst1));
		}
	}
	
	private ArgumentDefinitionDetailsList createPifTaskDefFields() {
		ArgumentDefinitionDetailsList fields = createTaskDefFields();
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
	
	private ArgumentDefinitionDetailsList createPifPrDefFields() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("PIF_File_Type", "planning request file type", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_Start", "planning request start time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_File_Version", "planning request file version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_Status", "planning request status", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_Replan_Time", "planning request replan time", Attribute.TIME_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_SPF_Version", "planning request spf version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_PPF_Version", "planning request ppf version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_OPF_Version", "planning request opf version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_MTF_Version", "planning request mtf version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_WODB_Version", "planning request wodb version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_RC_Version", "planning request rc version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_KUP_Version", "planning request kup version", Attribute.STRING_TYPE_SHORT_FORM));
		fields.add(createArgDef("PIF_SI_Version", "planning request si version", Attribute.STRING_TYPE_SHORT_FORM));
		return fields;
	}
	
	private AttributeValueList createPifTaskFieldsValues(String src, String dest, String type, String stat,
			String subSys, String evName, String evSrc, Time evTime, String evId) {
		AttributeValueList fields = createPpfTaskFieldsValues(src, dest, type);
		fields.add(new AttributeValue(new Union(stat)));
		fields.add(new AttributeValue(new Union(subSys)));
		fields.add(new AttributeValue(new Union(evName)));
		fields.add(new AttributeValue(new Union(evSrc)));
		fields.add(new AttributeValue(evTime));
		fields.add(new AttributeValue(new Union(evId)));
		return fields;
	}

	private TriggerDetailsList createPifTaskTrigger(Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.START, exec));
		return list;
	}

	private AttributeValueList createPifPrFieldsValues(String fType, Time start, String fVer, String stat,
			Time replan, String spf, String ppf, String opf, String mtf, String wodb, String rc, String kup,
			String si) {
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
	public void pif() throws MALException, MALInteractionException, ParseException {
		String prDefName = "goce pif pr def";
		TaskDefinitionDetails taskDef = createTaskDef("goce pif task def", "incremental task", prDefName,
				createPifTaskDefFields(), createTaskDefArgs());
		Long taskDefId = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = createPrDef(prDefName, "plan2", createPifPrDefFields());
		prDef.setTaskDefNames(getTaskNameList(taskDef.getName().getValue()));
		Long prDefId = submitPrDef(prDef);
		
		String prName = "goce pif pr 1";
		TaskInstanceDetails taskInst = createTaskInst("MCEMON", "ENA_MON_ID_PASW v01", prName,
				createPifTaskFieldsValues("FDS", "MPS", "Time-tagged sequence", "Enabled", "CDMU_CTR", "MCEMON",
						"SPF", parseTime("UTC=2008-04-09T15:00:00.000"), ""),
				createTaskArgsValues((short)1, "MON_ID", "Monitoring id", "Raw", "Decimal", "", "60000"),
				createPifTaskTrigger(parseTime("UTC=2007-08-31T20:03:23")));
		// TODO store task instance in COM archive?
		PlanningRequestInstanceDetails prInst = createPrInst(prName, "goce plan 3",
				createPifPrFieldsValues("FOS plan increment file", parseTime("UTC=2008-04-07T00:00:00"), "2",
						"Generated", null, "1", "1", "1", "0", "GODB_013", "3", "1", "1"));
		// TODO store pr instance in COM archive?
		stub.submitPlanningRequest(prDefId, null, prInst);
	}
	
	private ArgumentDefinitionDetailsList createSpfTaskDefArgs() {
		ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
		args.add(createArgDef("EV_Parameters_count", "task event parameters count", Attribute.OCTET_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Name", "task parameter name", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Description", "task parameter description",
				Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Representation", "task parameter representation",
				Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Radix", "task parameter radix", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Unit", "task parameter unit", Attribute.STRING_TYPE_SHORT_FORM));
		args.add(createArgDef("EV_Parameter_Value", "task parameter value", Attribute.STRING_TYPE_SHORT_FORM));
		return args;
	}

	private AttributeValueList createTaskArgsZeroValues() {
		AttributeValueList args = new AttributeValueList();
		args.add(new AttributeValue(new UOctet((short)0)));
//		args.add(new AttributeValue(new Union(name)));
//		args.add(new AttributeValue(new Union(desc)));
//		args.add(new AttributeValue(new Union(repr)));
//		args.add(new AttributeValue(new Union(radix)));
//		args.add(new AttributeValue(new Union(unit)));
//		args.add(new AttributeValue(new Union(value)));
		return args;
	}

	// SPF - skeleton planning file
	public void spf() throws MALException, MALInteractionException, ParseException {
		String prDefName = "goce spf pr def";
		TaskDefinitionDetails taskDef = createTaskDef("goce spf task def", "skeleton plan task", prDefName, null,
				createSpfTaskDefArgs());
		Long taskDefId = submitTaskDef(taskDef);
	
		PlanningRequestDefinitionDetails prDef = createPrDef("plan3", prDefName, createPrDefFields());
		prDef.setTaskDefNames(getTaskNameList(taskDef.getName().getValue()));
		Long prDefId = submitPrDef(prDef);
		
		for (int i = 0; i < 3; ++i) {
			String prName = "goce spf pr "+(4+i);
			TaskInstanceDetails taskInst = createTaskInst("NODE", "goce_task_"+(3+i), prName, null,
					createTaskArgsZeroValues(), null);
			// TODO store task instance in com arc
			PlanningRequestInstanceDetails prInst = createPrInst(prName, "GOCE plan "+(4+i),
					createPrFieldsValues(parseTime("UTC=2008-06-02T00:56:57"), "Event", "Ascending node crossing"));
			// TODO store pr instance in com arc
			stub.submitPlanningRequest(prDefId, null, prInst);
		}
	}
	
	// OPF - operations planning file
	public void opf() throws MALException, MALInteractionException, ParseException {
		String prDefName = "goce opf pr def";
		TaskDefinitionDetails taskDef = createTaskDef("goce opf task", "operations task 4", prDefName, createTaskDefFields(),
				createTaskDefArgs());
		Long taskDefID = submitTaskDef(taskDef);
		
		PlanningRequestDefinitionDetails prDef = createPrDef("plan4", prDefName, createPrDefFields());
		prDef.setTaskDefNames(getTaskNameList(taskDef.getName().getValue()));
		Long prDefId = submitPrDef(prDef);
		
		String prName = "goce opf pr";
		TaskInstanceDetails taskInst = createTaskInst("MCDD10HZ", "DIS_DFACS_10_HZ v01", prName,
				createPpfTaskFieldsValues("FCT", "MPS", "Time-tagged Sequence"), createTaskArgsZeroValues(),
				createPpfTaskTriggers(null, parseTime("UTC=2007-01-02T12:10:00")));
		// TODO store task inst in com arc
		PlanningRequestInstanceDetails prInst = createPrInst(prName, "goce plan 7",
				createPrFieldsValues(parseTime("UTC=2007-01-01T12:10:00"), "Request", ""));
		// TODO store pr inst in com arc
		stub.submitPlanningRequest(prDefId, null, prInst);
	}
}
