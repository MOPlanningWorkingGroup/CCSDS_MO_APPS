package org.ccsds.moims.mo.mal.planning.provider;

import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.task.TaskHelper;

/**
 * Helper class helps to manage service registrations.
 * @author krikse
 *
 */
public class ProviderInitCenter {
	
	private static boolean isPlanningRequestRunning = false;
	private static boolean isTaskRunning = false;
	private static boolean isBaseRunning = false;
	
	private static void startBase() throws MALException {
		if (!isBaseRunning) {
			isBaseRunning = true;
			MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
	
	public static void startPlanningRequestRegistry() throws MALException {
		if (!isPlanningRequestRunning) {
			startBase();
			isPlanningRequestRunning = true;
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
	
	public static void startTaskRegistry() throws MALException {
		if (!isTaskRunning) {
			startBase();
			isTaskRunning = true;
			TaskHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}

}
