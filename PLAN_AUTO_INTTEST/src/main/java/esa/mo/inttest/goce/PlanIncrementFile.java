package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

public class PlanIncrementFile extends CommonFile {

	private static final String taskDefName = "goce pif task def";
	private static final String prDefName = "goce pif pr def";
	private static final String prName = "goce pif pr";
	
	public PlanIncrementFile() {
	}
	
	protected ArgumentDefinitionDetailsList createPifTaskDefFields() {
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
	
	protected ArgumentDefinitionDetailsList createPifPrDefFields() {
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
	
	// FIXME too many arguments
	protected AttributeValueList createPifTaskFieldsValues(String src, String dest, String type, String stat,
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

	protected TriggerDetailsList createPifTaskTrigger(Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.START, exec));
		return list;
	}

	// FIXME too many arguments
	protected AttributeValueList createPifPrFieldsValues(String fType, Time start, String fVer, String stat,
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

	public TaskDefinitionDetails createTaskDef() {
		return createTaskDef(taskDefName, "incremental task", prDefName, createPifTaskDefFields(), createTaskDefArgs());
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = createPrDef(prDefName, "plan2", createPifPrDefFields());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public TaskInstanceDetails createTaskInst() throws ParseException {
		TaskInstanceDetails taskInst = createTaskInst("MCEMON", "ENA_MON_ID_PASW v01", prName,
				createPifTaskFieldsValues("FDS", "MPS", "Time-tagged sequence", "Enabled", "CDMU_CTR", "MCEMON",
						"SPF", parseTime("UTC=2008-04-09T15:00:00.000"), ""),
				createTaskArgsValues((short)1, "MON_ID", "Monitoring id", "Raw", "Decimal", "", "60000"),
				createPifTaskTrigger(parseTime("UTC=2007-08-31T20:03:23")));
		return taskInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst(TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(prName, "goce plan 3",
				createPifPrFieldsValues("FOS plan increment file", parseTime("UTC=2008-04-07T00:00:00"), "2",
						"Generated", null, "1", "1", "1", "0", "GODB_013", "3", "1", "1"));
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
