package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
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

/**
 * PIF - Plan Increment File.
 */
public class PlanIncrementFile extends CommonFile {

	private static final String TASK_DEF_NAME = "goce pif task def";
	private static final String PR_DEF_NAME = "goce pif pr def";
	private static final String PR_NAME = "goce pif pr";
	
	public PlanIncrementFile() {
	}
	
	/**
	 * Defines some additional <RQ> fields and some <EV> fields.
	 * @param argDefs
	 * @return
	 */
	protected ArgumentDefinitionDetailsList setPifTaskDefArgDefs(ArgumentDefinitionDetailsList argDefs) {
//		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
//		argDefs.add(createArgDef("RQ_Source", "task source", Attribute.STRING_TYPE_SHORT_FORM));
//		argDefs.add(createArgDef("RQ_Destination", "task destination", Attribute.STRING_TYPE_SHORT_FORM));
//		argDefs.add(createArgDef("RQ_Type", "task type", Attribute.STRING_TYPE_SHORT_FORM));
		argDefs.add(createArgDef("RQ_Status", /*"task status",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("RQ_Subsystem", /*"task sub-system",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Name", /*"task parent event name",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Source", /*"task parent event source",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_Time", /*"task parent event time",*/ Attribute.TIME_TYPE_SHORT_FORM, null));
		argDefs.add(createArgDef("EV_ID", /*"task parent event id",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		return argDefs;
	}
	
	protected ArgumentDefinitionDetailsList createPifPrDefArgDefs() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("PIF_File_Type", /*"planning request file type",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_Start", /*"planning request start time",*/ Attribute.TIME_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_File_Version", /*"planning request file version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_Status", /*"planning request status",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_Replan_Time", /*"planning request replan time",*/ Attribute.TIME_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_SPF_Version", /*"planning request spf version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_PPF_Version", /*"planning request ppf version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_OPF_Version", /*"planning request opf version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_MTF_Version", /*"planning request mtf version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_WODB_Version", /*"planning request wodb version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_RC_Version", /*"planning request rc version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_KUP_Version", /*"planning request kup version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		fields.add(createArgDef("PIF_SI_Version", /*"planning request si version",*/ Attribute.STRING_TYPE_SHORT_FORM, null));
		return fields;
	}
	
	protected void setPifTaskArgs(TaskInstanceDetails taskInst, String stat, String subSys, String evName, String evSrc,
			Time evTime, String evId) {
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Status"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(stat)));
		
		taskInst.getArgumentDefNames().add(new Identifier("RQ_Subsystem"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(subSys)));
		
		taskInst.getArgumentDefNames().add(new Identifier("EV_Name"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(evName)));
		
		taskInst.getArgumentDefNames().add(new Identifier("EV_Source"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(evSrc)));
		
		taskInst.getArgumentDefNames().add(new Identifier("EV_Time"));
		taskInst.getArgumentValues().add(new AttributeValue(evTime));
		
		taskInst.getArgumentDefNames().add(new Identifier("EV_ID"));
		taskInst.getArgumentValues().add(new AttributeValue(new Union(evId)));
	}

	protected TriggerDetailsList createPifTaskTrigger(Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.START, exec));
		return list;
	}

	protected PlanningRequestInstanceDetails setPifPrArgs1(PlanningRequestInstanceDetails prInst, String fType,
			Time start, String fVer, String stat, Time replan) {
		prInst.setArgumentDefNames(new IdentifierList());
		prInst.setArgumentValues(new AttributeValueList());
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_File_Type"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(fType)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_Start"));
		prInst.getArgumentValues().add(new AttributeValue(start));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_File_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(fVer)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_Status"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(stat)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_Replan_Time"));
		prInst.getArgumentValues().add((replan != null) ? new AttributeValue(replan) : null);
		return prInst;
	}
	
	protected PlanningRequestInstanceDetails setPifPrArgs2(PlanningRequestInstanceDetails prInst, String spf,
			String ppf, String opf, String mtf, String wodb, String rc, String kup, String si) {
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_SPF_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(spf)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_PPF_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(ppf)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_OPF_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(opf)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_MTF_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(mtf)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_WPDB_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(wodb)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_RC_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(rc)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_KUP_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(kup)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_SI_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(si)));
		return prInst;
	}

	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = createTaskDef(TASK_DEF_NAME, "incremental task", PR_DEF_NAME/*,
				createPifTaskDefFields(), createTaskDefArgs()*/);
		ArgumentDefinitionDetailsList argDefs = createTaskDefArgDefs();
		setPifTaskDefArgDefs(argDefs);
		setTaskDefParamDefs(argDefs);
		taskDef.setArgumentDefs(argDefs);
		return taskDef;
	}
	
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = createPrDef(PR_DEF_NAME, "plan2"/*, createPifPrDefFields()*/);
		prDef.setArgumentDefs(createPifPrDefArgDefs());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	public TaskInstanceDetails createTaskInst() throws ParseException {
		TaskInstanceDetails taskInst = createTaskInst("MCEMON", "ENA_MON_ID_PASW v01", PR_NAME/*,
				createPifTaskFieldsValues("FDS", "MPS", "Time-tagged sequence", "Enabled", "CDMU_CTR", "MCEMON",
						"SPF", parseTime("UTC=2008-04-09T15:00:00.000"), ""),
				createTaskArgsValues((short)1, "MON_ID", "Monitoring id", "Raw", "Decimal", "", "60000")*/,
				createPifTaskTrigger(parseTime("UTC=2007-08-31T20:03:23")));
		setTaskArgs(taskInst, "FDS", "MPS", "Time-tagged sequence");
		setPifTaskArgs(taskInst, "Enabled", "CDMU_CTR", "MCEMON", "SPF", parseTime("UTC=2008-04-09T15:00:00.000"), "");
		setTaskParamsCount(taskInst, (short)1);
		setTaskParam(taskInst, "MON_ID", "Monitoring id", "Raw", "Decimal", "", "6000");
		return taskInst;
	}
	
	public PlanningRequestInstanceDetails createPrInst(TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = createPrInst(PR_NAME, "goce plan 3"/*,
				createPifPrFieldsValues("FOS plan increment file", parseTime("UTC=2008-04-07T00:00:00"), "2",
						"Generated", null, "1", "1", "1", "0", "GODB_013", "3", "1", "1")*/);
		setPifPrArgs1(prInst, "FOS plan increment file", parseTime("UTC=2008-04-07T00:00:00"), "2", "GENERATED", null);
		setPifPrArgs2(prInst, "1", "1", "1", "0", "GODB_013", "3", "1", "1");
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
