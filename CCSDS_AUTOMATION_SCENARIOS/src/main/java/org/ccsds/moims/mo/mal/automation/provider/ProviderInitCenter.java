package org.ccsds.moims.mo.mal.automation.provider;

import org.ccsds.moims.mo.automation.AutomationHelper;
import org.ccsds.moims.mo.automation.proceduredefinition.ProcedureDefinitionHelper;
import org.ccsds.moims.mo.automation.procedureexecution.ProcedureExecutionHelper;
import org.ccsds.moims.mo.automation.scheduleexecution.ScheduleExecutionHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;

/**
 * Helper class helps to manage service registrations.
 * @author krikse
 *
 */
public class ProviderInitCenter {
	
	private static boolean isProcedureDefinitionRunning = false;
	private static boolean isProcedureExecutionRunning = false;
	private static boolean isScheduleExecutionRunning = false;
	private static boolean isBaseRunning = false;
	
	private static void startBase() throws MALException {
		if (!isBaseRunning) {
			isBaseRunning = true;
			MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			AutomationHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
	
	public static void startProcedureDefinition() throws MALException {
		if (!isProcedureDefinitionRunning) {
			startBase();
			isProcedureDefinitionRunning = true;
			ProcedureDefinitionHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
	
	public static void startProcedureExecution() throws MALException {
		if (!isProcedureExecutionRunning) {
			startBase();
			isProcedureExecutionRunning = true;
			ProcedureExecutionHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
	
	public static void startScheduleExecution() throws MALException {
		if (!isScheduleExecutionRunning) {
			startBase();
			isScheduleExecutionRunning = true;
			ScheduleExecutionHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}

}
