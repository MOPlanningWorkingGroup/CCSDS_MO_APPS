/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimingDetailsList;
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
	 * Defines version arguments from <PIF_Header>.
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
	protected TimingDetailsList createPifTaskTrigger(Time exec) {
		TimingDetailsList list = new TimingDetailsList();
		list.add(createTaskTiming(TriggerName.START, createAbsTimeTrig(exec)));
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
		
		addPrArg(prInst, "PIF_File_Type", new Union(fType));
		addPrArg(prInst, "PIF_Start", start);
		addPrArg(prInst, "PIF_File_Version", new Union(fVer));
		addPrArg(prInst, "PIF_Status", new Union(stat));
		addPrArg(prInst, "PIF_Replan_Time", replan);
		
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
		
		addPrArg(prInst, "PIF_SPF_Version", new Union(spf));
		addPrArg(prInst, "PIF_PPF_Version", new Union(ppf));
		addPrArg(prInst, "PIF_OPF_Version", new Union(opf));
		addPrArg(prInst, "PIF_MTF_Version", new Union(mtf));
		addPrArg(prInst, "PIF_WODB_Version", new Union(wodb));
		addPrArg(prInst, "PIF_RC_Version", new Union(rc));
		addPrArg(prInst, "PIF_KUP_Version", new Union(kup));
		addPrArg(prInst, "PIF_SI_Version", new Union(si));
		
		return prInst;
	}

	/**
	 * Creates Task definition. <RQ> element in XML.
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
	 * Creates PR definition. <PIF_Header> element in XML.
	 * @param taskDefNames
	 * @return
	 */
	public PlanningRequestDefinitionDetails createPrDef(LongList taskDefIds) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("PIF", "PIF from PIF");
		prDef.setArgumentDefs(createPifVersionArgDefs());
		prDef.setTaskDefIds(taskDefIds);
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
	 * Creates Task instance. <RQ> element in XML.
	 * @return
	 * @throws ParseException
	 */
	public TaskInstanceDetails createTaskInst(Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(/*"MCEMON-1"*/id, defId, null);
		taskInst.setTimingConstraints(createPifTaskTrigger(parseTime("UTC=2007-08-31T20:03:23")));
		
		addTaskArg(taskInst, "Parent_Event_Name", new Union("MCEMON"));
		addTaskArg(taskInst, "Parent_Event_Source", new Union("SPF"));
		addTaskArg(taskInst, "Parent_Event_Time", parseTime("UTC=2008-04-09T15:00:00.000"));
		addTaskArg(taskInst, "RQ_Parameters_count", new UShort(1));
		addTaskArg(taskInst, "MON_ID", new Union(60000L)); // long
		
		return taskInst;
	}
	
	/**
	 * Creates PR instance. <PIF_Header> element in XML.
	 * @param taskInsts
	 * @return
	 * @throws ParseException
	 */
	public PlanningRequestInstanceDetails createPrInst(Long id, Long defId, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(id, defId, null);
		TimingDetailsList trigs = new TimingDetailsList();
		trigs.add(createTaskTiming(TriggerName.START, createAbsTimeTrig(parseTime("UTC=2008-04-07T00:00:00"))));
		prInst.setTimingConstraints(trigs);
		addPrArg(prInst, "PIF_Replan_Time", null);
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
