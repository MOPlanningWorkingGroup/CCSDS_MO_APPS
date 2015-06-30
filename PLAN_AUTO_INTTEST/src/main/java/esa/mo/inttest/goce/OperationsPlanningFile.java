/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.goce;

import java.text.ParseException;

import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;

import esa.mo.inttest.pr.consumer.PlanningRequestConsumer;

/**
 * OPF - Operations Planning File.
 */
public class OperationsPlanningFile extends CommonFile {

	/**
	 * Creates Task definition. <RQ> element in XML.
	 * @return
	 */
	public TaskDefinitionDetails createTaskDef() {
		TaskDefinitionDetails taskDef = PlanningRequestConsumer.createTaskDef("MCDD10HZ", "DIS_DFACS_10_HZ v01");
		ArgumentDefinitionDetailsList argDefs = createSrcDestTypeArgDefs("FCT");
		setParamsCountArgDef(argDefs);
		taskDef.setArgumentDefs(argDefs);
		return taskDef;
	}
	
	/**
	 * Creates PR definition. <EVRQ> element in XML.
	 * @param taskDefIds
	 * @return
	 */
	public PlanningRequestDefinitionDetails createPrDef(LongList taskDefIds) {
		PlanningRequestDefinitionDetails prDef = PlanningRequestConsumer.createPrDef("OPF", "EVRQ from OPF");
		prDef.setTaskDefIds(taskDefIds);
		return prDef;
	}
	
	/**
	 * Returns PR name.
	 * @return
	 */
	public String getPrName() {
		return "OPF-1";
	}
	
	/**
	 * Creates Task instance. <RQ> element in XML.
	 * @param id
	 * @param defId
	 * @param prId
	 * @return
	 * @throws ParseException
	 */
	public TaskInstanceDetails createTaskInst(Long id, Long defId, Long prId) throws ParseException {
		TaskInstanceDetails taskInst = PlanningRequestConsumer.createTaskInst(/*"MCDD10HZ"*/id, defId, null);
		addTaskArg(taskInst, "RQ_Parameters_count", new UShort(0));
		taskInst.setTimingConstraints(createPpfTaskTriggers(null, parseTime("UTC=2007-01-02T12:10:00")));
		return taskInst;
	}
	
	/**
	 * Creates PR instance. <EVRQ> element in XML.
	 * @param id
	 * @param defId
	 * @param taskInsts
	 * @return
	 * @throws ParseException
	 */
	public PlanningRequestInstanceDetails createPrInst(Long id, Long defId, TaskInstanceDetailsList taskInsts) throws ParseException {
		PlanningRequestInstanceDetails prInst = PlanningRequestConsumer.createPrInst(/*getPrName()*/id, defId, null);
		prInst.setTasks(taskInsts);
		return prInst;
	}
}
