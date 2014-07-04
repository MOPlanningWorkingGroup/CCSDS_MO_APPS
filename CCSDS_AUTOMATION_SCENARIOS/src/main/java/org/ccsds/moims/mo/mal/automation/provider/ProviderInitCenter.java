package org.ccsds.moims.mo.mal.automation.provider;

import org.ccsds.moims.mo.automation.AutomationHelper;
import org.ccsds.moims.mo.automation.proceduredefinitionservice.ProcedureDefinitionServiceHelper;
import org.ccsds.moims.mo.automation.procedureexecutionservice.ProcedureExecutionServiceHelper;
import org.ccsds.moims.mo.automation.scheduleexecutionservice.ScheduleExecutionServiceHelper;
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
			ProcedureDefinitionServiceHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
	
	public static void startProcedureExecution() throws MALException {
		if (!isProcedureExecutionRunning) {
			startBase();
			isProcedureExecutionRunning = true;
			ProcedureExecutionServiceHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
	
	public static void startScheduleExecution() throws MALException {
		if (!isScheduleExecutionRunning) {
			startBase();
			isScheduleExecutionRunning = true;
			ScheduleExecutionServiceHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}

}
