package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UShort;
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

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

/**
 * PIF - Plan Increment File.
 */
public class PlanIncrementFile extends CommonFile {

	/**
	 * Defines some additional <RQ> fields and some <EV> fields.
	 * @param argDefs
	 * @return
	 */
	protected ArgumentDefinitionDetailsList setSubsysEventArgDefs(ArgumentDefinitionDetailsList argDefs) {
		argDefs.add(createArgDef("RQ_Subsystem", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("CDMU_CTR")));
		argDefs.add(createArgDef("Parent_Event_Name", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("Parent_Event_Source", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, null));
		argDefs.add(createArgDef("Parent_Event_Time", null, Attribute.TIME_TYPE_SHORT_FORM, null, null, null, null));
		return argDefs;
	}
	
	/**
	 * Defines version arguments.
	 * @return
	 */
	protected ArgumentDefinitionDetailsList createPifVersionArgDefs() {
		ArgumentDefinitionDetailsList fields = new ArgumentDefinitionDetailsList();
		fields.add(createArgDef("PIF_File_Type", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("FOS PLAN INCREMENT FILE")));
		fields.add(createArgDef("PIF_File_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("2")));
		fields.add(createArgDef("PIF_Replan_Time", null, Attribute.TIME_TYPE_SHORT_FORM, null, null, null, null));
		fields.add(createArgDef("PIF_SPF_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("1")));
		fields.add(createArgDef("PIF_PPF_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("1")));
		fields.add(createArgDef("PIF_OPF_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("1")));
		fields.add(createArgDef("PIF_MTF_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("0")));
		fields.add(createArgDef("PIF_WODB_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("GODB_013")));
		fields.add(createArgDef("PIF_RC_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("3")));
		fields.add(createArgDef("PIF_KUP_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("1")));
		fields.add(createArgDef("PIF_SI_Version", null, Attribute.STRING_TYPE_SHORT_FORM, null, null, null, new Union("1")));
		return fields;
	}
	
	/**
	 * Creates Start trigger of absolute time.
	 * @param exec
	 * @return
	 */
	protected TriggerDetailsList createPifTaskTrigger(Time exec) {
		TriggerDetailsList list = new TriggerDetailsList();
		list.add(createTaskTrigger(TriggerName.START, createAbsTimeTrig(exec)));
		return list;
	}

	/**
	 * Sets some arguments.
	 * @param prInst
	 * @param fType
	 * @param start
	 * @param fVer
	 * @param stat
	 * @param replan
	 * @return
	 */
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
	
	/**
	 * Sets some more (version) arguments.
	 * @param prInst
	 * @param spf
	 * @param ppf
	 * @param opf
	 * @param mtf
	 * @param wodb
	 * @param rc
	 * @param kup
	 * @param si
	 * @return
	 */
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
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_WODB_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(wodb)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_RC_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(rc)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_KUP_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(kup)));
		
		prInst.getArgumentDefNames().add(new Identifier("PIF_SI_Version"));
		prInst.getArgumentValues().add(new AttributeValue(new Union(si)));
		return prInst;
	}

	/**
	 * Creates task definition.
	 * @return
	 */
	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("MCEMON", "ENA_MON_ID_PASW v01");
		ArgumentDefinitionDetailsList argDefs = createSrcDestTypeArgDefs("FDS");
		setSubsysEventArgDefs(argDefs);
		setParamsCountArgDef(argDefs);
		argDefs.add(createArgDef("MON_ID", "Monitoring ID", Attribute.LONG_TYPE_SHORT_FORM, "", "Raw", "Decimal", null));
		taskDef.setArgumentDefs(argDefs);
		return taskDef;
	}
	
	/**
	 * Creates PR definition.
	 * @param taskDefNames
	 * @return
	 */
	public PlanningRequestDefinitionDetails createPrDef(IdentifierList taskDefNames) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("PIF", "PIF from PIF");
		prDef.setArgumentDefs(createPifVersionArgDefs());
		prDef.setTaskDefNames(taskDefNames);
		return prDef;
	}
	
	/**
	 * Returns PR instance name.
	 * @return
	 */
	public String getPrName() {
		return "PIF-1";
	}
	
	/**
	 * Creates Task instance.
	 * @return
	 * @throws ParseException
	 */
	public TaskInstanceDetails createTaskInst(Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(/*"MCEMON-1"*/id, defId, null/*, getPrName()*/);
		taskInst.setTimingConstraints(createPifTaskTrigger(parseTime("UTC=2007-08-31T20:03:23")));
		setTaskArg(taskInst, "Parent_Event_Name", new AttributeValue(new Union("MCEMON")));
		setTaskArg(taskInst, "Parent_Event_Source", new AttributeValue(new Union("SPF")));
		setTaskArg(taskInst, "Parent_Event_Time", new AttributeValue(parseTime("UTC=2008-04-09T15:00:00.000")));
		setTaskArg(taskInst, "RQ_Parameters_count", new AttributeValue(new UShort(1)));
		setTaskArg(taskInst, "MON_ID", new AttributeValue(new Union(60000L))); // long
		return taskInst;
	}
	
	/**
	 * Creates PR instance.
	 * @param taskInsts
	 * @return
	 * @throws ParseException
	 */
	public PlanningRequestInstanceDetails createPrInst(Long id, Long defId, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(/*getPrName()*/id, defId, null);
		TriggerDetailsList trigs = new TriggerDetailsList();
		trigs.add(createTaskTrigger(TriggerName.START, createAbsTimeTrig(parseTime("UTC=2008-04-07T00:00:00"))));
		prInst.setTimingConstraints(trigs);
		setPrArg(prInst, "PIF_Replan_Time", null);
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
