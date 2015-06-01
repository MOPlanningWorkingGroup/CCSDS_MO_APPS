package esa.mo.inttest.pr.provider;

import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;

/**
 * Planning Request Service plugin. Hooks to PR submit, update and remove methods.
 */
public interface Plugin {

	/**
	 * Set provider to use.
	 * @param prov
	 */
	void setProv(PlanningRequestProvider prov);
	
	/**
	 * Called when new PRs are added.
	 * @param prs
	 */
	void onPrSubmit(PlanningRequestInstanceDetails pr);
	
	/**
	 * Called when PR has been updated.
	 * @param prs
	 * @param stats
	 */
	void onPrUpdate(PlanningRequestInstanceDetails pr, PlanningRequestStatusDetails stat);
	
	/**
	 * Called when PR has been removed.
	 * @param prInstIds
	 */
	void onPrRemove(Long prId);
}
